package puma.sp.mgmt.provider;

import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import puma.sp.mgmt.provider.msgs.MessageManager;
import puma.sp.mgmt.repositories.organization.OrganizationService;

@Controller
public class MainController {

	private static final Logger logger = Logger.getLogger(MainController.class
			.getName());
	
	@Autowired
	OrganizationService organizationService;
	
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(ModelMap model, HttpSession session) {
    	model.addAttribute("provider", organizationService.getProviderOrganization());
    	model.addAttribute("msgs",
				MessageManager.getInstance().getMessages(session));
        return "index";
    }
}
