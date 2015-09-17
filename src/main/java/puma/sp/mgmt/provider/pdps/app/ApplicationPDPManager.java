package puma.sp.mgmt.provider.pdps.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import puma.rest.client.AppPDPClient;

public class ApplicationPDPManager {

	private static final Logger logger = Logger
			.getLogger(ApplicationPDPManager.class.getName());
	
	private static ApplicationPDPManager instance;
	
	public static ApplicationPDPManager getInstance() {
		if(instance == null) {
			instance = new ApplicationPDPManager();
		}
		return instance;
	}
	
	private Set<AppPDPClient> pdpHelpers = new HashSet<>();
	
	public Set<AppPDPClient> getApplicationPDPs() {
		return this.pdpHelpers;
	}
	
	private Map<String, Tuple<String, AppPDPClient>> applicationPDPsById = new HashMap<>();
	
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
	
	public Iterable<AppPDPClient> getPDPsByName(String name) {
		ArrayList<AppPDPClient> list = new ArrayList<>();
		for(Tuple<String, AppPDPClient> tuple : applicationPDPsById.values()) {
			if(tuple.getLeft().equals(name)) list.add(tuple.getRight());
		}
		return list;
	}

	public void register(String baseUrl, String name) {
		final AppPDPClient client = new AppPDPClient(baseUrl, name);
		/*String pdpId;
		try {
			pdpId = applicationPDP.getId();
		} catch (RemoteException e) {
			logger.log(Level.SEVERE, "WTF, cannot reach the Application PDP the moment it registers itself?", e);
			return;
		}*/
		try {
			logger.log(Level.INFO, "Received Application PDP registration: " + baseUrl + " - " + name);
			this.applicationPDPsById.put(client.getId(), new Tuple<>(name, client));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "WTF, cannot reach the Application PDP the moment it registers itself?", e);
			return;
		}
		this.pdpHelpers.add(client);
		logger.info("Completed Application PDP registration");
	}

	public void deregister(String baseUrl, String name) {
		final AppPDPClient client = new AppPDPClient(baseUrl, name);
		this.pdpHelpers.remove(client);
		this.applicationPDPsById.remove(getId(client));
		logger.info("Received Application PDP deregistration");
	}
	
	private String getId(AppPDPClient applicationPDP) {
		for(Entry<String, Tuple<String, AppPDPClient>> e: this.applicationPDPsById.entrySet()) {
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
		Tuple<String, AppPDPClient> tuple = applicationPDPsById.get(pdpId);
		return buildOverview(tuple.getLeft(), tuple.getRight());
	}
	
	/**
	 * Helper function to build the overview of a certain Application PDP.
	 * 
	 * @param pdp
	 * @return
	 */
	private ApplicationPDPOverview buildOverview(String langType, AppPDPClient pdp) {
		String status;
		try {
			status = pdp.getStatus();
		} catch (Exception e) {
			status = "Exception: " + e.getMessage();
		}
		String id;
		try {
			id = pdp.getId();
		} catch (Exception e) {
			id = "Exception: " + e.getMessage();
		}
		String policy;
		try {
			policy = pdp.getApplicationPolicy();
		} catch (Exception e) {
			policy = "Exception: " + e.getMessage();
		}
		return new ApplicationPDPOverview(id, status, langType, policy);
	}

}
