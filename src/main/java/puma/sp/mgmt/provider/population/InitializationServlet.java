/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package puma.sp.mgmt.provider.population;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import puma.sp.mgmt.model.attribute.Attribute;
import puma.sp.mgmt.model.attribute.AttributeFamily;
import puma.sp.mgmt.model.attribute.Multiplicity;
import puma.sp.mgmt.model.organization.Tenant;
import puma.sp.mgmt.model.user.User;
import puma.sp.mgmt.repositories.user.UserRepository;

/**
 *
 * @author jasper
 */

public class InitializationServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(InitializationServlet.class.getCanonicalName());
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InitializationServlet() {
        super();
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext (this);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            PrintWriter out = response.getWriter();    
            try {/*
            		//// Retrieve services
            		RepositoryFetcher fetcher = new RepositoryFetcher();
            		JpaRepository<User, Long> userRepository = null; //fetcher.getUserRepository();
            		JpaRepository<Tenant, Long> tenantRepository = null; //fetcher.getTenantRepository();
            		JpaRepository<Attribute, Long> attributeRepository = null; // fetcher.getAttributeRepository();
            		
                    //// Remove all
                    tenantRepository.deleteAll();
                    for (User nextUser: userService.allUnaffiliated()) {
                    	userService.deleteUser(nextUser);
                    	for (Attribute nextAttribute: nextUser.getAttributes())
                    		attributeService.deleteAttribute(nextAttribute);
                    }
                    out.write("Initialization: cleared all data");

                    //// Create all
                    
                    // Main tenant account for a large telecom organization A
                    Tenant largeTelecomOrganizationA = new Tenant();
                    largeTelecomOrganizationA.setName("Large telecom organization A");
                    largeTelecomOrganizationA.setImageName("telephoneLogo.png");
                    largeTelecomOrganizationA.setLocallyManaged(Boolean.FALSE);
                    largeTelecomOrganizationA.setAuthnRequestEndpoint("http://localhost:8080/Telecom-IdP/ProcessAuthenticationRequest");
                    largeTelecomOrganizationA.setAttrRequestEndpoint("http://localhost:8080/Telecom-IdP/AttributeQueryService?Query");
                    tenantService.addTenant(largeTelecomOrganizationA);
                    
                    // Subtenant of large telecom organization A which has its own identity provider using a proxyTenant cynalcoMedicsResearch = new Tenant();
                    Tenant largeSubtenantOfA = new Tenant();
                    largeSubtenantOfA.setName("Large subtenant of organization A");
                    largeSubtenantOfA.setImageName("screwdriverLogo.png");
                    largeSubtenantOfA.setLocallyManaged(Boolean.FALSE);
                    largeSubtenantOfA.setAuthnRequestEndpoint("http://localhost:8080/Telecom-Proxy/SAMLReqEndpoint");
                    largeSubtenantOfA.setAttrRequestEndpoint("http://localhost:8080/Telecom-Proxy/Telecom-Proxy/Query?Send");
                    largeSubtenantOfA.setSuperTenant(largeTelecomOrganizationA);
                    tenantService.addTenant(largeSubtenantOfA);
                    
                    // Subtenant of large telecom organization A which performs user management locally on PUMA service
                    Tenant smallSubtenantOfA = new Tenant();
                    smallSubtenantOfA.setName("Small subtenant of organization A");
                    smallSubtenantOfA.setImageName("accountingLogo.png");
                    smallSubtenantOfA.setLocallyManaged(Boolean.TRUE);
                    smallSubtenantOfA.setSuperTenant(largeTelecomOrganizationA);                    
                    tenantService.addTenant(smallSubtenantOfA);       
                    
                    // Small logistics organization B, which performs user management locally on PUMA service
                    Tenant logisticsOrganizationB = new Tenant();
                    logisticsOrganizationB.setName("Logistics organization B");
                    logisticsOrganizationB.setImageName("vermaelen.jpg");
                    logisticsOrganizationB.setLocallyManaged(Boolean.TRUE);
                    logisticsOrganizationB.setSuperTenant(null);                    
                    tenantService.addTenant(logisticsOrganizationB);     
                    
                    // Unaffiliated provider administrator Admin                    
                    User admin = new User();
                    admin.setLoginName("Admin");
                    admin.setPassword("Admin");
                    admin.setOrganization(null);
                    userService.addUser(admin);
                    
                    // Unaffiliated tenant administrator TenantAdmin      
                    User tenantAdmin = new User();
                    tenantAdmin.setLoginName("TenantAdmin");
                    tenantAdmin.setPassword("TenantAdmin");
                    tenantAdmin.setOrganization(null);
                    userService.addUser(tenantAdmin);
                    
                    // Unaffiliated service administrator ServiceAdmin
                    User serviceAdmin = new User();
                    serviceAdmin.setLoginName("ServiceAdmin");
                    serviceAdmin.setPassword("ServiceAdmin");
                    serviceAdmin.setOrganization(null);
                    userService.addUser(serviceAdmin);
                    
                    // Attribute types if not existing
                    AttributeType type = attributeService.typeByName("role");
                    if (type == null) {
	                    AttributeType role = new AttributeType();
	                    role.setName("role");
	                    role.setFamily(TypeFamily.GROUPED);
	                    attributeService.addAttributeType(role);
	                    type = role;
                    }
                    
                    Attribute attrAdminTenant = new Attribute();
                    attrAdminTenant.setAttributeKey(type);
                    attrAdminTenant.setAttributeValue("TenantAdministrator");
                    attrAdminTenant.setUser(admin);
                    attributeService.addAttribute(attrAdminTenant);
                    
                    Attribute attrAdminProvider = new Attribute();
                    attrAdminProvider.setAttributeKey(type);
                    attrAdminProvider.setAttributeValue("ServiceAdministrator");
                    attrAdminProvider.setUser(admin);
                    attributeService.addAttribute(attrAdminProvider);
                    */
                    
                    
                    /*
                    AttributeType email = serviceMgmt.createAttributeType("E-Mail");
                    AttributeType fullName = serviceMgmt.createAttributeType("Full Name");
                    AttributeType role = serviceMgmt.createAttributeType("MiddlewareRole");
                    Set<String> types = new HashSet<String>(1);
                    types.add(email.getName());
                    serviceMgmt.createService("DocumentSendingService", types);
                    types.add(fullName.getName());
                    serviceMgmt.createService("TemplateUploadService", types);
                    serviceMgmt.createService("DocumentReadingService", new HashSet<String>());
                    attributeMgmt.addAttribute(email, "jos@vermaelenprojects.be", jos);
                    attributeMgmt.addAttribute(fullName, "Jos Vermaelen", jos);
                    attributeMgmt.addAttribute(email, "thomas@vermaelen.be", thomas);
                    attributeMgmt.addAttribute(fullName, "Thomas Vermaelen", thomas);
                    attributeMgmt.addAttribute(email, "alain.vandam@cynalcomedics.be", alain);
                    attributeMgmt.addAttribute(fullName, "Alain Vandam", alain);
                    attributeMgmt.addAttribute(role, "MiddlewareAdmin", admin);
                    out.write("Initialization: created all data\n\n");
                    out.write("Registered entities: \n");
                    for (Tenant next: tenantRepository.findAll())
                    	out.write("Tenant: " + next.getName() + "\n");
                    for (User next: userRepository.findAll())
                    	out.write("User: " + next.getLoginName() + "\n");
                    */
            } catch (Exception e) {                        
            	logger.log(Level.SEVERE, "Could not process request", e);
            }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
