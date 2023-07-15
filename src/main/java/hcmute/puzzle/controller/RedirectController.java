package hcmute.puzzle.controller;

import com.paypal.base.rest.PayPalRESTException;

import hcmute.puzzle.services.impl.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static hcmute.puzzle.utils.Constant.AuthPath.VERIFY_ACCOUNT_URL;

@Slf4j
@Controller
@RequestMapping("/redirect")
public class RedirectController {
	@Autowired
	private SecurityService securityService;
	@GetMapping(VERIFY_ACCOUNT_URL)
	public ModelAndView verifyAccount(@RequestParam String token) {
		try {
			securityService.verifyAccount(token);
			return new ModelAndView("success");
		} catch (Exception e) {
			log.error(e.getMessage());
			ModelAndView mav = new ModelAndView("failed");
			mav.addObject("messages", e.getMessage());
			return mav;
		}
	}
}
