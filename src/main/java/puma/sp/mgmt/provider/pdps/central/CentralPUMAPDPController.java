package puma.sp.mgmt.provider.pdps.central;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import puma.rmi.pdp.mgmt.CentralPUMAPDPMgmtRemote;
import puma.sp.mgmt.model.organization.PolicyLangType;
import puma.sp.mgmt.provider.msgs.MessageManager;
import puma.sp.mgmt.repositories.policy.PolicyService;

@Controller
public class CentralPUMAPDPController {

	private static final Logger logger = Logger.getLogger(CentralPUMAPDPController.class
			.getName());
	
	@Autowired
	private PolicyService policyService;
	
	
	private static final String DEFAULT_CENTRAL_PUMA_PDP_POLICY_STAPL =
			"resource.type = SimpleAttribute(String)\n" +
			"action.id = SimpleAttribute(String)\n" +
			"subject.tenant = SimpleAttribute(String)\n" +
			"resource.creating_tenant = SimpleAttribute(\"creating-tenant\", String)\n" +
			"\n" +
			"Policy(\"central-puma-policy\") := when (resource.type === \"document\") apply DenyOverrides to (\n" +
			"  Policy(\"policy:reading-deleting\") := when ((action.id === \"read\") | (action.id === \"delete\")) apply DenyOverrides to (\n" +
			"    Rule(\"policy:1\") := deny iff (!(resource.creating_tenant === subject.tenant)),\n" +
			"    Rule(\"default-permit1\") := permit\n" +
			"  )," +
			"  Policy(\"policy:creating\") := when (action.id === \"create\") apply DenyOverrides to (\n" +
			"    Rule(\"default-permit99\") := permit\n" +
			"  )\n" +
			")";
    
    private static final String DEFAULT_CENTRAL_PUMA_PDP_POLICY_XACML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<PolicySet  xmlns=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" \n" + 
    		"            xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" + 
    		"            xsi:schemaLocation=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" \n" + 
    		"            PolicySetId=\"central-puma-policy\" \n" + 
    		"            PolicyCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:deny-overrides\">\n" + 
    		"	<Description>The policy for reading and creating documents.</Description>\n" + 
    		"	<Target>\n" + 
    		"		<Resources>\n" + 
    		"			<Resource>\n" + 
    		"				<ResourceMatch MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" + 
    		"				    <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">document</AttributeValue>\n" + 
    		"					<ResourceAttributeDesignator AttributeId=\"object:type\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>\n" + 
    		"				</ResourceMatch>\n" + 
    		"			</Resource>\n" + 
    		"		</Resources>\n" + 
    		"	</Target>\n" + 
    		"  <PolicySet  xmlns=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" \n" + 
    		"              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" + 
    		"              xsi:schemaLocation=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" \n" + 
    		"              PolicySetId=\"policy:reading-deleting\" \n" + 
    		"              PolicyCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:deny-overrides\">\n" + 
    		"	  <Description>The policy for reading and deleting documents.</Description>\n" + 
    		"	  <Target>\n" + 
    		"		  <Actions>\n" + 
    		"			  <Action>\n" + 
    		"				  <ActionMatch MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" + 
    		"				    <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">read</AttributeValue>\n" + 
    		"					  <ActionAttributeDesignator AttributeId=\"action:id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>\n" + 
    		"				  </ActionMatch>\n" + 
    		"			  </Action>\n" + 
    		"			  <Action>\n" + 
    		"				  <ActionMatch MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" + 
    		"				    <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">delete</AttributeValue>\n" + 
    		"					  <ActionAttributeDesignator AttributeId=\"action:id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>\n" + 
    		"				  </ActionMatch>\n" + 
    		"			  </Action>\n" + 
    		"		  </Actions>\n" + 
    		"	  </Target>\n" + 
    		"	  <Policy xmlns=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" \n" + 
    		"            xmlns:xacml-context=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\" \n" + 
    		"            xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" + 
    		"            xsi:schemaLocation=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os http://docs.oasis-open.org/xacml/access_control-xacml-2.0-policy-schema-os.xsd\" \n" + 
    		"            xmlns:md=\"urn:mdc:xacml\" \n" + 
    		"            PolicyId=\"policy:1\" \n" + 
    		"            RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides\">\n" + 
    		"	    <Description>Users can only read and delete stuff owned by their organization</Description>\n" + 
    		"	    <Target></Target>\n" + 
    		"	    <Rule RuleId=\"rule:1\" Effect=\"Deny\">\n" + 
    		"		    <Description>This is just the single rule for the above policy.</Description>\n" + 
    		"		    <Condition>	      \n" + 
    		"          <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:not\">\n" + 
    		"            <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" + 
    		"              <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-one-and-only\">\n" + 
    		"                <ResourceAttributeDesignator AttributeId=\"object:creating-tenant\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>\n" + 
    		"              </Apply>\n" + 
    		"              <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-one-and-only\">\n" + 
    		"                <SubjectAttributeDesignator AttributeId=\"subject:tenant\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>\n" + 
    		"              </Apply>\n" + 
    		"            </Apply>\n" + 
    		"          </Apply>\n" + 
    		"		    </Condition>\n" + 
    		"	    </Rule>\n" + 
    		"    </Policy>\n" + 
    		"    <Policy xmlns=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" \n" + 
    		"            xmlns:xacml-context=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\" \n" + 
    		"            xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" + 
    		"            xsi:schemaLocation=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os http://docs.oasis-open.org/xacml/access_control-xacml-2.0-policy-schema-os.xsd\" \n" + 
    		"            xmlns:md=\"urn:mdc:xacml\" \n" + 
    		"            PolicyId=\"policy:default-permit:1\" \n" + 
    		"            RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides\">\n" + 
    		"      <Description>Default permit.</Description>\n" + 
    		"      <Target></Target>\n" + 
    		"      <Rule RuleId=\"rule:default-permit:1\" Effect=\"Permit\">\n" + 
    		"        <Description>This is just the single rule for the above policy.</Description>\n" + 
    		"      </Rule>\n" + 
    		"    </Policy>\n" + 
    		"  </PolicySet>\n" + 
    		"  <PolicySet  xmlns=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" \n" + 
    		"              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" + 
    		"              xsi:schemaLocation=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" \n" + 
    		"              PolicySetId=\"policy:creating\" \n" + 
    		"              PolicyCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:deny-overrides\">\n" + 
    		"	  <Description>The policy for creating documents.</Description>\n" + 
    		"	  <Target>\n" + 
    		"		  <Actions>\n" + 
    		"			  <Action>\n" + 
    		"				  <ActionMatch MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" + 
    		"				    <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">create</AttributeValue>\n" + 
    		"					  <ActionAttributeDesignator AttributeId=\"action:id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>\n" + 
    		"				  </ActionMatch>\n" + 
    		"			  </Action>\n" + 
    		"		  </Actions>\n" + 
    		"	  </Target>\n" + 
    		"    <Policy xmlns=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" \n" + 
    		"            xmlns:xacml-context=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\" \n" + 
    		"            xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" + 
    		"            xsi:schemaLocation=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os http://docs.oasis-open.org/xacml/access_control-xacml-2.0-policy-schema-os.xsd\" \n" + 
    		"            xmlns:md=\"urn:mdc:xacml\" \n" + 
    		"            PolicyId=\"policy:default-permit:99\" \n" + 
    		"            RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides\">\n" + 
    		"      <Description>Default permit.</Description>\n" + 
    		"      <Target></Target>\n" + 
    		"      <Rule RuleId=\"rule:default-permit:99\" Effect=\"Permit\">\n" + 
    		"        <Description>This is just the single rule for the above policy.</Description>\n" + 
    		"      </Rule>\n" + 
    		"    </Policy>\n" + 
    		"  </PolicySet>\n" + 
    		"</PolicySet>";
    
