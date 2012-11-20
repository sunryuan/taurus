package com.dp.bigdata.taurus.frontend.client.scheduler.view.interfaces;

import org.synthful.smartgwt.client.widgets.UIListGrid;

import com.dp.bigdata.taurus.frontend.client.common.IsCanvas;


public interface ISchedulerView  extends IsCanvas{
	interface ISchedulerPresenter {
	}
	
	public UIListGrid getTasksListGrid();
	
	public UIListGrid getAttempsListGrid();
}
