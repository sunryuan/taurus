package com.dp.bigdata.taurus.web.servlet;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

/**
 * LoginServlet
 * 
 * @author damon.zhu
 */

public class LoginServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 8471117450126373174L;
    public static final String USER_NAME = "taurus-user";
    public static final String USER_GROUP = "taurus-group";
    public static final String USER_POWER = "taurus-user-power";
    private static final String URL = "ldap://192.168.50.11:389/";
    private static final String BASEDN = "OU=Technolog Department,OU=shoffice,DC=dianpingoa,DC=com";
    private static final String FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter("username");
        String password = request.getParameter("password");

        if(StringUtils.isBlank(password)){
           response.setStatus(401);
           return;
        }
        
        System.out.println("login request");
        System.out.println("userName : " + userName);

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, FACTORY);
        env.put(Context.PROVIDER_URL, URL + "DC=dianpingoa,DC=com");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "cn=" + userName + "," + BASEDN);
        env.put(Context.SECURITY_CREDENTIALS, password);

        LdapContext ctx = null;
        try {
            ctx = new InitialLdapContext(env, null);
        } catch (javax.naming.AuthenticationException e) {
            System.out.println("Authentication faild: " + e.toString());
        } catch (Exception e) {
            System.out.println("Something wrong while authenticating: " + e.toString());
        }

        if (ctx == null) {
            response.setStatus(401);
            System.out.println("longin fail!");
        } else {
            HttpSession session = request.getSession();
            session.setAttribute(USER_NAME, userName);
            /*
             * session.setAttribute(USER_GROUP, group); session.setAttribute(USER_POWER, power);
             */
            System.out.println("login success!");
            response.setStatus(200);
        }
    }
    
    public static void main(String[] args) {
   	 String userName = "damon.zhu";
       String password = "";
       System.out.println("login request");
       System.out.println("userName : " + userName);

       Hashtable<String, String> env = new Hashtable<String, String>();
       env.put(Context.INITIAL_CONTEXT_FACTORY, FACTORY);
       env.put(Context.PROVIDER_URL, URL + "DC=dianpingoa,DC=com");
       env.put(Context.SECURITY_AUTHENTICATION, "simple");
       env.put(Context.SECURITY_PRINCIPAL, "cn=" + userName + "," + BASEDN);
       env.put(Context.SECURITY_CREDENTIALS, password);

       LdapContext ctx = null;
       try {
           ctx = new InitialLdapContext(env, null);
       } catch (javax.naming.AuthenticationException e) {
           System.out.println("Authentication faild: " + e.toString());
       } catch (Exception e) {
           System.out.println("Something wrong while authenticating: " + e.toString());
       }

       if (ctx == null) {
           System.out.println("longin fail!");
       } else {
           System.out.println("login success!");
       }
   	 
   }
}
