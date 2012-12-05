package com.dp.bigdata.taurus.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * TaskProxyServlet
 * @author damon.zhu
 *
 */
public class TaskProxyServlet extends HttpServlet {
    
    /**
     * 
     */
    private static final long serialVersionUID = 2904070524158595744L;
    
    private static final String DELETE = "delete";
    private static final String SUSPEND = "suspend";
    private static final String EXECUTE = "execute";

    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        System.out.println("hererer");
    }
    
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String taskID = request.getParameter("id");
        
        System.out.println(action + "   "  + taskID);
        
        if(action.equals(DELETE)){
            response.setStatus(200);
        }else if(action.equals(SUSPEND)){
            response.setStatus(400);
        }else if(action.equals(EXECUTE)){
            
        }else{
            
        }
        
        

    
    }

}
