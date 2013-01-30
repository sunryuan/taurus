package com.dp.bigdata.taurus.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.google.gson.Gson;

public class BatchTaskServlet extends HttpServlet{

	private static final long serialVersionUID = 2348545179764589572L;
	private static final Log s_logger = LogFactory.getLog(BatchTaskServlet.class);
	
//	private static final String MULTI_INSTANCE = "multiInstance";
//	private static final String IS_AUTO_RETRY = "isAutoRetry";
	private static final String TASK_TYPE = "taskType";
	
	private static final String[] PARAM_NAME_LIST = {"taskName", "hostName",
		"taskState","taskCommand","multiInstance","crontab","dependency","proxyUser",
		"maxExecutionTime","maxWaitTime","retryTimes","description","alertCondition",
		"alertType","alertGroup","alertUser"};
	
	private static Map<String, IFieldHandler> FIELD_NAME_TO_HANDLER_MAP = 
		new HashMap<String, IFieldHandler>();
	
	static{
//		BooleanFieldHandler bfHandler = new BooleanFieldHandler();
		TaskTypeFieldHandler ttfHandler = new TaskTypeFieldHandler();
//		FIELD_NAME_TO_HANDLER_MAP.put(MULTI_INSTANCE, bfHandler);
//		FIELD_NAME_TO_HANDLER_MAP.put(IS_AUTO_RETRY, bfHandler);
		FIELD_NAME_TO_HANDLER_MAP.put(TASK_TYPE, ttfHandler);
	}
	

    private static String XSL_UPLOAD_TMP_DIR;
    private static String RESTLET_SERVER;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = getServletContext();
        XSL_UPLOAD_TMP_DIR = context.getInitParameter("XSL_UPLOAD_TMP_DIR");
        RESTLET_SERVER = context.getInitParameter("RESTLET_SERVER") + "task";
    }

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException{
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024 * 1024);
		ServletFileUpload upload = new ServletFileUpload(factory);

		try {
			@SuppressWarnings("unchecked")
			List<FileItem> items = upload.parseRequest(req);
			FileItem item = items.get(0);
            File file = new File(XSL_UPLOAD_TMP_DIR + item.getName());
			item.write(file);
			HttpSession session = req.getSession();
			String username = (String)session.getAttribute(LoginServlet.USER_NAME);
			List<Representation> repList = createRepFromExcel(file, username);
			List<String> taskList = getTaskFromExcel(file);
			List<Result> results = new ArrayList<Result>();
            ClientResource taskResource = new ClientResource(RESTLET_SERVER);
			for(int i = 0; i < repList.size(); i++){
				boolean success = false;
				try{
					taskResource.post(repList.get(i));
					Status status = taskResource.getResponse().getStatus();
					if(Status.SUCCESS_CREATED.equals(status)){
						success = true;
					}
					Result result = new Result(taskList.get(i), success);
					results.add(result);
				} catch(Exception e){
					Result result = new Result(taskList.get(i), success);
					results.add(result);
					continue;
				}
			}
			//write results as json back to response
			processResponse(results, resp);
		} catch (FileUploadException e) {
			s_logger.error(e);
			resp.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		} catch (Exception e){
			s_logger.error(e);
			resp.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	private List<String> getTaskFromExcel(File file) throws BiffException, IOException{
		Workbook workbook = Workbook.getWorkbook(file);
		Sheet s = workbook.getSheet(0);
		int rowNum = s.getRows();
		List<String> result = new ArrayList<String>();
		for(int i=1; i < rowNum; i++){
			result.add(s.getCell(0, i).getContents());
		}
		return result;
	}

	private List<Representation> createRepFromExcel(File file, String username) throws BiffException, IOException{
		if(StringUtils.isEmpty(username)){
			s_logger.error("current username is empty!");
			throw new RuntimeException("current username is empty");
		}
		Workbook workbook = Workbook.getWorkbook(file);
		Sheet s = workbook.getSheet(0);
		int columnNum = s.getColumns();
		int rowNum = s.getRows();
		List<Representation> result = new ArrayList<Representation>();

		for(int i=1; i < rowNum; i++){
			Form form = new Form();
			for(int j=0; j < columnNum; j++){
				String paramName = PARAM_NAME_LIST[j];
				String value = s.getCell(j, i).getContents();
				IFieldHandler fh = FIELD_NAME_TO_HANDLER_MAP.get(paramName);
				if(fh != null)
					value = fh.process(value);
				form.add(paramName, value);
			}
			//TODO modified when login module is ready
			form.add("creator", username);
			Representation r = form.getWebRepresentation();
			r.setMediaType(MediaType.APPLICATION_XML);
			result.add(r);
		}
		return result;	
	}

	private void processResponse(List<Result> results, HttpServletResponse resp) throws IOException{
		PrintWriter writer = resp.getWriter();
		resp.setContentType("application/json");
		Gson gson = new Gson();  
		String json = gson.toJson(results);    
		writer.write(json);
		writer.close();
		s_logger.info(json);
	}

	private static final class Result{
		private final String name;
		private final boolean success;

		public Result(String name, boolean success) {
			super();
			this.name = name;
			this.success = success;
		}
	}
	
	private static interface IFieldHandler{
		public String process(String orignalValue);
	}
	
//	private static final class BooleanFieldHandler implements IFieldHandler{
//
//		@Override
//		public String process(String orignalValue) {
//			if(orignalValue != null && orignalValue.equalsIgnoreCase("yes"))
//				return "1";
//			else
//				return "0";
//		}
//	}
	
	private static final class TaskTypeFieldHandler implements IFieldHandler{

		@Override
		public String process(String orignalValue) {
			if(orignalValue != null){
				if(orignalValue.equalsIgnoreCase("hadoop"))
					return "hadoop";
				else if(orignalValue.equalsIgnoreCase("wormhole"))
					return "wormhole";
				else
					return "others";
			}else{
				return "others";
			}
		}
		
	}

}
