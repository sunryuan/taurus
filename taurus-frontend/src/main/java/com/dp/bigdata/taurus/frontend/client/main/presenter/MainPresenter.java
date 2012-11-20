package com.dp.bigdata.taurus.frontend.client.main.presenter;

import com.dp.bigdata.taurus.frontend.client.common.IsCanvas;
import com.dp.bigdata.taurus.frontend.client.main.MainEventBus;
import com.dp.bigdata.taurus.frontend.client.main.view.MainView;
import com.dp.bigdata.taurus.frontend.client.main.view.interfaces.IMainView;
import com.dp.bigdata.taurus.frontend.client.view.widget.LoginWindow;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.IsWidget;
import com.mvp4g.client.Mvp4gLoader;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.BasePresenter;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

@Presenter(view = MainView.class)
public class MainPresenter extends BasePresenter<IMainView, MainEventBus>
		implements Mvp4gLoader<MainEventBus> {

	@Override
	public void bind() {
		checkUserLoginState();
		
//		view.getWorkflowTab().getSmartObject().addTabSelectedHandler(new TabSelectedHandler() {
//
//			@Override
//			public void onTabSelected(TabSelectedEvent event) {
//				eventBus.loadWorkflowModule();
//			}
//		});
		
		view.getSchedulerTab().getSmartObject().addTabSelectedHandler(new TabSelectedHandler() {
			
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				eventBus.loadSchedulerModule();
			}
		});
		
		view.getPoolTab().getSmartObject().addTabSelectedHandler(new TabSelectedHandler() {
			
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				eventBus.loadPoolModule();
			}
		});
		
		view.getLoginButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (view.getLoginButton().getTitle().equals("登陆")){
					LoginWindow login = new LoginWindow(eventBus);
					login.show();
				}else{
					Cookies.removeCookie("user");
					view.getLoginButton().setTitle("登陆");
					view.getUserLoginInfo().setContents("");
					checkUserLoginState();
				}
			}
		});
	}
	
	private void checkUserLoginState(){
		if (Cookies.getCookie("user") == null){
			LoginWindow login = new LoginWindow(eventBus);
			login.show();
		}else{
			onChangeUserLoginState(Cookies.getCookie("user"));
		}
	}

	public void onChangeTaskTab(IsCanvas canvas) {
		System.out.println("onChangeTaskTab");
		view.setTaskTab(canvas.asCanvas());
	}

//	public void onChangeWorkflowTab(IsCanvas canvas) {
//		System.out.println("onChangeWorkflowTab");
//		view.setWorkflowTab(canvas.asCanvas());
//	}
//	
	public void onChangeSchedulerTab(IsCanvas canvas) {
		System.out.println("onChangeSchedulerTab");
		view.setSchedulerTab(canvas.asCanvas());
	}
	
	public void onChangePoolTab(IsCanvas canvas) {
		System.out.println("onChangePoolTab");
		view.setPoolTab(canvas.asCanvas());
	}

	public void onChangeBody(IsWidget canvas) {
		view.setBody(canvas.asWidget());
	}

	public void onChangeHeader(Canvas canvas) {
		view.setHeader(canvas);
	}

	public void onChangeFooter(Canvas canvas) {
		view.setFooter(canvas);
	}

	public void onChangeLeftNavigation(Canvas canvas) {
		view.setLeftNav(canvas);
	}

	public void onStart() {
		eventBus.loadTaskModule();
	}

	public void preLoad(MainEventBus eventBus, String eventName,
			Object[] params, Command load) {
		load.execute();
	}

	public void onSuccess(MainEventBus eventBus, String eventName,
			Object[] params) {
		System.out.print("on success");
	}

	public void onFailure(MainEventBus eventBus, String eventName,
			Object[] params, Throwable err) {
	}
	
	public void onChangeUserLoginState(String username){
		view.getUserLoginInfo().setContents(username + ",欢迎登陆系统");
		view.getLoginButton().setTitle("注销");
	}
}