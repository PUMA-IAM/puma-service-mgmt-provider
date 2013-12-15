package puma.sp.mgmt.provider.tenants;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import puma.sp.mgmt.model.organization.Tenant;
import puma.sp.mgmt.model.organization.TenantMgmtType;
import puma.sp.mgmt.model.user.User;
import puma.sp.mgmt.provider.msgs.MessageManager;
import puma.sp.mgmt.repositories.organization.TenantService;
import puma.sp.mgmt.repositories.user.UserService;

@Controller
public class TenantController {
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	UserService userService;

	@RequestMapping(value = "/tenants")
	public String tenantOverview(ModelMap model, HttpSession session) {
		model.addAttribute("tenants", tenantService.findAll());

		model.addAttribute("msgs",
				MessageManager.getInstance().getMessages(session));
		return "tenants/overview";
	}

	@RequestMapping(value = "/tenants/create-impl", method = RequestMethod.POST)
	public String createTenantImplementation(ModelMap model,
			HttpSession session, 
			@RequestParam("name") String name,
			@RequestParam("mgmt-type") String mgmtType,
			@RequestParam(value = "authn-endpoint", defaultValue = "") String authnEndpoint,
			@RequestParam(value = "attr-endpoint", defaultValue = "") String attrEndpoint,
			@RequestParam(value = "idp-public-key", defaultValue = "") String idpPublicKey,
			@RequestParam(value = "authz-endpoint", defaultValue = "") String authzEndpoint,
			@RequestParam(value = "loginName", defaultValue = "admin") String userName,
			@RequestParam(value = "password", defaultValue = "admin") String password) {
		// translate the mgmt type
		TenantMgmtType realMgmtType = TenantMgmtType.Locally;
		if(mgmtType == "fedauthn") {
			realMgmtType = TenantMgmtType.FederatedAuthentication;
		} else if(mgmtType == "fedauthz") {
			realMgmtType = TenantMgmtType.FederatedAuthorization;
		}
		Long tenantId = null;
		try {
			Tenant tenant = new Tenant(name, realMgmtType, authnEndpoint, idpPublicKey, attrEndpoint, authzEndpoint);
			// also construct user
			User user = new User();
			user.setLoginName(userName);
			user.setPassword(password);
			user.setTenant(tenant);
			this.tenantService.addTenant(tenant);
			this.userService.addUser(user);
			tenantId = tenant.getId();
			MessageManager.getInstance().addMessage(session, "success", "Tenant successfully created.");
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			MessageManager.getInstance().addMessage(session, "failure", "Could not create tenant - user initialization failed: " + e.getMessage());
			return "redirect:/tenants";
		}		
		return "redirect:/tenants/" + tenantId;
	}

	@RequestMapping(value = "/tenants/{tenantId}")
	public String applicationPDP(@PathVariable("tenantId") Long tenantId,
			ModelMap model, HttpSession session) {
		Tenant tenant = tenantService.findOne(tenantId);

		// First check whether the tenant exists
		if(tenant == null) {
			MessageManager.getInstance().addMessage(session, "failure", "Tenant with id " + tenantId + " not found.");
			return "redirect:/tenants";			
		}
		
		model.addAttribute("tenant", tenant);		
		model.addAttribute("msgs",
				MessageManager.getInstance().getMessages(session));
		return "tenants/tenant";
	}

	@RequestMapping(value = "/tenants/{tenantId}/delete")
	public String deleteTenant(@PathVariable("tenantId") Long tenantId,
			ModelMap model, HttpSession session) {
		Tenant tenant = tenantService.findOne(tenantId);

		// First check whether the tenant exists
		if(tenant == null) {
			MessageManager.getInstance().addMessage(session, "failure", "Tenant with id " + tenantId + " not found.");
			return "redirect:/tenants";			
		}
		
		// if existing: delete it
		String name = tenant.getName();
		tenantService.deleteTenant(tenantId);
		MessageManager.getInstance().addMessage(session, "success", "Tenant " + name + " succesfully deleted.");
		return "redirect:/tenants";
	}

	@RequestMapping(value = "/tenants/{tenantId}/subtenants/create-impl", method = RequestMethod.POST)
	public String createSubtenantImplementation(ModelMap model,
			HttpSession session, @PathVariable("tenantId") Long tenantId,  
			@RequestParam("name") String name,
			@RequestParam("mgmt-type") String mgmtType,
			@RequestParam(value = "authn-endpoint", defaultValue = "") String authnEndpoint,
			@RequestParam(value = "attr-endpoint", defaultValue = "") String attrEndpoint,
			@RequestParam(value = "idp-public-key", defaultValue = "") String idpPublicKey,
			@RequestParam(value = "authz-endpoint", defaultValue = "") String authzEndpoint) {
		Tenant tenant = tenantService.findOne(tenantId);

		// First check whether the tenant exists
		if(tenant == null) {
			MessageManager.getInstance().addMessage(session, "failure", "Tenant with id " + tenantId + " not found.");
			return "redirect:/tenants";			
		}
		
		// translate the mgmt type
		TenantMgmtType realMgmtType = TenantMgmtType.Locally;
		if(mgmtType == "fedauthn") {
			realMgmtType = TenantMgmtType.FederatedAuthentication;
		} else if(mgmtType == "fedauthz") {
			realMgmtType = TenantMgmtType.FederatedAuthorization;
		}
		
		Tenant subtenant = new Tenant(name, realMgmtType, authnEndpoint, idpPublicKey, attrEndpoint, authzEndpoint);
		subtenant.setSuperTenant(tenant);
		tenantService.addTenant(subtenant);
		Long subtenantId = subtenant.getId();
		MessageManager.getInstance().addMessage(session, "success", "Subtenant successfully created.");
		
		return "redirect:/tenants/" + subtenantId;
	}

}
