package com.dp.bigdata.taurus.frontend.client.view.widget;

import java.util.Date;

import org.restlet.client.resource.Result;

import com.dp.bigdata.taurus.frontend.client.main.MainEventBus;
import com.dp.bigdata.taurus.frontend.client.service.ServiceApi;
import com.dp.bigdata.taurus.frontend.client.service.UserService;
import com.dp.bigdata.taurus.restlet.shared.UserDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

public class LoginWindow extends Window {
	private final UserService userService = GWT.create(UserService.class);	
	
	public LoginWindow(final MainEventBus eventBus) {
		super();
		
		setWidth(360);
		setHeight(130);
		setTitle("登陆");
		setShowMinimizeButton(false);
		setIsModal(true);
		setShowModalMask(true);
		setShowCloseButton(false);
		centerInPage();

//		addCloseClickHandler(new CloseClickHandler() {
//			public void onCloseClick(CloseClickEvent event) {
//				destroy();
//			}
//		});

		DynamicForm form = new DynamicForm();
		//form.setHeight100();
		form.setWidth100();
		form.setPadding(5);
		form.setLayoutAlign(VerticalAlignment.CENTER);
		final TextItem usernameItem = new TextItem("用户名");
		final PasswordItem passwordItem = new PasswordItem("密码");
		SubmitItem submitItem = new SubmitItem("submit", "提交");
		form.setFields(usernameItem, passwordItem, submitItem);
		//form.setCanSubmit(false);
		
		userService.getClientResource().setReference(
				ServiceApi.USERSERVICE);
		submitItem.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String username = usernameItem.getValueAsString();
				String password = passwordItem.getValueAsString();
				if (username == null || password == null || username.equals("")
						|| password.equals("")) {
					SC.say("用户名或密码不能为空");
				} else {
					final UserDTO user = new UserDTO(username, password);
					userService.logon(user, new Result<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							Date now = new Date();
							long nowLong = new Date().getTime();
							nowLong = nowLong + (1000 * 60 * 60 * 24 * 1);//one day
							now.setTime(nowLong);
							Cookies.setCookie("user", user.getName(), now);
							eventBus.changeUserLoginState(user.getName());
							destroy();
						}
						
						@Override
						public void onFailure(Throwable caught) {
							SC.say("	登陆失败,请重新输入用户名和密码");
						}
					});
				}
			}
		});
		addItem(form);
	}

}
