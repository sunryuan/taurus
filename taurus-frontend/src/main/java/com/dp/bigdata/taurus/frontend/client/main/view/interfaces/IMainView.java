package com.dp.bigdata.taurus.frontend.client.main.view.interfaces;

import org.synthful.smartgwt.client.widgets.UITab;

import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;

public interface IMainView {

	public void setBody(Widget body);

	public void setHeader(Canvas header);

	public void setFooter(Canvas footer);

	public void setLeftNav(Canvas leftNav);

	public void setTaskTab(Canvas canvas);

//	public void setWorkflowTab(Canvas canvas);
	
	public void setSchedulerTab(Canvas canvas);
	
	public void setPoolTab(Canvas canvas);

	public UITab getTaskTab();

//	public UITab getWorkflowTab();

	public UITab getPoolTab();

	public UITab getSchedulerTab();
	
	public IButton getLoginButton();
	
	public Label getUserLoginInfo();

}
