package com.dp.bigdata.taurus.frontend.client.scheduler.view;

import org.synthful.smartgwt.client.widgets.UIListGrid;
import org.synthful.smartgwt.client.widgets.UIVLayout;

import com.dp.bigdata.taurus.frontend.client.scheduler.SchedulerAttemptListField;
import com.dp.bigdata.taurus.frontend.client.scheduler.SchedulerTaskListField;
import com.dp.bigdata.taurus.frontend.client.scheduler.view.interfaces.ISchedulerView;
import com.dp.bigdata.taurus.frontend.client.view.widget.ReverseComposite;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;

public class SchedulerView extends
		ReverseComposite<ISchedulerView.ISchedulerPresenter> implements
		ISchedulerView {

	interface SchedulerViewUiBinder extends UiBinder<VLayout, SchedulerView> {
	};

	private static SchedulerViewUiBinder uiBinder = GWT
			.create(SchedulerViewUiBinder.class);

	@UiField
	UIVLayout schedulerLayout;
	@UiField
	UIListGrid tasksListGrid;
	@UiField
	UIListGrid attempsListGrid;

	public SchedulerView() {
		uiBinder.createAndBindUi(this);

		setupTaskListGrid();
		setupAttempsListGrid();
	}

	private void setupTaskListGrid() {
		ListGridField taskIdField = new ListGridField(
				SchedulerTaskListField.TASKID.getValue(), "ID");
		ListGridField taskNameField = new ListGridField(
				SchedulerTaskListField.TASKNAME.getValue(), "名称");
		ListGridField executorField = new ListGridField(
				SchedulerTaskListField.EXECUTOR.getValue(), "调度人");
		ListGridField crontabField = new ListGridField(
				SchedulerTaskListField.CRONTAB.getValue(), "调度时间");
		ListGridField commandField = new ListGridField(
				SchedulerTaskListField.TASKCOMMAND.getValue(), "操作");
		tasksListGrid.setFields(taskIdField, taskNameField, executorField,
				crontabField, commandField);

	}

	private void setupAttempsListGrid() {
		ListGridField attemptidField = new ListGridField(
				SchedulerAttemptListField.ATTEMPTID.getValue(), "attemptid");
		ListGridField instanceidField = new ListGridField(
				SchedulerAttemptListField.INSTANCEID.getValue(), "instanceid");
		ListGridField starttimeField = new ListGridField(
				SchedulerAttemptListField.STARTTIME.getValue(), "启动时间");
		ListGridField endtimeField = new ListGridField(
				SchedulerAttemptListField.ENDTIME.getValue(), "结束时间");
		ListGridField returnValueField = new ListGridField(
				SchedulerAttemptListField.RETURNVALUE.getValue(), "返回值");
		ListGridField statusField = new ListGridField(
				SchedulerAttemptListField.STATUS.getValue(), "状态");
		ListGridField commandField = new ListGridField(
				SchedulerAttemptListField.COMMAND.getValue(), "操作");
		attempsListGrid.setFields(attemptidField, instanceidField, starttimeField,
				endtimeField, returnValueField, statusField, commandField);
	}

	@Override
	public Canvas asCanvas() {
		return schedulerLayout;
	}

	@Override
	public UIListGrid getTasksListGrid() {
		return tasksListGrid;
	}

	@Override
	public UIListGrid getAttempsListGrid() {
		return attempsListGrid;
	}

}
