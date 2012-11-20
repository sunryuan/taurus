package com.dp.bigdata.taurus.frontend.client.task;

import com.dp.bigdata.taurus.frontend.client.task.presenter.TaskPresenter;
import com.mvp4g.client.annotation.Event;
import com.mvp4g.client.annotation.Events;
import com.mvp4g.client.event.EventBus;

@Events(startPresenter=TaskPresenter.class, module=TaskModule.class)
public interface TaskEventBus extends EventBus{
	
	@Event (handlers = TaskPresenter.class)
	public void loadTaskModule();
	
//	@Event (handlers = TaskPresenter.class)
//	public void changeTargetTaskTypePanel(int type);
}
