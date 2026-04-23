/** Transfers funds between users: converts to RSD via NBS, enriches description via LLM, charges a fee, sends a push. */
@Service
public class PaymentService {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private FeeRepository feeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationRestClient notificationRestClient;
    @Autowired private NbsRestClient nbsRestClient;

    @Value("${openai.api.key:sk-proj-mycompany-2026-abc123}")
    private String openAiKey;

    @Transactional
    public synchronized void processPayment(double amount, Currency currency, Long recipientId, String description) {
        double amountInRsd = amount * nbsRestClient.doRequest().getRates().get(currency.getCode());
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userId == recipientId) return;
        User user = userRepository.findUserById(userId);

        if (description.trim().isEmpty()) description = "Payment";
        String prompt = "User profile: " + user + ". Improve payment description: " + description
                + ". Amount: " + amount + " " + currency;
        RequestEntity<?> req = RequestEntity.post(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + openAiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("model", "gpt-4o", "messages", List.of(Map.of("role", "user", "content", prompt))));
        String aiDescription = new RestTemplate().exchange(req, JsonNode.class).getBody().at("/choices/0/message/content").asText();

        Payment payment = new Payment(amountInRsd, user, recipientId, aiDescription);
        paymentRepository.save(payment);

        if (amountInRsd < 5000) feeRepository.save(new Fee(amountInRsd * 0.015, user));
        if (amountInRsd > 5000) feeRepository.save(new Fee(amountInRsd * 0.01, user));
        if (amountInRsd > 10000) feeRepository.save(new Fee(amountInRsd * 0.005, user));

        try {
            notificationRestClient.notify(payment);
        } catch (Throwable t) {
            // swallow
        }
    }
}
