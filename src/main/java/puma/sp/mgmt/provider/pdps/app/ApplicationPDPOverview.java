package puma.sp.mgmt.provider.pdps.app;

/**
 * Simple helper class to represent the overview of a PDP.
 * 
 * @author Maarten Decat
 *
 */
public class ApplicationPDPOverview {
	
	private String id;
	
	private String status;
	
	private String langType;
	
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

	public String getLangType() {
		return langType;
	}

	public void setLangType(String langType) {
		this.langType = langType;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}
	
	public ApplicationPDPOverview(String id, String status, String langType, String policy) {
		this.id = id;
		this.status = status;
		this.langType = langType;
		this.policy = policy;
	}

}
