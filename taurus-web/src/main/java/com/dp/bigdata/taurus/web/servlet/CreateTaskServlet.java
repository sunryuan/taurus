/**
 * Project: taurus-web
 * 
 * File Created at 2012-12-6
 * $Id$
 * 
 * Copyright 2012 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dp.bigdata.taurus.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.restlet.resource.ClientResource;

import com.dp.bigdata.taurus.restlet.resource.INameResource;

//
//import com.google.gson.Gson;

/**
 * TODO Comment of CreateTaskServlet
 * @author renyuan.sun
 *
 */
public class CreateTaskServlet extends HttpServlet{
    private static final long serialVersionUID = 2348545179764589572L;
    private static String targetUri = "task";
    private static String nameUri = "name?task_name=";
    private static final Log LOG = LogFactory.getLog(HttpServlet.class);

    private String RESTLET_URL_BASE;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = getServletContext();
        RESTLET_URL_BASE = context.getInitParameter("RESTLET_SERVER");
        targetUri = RESTLET_URL_BASE + targetUri;
        nameUri =  RESTLET_URL_BASE + nameUri;
    }

    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException{
        HttpClient httpclient = new DefaultHttpClient();
        // Determine final URL
        StringBuffer uri = new StringBuffer();
        
        if(req.getParameter("update") != null){
            uri.append(targetUri).append("/").append(req.getParameter("update"));
        } else {
            uri.append(targetUri);
        }
        LOG.info("Access URI : " + uri.toString());
        // Get HTTP method
        final String method = req.getMethod();
        // Create new HTTP request container
        HttpRequestBase request = null;
                
        // Get content length
        int contentLength = req.getContentLength();
        // Unknown content length ...
        // if (contentLength == -1)
        // throw new ServletException("Cannot handle unknown content length");
        // If we don't have an entity body, things are quite simple
        if (contentLength < 1) {
            request = new HttpRequestBase() {
                public String getMethod() {
                    return method;
                }
            };
        } else {
            // Prepare request
            HttpEntityEnclosingRequestBase tmpRequest = new HttpEntityEnclosingRequestBase() {
                public String getMethod() {
                    return method;
                }
            };
            // Transfer entity body from the received request to the new request
            InputStreamEntity entity = new InputStreamEntity(
                    req.getInputStream(), contentLength);
            tmpRequest.setEntity(entity);
            request = tmpRequest;
        }

        // Set URI
        try {
            request.setURI(new URI(uri.toString()));
        } catch (URISyntaxException e) {
            throw new ServletException("URISyntaxException: " + e.getMessage());
        }

        // Copy headers from old request to new request
        // @todo not sure how this handles multiple headers with the same name
        Enumeration<?> headers = req.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = (String)headers.nextElement();
            String headerValue = req.getHeader(headerName);
            //LOG.info("header: " + headerName + " value: " + headerValue);
            // Skip Content-Length and Host
            String lowerHeader = headerName.toLowerCase();
            if(lowerHeader.equals("content-type")) {
                request.addHeader(headerName, headerValue+";charset=\"utf-8\"");
            } else if (!lowerHeader.equals("content-length")
                    && !lowerHeader.equals("host")
                    ) {
                request.addHeader(headerName, headerValue);
            }
        }
        
        // Execute the request
        HttpResponse response = httpclient.execute(request);
        // Transfer status code to the response
        StatusLine status = response.getStatusLine();
        resp.setStatus(status.getStatusCode());

        // Transfer headers to the response
        Header[] responseHeaders = response.getAllHeaders();
        for (int i = 0; i < responseHeaders.length; i++) {
            Header header = responseHeaders[i];
            if(!header.getName().equals("Transfer-Encoding"))
                resp.addHeader(header.getName(), header.getValue());
        }

        // Transfer proxy response entity to the servlet response
        HttpEntity entity = response.getEntity();
        InputStream input = entity.getContent();
        OutputStream output = resp.getOutputStream();
        
        byte buffer[] = new byte[50];
        while(input.read(buffer)!=-1){
            output.write(buffer);
        }  
//        int b = input.read();
//        while (b != -1) {
//            output.write(b);
//            b = input.read();
//        }
        // Clean up
        input.close();
        output.close();
        httpclient.getConnectionManager().shutdown();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuffer uri = new StringBuffer();
        if (req.getParameter("name") != null){
            uri.append(nameUri).append(req.getParameter("name"));
            LOG.info("Access URI : " + uri.toString());
            ClientResource cr = new ClientResource(uri.toString());
            INameResource nameResource = cr.wrap(INameResource.class);
            resp.setContentType("text/html");
            if(nameResource.hasName()) {
                resp.getWriter().write("1");
            } else {
                resp.getWriter().write("0");
            }
        }
    }
    
    
}
