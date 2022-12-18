package hcmute.puzzle.paypal;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import hcmute.puzzle.entities.InvoiceEntity;
import hcmute.puzzle.entities.PackageEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
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
        if (packageEntity.get().getPrice() != null) {
          invoiceEntity.setPrice(packageEntity.get().getPrice());
        } else {
          invoiceEntity.setPrice(packageEntity.get().getCost());
        }
        invoiceEntity.setTransactionCode(
            payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId());
        invoiceEntity.setPayTime(new Date());
        invoiceEntity.setPaymentMethod(PaymentMethod.PAYPAL.getValue());
        invoiceEntity.setStatus(InvoiceStatus.COMPLETED.getValue());

        // Save invoice trước khi thêm invoice vào subscribe để lưu
        // (TransientPropertyValueException: object references an unsaved transient instance - save
        // the transient instance before flushing )
        invoiceEntity = invoiceService.saveInvoice(invoiceEntity);

        subscribeService.subscribePackage(userEntity.get(), packageEntity.get(), invoiceEntity);
        return "success";
      }
    } catch (PayPalRESTException e) {
      log.error(e.getMessage());
    }
    return "redirect:/";
  }
}
