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

import puma.applicationpdp.PDPMgmtHelper;
import puma.applicationpdp.PDPRegistryRemote;
import puma.rmi.pdp.mgmt.ApplicationPDPMgmtRemote;

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
	
	private Set<PDPMgmtHelper> pdpHelpers = new HashSet<PDPMgmtHelper>();
	
	public Set<PDPMgmtHelper> getApplicationPDPs() {
		return this.pdpHelpers;
	}
	
	private Map<String, Tuple<String, ApplicationPDPMgmtRemote>> applicationPDPsById = new HashMap<String, Tuple<String, ApplicationPDPMgmtRemote>>();
	
	private static class Tuple<L,R> {
		private L left;
		private R right;
		
		public Tuple(L left, R right) {
			this.left = left;
			this.right = right;
		}
		
		public L getLeft() {
			return left;
		}
		
		public R getRight() {
			return right;
		}
	}

	@Override
	public void register(PDPMgmtHelper helper) {
		/*String pdpId;
		try {
			pdpId = applicationPDP.getId();
		} catch (RemoteException e) {
			logger.log(Level.SEVERE, "WTF, cannot reach the Application PDP the moment it registers itself?", e);
			return;
		}*/
		Map<String, ApplicationPDPMgmtRemote> pdps = helper.getAll();
		try {
			for(Entry<String, ApplicationPDPMgmtRemote> entry : pdps.entrySet()) {
				final ApplicationPDPMgmtRemote pdp = entry.getValue();
				this.applicationPDPsById.put(pdp.getId(), new Tuple<>(entry.getKey(), pdp));
			}
		} catch (RemoteException e) {
			logger.log(Level.SEVERE, "WTF, cannot reach the Application PDP the moment it registers itself?", e);
			return;
		}
		this.pdpHelpers.add(helper);
		logger.info("Received Application PDP registration");
	}

	@Override
	public void deregister(PDPMgmtHelper helper) {
		this.pdpHelpers.remove(helper);
		for(ApplicationPDPMgmtRemote pdp : helper.getAll().values()) {
			this.applicationPDPsById.remove(getId(pdp));
		}
		logger.info("Received Application PDP deregistration");
	}
	
	private String getId(ApplicationPDPMgmtRemote applicationPDP) {
		for(Entry<String, Tuple<String, ApplicationPDPMgmtRemote>> e: this.applicationPDPsById.entrySet()) {
			if(e.getValue().getRight() == applicationPDP) {
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
		for(String id: this.applicationPDPsById.keySet()) {
			result.add(getOverview(id));
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
		Tuple<String, ApplicationPDPMgmtRemote> tuple = applicationPDPsById.get(pdpId);
		return buildOverview(tuple.getLeft(), tuple.getRight());
	}
	
	/**
	 * Helper function to build the overview of a certain Application PDP.
	 * 
	 * @param pdp
	 * @return
	 */
	private ApplicationPDPOverview buildOverview(String langType, ApplicationPDPMgmtRemote pdp) {
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
		return new ApplicationPDPOverview(id, status, langType, policy);
	}

}
