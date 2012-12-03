package com.dp.bigdata.taurus.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 8471117450126373174L;
    private static final String COOKIE_NAME = "taurus-user";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String landing_name=request.getParameter("username");
        String landing_password=request.getParameter("password");
        
        System.out.println(landing_name);
        System.out.println(landing_password);
        
        Cookie[] cookies = request.getCookies();
        for(Cookie ck : cookies){
            String ckName = ck.getName();
            if(ckName.equals(COOKIE_NAME)){
                return;
            }
        }
        Cookie cookie = new Cookie(COOKIE_NAME, landing_name);
        cookie.setMaxAge(120);
        response.addCookie(cookie);
        
        
        
    }
    
}
