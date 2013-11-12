package ca.digitalcave.moss.restlet.model;

import java.util.Date;
import java.util.Map;

import org.restlet.security.User;

public class Account extends User {
	private int id;
	private String activationKey;
	private Date createdAt;
	private Date modifiedAt;

	public Account() {
	}
	
	public Account(Map<String, Object> map) {
		// User is not serializable which breaks caching, so a map is used
		this.setId((Integer) map.get("id"));
		this.setActivationKey((String) map.get("activationKey"));
		this.setCreatedAt((Date) map.get("createdAt"));
		this.setModifiedAt((Date) map.get("modifiedAt"));
		this.setIdentifier((String) map.get("identifier"));
		this.setSecretString((String) map.get("secretString"));
		this.setEmail((String) map.get("email"));
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public Date getModifiedAt() {
		return modifiedAt;
	}
	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
	
	public void setSecretString(String secret) {
		setSecret(secret == null ? null : secret.toCharArray());
	}
	public String getSecretString() {
		return getSecret() == null ? null : new String(getSecret());
	}
	
	public String getActivationKey() {
		return activationKey;
	}
	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}
}
