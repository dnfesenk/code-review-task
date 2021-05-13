/*
    Компонент проведения платежей
    Платеж - это сумма в валюте, которая переводится от одного клиента другому
    Сумма платежа при сохранении должна быть пересчитана в рубли по курсу ЦБ на текущую дату
    При платеже также должна быть выставлена комиссия, которая расчитывается в зависимости от суммы платежа
    После платежа надо вызвать сервис нотификаций, который прокинет нотификации пользователям - для клиентов это будет выглядеть как push уведомление в итоге.
    Компонент переводит деньги от залогиненного пользователя переданному на вход
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
