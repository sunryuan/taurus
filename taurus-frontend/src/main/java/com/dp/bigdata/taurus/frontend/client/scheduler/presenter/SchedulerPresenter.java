package com.dp.bigdata.taurus.frontend.client.scheduler.presenter;

import java.util.ArrayList;

import org.restlet.client.resource.Result;

import com.dp.bigdata.taurus.restlet.shared.AttemptDTO;
import com.dp.bigdata.taurus.restlet.shared.TaskDTO;
import com.dp.bigdata.taurus.frontend.client.scheduler.InstanceStatus;
import com.dp.bigdata.taurus.frontend.client.scheduler.SchedulerAttemptListField;
import com.dp.bigdata.taurus.frontend.client.scheduler.SchedulerEventBus;
import com.dp.bigdata.taurus.frontend.client.scheduler.SchedulerTaskListField;
import com.dp.bigdata.taurus.frontend.client.scheduler.view.SchedulerView;
import com.dp.bigdata.taurus.frontend.client.scheduler.view.interfaces.ISchedulerView;
import com.dp.bigdata.taurus.frontend.client.service.AttempsService;
import com.dp.bigdata.taurus.frontend.client.service.ServiceApi;
import com.dp.bigdata.taurus.frontend.client.service.TasksService;
import com.google.gwt.core.client.GWT;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.BasePresenter;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;

@Presenter(view = SchedulerView.class)
public class SchedulerPresenter extends
		BasePresenter<ISchedulerView, SchedulerEventBus> implements
		ISchedulerView.ISchedulerPresenter {
	
	private final TasksService tasksService = GWT.create(TasksService.class);
	private final AttempsService attempsService = GWT.create(AttempsService.class);

	public void onLoadSchedulerModule() {
		eventBus.changeSchedulerTab(view);
	}
	
	@Override
	public void bind() {
		tasksService.getClientResource().setReference(ServiceApi.TASKSERVICE);
		tasksService.retrieve(new Result<ArrayList<TaskDTO>>() {
			
			@Override
			public void onSuccess(ArrayList<TaskDTO> result) {
				view.getTasksListGrid().removeAll();
				if (result != null && result.size() > 0){
					for (int i = 0; i < result.size(); i++){
						TaskDTO taskDto = result.get(i);
						ListGridRecord rec = new ListGridRecord();
						rec.setAttribute(SchedulerTaskListField.TASKID.getValue(), taskDto.getTaskid());
						rec.setAttribute(SchedulerTaskListField.TASKNAME.getValue(), taskDto.getName());
						rec.setAttribute(SchedulerTaskListField.EXECUTOR.getValue(), taskDto.getCreator());
						rec.setAttribute(SchedulerTaskListField.CRONTAB.getValue(), taskDto.getCrontab());
						rec.setAttribute(SchedulerTaskListField.TASKTYPE.getValue(), "任务");
						view.getTasksListGrid().addData(rec);
					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("fail to get taskdto arraylist");
			}
		});
		
		view.getTasksListGrid().addRecordClickHandler(new RecordClickHandler() {
			
			@Override
			public void onRecordClick(RecordClickEvent event) {
				view.getAttempsListGrid().removeAll();
				Record rec = event.getRecord();
				String taskId = rec.getAttribute(SchedulerTaskListField.TASKID.getValue());
				
				if (taskId != null && !"".equals(taskId)){
					attempsService.getClientResource().setReference(ServiceApi.ATTEMPTSSERVICE + taskId);
					attempsService.retrieve(new Result<ArrayList<AttemptDTO>>() {
						
						@Override
						public void onSuccess(ArrayList<AttemptDTO> result) {
							if (result != null && result.size() > 0){
								for (int i = 0; i < result.size(); i++){
									AttemptDTO attemptDto = result.get(i);
									ListGridRecord rec = new ListGridRecord();
									rec.setAttribute(SchedulerAttemptListField.ATTEMPTID.getValue(), attemptDto.getAttemptID());
									rec.setAttribute(SchedulerAttemptListField.INSTANCEID.getValue(), attemptDto.getInstanceID());
									rec.setAttribute(SchedulerAttemptListField.STARTTIME.getValue(), attemptDto.getStartTime());
									rec.setAttribute(SchedulerAttemptListField.ENDTIME.getValue(), attemptDto.getEndTime());
									rec.setAttribute(SchedulerAttemptListField.RETURNVALUE.getValue(), attemptDto.getReturnValue());
									int instanceStatus = attemptDto.getStatus();
									rec.setAttribute(SchedulerAttemptListField.STATUS.getValue(), InstanceStatus.getInstanceRunState(instanceStatus));
									view.getAttempsListGrid().addData(rec);
								}
							}
							
						}
						
						@Override
						public void onFailure(Throwable caught) {
							System.out.println("fail to get attemps");
						}
					});
				}
			}
		});
	}
}
