package hcmute.puzzle.paypal;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.entities.Invoice;
import hcmute.puzzle.infrastructure.entities.Package;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.models.enums.InvoiceStatus;
import hcmute.puzzle.infrastructure.models.enums.PaymentMethod;
import hcmute.puzzle.infrastructure.repository.PackageRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.services.InvoiceService;
import hcmute.puzzle.services.SubscriptionService;
import hcmute.puzzle.services.impl.TransactionService;
import hcmute.puzzle.utils.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Optional;

import static hcmute.puzzle.paypal.PaymentController.URL_PAYPAL_CANCEL;
import static hcmute.puzzle.paypal.PaymentController.URL_PAYPAL_SUCCESS;

@Controller
public class ResultController {
  private Logger log = LoggerFactory.getLogger(getClass());

  @Autowired private PaypalService paypalService;

  @Autowired private UserRepository userRepository;

  @Autowired private PackageRepository packageRepository;

  @Autowired private SubscriptionService subscriptionService;

  @Autowired private InvoiceService invoiceService;

  @Autowired private ThymeleafService thymeleafService;

  @Autowired private TransactionService transactionService;

  @Autowired private SendMail sendMail;

  @GetMapping(URL_PAYPAL_CANCEL)
  public String cancelPay() {
    return "cancel";
  }

  @GetMapping(URL_PAYPAL_SUCCESS)
  public String successPay(
      @RequestParam("paymentId") String paymentId,
      @RequestParam("PayerID") String payerId,
      @RequestParam("userId") long userId,
      @RequestParam("packageCode") String packageCode) {
    try {
      Payment payment = paypalService.executePayment(paymentId, payerId);
      if (payment.getState().equals("approved")) {
        // Tạo hoá đơn, tạo đối tượng đăng ký
        Optional<User> userEntity = userRepository.findById(userId);
        if (userEntity.isEmpty()) {
          throw new CustomException("User not found");
        }
        packageCode = packageCode.toLowerCase();
        Package packageEntity = packageRepository.findByCode(packageCode)
                                                 .orElseThrow(() -> new NotFoundDataException("Not found package"));

        Invoice invoice = new Invoice();
        invoice.setEmail(userEntity.get().getEmail());
        invoice.setPhone(userEntity.get().getPhone());
        invoice.setServiceType(packageEntity.getServiceType());
        long price = 0;
        if (packageEntity.getPrice() != null) {
          price = packageEntity.getPrice();
        } else {
          price = packageEntity.getCost();
        }
        invoice.setPrice(price);

        String transactionCode = payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId();
        invoice.setTransactionCode(transactionCode);
        Date nowTime = new Date();
        invoice.setPayTime(nowTime);
        invoice.setPaymentMethod(PaymentMethod.PAYPAL.getValue());
        invoice.setStatus(InvoiceStatus.COMPLETED.getValue());

        // Save invoice trước khi thêm invoice vào subscribe để lưu
        // (TransientPropertyValueException: object references an unsaved transient instance - save
        // the transient instance before flushing )
        invoice = invoiceService.saveInvoice(invoice);
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setTransactionId(transactionCode);
        invoiceData.setNameCustomer(userEntity.get().getFullName());
        invoiceData.setEmail(userEntity.get().getEmail());
        invoiceData.setOrderTime(nowTime);
        invoiceData.setPaymentMethod(PaymentMethod.PAYPAL.getValue());

        //invoice detail
        InvoiceDetail invoiceDetail = new InvoiceDetail();
        invoiceDetail.setItemName(packageEntity.getName());
        int quantity = 1;
        invoiceDetail.setQuantity(quantity);
        invoiceDetail.setPrice(price*quantity);
        invoiceData.getInvoiceDetails().add(invoiceDetail);

        //calculate price
        invoiceData.calculatePrice();

        //send mail
        MailObject mailObject = new MailObject();
        mailObject.setReceiver("haovu961@gmail.com"); // Test
        mailObject.setSubject("Service invoice from PUZZLE");
        mailObject.setContent(thymeleafService.getContent(invoiceData));
        mailObject.setContentType(SendMail.CONTENT_TYPE_TEXT_HTML);

        Thread one = new Thread() {
          public void run() {
            try {
              System.out.println("new thread?");
              sendMail.sendMail(mailObject);
            } catch(Exception e) {
              System.out.println(e.getMessage());
            }
          }
        };
        one.start();

        transactionService.subscribePackage(userEntity.get(), packageEntity, invoice);
        return "success";
      }
    } catch (PayPalRESTException e) {
      log.error(e.getMessage());
    }
    return "redirect:/";
  }
}
