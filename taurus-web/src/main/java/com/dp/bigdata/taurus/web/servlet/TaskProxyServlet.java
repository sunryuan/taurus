package com.dp.bigdata.taurus.web.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.resource.ClientResource;

import com.dp.bigdata.taurus.restlet.resource.IManualTaskResource;
import com.dp.bigdata.taurus.restlet.resource.ITaskResource;

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
    private static final String RESUME = "resume";

    private String RESTLET_URL_BASE;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = getServletContext();
        RESTLET_URL_BASE = context.getInitParameter("RESTLET_SERVER");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action").toLowerCase();
        String taskID = request.getParameter("id");
        
        ClientResource taskCr = new ClientResource(RESTLET_URL_BASE + "task/" + taskID);
        ITaskResource taskResource = taskCr.wrap(ITaskResource.class);

        ClientResource manualCr = new ClientResource(RESTLET_URL_BASE + "manualtask/" + taskID);
        IManualTaskResource manualResource = manualCr.wrap(IManualTaskResource.class);

        if(action.equals(DELETE)){
            taskResource.remove();
            System.out.println("Delete result code : " + manualCr.getStatus().getCode());
            response.setStatus(taskCr.getStatus().getCode());
        }else if(action.equals(SUSPEND)){
            manualResource.suspend();
            System.out.println("Suspend result code : " + manualCr.getStatus().getCode());
            response.setStatus(manualCr.getStatus().getCode());
        }else if(action.equals(EXECUTE)){
            manualResource.start();
            System.out.println("Execute result code : " + manualCr.getStatus().getCode());
            response.setStatus(manualCr.getStatus().getCode());
        } else if (action.equals(RESUME)) {
            manualResource.resume();
            System.out.println("Resume result code : " + manualCr.getStatus().getCode());
            response.setStatus(manualCr.getStatus().getCode());
        }
    }

}
