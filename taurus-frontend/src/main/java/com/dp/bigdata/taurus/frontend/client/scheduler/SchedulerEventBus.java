package com.dp.bigdata.taurus.frontend.client.scheduler;

import com.dp.bigdata.taurus.frontend.client.common.IsCanvas;
import com.dp.bigdata.taurus.frontend.client.scheduler.presenter.SchedulerPresenter;
import com.mvp4g.client.annotation.Event;
import com.mvp4g.client.annotation.Events;
import com.mvp4g.client.event.EventBus;
import com.mvp4g.client.presenter.NoStartPresenter;

@Events(startPresenter = NoStartPresenter.class, module = SchedulerModule.class)
public interface SchedulerEventBus extends EventBus{
	@Event (handlers = SchedulerPresenter.class)
	public void loadSchedulerModule();
	
	@Event( forwardToParent = true )
	public void changeSchedulerTab(IsCanvas isCanvas);

}
