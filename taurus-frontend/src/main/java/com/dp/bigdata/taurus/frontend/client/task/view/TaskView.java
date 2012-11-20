package com.dp.bigdata.taurus.frontend.client.task.view;

import org.synthful.smartgwt.client.widgets.UIIButton;

import com.dp.bigdata.taurus.frontend.client.common.IsCanvas;
import com.dp.bigdata.taurus.frontend.client.task.TaskType;
import com.dp.bigdata.taurus.frontend.client.task.view.interfaces.ITaskView;
import com.dp.bigdata.taurus.frontend.client.view.widget.ReverseComposite;
import com.dp.bigdata.taurus.frontend.client.view.widget.UploadDynamicForm;
import com.dp.bigdata.taurus.restlet.shared.GWTTaskDetailControlName;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.NamedFrame;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.SectionItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.UploadItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class TaskView extends ReverseComposite<ITaskView.ITaskPresenter>
		implements ITaskView, IsCanvas {

	interface TaskViewUiBinder extends UiBinder<VLayout, TaskView> {
	};

	private static TaskViewUiBinder uiBinder = GWT
			.create(TaskViewUiBinder.class);

	@UiField
	VLayout taskLayout;
	@UiField
	VLayout setLayout;
	@UiField
	UIIButton addTaskButton;
	@UiField
	UIIButton clearTaskButton;
	@UiField
	HLayout taskButtonLayout;

	UploadDynamicForm taskForm;


	// task edit items
	final HiddenItem taskIDItem = new HiddenItem(
			GWTTaskDetailControlName.TASKID.getName());
	final TextItem taskNameItem = new TextItem(
			GWTTaskDetailControlName.TASKNAME.getName(), "作业名");
	final SelectItem taskTypeItem = new SelectItem(
			GWTTaskDetailControlName.TASKTYPE.getName(), "作业类型");
	final SelectItem taskPoolItem = new SelectItem(
			GWTTaskDetailControlName.TASKPOOL.getName(), "POOL");
	final UploadItem taskFileItem = new UploadItem(
			GWTTaskDetailControlName.TASKFILE.getName(), "作业文件(不支持中文和空格)");
	final TextItem taskCommandItem  = new TextItem(
			GWTTaskDetailControlName.TASKCOMMAND.getName(), "命令");
	final TextItem taskCrontabItem  = new TextItem(
			GWTTaskDetailControlName.CRONTAB.getName(), "crontab");
	final TextItem taskMailItem  = new TextItem(
			GWTTaskDetailControlName.TASKMAIL.getName(), "报警收件人邮件(逗号分隔)");
	final TextItem taskProxyUserItem  = new TextItem(
			GWTTaskDetailControlName.PROXYUSER.getName(), "以该用户身份运行(默认nobody)");
	final TextItem taskExecItem  = new TextItem(
			GWTTaskDetailControlName.MAXEXECUTIONTIME.getName(), "最长执行时间");
	final TextItem taskWaitItem  = new TextItem(
			GWTTaskDetailControlName.MAXWAITTIME.getName(), "最长等待时间");
	final TextItem taskDependencyItem  = new TextItem(
			GWTTaskDetailControlName.DEPENDENCY.getName(), "依赖表达式");
	final TextItem taskRetryItem  = new TextItem(
			GWTTaskDetailControlName.RETRYTIMES.getName(), "重试次数");
	final TextItem taskMultiInstanceItem  = new TextItem(
			GWTTaskDetailControlName.MULTIINSTANCE.getName(), "可同时运行实例个数");
	
	public Canvas asCanvas() {
		return taskLayout;
	}

	public TaskView() {
		uiBinder.createAndBindUi(this);
		init();
	}

	private void init() {
		NamedFrame frame = new NamedFrame("upload_frame");
		frame.setWidth("1");
		frame.setHeight("1");
		frame.setVisible(false);
		taskLayout.addMember(frame);
		setupPannel();
	}

	private void setupPannel() {
		taskForm = new UploadDynamicForm();
		taskForm.setGroupTitle("Task");  
		taskForm.setIsGroup(true);  
		taskForm.setCellPadding(6);
		taskForm.setCanDragResize(true);  
		taskForm.setResizeFrom("BR");  
		taskForm.setMargin(10);
		taskTypeItem.setValueMap(TaskType.HADOOP.value(),
				TaskType.HIVE.value(), TaskType.WORMHOLE.value(),
				TaskType.SHELL_SCRIPT.value());

		taskTypeItem.setDefaultToFirstOption(true);
		taskPoolItem.setDefaultToFirstOption(true);
		SectionItem section1 = new SectionItem();  
	    section1.setDefaultValue("基本设置");  
	    section1.setSectionExpanded(true);  
	    section1.setItemIds(GWTTaskDetailControlName.TASKNAME.getName(),
	    		GWTTaskDetailControlName.TASKTYPE.getName(),
	    		GWTTaskDetailControlName.TASKPOOL.getName(),
	    		GWTTaskDetailControlName.TASKFILE.getName(),
	    		GWTTaskDetailControlName.TASKCOMMAND.getName(),
	    		GWTTaskDetailControlName.CRONTAB.getName(),
	    		GWTTaskDetailControlName.TASKMAIL.getName(),
	    		GWTTaskDetailControlName.PROXYUSER.getName());
	    SectionItem section2 = new SectionItem();  
	    section2.setDefaultValue("其他设置");  
	    section2.setSectionExpanded(false);  
	    section2.setItemIds(GWTTaskDetailControlName.MAXEXECUTIONTIME.getName(),
	    		GWTTaskDetailControlName.MAXWAITTIME.getName(),
	    		GWTTaskDetailControlName.DEPENDENCY.getName(),
	    		GWTTaskDetailControlName.RETRYTIMES.getName(),
	    		GWTTaskDetailControlName.MULTIINSTANCE.getName());
		taskForm.setFields(section1,taskNameItem,
				taskTypeItem, taskPoolItem, taskFileItem,
				taskCommandItem,taskCrontabItem,taskMailItem,taskProxyUserItem,
				section2,taskExecItem, taskWaitItem,
				taskDependencyItem, taskRetryItem,taskMultiInstanceItem);

		Button uploadButton = new Button("提交");
		uploadButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				String fileName = taskFileItem.getValueAsString();
				if (fileName != null
						&& (fileName.endsWith(".gz") || fileName
								.endsWith(".zip"))) {
					taskForm.submitForm();
				} else {
					SC.say("请选择正确的上传文件,作业类型只支持tar.gz, gz, zip");
				}
			}
		});
		setLayout.addMember(taskForm);
	}

	public TextItem getTaskNameItem() {
		return taskNameItem;
	}

	public SelectItem getTaskTypeItem() {
		return taskTypeItem;
	}

	public SelectItem getTaskPoolItem() {
		return taskPoolItem;
	}

	@Override
	public UploadItem getTaskFileItem() {
		return taskFileItem;
	}

	@Override
	public UIIButton getAddTaskButton() {
		return addTaskButton;
	}

	@Override
	public HiddenItem getTaskIDItem() {
		return taskIDItem;
	}

	@Override
	public UploadDynamicForm getTaskForm() {
		return taskForm;
	}

	@Override
	public UIIButton getClearTaskButton() {
		return clearTaskButton;
	}

	public TextItem getTaskCommandItem() {
		return taskCommandItem;
	}

	public TextItem getTaskCrontabItem() {
		return taskCrontabItem;
	}

	public TextItem getTaskMailItem() {
		return taskMailItem;
	}

	public TextItem getTaskProxyUserItem() {
		return taskProxyUserItem;
	}

	public TextItem getTaskExecItem() {
		return taskExecItem;
	}

	public TextItem getTaskWaitItem() {
		return taskWaitItem;
	}

	public TextItem getTaskDependencyItem() {
		return taskDependencyItem;
	}

	public TextItem getTaskRetryItem() {
		return taskRetryItem;
	}

	public TextItem getTaskMultiInstanceItem() {
		return taskMultiInstanceItem;
	}
}
