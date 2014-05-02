package puma.sp.mgmt.provider.evaluation;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import puma.sp.mgmt.model.attribute.Attribute;
import puma.sp.mgmt.model.attribute.AttributeFamily;
import puma.sp.mgmt.model.organization.Tenant;
import puma.sp.mgmt.model.user.User;
import puma.sp.mgmt.repositories.attribute.AttributeFamilyService;
import puma.sp.mgmt.repositories.attribute.AttributeRepository;
import puma.sp.mgmt.repositories.attribute.AttributeService;
import puma.sp.mgmt.repositories.organization.TenantService;
import puma.sp.mgmt.repositories.user.UserRepository;
import puma.sp.mgmt.repositories.user.UserService;

@Controller
public class UserProvision {
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private TenantService tenantService;
	
	@Autowired
	private AttributeFamilyService familyService;
	@Autowired
	private AttributeService attributeService;
	@Autowired
	private AttributeRepository attributeRepository;

	@ResponseBody
	@RequestMapping(value = "/createSubject", method = RequestMethod.GET)
	public String createUser(@RequestParam MultiValueMap<String, String> params) {
		String name = params.getFirst("name");
		String password = params.getFirst("password");
		Long tenant = -1L;
		if (params.containsKey("tenant"))
			tenant = Long.parseLong(params.getFirst("tenant"));
		if (name == null)
			throw new IllegalArgumentException("No name given~");
		if (password == null)
			password = " ";
		params.remove("name");
		params.remove("password");
		params.remove("tenant");
		try {
			Tenant t = this.tenantService.findOne(tenant);
			if (t == null)
				t = null;
			User user = new User();
			user.setLoginName(name);
			user.setPassword(password);
			user.setTenant(t);
			this.userService.addUser(user);
			for (String next: params.keySet())
				for (String value: params.get(next))
					this.createAttribute(Long.parseLong(next), value, user.getId());
			return user.getId().toString();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return "0";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/removeSubject", method = RequestMethod.GET)
	public String removeTenant(@RequestParam("id") Long id) {
		User u = this.userService.byId(id);
		if (u == null) 
			return Boolean.FALSE.toString();
		else {
			this.userRepo.delete(id);
			return Boolean.TRUE.toString();
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/createAttribute", method = RequestMethod.GET)
	public String createAttribute(@RequestParam("family") Long family, @RequestParam("value") String value, @RequestParam("user") Long id) {
		AttributeFamily fam = this.familyService.findOne(family);
		if (fam == null)
			return "0";
		User user = this.userService.byId(id);
		if (user == null)
			return "0";
		Attribute attr = new Attribute();
		attr.setFamily(fam);
		attr.setUser(user);
		attr.setValue(value);
		this.attributeService.addAttribute(attr);
		return attr.getId().toString();		
	}

	@ResponseBody
	@RequestMapping(value = "/removeAttribute", method = RequestMethod.GET)
	public String removeAttribute(@RequestParam("id") Long id) {
		Attribute a = this.attributeService.findOne(id);
		if (a == null) 
			return Boolean.FALSE.toString();
		else {
			this.attributeService.deleteAttribute(a);
			return Boolean.TRUE.toString();
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/removeAllUsers", method = RequestMethod.GET)
	public void removeAll() {
		this.attributeRepository.deleteAll();
		this.userRepo.deleteAll();	
	}

}
