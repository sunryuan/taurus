package com.dp.bigdata.taurus.frontend.client.main.view;

import java.util.Collection;

import org.synthful.smartgwt.client.widgets.UITab;

import com.dp.bigdata.taurus.frontend.client.main.view.interfaces.IMainView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.TabSet;

public class MainView extends Canvas implements IMainView {

	interface MainViewUiBinder extends UiBinder<VLayout, MainView> {
	};

	private static MainViewUiBinder uiBinder = GWT
			.create(MainViewUiBinder.class);

	@UiField
	VLayout mainLayout;
	@UiField
	HLayout middleLayout;
	@UiField
	HLayout headerLayout;
	@UiField
	HLayout footerLayout;
	@UiField
	TabSet tabSet;
	@UiField
	UITab taskTab;
//	@UiField
//	UITab workflowTab;
	@UiField
	UITab poolTab;
	@UiField
	UITab schedulerTab;

	IButton loginButton;
	Label userLoginInfo;

	public void setTaskTab(Canvas canvas) {
		taskTab.setPane(canvas);
	}

//	public void setWorkflowTab(Canvas canvas) {
//		workflowTab.setPane(canvas);
//	}

	public void setPoolTab(Canvas canvas) {
		poolTab.setPane(canvas);
	}

	public void setSchedulerTab(Canvas canvas) {
		schedulerTab.setPane(canvas);
	}

	public MainView() {
		uiBinder.createAndBindUi(this);
		setupHeader();
		setupFooter();
		setupBody();

		refresh();
	}

	private void removeAllChildren(Layout layout) {
		Canvas[] children = layout.getChildren();
		for (int i = 0; i < children.length; i++) {
			layout.removeChild(children[i]);
		}
	}

	private void refresh() {
		mainLayout.draw();
	}

	public void setBody(Widget body) {
		removeAllChildren(middleLayout);
		middleLayout.addMember(body);
		refresh();
	}

	public void setupBody() {
	}

	public void setupHeader() {
		removeAllChildren(headerLayout);

		// headerLayout.setLayoutMargin(10);
		// headerLayout.setMembersMargin(10);

		Img dpLogo = new Img("/images/dianpinglogo.jpg");
		dpLogo.setImageType(ImageStyle.STRETCH);
		dpLogo.setAlign(Alignment.LEFT);
		dpLogo.setWidth("3%");

		final Label name = new Label();
		name.setWidth(700);
		name.setContents("海量数据作业平台管理系统");
		name.setOverflow(Overflow.HIDDEN);
		name.setAlign(Alignment.CENTER);
		name.setWidth("72%");
		name.setStyleName("logoName");

		Collection<String> cks = Cookies.getCookieNames();
		for (String ck : cks) {
			System.out.println(ck);
			System.out.println(Cookies.getCookie(ck));
		}

		userLoginInfo = new Label();
		userLoginInfo.setWidth(150);
		// userLoginInfo.setWidth("10%");
		loginButton = new IButton("登陆");
		// loginButton.setWidth("8%");

		// HLayout vStack = new VLayout();
		// vStack.setWidth("10%");
		// vStack.setMembersMargin(1);
		// vStack.addMember(loginButton);
		// vStack.addMember(userLoginInfo);
		// vStack.setHeight(50);

		headerLayout.addMember(dpLogo);
		headerLayout.addMember(name);
		headerLayout.addMember(userLoginInfo);
		headerLayout.addMember(loginButton);
	}

	public void setupFooter() {
		removeAllChildren(footerLayout);
		Label footerLabel = new Label();
		footerLabel
				.setContents("©2003-2012 dianping.com, All Rights Reserved.");
		footerLabel.setAlign(Alignment.CENTER);
		footerLabel.setWidth100();
		footerLayout.addMember(footerLabel);
	}

	public void setLeftNav(Canvas leftNav) {
	}

	public void setFooter(Canvas footer) {
		// TODO Auto-generated method stub

	}

	public void setHeader(Canvas header) {
		// TODO Auto-generated method stub
	}

	@Override
	public UITab getTaskTab() {
		return taskTab;
	}

//	@Override
//	public UITab getWorkflowTab() {
//		return workflowTab;
//	}

	@Override
	public UITab getPoolTab() {
		return poolTab;
	}

	@Override
	public UITab getSchedulerTab() {
		return schedulerTab;
	}

	@Override
	public IButton getLoginButton() {
		return loginButton;
	}

	@Override
	public Label getUserLoginInfo() {
		return userLoginInfo;
	}
}
