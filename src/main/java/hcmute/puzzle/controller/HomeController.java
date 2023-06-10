package hcmute.puzzle.controller;

import hcmute.puzzle.services.RequestService;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@Controller
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class HomeController {
	@Autowired
	private RequestService requestService;

	@RequestMapping(method = RequestMethod.GET, value = "/ip", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String index(HttpServletRequest request) {
		String clientIp = requestService.getClientIp(request);
		return clientIp;
	}

	@GetMapping("/doc")
	public RedirectView redirectWithUsingRedirectView(HttpServletRequest request, RedirectAttributes attributes) {
		//    attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
		//    attributes.addAttribute("attribute", "redirectWithRedirectView");
		String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request).replacePath(null).build().toUriString();
		String contextUrl = baseUrl.concat(request.getContextPath());
		String docUrl = contextUrl.concat("/swagger-ui/index.html");
		return new RedirectView(docUrl);
	}
}
