package puma.sp.mgmt.provider.attrs;

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
		return "organizations/organization";
	}

	@RequestMapping(value = "/organizations/{organizationId}/attribute-families/create-impl", method = RequestMethod.POST)
	public String createAttributeFamilyImplementation(ModelMap model,
			HttpSession session,
			@PathVariable("organizationId") Long organizationId,
			@RequestParam("name") String name,
			@RequestParam("multiplicity") String multiplicity,
			@RequestParam("dataType") String dataType) {
		Organization org = organizationService.findOne(organizationId);

		// First check whether the organization exists
		if (org == null) {
			MessageManager.getInstance().addMessage(session, "failure",
					"Organization with id " + organizationId + " not found.");
			return "redirect:/";
		}

		// translate multiplicy
		Multiplicity realMultiplicity = Multiplicity.ATOMIC; // TODO

		// translate datatype
		DataType realDataType = DataType.Boolean; // TODO

		AttributeFamily af = new AttributeFamily(name, realMultiplicity,
				realDataType, org);
		attributeFamilyService.add(af);
		MessageManager.getInstance().addMessage(session, "success",
				"Subtenant successfully created.");

		return "redirect:/organizations/" + organizationId;
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
		attributeFamilyService.delete(af);
		MessageManager.getInstance().addMessage(session, "success", "Attribute family " + name + " succesfully deleted.");
		return "redirect:/organizations/" + organizationId;
	}

}
