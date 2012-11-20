package com.dp.bigdata.taurus.frontend.client.main;

import com.dp.bigdata.taurus.frontend.client.common.IsCanvas;
import com.dp.bigdata.taurus.frontend.client.main.presenter.MainPresenter;
import com.dp.bigdata.taurus.frontend.client.pool.PoolModule;
import com.dp.bigdata.taurus.frontend.client.scheduler.SchedulerModule;
import com.dp.bigdata.taurus.frontend.client.task.TaskModule;
import com.dp.bigdata.taurus.frontend.client.workflow.WorkflowModule;
import com.mvp4g.client.annotation.Debug;
import com.mvp4g.client.annotation.Event;
import com.mvp4g.client.annotation.Events;
import com.mvp4g.client.annotation.Start;
import com.mvp4g.client.annotation.Debug.LogLevel;
import com.mvp4g.client.annotation.module.ChildModule;
import com.mvp4g.client.annotation.module.ChildModules;
import com.mvp4g.client.annotation.module.DisplayChildModuleView;
import com.mvp4g.client.event.EventBus;
import com.smartgwt.client.widgets.Canvas;

@Events(startPresenter = MainPresenter.class)
@Debug (logLevel=LogLevel.DETAILED)
@ChildModules({@ChildModule(moduleClass = PoolModule.class, async = false , autoDisplay = false),
			   @ChildModule(moduleClass = SchedulerModule.class, async = false, autoDisplay = false),
			   @ChildModule(moduleClass = TaskModule.class, async = false)})
//			   @ChildModule(moduleClass = WorkflowModule.class, async = false, autoDisplay= false)})
public interface MainEventBus extends EventBus{
	
	@Event( handlers = MainPresenter.class )
	public void changeHeader(Canvas header);
	
	@Event( handlers = MainPresenter.class )
	public void changeFooter(Canvas footer);
	
	@Event( handlers = MainPresenter.class )
	public void changeLeftNavigation(Canvas leftNav);
	
	@Start
	@Event( handlers = MainPresenter.class)
	public void start();
	
	@Event(forwardToModules = TaskModule.class)
	public void loadTaskModule();
//	
//	@Event(forwardToModules = WorkflowModule.class)
//	public void loadWorkflowModule();
	
	@Event(forwardToModules = SchedulerModule.class)
	public void loadSchedulerModule();
	
	@Event(forwardToModules = PoolModule.class)
	public void loadPoolModule();
	
	@DisplayChildModuleView(TaskModule.class)
	@Event( handlers = MainPresenter.class )
	public void changeTaskTab(IsCanvas canvas);
	
//	@Event( handlers = MainPresenter.class )
//	public void changeWorkflowTab(IsCanvas canvas);
	
	@Event( handlers = MainPresenter.class )
	public void changeSchedulerTab(IsCanvas canvas);
	
	@Event( handlers = MainPresenter.class )
	public void changeUserLoginState(String username);
	
	@Event( handlers = MainPresenter.class )
	public void changePoolTab(IsCanvas canvas);

}
