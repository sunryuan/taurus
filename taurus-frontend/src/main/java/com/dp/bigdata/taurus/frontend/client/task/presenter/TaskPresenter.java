package com.dp.bigdata.taurus.frontend.client.task.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.restlet.client.resource.Result;

import com.dp.bigdata.taurus.restlet.shared.PoolDTO;
import com.dp.bigdata.taurus.frontend.client.service.PoolsService;
import com.dp.bigdata.taurus.frontend.client.service.ServiceApi;
import com.dp.bigdata.taurus.frontend.client.service.TasksService;
import com.dp.bigdata.taurus.frontend.client.task.TaskEventBus;
import com.dp.bigdata.taurus.frontend.client.task.view.TaskView;
import com.dp.bigdata.taurus.frontend.client.task.view.interfaces.ITaskView;
import com.dp.bigdata.taurus.frontend.client.view.widget.UploadListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.BasePresenter;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

@Presenter(view = TaskView.class)
public class TaskPresenter extends BasePresenter<ITaskView, TaskEventBus>
		implements ITaskView.ITaskPresenter {

	private final PoolsService poolService = GWT.create(PoolsService.class);
	private final TasksService taskService = GWT.create(TasksService.class);
	private Map<String,Integer> poolNameToIdMap;
	public void onLoadTaskModule() {
		System.out.println("TaskPresenter on onLoadTaskModule");
	}

	private void retrievePoolNames() {
		poolService.getClientResource().setReference(ServiceApi.POOLSSERVICE);
		poolService.retrieve(new Result<ArrayList<PoolDTO>>() {
			public void onFailure(Throwable caught) {
				System.out.println("fail to receive pool names");
			}

			public void onSuccess(ArrayList<PoolDTO> result) {
				String[] poolNames = new String[result.size()];
				poolNameToIdMap = new HashMap<String,Integer>();
				for (int i = 0; i < result.size(); i++) {
					poolNames[i] = result.get(i).getName();
					poolNameToIdMap.put(result.get(i).getName(),result.get(i).getId());
				}
				view.getTaskPoolItem().setValueMap(poolNames);
			}
		});
	}


	@Override
	public void bind() {

		retrievePoolNames();
		// set default taskTemplateUploadForm action
		view.getTaskForm().setAction(ServiceApi.TASKSERVICE);
		view.getTaskTypeItem().setDefaultToFirstOption(true);
		view.getTaskPoolItem().setDefaultToFirstOption(true);
		
		//view.getTaskTemplateUploadForm().setAction(ServiceApi.TEMPLATESERVICE);
		view.getTaskForm().setUploadListener(
			new UploadListener() {
				@Override
				public void uploadComplete(String status) {
					if ("201".equals(status)) {
						SC.say("添加作业成功");
					} else {
						SC.say("添加作业失败");
					}
				}
			});
		
		view.getAddTaskButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String taskNameStr = view.getTaskNameItem().getValueAsString();
				String taskCommandStr = view.getTaskCommandItem().getValueAsString();
				String crontabStr = view.getTaskCrontabItem().getValueAsString();
				String executorStr = view.getTaskProxyUserItem().getValueAsString();
				String pool = view.getTaskPoolItem().getValueAsString();
//				int poolID = poolNameToIdMap.get(pool)==null?0:poolNameToIdMap.get(pool);
				String type = view.getTaskTypeItem().getValueAsString();
				String fileName = view.getTaskFileItem().getValueAsString();
				String dependencyexpr = view.getTaskDependencyItem().getValueAsString();
				int maxExecutionTimeInt = Integer.parseInt(view.getTaskExecItem()
						.getValueAsString());
				int maxWaitTimeInt = Integer.parseInt(view.getTaskWaitItem()
						.getValueAsString());
				int retryTimesInt = Integer.parseInt(view.getTaskRetryItem()
						.getValueAsString());
				int multiInstanceNum = Integer.parseInt(view.getTaskMultiInstanceItem()
						.getValueAsString());

				if (taskNameStr == null || taskNameStr.equals("")
						|| taskCommandStr == null || taskCommandStr.equals("")
						|| crontabStr == null || crontabStr.equals("")
						|| executorStr == null || executorStr.equals("")) {
					SC.say("填写信息不完整，请重新检查");
					return;
				}
//				taskDto.setName(taskNameStr);
//				taskDto.setCommand(taskCommandStr);
//				taskDto.setCrontab(crontabStr);
//				taskDto.setProxyuser(executorStr);
//				taskDto.setCreator(Cookies.getCookie("user"));
//				taskDto.setDependencyexpr(dependencyexpr);
//				taskDto.setExecutiontimeout(maxExecutionTimeInt);
//				taskDto.setWaittimeout(maxWaitTimeInt);
//				taskDto.setRetrytimes(retryTimesInt);
//				taskDto.setAllowmultiinstances(multiInstanceNum);
//				taskDto.setType(type);
//				taskDto.setPoolid(poolID);
		
				if (fileName != null
						&& (fileName.endsWith(".gz") || fileName
								.endsWith(".zip"))) {
					view.getTaskForm().submitForm();
				} else {
					SC.say("请选择正确的上传文件,作业类型只支持tar.gz, gz, zip");
					return;
				}
				
//				taskService.create(taskDto, new Result<Void>() {
//					@Override
//					public void onSuccess(Void result) {
//						SC.say("提交作业成功");
//					}
//					@Override
//					public void onFailure(Throwable caught) {
//						SC.say("提交作业失败");
//					}
//				});
			};
		});
		view.getClearTaskButton().addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				view.getTaskForm().clearValues();
			}
		});
	}
}
