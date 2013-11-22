package puma.sp.mgmt.provider.population;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import puma.sp.mgmt.model.organization.Organization;
import puma.sp.mgmt.repositories.organization.OrganizationService;
import puma.sp.mgmt.repositories.organization.OrganizationServiceImpl;

/**
 * Context listener for guaranteeing some starting properties for the database:
 * - there should always be a "provider" organization.
 * 
 * @author Maarten Decat
 *
 */
@Configuration
public class DatabaseInitializer {

	private static final Logger logger = Logger.getLogger(DatabaseInitializer.class
			.getName());
	
	@Autowired
	OrganizationService organizationService;
	
	@PostConstruct
	public void contextInitialized() {
		// Make sure that there is one organization called "provider".
		Organization providerOrg = organizationService.getProviderOrganization();
		if(providerOrg == null) {
			providerOrg = new Organization(OrganizationServiceImpl.PROVIDER_ORGANIZATION_NAME);
			organizationService.addOrganization(providerOrg);
			logger.info("Created provider organization");
		}
	}

}
