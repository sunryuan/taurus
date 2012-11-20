package com.dp.bigdata.taurus.frontend.client.pool;

import com.dp.bigdata.taurus.frontend.client.common.IsCanvas;
import com.dp.bigdata.taurus.frontend.client.pool.presenter.PoolPresenter;
import com.mvp4g.client.annotation.Event;
import com.mvp4g.client.annotation.Events;
import com.mvp4g.client.event.EventBus;
import com.mvp4g.client.presenter.NoStartPresenter;

@Events(startPresenter = NoStartPresenter.class, module = PoolModule.class)
public interface PoolEventBus extends EventBus{
	@Event (handlers = PoolPresenter.class)
	public void loadPoolModule();
	
	@Event( forwardToParent = true )
	public void changePoolTab(IsCanvas isCanvas);
}
