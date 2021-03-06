package puma.sp.mgmt.provider.attrs;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import puma.sp.mgmt.model.attribute.AttributeFamily;
import puma.sp.mgmt.model.attribute.DataType;
import puma.sp.mgmt.model.attribute.Multiplicity;
import puma.sp.mgmt.model.organization.Organization;
import puma.sp.mgmt.model.organization.Tenant;
import puma.sp.mgmt.provider.msgs.MessageManager;
import puma.sp.mgmt.repositories.attribute.AttributeFamilyService;
import puma.sp.mgmt.repositories.attribute.AttributeService;
import puma.sp.mgmt.repositories.organization.OrganizationService;
import puma.sp.mgmt.repositories.organization.TenantService;

@Controller
public class OrganizationController {

	@Autowired
	AttributeService attributeService;
	
	@Autowired
	AttributeFamilyService attributeFamilyService;

	@Autowired
	OrganizationService organizationService;

	@Autowired
	TenantService tenantService;

	// @RequestMapping(value = "/tenants")
	// public String tenantOverview(ModelMap model, HttpSession session) {
	// model.addAttribute("tenants", attributeService.findAll());
	//
	// model.addAttribute("msgs",
	// MessageManager.getInstance().getMessages(session));
	// return "tenants/overview";
	// }
	//
	// @RequestMapping(value = "/tenants/create-impl", method =
	// RequestMethod.POST)
	// public String createTenantImplementation(ModelMap model,
	// HttpSession session,
	// @RequestParam("name") String name,
	// @RequestParam("mgmt-type") String mgmtType,
	// @RequestParam(value = "authn-endpoint", defaultValue = "") String
	// authnEndpoint,
	// @RequestParam(value = "attr-endpoint", defaultValue = "") String
	// attrEndpoint,
	// @RequestParam(value = "idp-public-key", defaultValue = "") String
	// idpPublicKey,
	// @RequestParam(value = "authz-endpoint", defaultValue = "") String
	// authzEndpoint) {
	// // translate the mgmt type
	// TenantMgmtType realMgmtType = TenantMgmtType.Locally;
	// if(mgmtType == "fedauthn") {
	// realMgmtType = TenantMgmtType.FederatedAuthentication;
	// } else if(mgmtType == "fedauthz") {
	// realMgmtType = TenantMgmtType.FederatedAuthorization;
	// }
	//
	// Tenant tenant = new Tenant(name, realMgmtType, authnEndpoint,
	// idpPublicKey, attrEndpoint, authzEndpoint);
	// attributeService.addTenant(tenant);
	// Long tenantId = tenant.getId();
	// MessageManager.getInstance().addMessage(session, "success",
	// "Tenant successfully created.");
	//
	// return "redirect:/tenants/" + tenantId;
	// }

	@RequestMapping(value = "/organizations/{organizationId}")
	public String organizationOverview(
			@PathVariable("organizationId") Long organizationId,
			ModelMap model, HttpSession session) {
		// First check whether this organization is a Tenant. If so: redirect to
		// the tenant's page
		// since this will provide more details.
		Tenant tenant = tenantService.findOne(organizationId);
		if (tenant != null) {
			return "redirect:/tenants/" + organizationId;
		}

		// Now handle the normal organization view
		Organization org = organizationService.findOne(organizationId);
		// Check whether the tenant exists
		if (org == null) {
			MessageManager.getInstance().addMessage(session, "failure",
					"Organization with id " + organizationId + " not found.");
			return "redirect:/";
		}

		model.addAttribute("org", org);
		model.addAttribute("msgs",
				MessageManager.getInstance().getMessages(session));
		List<String> dataTypes = new ArrayList<String>(DataType.values().length);
		for (DataType next: DataType.values())
			dataTypes.add(next.toString());
		model.addAttribute("datatypes", dataTypes);
		List<String> multiplicityValues = new ArrayList<String>(Multiplicity.values().length);
		for (Multiplicity next: Multiplicity.values()) 
			multiplicityValues.add(next.toString());
		model.addAttribute("multiplicityValues", multiplicityValues);
		return "organizations/organization";
	}

	@RequestMapping(value = "/organizations/{organizationId}/attribute-families/create-impl", method = RequestMethod.POST)
	public String addFamily(
			@PathVariable("organizationId") Long organizationId, 
			@RequestParam("name") String name,
			@RequestParam("xacmlid") String xacmlIdentifier,
			@RequestParam("multiplicity") String multiplicity,
			@RequestParam("datatype") String datatype,
			ModelMap model, HttpSession session, HttpServletRequest request	
			) {
		Organization organization = this.organizationService.findOne(organizationId);
		AttributeFamily family = new AttributeFamily();
		family.setDataType(DataType.valueOf(datatype));
		family.setDefinedBy(organization);
		family.setMultiplicity(Multiplicity.valueOf(multiplicity));
		family.setName(name);
		family.setXacmlIdentifier(xacmlIdentifier);
		this.attributeFamilyService.add(family);
		return "redirect:/organizations/" + organizationId.toString();		
	}

	@RequestMapping(value = "/organizations/{organizationId}/attribute-families/{attributeFamilyId}/delete")
	public String deleteAttributeFamily(ModelMap model, HttpSession session,
			@PathVariable("organizationId") Long organizationId,
			@PathVariable("attributeFamilyId") Long attributeFamilyId) {		
		// First check whether the organization exists
		Organization org = organizationService.findOne(organizationId);
		if (org == null) {
			MessageManager.getInstance().addMessage(session, "failure",
					"Organization with id " + organizationId + " not found.");
			return "redirect:/organizations/" + organizationId;
		}
		
		// Then check whether the attribute family exists
		AttributeFamily af = attributeFamilyService.findOne(attributeFamilyId);
		if (af == null) {
			MessageManager.getInstance().addMessage(session, "failure",
					"Attribute family with id " + attributeFamilyId + " not found.");
			return "redirect:/organizations/" + organizationId;
		}
		
		// Now do the delete the attribute family
		String name = af.getName();
		attributeFamilyService.delete(af.getId());
		MessageManager.getInstance().addMessage(session, "success", "Attribute family " + name + " succesfully deleted.");
		return "redirect:/organizations/" + organizationId;
	}

}
