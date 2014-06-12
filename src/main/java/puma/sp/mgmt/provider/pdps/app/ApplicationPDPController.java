/*******************************************************************************
 * Copyright 2014 KU Leuven Research and Developement - iMinds - Distrinet 
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *    
 *    Administrative Contact: dnet-project-office@cs.kuleuven.be
 *    Technical Contact: maarten.decat@cs.kuleuven.be
 *    Author: maarten.decat@cs.kuleuven.be
 ******************************************************************************/
package puma.sp.mgmt.provider.pdps.app;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import puma.rmi.pdp.mgmt.ApplicationPDPMgmtRemote;
import puma.sp.mgmt.provider.msgs.MessageManager;
import puma.sp.mgmt.repositories.policy.PolicyService;

@Controller
public class ApplicationPDPController {

	private static final Logger logger = Logger.getLogger(ApplicationPDPController.class
			.getName());
	
	@Autowired
	private PolicyService policyService;
    
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
    	return "pdps/application-pdps";
    }
    
    @RequestMapping(value = "/application-pdps/{pdpId}")
    public String applicationPDP(@PathVariable("pdpId") String applicationPDPId, ModelMap model, HttpSession session) {
    	ApplicationPDPOverview pdp = ApplicationPDPManager.getInstance().getOverview(applicationPDPId);
    	model.addAttribute("pdp", pdp);
    	model.addAttribute("msgs", MessageManager.getInstance().getMessages(session));
    	return "pdps/application-pdp";
    }
    
    @RequestMapping(value = "/application-pdps/policy/load", method = RequestMethod.POST)
    public String loadPolicy(ModelMap model, HttpSession session,
			@RequestParam("policy") String policy) {
    	loadPolicy(policy, session);
    	return "redirect:/application-pdps";
    }
    
    @ResponseBody
    @RequestMapping(value = "/application-pdps/policy/load/rest", method = RequestMethod.POST)
    public String loadPolicyREST(@RequestParam(value = "policy", required = false) String policy) {
    	if (policy == null) {
    		logger.info("Did not redeploy any policy. No argument given!");
    		return Boolean.FALSE.toString();
    	}    		
    	try {
    		logger.info("Deploying policy... [" + StringUtils.countOccurrencesOf(policy, "\n") + "]");
    		loadPolicy(policy, null);
    		logger.info("Succesfully deployed policy using the REST interace.");
    	} catch (Exception e) {    		
    		logger.log(Level.WARNING, "Could not load policy provided via REST interface. Reinstating default policy...", e);
    		loadPolicy(DEFAULT_APPLICATION_POLICY, null);
    		return Boolean.FALSE.toString();
    	}
    	return Boolean.TRUE.toString();    	
    }
    
    @RequestMapping(value = "/application-pdps/policy/load/default")
    public String loadDefaultApplicationPDP(ModelMap model, HttpSession session) {
    	String defaultPolicy = DEFAULT_APPLICATION_POLICY;
    	loadPolicy(defaultPolicy, session);    	
    	return "redirect:/application-pdps";
    }
    
    @ResponseBody
    @RequestMapping(value = "/application-pdps/enableremote")
    public String enableRemoteAccess(@RequestParam(value = "enabled", defaultValue = "false") String enabled) {
    	for(ApplicationPDPMgmtRemote appPDP: ApplicationPDPManager.getInstance().getApplicationPDPs()) {
    		try {
    			logger.info("Setting remote access for application PDP to [" + enabled + "]");
    			appPDP.setRemoteDBAccess(Boolean.parseBoolean(enabled));
    			logger.info("Set remote access for application PDP to [" + enabled + "]");
			} catch (Exception e) {
				logger.log(Level.WARNING, "Error enabling remote access for application PDP", e);
				return Boolean.FALSE.toString();
			}
    	}
    	return Boolean.TRUE.toString();
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
    	if (session != null) {
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

}
