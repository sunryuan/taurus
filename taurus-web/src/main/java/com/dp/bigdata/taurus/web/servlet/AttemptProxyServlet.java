package com.dp.bigdata.taurus.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.resource.ClientResource;

import com.dp.bigdata.taurus.restlet.resource.IAttemptResource;
import com.dp.bigdata.taurus.web.common.Constant;

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

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String attemptID = request.getParameter("id");

        ClientResource attemptCr = new ClientResource(Constant.RESTFUL_URL_BASE + "attempt/" + attemptID);
        IAttemptResource attemptResource = attemptCr.wrap(IAttemptResource.class);

        attemptResource.kill();
        response.setStatus(attemptCr.getStatus().getCode());
    }

}
