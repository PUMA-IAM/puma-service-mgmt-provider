package puma.sp.mgmt.provider.pdps;

/**
 * Simple helper class to represent the overview of a PDP.
 * 
 * @author Maarten Decat
 *
 */
public class PDPOverview {
	
	private String id;
	
	private String status;
	
	private String policy;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}
	
	public PDPOverview(String id, String status, String policy) {
		this.id = id;
		this.status = status;
		this.policy = policy;
	}

}
