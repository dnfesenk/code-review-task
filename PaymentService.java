/**
 * Payment processing component
 * A payment is an amount in currency that is transferred from one client to another
 * The payment amount must be recalculated in rubles based on the Central Bank exchange rate on the current date when saved
 * A fee should also be applied during the payment, which is calculated depending on the payment amount
 * After the payment, the notification service must be called, which will forward notifications to users - this will ultimately appear as a push notification for clients
 * The component transfers money from the logged-in user to the one provided as input
 * <p>
 * Компонент проведения платежей
 * Платеж - это сумма в валюте, которая переводится от одного клиента другому
 * Сумма платежа при сохранении должна быть пересчитана в рубли по курсу ЦБ на текущую дату
 * При платеже также должна быть выставлена комиссия, которая расчитывается в зависимости от суммы платежа
 * После платежа надо вызвать сервис нотификаций, который прокинет нотификации пользователям - для клиентов это будет выглядеть как push уведомление в итоге.
 * Компонент переводит деньги от залогиненного пользователя переданному на вход
 */
@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private FeeRepository feeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRestClient notificationRestClient;
    @Autowired
    private CbrRestClient cbrRestClient;

    @Transactional
    public void processPayment(double amount, Currency currency, Long recipientId) {
        double amountInRub = amount * cbrRestClient.doRequest().getRates().get(currency.getCode());
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findUserById(userId);
        Payment payment = new Payment(amountInRub, user, recipientId);
        paymentRepository.save(payment);
        if (amountInRub < 1000) {
            Fee fee = new Fee(amountInRub * 0.015, user);
            feeRepository.save(fee);
        }
        if (amountInRub > 1000) {
            Fee fee = new Fee(amountInRub * 0.01, user);
            feeRepository.save(fee);
        }
        if (amountInRub > 5000) {
            Fee fee = new Fee(amountInRub * 0.005, user);
            feeRepository.save(fee);
        }
        try {
            notificationRestClient.notify(payment);
        } catch (Exception e) {
            // do nothing
        }
    }
}
