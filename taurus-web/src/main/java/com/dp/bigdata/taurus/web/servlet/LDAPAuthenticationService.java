package com.dp.bigdata.taurus.web.servlet;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.log4j.Logger;

public class LDAPAuthenticationService {

	private static Logger logger = Logger.getLogger(LDAPAuthenticationService.class);

	private static String loginAttribute = "sAMAccountName";

	public static final String USER_NAME = "taurus-user";

	public static final String USER_GROUP = "taurus-group";

	public static final String USER_POWER = "taurus-user-power";

	private String ldapUrl = "ldap://192.168.50.11:389/DC=dianpingoa,DC=com";

	private String ldapBaseDN = "OU=Technolog Department,OU=shoffice,DC=dianpingoa,DC=com";

	private String ldapFactory = "com.sun.jndi.ldap.LdapCtxFactory";

	private Control[] connCtls = null;

	private String solidDN = "cn=Users,DC=dianpingoa,DC=com";

	private String solidUsername = "lionauth";

	private String solidPwd = "bxHxXopGJOy78Jze3LWi";

	/**
	 * @throws Exception
	 * @throws AuthenticationException
	 * @throws NamingException
	 * @return if authentication succeeded, return user info; otherwise, return null;
	 * @throws
	 */
	public boolean authenticate(String userName, String password) throws Exception {
		LdapContext ctx = null;
		Hashtable<String, String> env = null;
		String shortName = null;
		try {
			shortName = getShortName(userName);
		} catch (NamingException e1) {
			logger.error(userName + " doesn't exist.", e1);
		}
		if (shortName != null) {
			env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
			env.put(Context.PROVIDER_URL, ldapUrl);// LDAP server
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, "cn=" + shortName + "," + ldapBaseDN);
			env.put(Context.SECURITY_CREDENTIALS, password);
			try {
				ctx = new InitialLdapContext(env, connCtls);
			} catch (javax.naming.AuthenticationException e) {
				logger.info("Authentication faild: " + e.toString());
				throw e;
			} catch (Exception e) {
				logger.error("Something wrong while authenticating: " + e.toString());
				throw e;
			}
			if (ctx != null) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	private String getShortName(String sAMAccountName) throws NamingException {
		String shortName = null;

		Hashtable<String, String> solidEnv = new Hashtable<String, String>();
		solidEnv.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
		solidEnv.put(Context.PROVIDER_URL, ldapUrl);// LDAP server
		solidEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
		solidEnv.put(Context.SECURITY_PRINCIPAL, "cn=" + solidUsername + "," + solidDN);
		solidEnv.put(Context.SECURITY_CREDENTIALS, solidPwd);
		LdapContext solidContext = new InitialLdapContext(solidEnv, connCtls);
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration en = solidContext.search("", loginAttribute + "=" + sAMAccountName, constraints);
		if (en == null) {
			logger.warn("Have no NamingEnumeration.");
			return shortName;
		}
		if (!en.hasMoreElements()) {
			logger.warn("Have no element.");
			return shortName;
		}
		while (en != null && en.hasMoreElements()) {
			Object obj = en.nextElement();
			if (obj instanceof SearchResult) {
				SearchResult sr = (SearchResult) obj;
				logger.debug(sr);
				Attributes attrs = sr.getAttributes();
				shortName = (String) attrs.get("cn").get();
			}
		}
		return shortName;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public void setLdapBaseDN(String ldapBaseDN) {
		this.ldapBaseDN = ldapBaseDN;
	}

	public void setLdapFactory(String ldapFactory) {
		this.ldapFactory = ldapFactory;
	}

	public void setSolidDN(String solidDN) {
		this.solidDN = solidDN;
	}

	public void setSolidUsername(String solidUsername) {
		this.solidUsername = solidUsername;
	}

	public void setSolidPwd(String solidPwd) {
		this.solidPwd = solidPwd;
	}

}
