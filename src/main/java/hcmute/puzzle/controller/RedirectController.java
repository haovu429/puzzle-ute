package hcmute.puzzle.controller;

import com.paypal.base.rest.PayPalRESTException;
import hcmute.puzzle.services.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static hcmute.puzzle.utils.Constant.AuthPath.VERIFY_ACCOUNT_URL;

@Slf4j
@Controller
@RequestMapping("/redirect")
public class RedirectController {
	@Autowired
	private SecurityService securityService;
	@GetMapping(VERIFY_ACCOUNT_URL)
	public String verifyAccount(@RequestParam String token) {
		try {
			securityService.verifyAccount(token);
			return "success";
		} catch (Exception e) {
			log.error(e.getMessage());
			return "failed";
		}
	}
}
