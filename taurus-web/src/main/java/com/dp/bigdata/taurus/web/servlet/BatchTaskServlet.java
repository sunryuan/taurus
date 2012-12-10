package com.dp.bigdata.taurus.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.google.gson.Gson;

public class BatchTaskServlet extends HttpServlet{

	private static final long serialVersionUID = 2348545179764589572L;
	private static final Log s_logger = LogFactory.getLog(BatchTaskServlet.class);
    private static final String FILE_DIR = "F:\\";
	//TODO need to be exactly the same as it's in restlet side
	private static final String[] PARAM_NAME_LIST = {"taskName","taskType","creator","description","poolId",
		"taskState","taskCommand","multiInstance","crontab","dependency","proxyUser",
		"maxExecutionTime","maxWaitTime","isAutoRetry","retryTimes"};


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
			File file = new File(FILE_DIR + item.getName());
			item.write(file);
			List<Representation> repList = createRepFromExcel(file);
			List<String> taskList = getTaskFromExcel(file);
			List<Result> results = new ArrayList<Result>();
			ClientResource taskResource = new ClientResource("http://localhost:8182/api/task");
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
		} catch (Exception e){
			s_logger.error(e);
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

	private List<Representation> createRepFromExcel(File file) throws BiffException, IOException{
		Workbook workbook = Workbook.getWorkbook(file);
		Sheet s = workbook.getSheet(0);
		int columnNum = s.getColumns();
		int rowNum = s.getRows();
		List<Representation> result = new ArrayList<Representation>();

		for(int i=1; i < rowNum; i++){
			Form form = new Form();
			for(int j=0; j < columnNum; j++){
				form.add(PARAM_NAME_LIST[j], s.getCell(j, i).getContents());
			}
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


	private void test(HttpServletRequest req, HttpServletResponse resp) throws IOException, InterruptedException{
		Thread.sleep(3000);
		PrintWriter writer = resp.getWriter();
		resp.setContentType("application/json");
		List<Result>list = new ArrayList<Result>();
		for(int i = 0; i < 4; i++){
			Result r = new Result("task-"+i, i%2 == 0? true : false);
			list.add(r);
		}
		Gson gson = new Gson();  
		String json = gson.toJson(list);    
		writer.write(json);
		System.out.print(json);
		writer.close();
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

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException{

	}

}
