package com.dp.bigdata.taurus.web.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.dp.bigdata.taurus.restlet.resource.IAttemptResource;

/**
 * AttemptProxyServlet
 * 
 * @author damon.zhu
 */
public class AttemptProxyServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -2924647981910768516L;

    private static final String KILL = "kill";
    private static final String LOG = "view-log";
    private String RESTLET_URL_BASE;
    private String ERROR_PAGE;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = getServletContext();
        RESTLET_URL_BASE = context.getInitParameter("RESTLET_SERVER");
        ERROR_PAGE = context.getInitParameter("ERROR_PAGE");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String attemptID = request.getParameter("id");
        String action = request.getParameter("action") == null ? "" : request.getParameter("action").toLowerCase();

        ClientResource attemptCr = new ClientResource(RESTLET_URL_BASE + "attempt/" + attemptID);
        IAttemptResource attemptResource = attemptCr.wrap(IAttemptResource.class);
        
        if (action.equals(KILL)) {
            attemptResource.kill();
            response.setStatus(attemptCr.getStatus().getCode());
        } else if (action.equals(LOG)) {
            response.setContentType("text/html;charset=utf-8");
            try {
                Representation rep = attemptCr.get(MediaType.TEXT_HTML);
                if (attemptCr.getStatus().getCode() == 200) {
                    OutputStream output = response.getOutputStream();
                    rep.write(output);
                    output.close();
                } else {
                    //getServletContext().getRequestDispatcher(ERROR_PAGE).forward(request, response);
                }
            } catch (Exception e) {
                getServletContext().getRequestDispatcher(ERROR_PAGE).forward(request, response);
            }
        }
    }
}