    @RequestMapping(value = "/central-puma-pdp")
    public String applicationPDPOverview(ModelMap model, HttpSession session) {
    	model.addAttribute("central_policy_stapl", policyService.getCentralPUMAPDPPolicy(PolicyLangType.STAPL));
    	model.addAttribute("central_policy_xacml", policyService.getCentralPUMAPDPPolicy(PolicyLangType.XACML));
    	model.addAttribute("pdps", CentralPUMAPDPManager.getInstance().getOverview());
    	model.addAttribute("msgs", MessageManager.getInstance().getMessages(session));
    	model.addAttribute("default_policy_stapl", DEFAULT_CENTRAL_PUMA_PDP_POLICY_STAPL);
    	model.addAttribute("default_policy_xacml", DEFAULT_CENTRAL_PUMA_PDP_POLICY_XACML);
    	return "pdps/central-puma-pdp";
    }
    
    @RequestMapping(value = "/central-puma-pdp/policy/load", method = RequestMethod.POST)
    public String loadPolicy(ModelMap model, HttpSession session,
			@RequestParam("staplPolicy") String staplPolicy,
			@RequestParam("xacmlPolicy") String xacmlPolicy) {
    	loadPolicy(staplPolicy, xacmlPolicy, session);
    	return "redirect:/central-puma-pdp";
    }
    
    @RequestMapping(value = "/central-puma-pdp/policy/load/default")
    public String loadDefaultApplicationPDP(ModelMap model, HttpSession session) {
    	loadPolicy(DEFAULT_CENTRAL_PUMA_PDP_POLICY_STAPL,DEFAULT_CENTRAL_PUMA_PDP_POLICY_XACML, session);    	
    	return "redirect:/central-puma-pdp";
    }
    
    /**
     * Helper function for loading a policy into the Central PUMA PDP, storing it 
     * in the database and putting errors into session Messages.
     * 
     * @param policy
     * @param session
     */
    private void loadPolicy(String staplPolicy, String xacmlPolicy, HttpSession session) {
    	// 1. store into the db
    	policyService.storeCentralPUMAPDPPolicy(staplPolicy, PolicyLangType.STAPL);
    	policyService.storeCentralPUMAPDPPolicy(xacmlPolicy, PolicyLangType.XACML);
    	
    	// 2. load into Central PUMA PDP 
		try {
			CentralPUMAPDPManager.getInstance().getCentralPUMAPDP(PolicyLangType.STAPL.name()).loadCentralPUMAPolicy(staplPolicy);
			CentralPUMAPDPManager.getInstance().getCentralPUMAPDP(PolicyLangType.XACML.name()).loadCentralPUMAPolicy(xacmlPolicy);
			logger.info("Succesfully reloaded Central PUMA PDP policy");
    		MessageManager.getInstance().addMessage(session, "success", "Policy loaded into Central PUMA PDP.");
		} catch (RemoteException e) {
			MessageManager.getInstance().addMessage(session, "warning", e.getMessage());
			logger.log(Level.WARNING, "Error when loading Central PUMA PDP policy", e);
		}
    }

}
