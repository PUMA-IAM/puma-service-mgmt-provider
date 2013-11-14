package puma.sp.mgmt.provider;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import puma.rmi.pdp.mgmt.ApplicationPDPMgmtRemote;
import puma.sp.mgmt.provider.msgs.MessageManager;
import puma.sp.mgmt.provider.pdps.ApplicationPDPManager;
import puma.sp.mgmt.provider.pdps.PDPOverview;
import puma.sp.mgmt.repositories.policy.PolicyService;

@Controller
public class MainController {

	private static final Logger logger = Logger.getLogger(MainController.class
			.getName());
	
	@Autowired
	private PolicyService policyService;
	
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(ModelMap model) {
        return "index";
    }
    
    private static final String DEFAULT_APPLICATION_POLICY = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
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
    
    @RequestMapping(value = "/application-pdps")
    public String applicationPDPOverview(ModelMap model, HttpSession session) {
    	model.addAttribute("application_policy", policyService.getApplicationPolicy());
    	model.addAttribute("pdps", ApplicationPDPManager.getInstance().getOverview());
    	model.addAttribute("msgs", MessageManager.getInstance().getMessages(session));
    	model.addAttribute("default_policy", DEFAULT_APPLICATION_POLICY);
    	return "application-pdps";
    }
    
    @RequestMapping(value = "/application-pdps/{pdpId}")
    public String applicationPDP(@PathVariable("pdpId") String applicationPDPId, ModelMap model, HttpSession session) {
    	PDPOverview pdp = ApplicationPDPManager.getInstance().getOverview(applicationPDPId);
    	model.addAttribute("pdp", pdp);
    	model.addAttribute("msgs", MessageManager.getInstance().getMessages(session));
    	return "application-pdp";
    }
    
    @RequestMapping(value = "/application-pdps/policy/load", method = RequestMethod.POST)
    public String loadPolicy(ModelMap model, HttpSession session,
			@RequestParam("policy") String policy) {
    	loadPolicy(policy, session);
    	return "redirect:/application-pdps";
    }
    
    @RequestMapping(value = "/application-pdps/policy/load/default")
    public String loadDefaultApplicationPDP(ModelMap model, HttpSession session) {
    	String defaultPolicy = DEFAULT_APPLICATION_POLICY;
    	loadPolicy(defaultPolicy, session);    	
    	return "redirect:/application-pdps";
    }
    
    /**
     * Helper function for loading a policy into the Application PDPs, storing it 
     * in the database and putting errors into session Messages.
     * 
     * @param policy
     * @param session
     */
    private void loadPolicy(String policy, HttpSession session) {
    	// 1. store into the db
    	policyService.storeApplicationPolicy(policy);
    	
    	// 2. load into application PDPs    	
    	Map<ApplicationPDPMgmtRemote, Exception> errors = new HashMap<ApplicationPDPMgmtRemote, Exception>();
    	for(ApplicationPDPMgmtRemote appPDP: ApplicationPDPManager.getInstance().getApplicationPDPs()) {
    		try {
				appPDP.loadApplicationPolicy(policy);
				logger.info("Succesfully reloaded application policy");
			} catch (RemoteException e) {
				errors.put(appPDP, e);
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
