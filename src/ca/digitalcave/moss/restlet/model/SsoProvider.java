package ca.digitalcave.moss.restlet.model;

import java.io.Serializable;
import java.util.Date;

public class SsoProvider implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String uuid;
	private String description;
	private String type;
	
	private String idpEntityId;
	private String idpSsoUrl;
	private String idpSlsUrl;
	private String idpSlsResponseUrl;
	private String idpX509Cert;
	
	private int version;
	private Date created;
	private Date modified;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIdpEntityId() {
		return idpEntityId;
	}
	public void setIdpEntityId(String idpEntityId) {
		this.idpEntityId = idpEntityId;
	}
	public String getIdpSsoUrl() {
		return idpSsoUrl;
	}
	public void setIdpSsoUrl(String idpSsoUrl) {
		this.idpSsoUrl = idpSsoUrl;
	}
	public String getIdpSlsUrl() {
		return idpSlsUrl;
	}
	public void setIdpSlsUrl(String idpSlsUrl) {
		this.idpSlsUrl = idpSlsUrl;
	}
	public String getIdpSlsResponseUrl() {
		return idpSlsResponseUrl;
	}
	public void setIdpSlsResponseUrl(String idpSlsResponseUrl) {
		this.idpSlsResponseUrl = idpSlsResponseUrl;
	}
	public String getIdpX509Cert() {
		return idpX509Cert;
	}
	public void setIdpX509Cert(String idpX509Cert) {
		this.idpX509Cert = idpX509Cert;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
}
