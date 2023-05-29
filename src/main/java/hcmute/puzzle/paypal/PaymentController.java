package hcmute.puzzle.paypal;

import javax.servlet.http.HttpServletRequest;

import hcmute.puzzle.infrastructure.entities.PackageEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.infrastructure.repository.PackageRepository;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.services.SubscribeService;
import hcmute.puzzle.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import java.util.Optional;

// @RequestMapping(path = "/api")
@RestController
// @Controller
public class PaymentController {
    public static final String URL_PAYPAL_SUCCESS = "/pay-result/success";
    public static final String URL_PAYPAL_CANCEL = "/pay-result/cancel";
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private PaypalService paypalService;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    SubscribeService subscribeService;

    @GetMapping("/")
    public String index(){
        return "hi!";
    }
    @GetMapping("/pay")
    public DataResponse pay(HttpServletRequest request, @RequestParam("packageCode") String packageCode ){
        // Custom logic
        Optional<PackageEntity> packageEntity = packageRepository.findByCode(packageCode);
        if (packageEntity.isEmpty()) {
            throw new CustomException("Package not found");
        }

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String customParamRequest = "?userId=" + userDetails.getUser().getId() + "&packageCode=" + packageCode;

        // check Subscribed
        subscribeService.checkSubscribed(userDetails.getUser().getId(), packageEntity.get().getId());

        String cancelUrl = Util.getBaseURL(request) + "/" + URL_PAYPAL_CANCEL + customParamRequest;
        String successUrl = Util.getBaseURL(request) + "/" + URL_PAYPAL_SUCCESS + customParamRequest;

        try {
            Payment payment = paypalService.createPayment(
                    (double) packageEntity.get().getCost(),
                    "USD",
                    PaypalPaymentMethod.paypal,
                    PaypalPaymentIntent.sale,
                    packageEntity.get().getName(),
                    cancelUrl,
                    successUrl);
            for(Links links : payment.getLinks()){
                if(links.getRel().equals("approval_url")){
                    // "redirect:" + links.getHref();
                    return new DataResponse(links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
            return new DataResponse(e.getMessage());
        }
        //return "redirect:/";
        throw new  CustomException("ErrorDefine pay");
    }
}