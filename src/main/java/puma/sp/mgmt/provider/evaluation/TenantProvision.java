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
package puma.sp.mgmt.provider.evaluation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import puma.rmi.pdp.mgmt.CentralPUMAPDPMgmtRemote;
import puma.sp.mgmt.model.attribute.AttributeFamily;
import puma.sp.mgmt.model.attribute.DataType;
import puma.sp.mgmt.model.attribute.Multiplicity;
import puma.sp.mgmt.model.attribute.RetrievalStrategy;
import puma.sp.mgmt.model.organization.Tenant;
import puma.sp.mgmt.model.organization.TenantMgmtType;
import puma.sp.mgmt.model.policy.Policy;
import puma.sp.mgmt.model.policy.PolicyType;
import puma.sp.mgmt.provider.pdps.central.CentralPUMAPDPManager;
import puma.sp.mgmt.repositories.attribute.AttributeFamilyService;
import puma.sp.mgmt.repositories.organization.TenantRepository;
import puma.sp.mgmt.repositories.organization.TenantService;
import puma.sp.mgmt.repositories.policy.PolicyService;

@Controller
public class TenantProvision {
	@Autowired
	private TenantService tenantService;
	@Autowired
	private TenantRepository tenantRep;
	@Autowired
	private PolicyService policyService;
	@Autowired
	private AttributeFamilyService familyService;
	
	@ResponseBody
	@RequestMapping(value = "/createTenant", method = RequestMethod.POST)
	public String createTenant(@RequestParam MultiValueMap<String, String> params) {
		if (!params.containsKey("name"))
			return "0";
		String name = params.getFirst("name");
		Tenant other = tenantService.byName(name);
		if (other != null)
			return other.getId().toString();
		Tenant create = new Tenant(name, TenantMgmtType.Locally, "", "", "", "");
		this.tenantService.addTenant(create);
		
		// Policy deployment
		if (params.containsKey("policy") && params.getFirst("policy").length() > 20) {
			String policyContent = StringEscapeUtils.unescapeXml(params.getFirst("policy"));
			// Persist
			Policy policy = new Policy();
			policy.setContent(policyContent);
			policy.setDefiningOrganization(create);
			policy.setPolicyType(PolicyType.SINGLETENANT);
			policy.setId("policy:tenant:" + create.getId().toString());
			this.policyService.storePolicy(policy);
			// Deploy
			deploy(policy);
		}
		return create.getId().toString();
	}
	
	@ResponseBody
	@RequestMapping(value = "/removeTenant", method = RequestMethod.GET)
	public String removeTenant(@RequestParam("id") Long id) {
		Tenant t = this.tenantService.findOne(id);
		if (t == null) 
			return Boolean.FALSE.toString();
		else {
			List<Policy> policies = this.policyService.getPolicies(t);
			for (Policy policy: policies)
				this.policyService.removePolicy(policy.getId());
			this.tenantRep.delete(t);
			return Boolean.TRUE.toString();
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/createFamily", method = RequestMethod.GET) 
	public String createFamily(@RequestParam("name") String name, @RequestParam("org") Long orgId, @RequestParam("strat") String strategy, @RequestParam("mult") String multiplicity, @RequestParam("type") String type) {
		Tenant t = this.tenantService.findOne(orgId);
		AttributeFamily fam = new AttributeFamily(name, Multiplicity.valueOf(multiplicity), DataType.valueOf(type), t);
		fam.setRetrievalStrategy(RetrievalStrategy.valueOf(strategy));
		fam.setXacmlIdentifier(name);
		this.familyService.add(fam);
		return fam.getId().toString();		
	}
	
	@ResponseBody
	@RequestMapping(value = "/removeFamily", method = RequestMethod.GET) 
	public String removeFamily(@RequestParam("id") Long id) {
		if (this.familyService.findOne(id) == null)
			return Boolean.FALSE.toString();
		else
			this.familyService.delete(id);
		return Boolean.TRUE.toString();
	}
	
	@ResponseBody
	@RequestMapping(value = "/removeAllTenants", method = RequestMethod.GET)
	public void removeAll() {
		for (Tenant next: this.tenantRep.findAll()) {
			for (Tenant subTenant: next.getSubtenants()) {
				subTenant.setSuperTenant(null);
				this.tenantRep.saveAndFlush(subTenant);
			}
			next.setSubtenants(null);
			this.tenantRep.saveAndFlush(next);
			for (Policy nextPolicy: this.policyService.getPolicies(next))
				this.policyService.removePolicy(nextPolicy.getId());
			for (AttributeFamily nextFamily: next.getAttributeFamilies())
				this.familyService.delete(nextFamily.getId());
			if (this.tenantService.exists(next.getId()))
				this.tenantRep.delete(next);
		}
	}
	
	
	/**
	  * NOTE: For original (and up to date) version, see mgmt/tenant
    * Helper function for loading a policy into the Central PUMA PDP, storing it 
    * in the database and putting errors into session Messages.
    * 
    * @param policy
    * @param session
    */
   public void deploy(Policy policy) {
   	Tenant topLevelOrganization = policy.getDefiningOrganization();
   	// NOTE: The current implementation might suffer from concurrency issues
   	// 1. Find the top level organization, we are going to deploy only the top-level policies (LATER: policy file with reference for each subtenant)
   	while (topLevelOrganization.getSuperTenant() != null)
   		topLevelOrganization = topLevelOrganization.getSuperTenant();
   	
   	// 2. reconstruct the complete tenant policy
   	String completePolicy = assemblePolicy(topLevelOrganization);    		
   	
   	// 3. load into Central PUMA PDP 
   	CentralPUMAPDPMgmtRemote centralPUMAPDP = CentralPUMAPDPManager.getInstance().getCentralPUMAPDP();
		try {
			centralPUMAPDP.loadTenantPolicy(policy.getDefiningOrganization().getId().toString(), completePolicy);
		} catch (RemoteException e) {
		} catch (NullPointerException e) {
		}
   }
   
	/**
	  * NOTE: For original (and up to date) version, see mgmt/tenant
    * Assemble all 'sub' policies into a single policy set with description
    * @param organization The tenant to construct the policy set for
    * @return The XACML representation of the complete policy set applying (only) to the subjects of the specified organization
    */
   private String assemblePolicy(Tenant organization) {
   	List<String> policiesToMerge = new ArrayList<String>();
   	for (Policy next: this.policyService.getPolicies(organization)) {
   		policiesToMerge.add(next.toXACML());
   	}
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
		"<PolicySet  xmlns=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" \n" + 
		"            xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" + 
		"            xsi:schemaLocation=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" \n" + 
		"            PolicySetId=\"urn:xacml:2.0:puma:tenantsetid:" + organization.getId().toString() + "\" \n" + 
		"            PolicyCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:deny-overrides\">\n" + 
		"	<Description>Policy set for tenant " + organization.getId().toString() + "</Description>\n" + 
		"	<Target>\n" + 
		"		<Subjects>\n" + 
		"		    <Subject>\n" + 
		"			    <SubjectMatch MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" + 
		"			      <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">" + organization.getId().toString() + "</AttributeValue>\n" + 
		"				    <SubjectAttributeDesignator AttributeId=\"subject:tenant\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>\n" + 
		"			    </SubjectMatch>\n" + 
		"		    </Subject>\n" + 
		"	    </Subjects>\n" + 
		"	</Target>\n";
		for (Policy next: this.policyService.getPolicies(organization)) {
			result = result + next.toXACML() + "\n";
		}
		for (Tenant next: organization.getSubtenants())
			result = result + this.assemblePolicy(next) + "\n";
		return result + "</PolicySet>";
   }
}
