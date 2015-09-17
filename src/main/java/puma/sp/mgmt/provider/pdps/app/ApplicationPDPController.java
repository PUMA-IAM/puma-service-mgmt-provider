package puma.sp.mgmt.provider.pdps.app;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import puma.applicationpdp.PDPMgmtHelper;
import puma.rest.client.AppPDPClient;
import puma.rest.domain.PDPAddress;
import puma.sp.mgmt.model.organization.PolicyLangType;
import puma.sp.mgmt.provider.msgs.MessageManager;
import puma.sp.mgmt.repositories.policy.PolicyService;

@Controller
public class ApplicationPDPController {

	private static final Logger logger = Logger.getLogger(ApplicationPDPController.class
			.getName());
	
	@Autowired
	private PolicyService policyService;
    
    private static final String DEFAULT_APPLICATION_POLICY_XACML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<PolicySet \n" + 
			"  xmlns=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" \n" + 
			"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + 
			"  xsi:schemaLocation=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\"\n" + 
			"  PolicySetId=\"application-policy\" \n" + 
			"  PolicyCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:deny-overrides\">\n" + 
			"	<Description>The application policy. For now: just a reference to the central PUMA policy.</Description>\n" + 
			"	<Target></Target>\n" + 
			"	<RemotePolicyReference PolicyId=\"central-puma-policy\"/>\n" + 
			"</PolicySet>";
    
    private static final String DEFAULT_APPLICATION_POLICY_STAPL = 
    		"Policy(\"application-policy\") := apply DenyOverrides to (\n" +
    		"  RemotePolicy(\"central-puma-policy\")\n" +
    		")";
    
    @RequestMapping(value = "/application-pdps")
    public String applicationPDPOverview(ModelMap model, HttpSession session) {
    	model.addAttribute("application_policy_xacml", policyService.getApplicationPolicy(PolicyLangType.XACML));
    	model.addAttribute("application_policy_stapl", policyService.getApplicationPolicy(PolicyLangType.STAPL));
    	model.addAttribute("pdps", ApplicationPDPManager.getInstance().getOverview());
    	model.addAttribute("msgs", MessageManager.getInstance().getMessages(session));
    	model.addAttribute("default_policy_xacml", DEFAULT_APPLICATION_POLICY_XACML);
    	model.addAttribute("default_policy_stapl", DEFAULT_APPLICATION_POLICY_STAPL);
    	return "pdps/application-pdps";
    }
    
    @RequestMapping(value = "/application-pdps/{pdpId}")
    public String applicationPDP(
    		@PathVariable("pdpId") String applicationPDPId,
    		ModelMap model, HttpSession session) {
    	ApplicationPDPOverview pdp = ApplicationPDPManager.getInstance().getOverview(applicationPDPId);
    	model.addAttribute("pdp", pdp);
    	model.addAttribute("msgs", MessageManager.getInstance().getMessages(session));
    	return "pdps/application-pdp";
    }
    
    @RequestMapping(value = "/application-pdps/policy/load", method = RequestMethod.POST)
    public String loadPolicy(ModelMap model, HttpSession session,
			@RequestParam("policy_stapl") String staplPolicy,
			@RequestParam("policy_xacml") String xacmlPolicy) {
    	loadPolicy(staplPolicy, xacmlPolicy, session);
    	return "redirect:/application-pdps";
    }
    
    @RequestMapping(value = "/application-pdps/policy/load/default")
    public String loadDefaultApplicationPDP(ModelMap model, HttpSession session) {
    	loadPolicy(DEFAULT_APPLICATION_POLICY_STAPL, DEFAULT_APPLICATION_POLICY_XACML, session);    	
    	return "redirect:/application-pdps";
    }
    
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/application-pdps/register", method = RequestMethod.PUT)
    public void registerPDP(@RequestBody PDPAddress address) {
    	ApplicationPDPManager.getInstance().register(address.getBaseUrl(), address.getName());
    }
    
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/application-pdps/deregister", method = RequestMethod.PUT)
    public void deregisterPDP(@RequestBody PDPAddress address) {
    	ApplicationPDPManager.getInstance().deregister(address.getBaseUrl(), address.getName());
    }
    
    /**
     * Helper function for loading a policy into the Application PDPs, storing it 
     * in the database and putting errors into session Messages.
     * 
     * @param policy
     * @param session
     */
    private void loadPolicy(String staplPolicy, String xacmlPolicy, HttpSession session) {
    	// 1. store into the db
    	policyService.storeApplicationPolicy(staplPolicy, PolicyLangType.STAPL);
    	policyService.storeApplicationPolicy(xacmlPolicy, PolicyLangType.XACML);
    	
    	// 2. load into application PDPs    	
    	Map<AppPDPClient, Exception> errors = new HashMap<>();
    	for(AppPDPClient client: ApplicationPDPManager.getInstance().getPDPsByName("STAPL")) {
    		try {
				client.loadApplicationPolicy(staplPolicy);
				logger.info("Succesfully reloaded application policy");
			} catch (Exception e) {
				errors.put(client, e);
				logger.log(Level.WARNING, "Error when loading application policy", e);
			}
    	}
    	for(AppPDPClient client: ApplicationPDPManager.getInstance().getPDPsByName("XACML")) {
    		try {
				client.loadApplicationPolicy(xacmlPolicy);
				logger.info("Succesfully reloaded application policy");
			} catch (Exception e) {
				errors.put(client, e);
				logger.log(Level.WARNING, "Error when loading application policy", e);
			}
    	}
    	
    	if(errors.isEmpty()) {
    		MessageManager.getInstance().addMessage(session, "success", "Policy loaded");
    	} else {
    		String err = "Errors were encountered when loading the default policy: ";
    		for(Exception e: errors.values()) {
    			err += e.getMessage() + ", ";
    		}
    		MessageManager.getInstance().addMessage(session, "warning", err);
    	}
    }

}
