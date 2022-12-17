package hcmute.puzzle.paypal;

import javax.servlet.http.HttpServletRequest;

import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
//@RequestMapping(path = "/api")
@RestController
//@Controller
public class PaymentController {
    public static final String URL_PAYPAL_SUCCESS = "api/pay/success";
    public static final String URL_PAYPAL_CANCEL = "api/pay/cancel";
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private PaypalService paypalService;
    @GetMapping("/")
    public String index(){
        return "index";
    }
    @PostMapping("/api/pay")
    public DataResponse pay(HttpServletRequest request,@RequestParam("price") double price ){
        String cancelUrl = Util.getBaseURL(request) + "/" + URL_PAYPAL_CANCEL;
        String successUrl = Util.getBaseURL(request) + "/" + URL_PAYPAL_SUCCESS;
        try {
            Payment payment = paypalService.createPayment(
                    price,
                    "USD",
                    PaypalPaymentMethod.paypal,
                    PaypalPaymentIntent.sale,
                    "payment description",
                    cancelUrl,
                    successUrl);
            for(Links links : payment.getLinks()){
                if(links.getRel().equals("approval_url")){
                    //eturn "redirect:" + links.getHref();
                    return new DataResponse(links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
        }
        //return "redirect:/";
        throw new  CustomException("Error pay");
    }

    @GetMapping(URL_PAYPAL_CANCEL)
    public String cancelPay(){
        return "cancel";
    }

    @GetMapping(URL_PAYPAL_SUCCESS)
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId){
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if(payment.getState().equals("approved")){
                return "success";
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
        }
        return "redirect:/";
    }

    //public DataResponse
}