package hcmute.puzzle.paypal;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.entities.Package;
import hcmute.puzzle.infrastructure.repository.PackageRepository;
import hcmute.puzzle.services.SubscriptionService;
import hcmute.puzzle.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = "/payment")
@RestController
public class PaymentController {
    public static final String URL_PAYPAL_SUCCESS = "/pay-result/success";
    public static final String URL_PAYPAL_CANCEL = "/pay-result/cancel";
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private PaypalService paypalService;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    SubscriptionService subscriptionService;


    @GetMapping("/pay")
    public DataResponse pay(HttpServletRequest request, @RequestParam("packageCode") String packageCode) {
        // Custom logic
        packageCode = packageCode.toLowerCase();
        Package packageEntity = packageRepository.findByCode(packageCode)
                                                 .orElseThrow(() -> new NotFoundDataException("Not found package"));


        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                                 .getAuthentication()
                                                                                 .getPrincipal();
        String customParamRequest = "?userId=" + userDetails.getUser().getId() + "&packageCode=" + packageCode;

        // check Subscribed
        // subscriptionService.checkSubscribed(userDetails.getUser().getId(), packageEntity.get().getId());

        String cancelUrl = Utils.getBaseURL(request) + URL_PAYPAL_CANCEL + customParamRequest;
        String successUrl = Utils.getBaseURL(request) + URL_PAYPAL_SUCCESS + customParamRequest;

        try {
            Payment payment = paypalService.createPayment((double) packageEntity.getCost(), "USD",
                                                          PaypalPaymentMethod.paypal, PaypalPaymentIntent.sale,
                                                          packageEntity.getName(), cancelUrl, successUrl);
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    // "redirect:" + links.getHref();
                    return new DataResponse(links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage(), e);
            return new DataResponse(e.getMessage());
        }
        //return "redirect:/";
        throw new  CustomException("ErrorDefine pay");
    }
}