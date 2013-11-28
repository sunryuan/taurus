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

import com.dp.bigdata.taurus.generated.module.User;

public class LDAPAuthenticationService {
	private static Logger logger = Logger.getLogger(LDAPAuthenticationService.class);
	private static String loginAttribute = "sAMAccountName";
	
	private String ldapUrl = "ldap://192.168.50.11:389/DC=dianpingoa,DC=com";
	private String ldapFactory = "com.sun.jndi.ldap.LdapCtxFactory"; 

	private Control[] connCtls = null;
	
	private String solidDN = "cn=Users,DC=dianpingoa,DC=com";
	private String solidUsername = "lionauth";
	private String solidPwd = "bxHxXopGJOy78Jze3LWi";
	
	public User authenticate(String userName, String password) throws Exception {
		User user = null;
		LdapContext ctx = null;
		Hashtable<String, String> env = null;
		String fullName = null;
		try {
			fullName = getFullName(userName);
		} catch (NamingException e1) {
			logger.error(userName+" doesn't exist.", e1);
		}
		if(fullName != null) {
			env = new Hashtable<String, String>();
	        env.put(Context.INITIAL_CONTEXT_FACTORY,ldapFactory);
	        env.put(Context.PROVIDER_URL, ldapUrl);//LDAP server
	        env.put(Context.SECURITY_AUTHENTICATION, "simple");
	        env.put(Context.SECURITY_PRINCIPAL, fullName);
	        env.put(Context.SECURITY_CREDENTIALS, password);
	        try{
	            ctx = new InitialLdapContext(env,connCtls);
	        }catch(javax.naming.AuthenticationException e){
	            logger.info("Authentication faild: "+e.toString());
	            throw e;
	        }catch(Exception e){
	        	logger.error("Something wrong while authenticating: "+e.toString());
	        	throw e;
	        }
	        if(ctx != null) {
	        	user = getUserInfo(fullName, ctx, userName);
	        }
		}
		return user;
	}

	@SuppressWarnings("rawtypes")
	public User getUserInfo(String cn, LdapContext ctx, String userName) {
		User user = new User();
		try {
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration en = ctx.search("", cn.substring(0, cn.indexOf(',')), constraints);
			if (en == null || !en.hasMoreElements()) {
				logger.warn("Have no NamingEnumeration.");
			}
			while (en != null && en.hasMoreElements()) {
				Object obj = en.nextElement();
				if (obj instanceof SearchResult) {
					SearchResult sr = (SearchResult) obj;
					logger.debug(sr);
					Attributes attrs = sr.getAttributes();
					
					
					user.setName(userName);
//					if(attrs.get("displayName")!=null) {
//						user.setName((String)attrs.get("displayName").get());
//					} else {
//						user.setName(userName);
//					}
					
					if(attrs.get("mail") != null) {
						user.setMail((String)attrs.get("mail").get());
					} else {
						user.setMail(userName+"@dianping.com");
					}
					
//					NamingEnumeration<String> xxx = attrs.getIDs();
//					while(xxx.hasMore()){
//						System.out.println(xxx.next());
//					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception in search():" + e);
		}
		return user;
	}
	
	@SuppressWarnings("rawtypes")
	private String getFullName(String sAMAccountName) throws NamingException {
		String fullName = null;
		
		Hashtable<String, String> solidEnv = new Hashtable<String, String>();
		solidEnv.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
		solidEnv.put(Context.PROVIDER_URL, ldapUrl);// LDAP server
		solidEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
		solidEnv.put(Context.SECURITY_PRINCIPAL, "cn=" + solidUsername + ","+ solidDN);
		solidEnv.put(Context.SECURITY_CREDENTIALS, solidPwd);
		LdapContext solidContext = new InitialLdapContext(solidEnv,connCtls);
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration en = solidContext.search("", loginAttribute+"=" + sAMAccountName, constraints);
		if (en == null) {
			logger.warn("Have no NamingEnumeration.");
			return fullName;
		}
		if (!en.hasMoreElements()) {
			logger.warn("Have no element.");
			return fullName;
		}
		while (en != null && en.hasMoreElements()) {
			Object obj = en.nextElement();
			if (obj instanceof SearchResult) {
				SearchResult sr = (SearchResult) obj;
				logger.debug(sr);
				fullName = sr.getNameInNamespace();
			}
		}
		return fullName;
	}
}
