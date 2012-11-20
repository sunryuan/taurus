package com.dp.bigdata.taurus.frontend.client.view.widget;

import org.restlet.client.resource.Result;

import com.dp.bigdata.taurus.frontend.client.service.ServiceApi;
import com.dp.bigdata.taurus.frontend.client.service.TasksService;
import com.dp.bigdata.taurus.restlet.shared.GWTTaskDetailControlName;
import com.dp.bigdata.taurus.restlet.shared.TaskDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class ScheduleUploadWindow extends Window {
	private final TasksService tasksService = GWT.create(TasksService.class);

	public ScheduleUploadWindow(final String templateId) {
		super();
		tasksService.getClientResource().setReference(ServiceApi.TASKSERVICE);

		VLayout vLayout = new VLayout(5);
		vLayout.setDefaultLayoutAlign(Alignment.CENTER);

		setWidth(320);
		setHeight(350);
		setTitle("调度详细设置");
		setShowMinimizeButton(false);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		addCloseClickHandler(new CloseClickHandler() {
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		final DynamicForm form = new DynamicForm();
		final TextItem taskName = new TextItem(
				GWTTaskDetailControlName.TASKNAME.getName(), "名称");
		final TextItem taskCommand = new TextItem(
				GWTTaskDetailControlName.TASKCOMMAND.getName(), "命令");
		final TextItem crontab = new TextItem(
				GWTTaskDetailControlName.CRONTAB.getName(), "crontab");
		final TextItem executor = new TextItem(
				GWTTaskDetailControlName.PROXYUSER.getName(),
				"以该用户身份运行(默认nobody)");

		final SpinnerItem maxExecutionTime = new SpinnerItem(
				GWTTaskDetailControlName.MAXEXECUTIONTIME.getName(),
				"最长执行时间(分钟)");
		maxExecutionTime.setDefaultValue(30);
		maxExecutionTime.setMin(30);
		maxExecutionTime.setMax(100);
		maxExecutionTime.setStep(1f);

		final SpinnerItem maxWaitTime = new SpinnerItem(
				GWTTaskDetailControlName.MAXWAITTIME.getName(), "最长等待时间(分钟)");
		maxWaitTime.setDefaultValue(10);
		maxWaitTime.setMin(10);
		maxWaitTime.setMax(60);
		maxWaitTime.setStep(1f);

		final SpinnerItem retryTimes = new SpinnerItem(
				GWTTaskDetailControlName.RETRYTIMES.getName(), "重试次数");
		retryTimes.setDefaultValue(1);
		retryTimes.setMin(1);
		retryTimes.setMax(5);
		retryTimes.setStep(1f);

		final SpinnerItem retryExpireTime = new SpinnerItem(
				//TODO:remove it
				GWTTaskDetailControlName.RETRYTIMES.getName(), "重试超期时间");
		retryExpireTime.setDefaultValue(30);
		retryExpireTime.setMin(30);
		retryExpireTime.setMax(60);
		retryExpireTime.setStep(1f);

		final CheckboxItem multiInstance = new CheckboxItem(
				GWTTaskDetailControlName.MULTIINSTANCE.getName(), "允许多实例");

		form.setFields(taskName, taskCommand, crontab, executor,
				maxExecutionTime, maxWaitTime, retryTimes, retryExpireTime, multiInstance);

		IButton submitButton = new IButton("提交");
		submitButton.setAlign(Alignment.CENTER);

		submitButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				TaskDTO taskDto = new TaskDTO();
				String taskNameStr = taskName.getValueAsString();
				String taskCommandStr = taskCommand.getValueAsString();
				String crontabStr = crontab.getValueAsString();
				String executorStr = executor.getValueAsString();
				int maxExecutionTimeInt = Integer.parseInt(maxExecutionTime
						.getValueAsString());
				int maxWaitTimeInt = Integer.parseInt(maxWaitTime
						.getValueAsString());
				int retryTimesInt = Integer.parseInt(retryTimes
						.getValueAsString());
				int retryExpireTimeInt = Integer.parseInt(retryExpireTime
						.getValueAsString());
				boolean multiInstanceBool = multiInstance.getValueAsBoolean();

				if (taskNameStr == null || taskNameStr.equals("")
						|| taskCommandStr == null || taskCommandStr.equals("")
						|| crontabStr == null || crontabStr.equals("")
						|| executorStr == null || executorStr.equals("")) {
					SC.say("填写信息不完整，请重新检查");
					return;
				}
				
				taskDto.setTaskid(templateId);
				taskDto.setName(taskNameStr);
				taskDto.setCommand(taskCommandStr);
				taskDto.setCrontab(crontabStr);
				taskDto.setProxyuser(executorStr);
				taskDto.setCreator(Cookies.getCookie("user"));
				taskDto.setExecutiontimeout(maxExecutionTimeInt);
				taskDto.setWaittimeout(maxWaitTimeInt);
				taskDto.setRetrytimes(retryTimesInt);
				taskDto.setRetryexpiretimeout(retryExpireTimeInt);
				taskDto.setAllowmultiinstances(1);				//TODO:modify


				tasksService.create(taskDto, new Result<Void>() {

					@Override
					public void onSuccess(Void result) {
						SC.say("提交作业成功");
						destroy();
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.say("提交作业失败");
					}
				});
			};
		});

		vLayout.setMembers(form, submitButton);
		addItem(vLayout);
	}
}
