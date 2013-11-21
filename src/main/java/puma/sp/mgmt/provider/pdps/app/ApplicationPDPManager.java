package puma.sp.mgmt.provider.pdps.app;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import puma.rmi.pdp.mgmt.ApplicationPDPMgmtRemote;
import puma.rmi.pdp.mgmt.PDPRegistryRemote;

public class ApplicationPDPManager implements PDPRegistryRemote {

	private static final Logger logger = Logger
			.getLogger(ApplicationPDPManager.class.getName());
	
	private static ApplicationPDPManager instance;
	
	public static ApplicationPDPManager getInstance() {
		if(instance == null) {
			instance = new ApplicationPDPManager();
		}
		return instance;
	}
	
	private Set<ApplicationPDPMgmtRemote> applicationPDPs = new HashSet<ApplicationPDPMgmtRemote>();
	
	public Set<ApplicationPDPMgmtRemote> getApplicationPDPs() {
		return this.applicationPDPs;
	}
	
	private Map<String, ApplicationPDPMgmtRemote> applicationPDPsById = new HashMap<String, ApplicationPDPMgmtRemote>();

	@Override
	public void register(ApplicationPDPMgmtRemote applicationPDP) {
		String pdpId;
		try {
			pdpId = applicationPDP.getId();
		} catch (RemoteException e) {
			logger.log(Level.SEVERE, "WTF, cannot reach the Application PDP the moment it registers itself?", e);
			return;
		}
		this.applicationPDPsById.put(pdpId, applicationPDP);
		this.applicationPDPs.add(applicationPDP);
		logger.info("Received Application PDP registration");
	}

	@Override
	public void deregister(ApplicationPDPMgmtRemote applicationPDP) {
		this.applicationPDPs.remove(applicationPDP);
		this.applicationPDPsById.remove(getId(applicationPDP));
		logger.info("Received Application PDP deregistration");
	}
	
	private String getId(ApplicationPDPMgmtRemote applicationPDP) {
		for(Entry<String, ApplicationPDPMgmtRemote> e: this.applicationPDPsById.entrySet()) {
			if(e.getValue() == applicationPDP) {
				return e.getKey();
			}
		}
		logger.warning("No key found for the given applicationPDP??");
		return null;
	}
	
	/**
	 * Returns an overview of all connected Application PDPs.
	 * 
	 * @return
	 */
	public List<ApplicationPDPOverview> getOverview() {
		List<ApplicationPDPOverview> result = new LinkedList<>();
		for(ApplicationPDPMgmtRemote pdp: this.applicationPDPs) {
			result.add(buildOverview(pdp));
		}
		return result;
	}
	
	/**
	 * Returns an overview of the Application PDP with given id.
	 * Returns null if no such Application PDP exists.
	 * 
	 * @param pdpId
	 * @return
	 */
	public ApplicationPDPOverview getOverview(String pdpId) {
		ApplicationPDPMgmtRemote pdp = applicationPDPsById.get(pdpId);
		return buildOverview(pdp);
	}
	
	/**
	 * Helper function to build the overview of a certain Application PDP.
	 * 
	 * @param pdp
	 * @return
	 */
	private ApplicationPDPOverview buildOverview(ApplicationPDPMgmtRemote pdp) {
		String status;
		try {
			status = pdp.getStatus();
		} catch (RemoteException e) {
			status = "RemoteException: " + e.getMessage();
		}
		String id;
		try {
			id = pdp.getId();
		} catch (RemoteException e) {
			id = "RemoteException: " + e.getMessage();
		}
		String policy;
		try {
			policy = pdp.getApplicationPolicy();
		} catch (RemoteException e) {
			policy = "RemoteException: " + e.getMessage();
		}
		return new ApplicationPDPOverview(id, status, policy);
	}

}
