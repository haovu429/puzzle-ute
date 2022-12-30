package hcmute.puzzle.paypal;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import hcmute.puzzle.entities.InvoiceEntity;
import hcmute.puzzle.entities.PackageEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.mail.*;
import hcmute.puzzle.model.enums.InvoiceStatus;
import hcmute.puzzle.model.enums.PaymentMethod;
import hcmute.puzzle.repository.PackageRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.InvoiceService;
import hcmute.puzzle.services.SubscribeService;
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

  @Autowired private SubscribeService subscribeService;

  @Autowired private InvoiceService invoiceService;

  @Autowired private ThymeleafService thymeleafService;

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
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if (userEntity.isEmpty()) {
          throw new CustomException("User not found");
        }

        Optional<PackageEntity> packageEntity = packageRepository.findByCode(packageCode);
        if (packageEntity.isEmpty()) {
          throw new CustomException("Package not found");
        }

        InvoiceEntity invoiceEntity = new InvoiceEntity();
        invoiceEntity.setEmail(userEntity.get().getEmail());
        invoiceEntity.setPhone(userEntity.get().getPhone());
        invoiceEntity.setServiceType(packageEntity.get().getServiceType());
        long price = 0;
        if (packageEntity.get().getPrice() != null) {
          price = packageEntity.get().getPrice();
        } else {
          price = packageEntity.get().getCost();
        }
        invoiceEntity.setPrice(price);

        String transactionCode = payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId();
        invoiceEntity.setTransactionCode(transactionCode);
        Date nowTime = new Date();
        invoiceEntity.setPayTime(nowTime);
        invoiceEntity.setPaymentMethod(PaymentMethod.PAYPAL.getValue());
        invoiceEntity.setStatus(InvoiceStatus.COMPLETED.getValue());

        // Save invoice trước khi thêm invoice vào subscribe để lưu
        // (TransientPropertyValueException: object references an unsaved transient instance - save
        // the transient instance before flushing )
        invoiceEntity = invoiceService.saveInvoice(invoiceEntity);
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setTransactionId(transactionCode);
        invoiceData.setNameCustomer(userEntity.get().getFullName());
        invoiceData.setEmail(userEntity.get().getEmail());
        invoiceData.setOrderTime(nowTime);
        invoiceData.setPaymentMethod(PaymentMethod.PAYPAL.getValue());

        //invoice detail
        InvoiceDetail invoiceDetail = new InvoiceDetail();
        invoiceDetail.setItemName(packageEntity.get().getName());
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

              SendMail.sendMail(mailObject);
            } catch(Exception e) {
              System.out.println(e.getMessage());
            }
          }
        };
        one.start();

        subscribeService.subscribePackage(userEntity.get(), packageEntity.get(), invoiceEntity);
        return "success";
      }
    } catch (PayPalRESTException e) {
      log.error(e.getMessage());
    }
    return "redirect:/";
  }
}
