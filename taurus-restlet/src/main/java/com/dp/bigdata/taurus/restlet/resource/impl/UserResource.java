package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.restlet.data.Status;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.dp.bigdata.taurus.restlet.shared.UserDTO;

/**
 * Resource url : http://xxx.xxx/api/user
 * @author damon.zhu
 *
 */
public class UserResource extends ServerResource {

	private static final String URL = "ldap://192.168.50.11:389/";
	private static final String BASEDN = "OU=Technolog Department,OU=shoffice,DC=dianpingoa,DC=com";
	private static final String FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private static LdapContext ctx = null;
	private final Control[] connCtls = null;

	@Post
	public void logon(UserDTO user) {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, FACTORY);
		env.put(Context.PROVIDER_URL, URL + "DC=dianpingoa,DC=com");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "cn=" + user.getName() + ","
				+ BASEDN);
		env.put(Context.SECURITY_CREDENTIALS, user.getPassword());
		try {
			ctx = new InitialLdapContext(env, connCtls);
		} catch (javax.naming.AuthenticationException e) {
			System.out.println("Authentication faild: " + e.toString());
		} catch (Exception e) {
			System.out.println("Something wrong while authenticating: "
					+ e.toString());
		}

		if (ctx == null){
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
		}
		else {
			setStatus(Status.SUCCESS_OK);
		}
	}
}
