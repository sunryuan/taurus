package com.dp.bigdata.taurus.frontend.client.task.view.interfaces;

import org.synthful.smartgwt.client.widgets.UIIButton;

import com.dp.bigdata.taurus.frontend.client.common.IsCanvas;
import com.dp.bigdata.taurus.frontend.client.view.widget.UploadDynamicForm;
import com.mvp4g.client.view.ReverseViewInterface;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.UploadItem;

public interface ITaskView extends
		ReverseViewInterface<ITaskView.ITaskPresenter>, IsCanvas {
	interface ITaskPresenter {
//		void onDeployButtonClick(String templateId);
	}

	UIIButton getAddTaskButton();
	
	UIIButton getClearTaskButton();

	HiddenItem getTaskIDItem();

	TextItem getTaskNameItem();

	SelectItem getTaskTypeItem();
	
	SelectItem getTaskPoolItem();

	UploadItem getTaskFileItem();

	UploadDynamicForm getTaskForm();
	
	TextItem getTaskCommandItem();

	TextItem getTaskCrontabItem();

	TextItem getTaskMailItem();

	TextItem getTaskProxyUserItem();

	TextItem getTaskExecItem();

	TextItem getTaskWaitItem();

	TextItem getTaskDependencyItem();

	TextItem getTaskRetryItem();
	
	TextItem getTaskMultiInstanceItem();

}
