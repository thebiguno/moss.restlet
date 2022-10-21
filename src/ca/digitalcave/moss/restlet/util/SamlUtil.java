package ca.digitalcave.moss.restlet.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;

import ca.digitalcave.moss.restlet.model.SsoProvider;
import ca.digitalcave.moss.restlet.plugin.AuthenticationHelper;

public class SamlUtil {

	/**
	 * Returns the Saml2Settings object, required for SAML SSO.  If 'isAllowSSO' is false,
	 * this method throws an exception.
	 */
	public static Saml2Settings getSettings(AuthenticationHelper helper, String ssoProviderId) throws Exception{
		return getSettings(helper, ssoProviderId, false);
	}
	
	/**
	 * Returns the Saml2Settings object, required for SAML SSO.  If 'isAllowSSO' is false,
	 * this method throws an exception.
	 */
	public static Saml2Settings getSettings(AuthenticationHelper helper, String ssoProviderId, boolean allowIncomplete) throws Exception{
		if (!helper.getConfig().showSSO){
			throw new Exception("SSO Not allowed");
		}
		
		final SsoProvider ssoProvider = helper.selectSSOProvider(ssoProviderId);
		
		final Map<String, Object> settingsMap = new HashMap<String, Object>();
		//General settings - this should be common for the entire application.
		settingsMap.put(SettingsBuilder.STRICT_PROPERTY_KEY, true);
		settingsMap.put(SettingsBuilder.DEBUG_PROPERTY_KEY, false);
		
		//SP Configuration - this should be common for the entire application.
//		settingsMap.put(SettingsBuilder.SP_ENTITYID_PROPERTY_KEY, "http://localhost:8686/phoebie/authentication/saml/metadata");
//		settingsMap.put(SettingsBuilder.SP_ASSERTION_CONSUMER_SERVICE_URL_PROPERTY_KEY, "http://localhost:8686/phoebie/authentication/saml/acs");
//		settingsMap.put(SettingsBuilder.SP_SINGLE_LOGOUT_SERVICE_URL_PROPERTY_KEY, "http://localhost:8686/phoebie/authentication/saml/sls");
		settingsMap.put(SettingsBuilder.SP_ASSERTION_CONSUMER_SERVICE_BINDING_PROPERTY_KEY, "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
		settingsMap.put(SettingsBuilder.SP_SINGLE_LOGOUT_SERVICE_BINDING_PROPERTY_KEY, "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
		settingsMap.put(SettingsBuilder.SP_NAMEIDFORMAT_PROPERTY_KEY, "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
		settingsMap.put(SettingsBuilder.SP_X509CERT_PROPERTY_KEY, "");	//TODO
		settingsMap.put(SettingsBuilder.SP_PRIVATEKEY_PROPERTY_KEY, "");	//TODO
		
		//IdP Configuration - this is specific to a given ssoProviderId.  Sane defaults for binding properties will be provided here, but the URLs and certificate must be provided by the implementing code.
//		settingsMap.put(SettingsBuilder.IDP_ENTITYID_PROPERTY_KEY, "https://sts.windows.net/c781fac8-78ef-440e-bac2-1fd21a12642e/");
//		settingsMap.put(SettingsBuilder.IDP_SINGLE_SIGN_ON_SERVICE_URL_PROPERTY_KEY, "https://login.microsoftonline.com/c781fac8-78ef-440e-bac2-1fd21a12642e/saml2");
//		settingsMap.put(SettingsBuilder.IDP_SINGLE_LOGOUT_SERVICE_URL_PROPERTY_KEY, "https://login.microsoftonline.com/c781fac8-78ef-440e-bac2-1fd21a12642e/saml2");
//		settingsMap.put(SettingsBuilder.IDP_SINGLE_LOGOUT_SERVICE_RESPONSE_URL_PROPERTY_KEY, "");
		settingsMap.put(SettingsBuilder.IDP_SINGLE_SIGN_ON_SERVICE_BINDING_PROPERTY_KEY, "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
		settingsMap.put(SettingsBuilder.IDP_SINGLE_LOGOUT_SERVICE_BINDING_PROPERTY_KEY, "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
//		settingsMap.put(SettingsBuilder.IDP_X509CERT_PROPERTY_KEY, "-----BEGIN CERTIFICATE-----MIIC8DCCAdigAwIBAgIQTddkoKnjl5NCnMIRT6TsaTANBgkqhkiG9w0BAQsFADA0MTIwMAYDVQQDEylNaWNyb3NvZnQgQXp1cmUgRmVkZXJhdGVkIFNTTyBDZXJ0aWZpY2F0ZTAeFw0yMjAzMzAxNTQxNDRaFw0yNTAzMzAxNTQxNDRaMDQxMjAwBgNVBAMTKU1pY3Jvc29mdCBBenVyZSBGZWRlcmF0ZWQgU1NPIENlcnRpZmljYXRlMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtHE9Nn9mhAdw3xpghWV+sxAOS+ymj1fonq7YfgDqhMAtAcfRFuqEn7Q/1z7tZgNgCODqHTvfkPoLur96NPr1VBKvvi/MULOsU8E5ByY4JKmVKni4vh8qTiC00223Ky5n4cYnZuUzgfvN1IYgu0XeI7WXJD99rCsHu/+RRMdiA7Dg6+U6962L2exN8hP2lupq/M05fG/KvIN6z2bYXV2Q6I97mPLqrLvOT+7C/AOtlsZdY+1jBB92K1unVv4Vy4n+Os18Mdiy4XgAnzJj51L1ZmRcPMgN2SFXscuEaeerGI373JKK30KxwiTQYQUfkex77qd/SKzsPwrWSGZ3nU4Q5QIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQBEXnbd9xQYasvEmL5bZPWZBRz7eakDi+rN5CwDxc4SNHhpV045V0RbqYxwbpzkM+mGt9nW0fqV501B+KuDp8BA7298aJd+Hlc58Xx+RoYAYh53jrKokcFppR/54uoh9p9ZGyA53KMCw6zUkVEil5/t31W+LdWYSbJTrleV4FHgt4ksdpTbcR37OFbzfi4VnXzA9bth2QUvi0Q3sGh6/VGauK1Olvdj0tws/wqiufvI1594X+PSNWAoHIrpU8SLtCw4arq8aJ/a4ZCQaMZ3hqC9ihLJeA9UaL9nfArLDCDWuvgamUBuS5iET8uXhlffG4FoXBWeyiqenwIM9iIqVD6U-----END CERTIFICATE-----");
		
		//Security settings - these can be specific to a given ssoProviderId, but most people will use the defaults.
		settingsMap.put(SettingsBuilder.SECURITY_NAMEID_ENCRYPTED, false);
		settingsMap.put(SettingsBuilder.SECURITY_AUTHREQUEST_SIGNED, false);
		settingsMap.put(SettingsBuilder.SECURITY_LOGOUTREQUEST_SIGNED, false);
		settingsMap.put(SettingsBuilder.SECURITY_LOGOUTRESPONSE_SIGNED, false);
		settingsMap.put(SettingsBuilder.SECURITY_WANT_MESSAGES_SIGNED, false);
		settingsMap.put(SettingsBuilder.SECURITY_WANT_ASSERTIONS_SIGNED, false);
		settingsMap.put(SettingsBuilder.SECURITY_SIGN_METADATA, "");
		settingsMap.put(SettingsBuilder.SECURITY_WANT_ASSERTIONS_ENCRYPTED, false);
		settingsMap.put(SettingsBuilder.SECURITY_WANT_NAMEID_ENCRYPTED, false);
		settingsMap.put(SettingsBuilder.SECURITY_REQUESTED_AUTHNCONTEXT, "urn:oasis:names:tc:SAML:2.0:ac:classes:Password");
		settingsMap.put(SettingsBuilder.SECURITY_REQUESTED_AUTHNCONTEXTCOMPARISON, "exact");
		settingsMap.put(SettingsBuilder.SECURITY_ALLOW_REPEAT_ATTRIBUTE_NAME_PROPERTY_KEY, false);
		settingsMap.put(SettingsBuilder.SECURITY_WANT_XML_VALIDATION, true);
		settingsMap.put(SettingsBuilder.SECURITY_SIGNATURE_ALGORITHM, "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
		settingsMap.put(SettingsBuilder.SECURITY_DIGEST_ALGORITHM, "http://www.w3.org/2001/04/xmlenc#sha256");
		settingsMap.put(SettingsBuilder.SECURITY_REJECT_DEPRECATED_ALGORITHM, true);
		
		//Organization settings - these should be common for the entire application.
		settingsMap.put(SettingsBuilder.ORGANIZATION_NAME, "digitalcave.ca");
		settingsMap.put(SettingsBuilder.ORGANIZATION_DISPLAYNAME, "digitalcave.ca");
		settingsMap.put(SettingsBuilder.ORGANIZATION_URL, "https://digitalcave.ca.ca");
		settingsMap.put(SettingsBuilder.ORGANIZATION_LANG, "en");
		
		//Contacts settings - these should be common for the entire application.
//		settingsMap.put(SettingsBuilder.CONTACT_TECHNICAL_GIVEN_NAME, "Enrich Help Desk");
//		settingsMap.put(SettingsBuilder.CONTACT_TECHNICAL_EMAIL_ADDRESS, "help@richer.ca");
//		settingsMap.put(SettingsBuilder.CONTACT_SUPPORT_GIVEN_NAME, "Enrich Help Desk");
//		settingsMap.put(SettingsBuilder.CONTACT_SUPPORT_EMAIL_ADDRESS, "help@richer.ca");
	
//		overrideSaml2SettingsMapValues(settingsMap, ssoProviderId);
		
		//These are the SAML endpoints from your application (the SP, Service Provider)
		String baseUrl = helper.getBaseUrl();
		if (!baseUrl.endsWith("/")){
			baseUrl = baseUrl + "/";
		}
		if (!baseUrl.endsWith("/authentication/")){
			baseUrl = baseUrl + "authentication/";
		}
		
		//These are the SAML endpoints from your IdP (Identity Provider), e.g. Microsoft, and are obtained when setting up a new SAML profile with your IdP.
		settingsMap.put(SettingsBuilder.IDP_ENTITYID_PROPERTY_KEY, ssoProvider.getIdpEntityId());
		settingsMap.put(SettingsBuilder.IDP_SINGLE_SIGN_ON_SERVICE_URL_PROPERTY_KEY, ssoProvider.getIdpSsoUrl());
		settingsMap.put(SettingsBuilder.IDP_SINGLE_LOGOUT_SERVICE_URL_PROPERTY_KEY, ssoProvider.getIdpSlsUrl());
		settingsMap.put(SettingsBuilder.IDP_X509CERT_PROPERTY_KEY, ssoProvider.getIdpX509Cert());
		
		//Add the ssoProviderId as a query parameter to the SP URLs.  This is required so that we know which SSO instance to load.
		settingsMap.put(SettingsBuilder.SP_ENTITYID_PROPERTY_KEY, baseUrl + "saml/" + ssoProvider.getUuid() + "/metadata");
		settingsMap.put(SettingsBuilder.SP_ASSERTION_CONSUMER_SERVICE_URL_PROPERTY_KEY, baseUrl + "saml/" + ssoProvider.getUuid() + "/acs");
		settingsMap.put(SettingsBuilder.SP_SINGLE_LOGOUT_SERVICE_URL_PROPERTY_KEY, baseUrl + "saml/" + ssoProvider.getUuid() + "/sls");
		
		if (allowIncomplete){
			if (StringUtils.isBlank(settingsMap.get(SettingsBuilder.IDP_ENTITYID_PROPERTY_KEY).toString())){
				settingsMap.put(SettingsBuilder.IDP_ENTITYID_PROPERTY_KEY, "http://example.com");
			}
			if (StringUtils.isBlank(settingsMap.get(SettingsBuilder.IDP_SINGLE_SIGN_ON_SERVICE_URL_PROPERTY_KEY).toString())){
				settingsMap.put(SettingsBuilder.IDP_SINGLE_SIGN_ON_SERVICE_URL_PROPERTY_KEY, "http://example.com");
			}
			if (StringUtils.isBlank(settingsMap.get(SettingsBuilder.IDP_SINGLE_LOGOUT_SERVICE_URL_PROPERTY_KEY).toString())){
				settingsMap.put(SettingsBuilder.IDP_SINGLE_LOGOUT_SERVICE_URL_PROPERTY_KEY, "http://example.com");
			}
			if (StringUtils.isBlank(settingsMap.get(SettingsBuilder.IDP_X509CERT_PROPERTY_KEY).toString())){
				//We insert an arbitrary X.509 Certificate into the configuration here.  We don't want it to be valid, just something that can parse.
				settingsMap.put(SettingsBuilder.IDP_X509CERT_PROPERTY_KEY, "-----BEGIN CERTIFICATE-----MIICEjCCAXsCAg36MA0GCSqGSIb3DQEBBQUAMIGbMQswCQYDVQQGEwJKUDEOMAwGA1UECBMFVG9reW8xEDAOBgNVBAcTB0NodW8ta3UxETAPBgNVBAoTCEZyYW5rNEREMRgwFgYDVQQLEw9XZWJDZXJ0IFN1cHBvcnQxGDAWBgNVBAMTD0ZyYW5rNEREIFdlYiBDQTEjMCEGCSqGSIb3DQEJARYUc3VwcG9ydEBmcmFuazRkZC5jb20wHhcNMTIwODIyMDUyNjU0WhcNMTcwODIxMDUyNjU0WjBKMQswCQYDVQQGEwJKUDEOMAwGA1UECAwFVG9reW8xETAPBgNVBAoMCEZyYW5rNEREMRgwFgYDVQQDDA93d3cuZXhhbXBsZS5jb20wXDANBgkqhkiG9w0BAQEFAANLADBIAkEAm/xmkHmEQrurE/0re/jeFRLl8ZPjBop7uLHhnia7lQG/5zDtZIUC3RVpqDSwBuw/NTweGyuP+o8AG98HxqxTBwIDAQABMA0GCSqGSIb3DQEBBQUAA4GBABS2TLuBeTPmcaTaUW/LCB2NYOy8GMdzR1mx8iBIu2H6/E2tiY3RIevV2OW61qY2/XRQg7YPxx3ffeUugX9F4J/iPnnu1zAxxyBy2VguKv4SWjRFoRkIfIlHX0qVviMhSlNy2ioFLy7JcPZb+v3ftDGywUqcBiVDoea0Hn+GmxZA-----END CERTIFICATE-----");
			}
		}
		
		final SettingsBuilder sb = new SettingsBuilder();
		sb.fromValues(settingsMap);
		
		return sb.build();
	}
}
