//package com.dp.bigdata.taurus.frontend.client.workflow;
//
//import com.dp.bigdata.taurus.frontend.client.common.IsCanvas;
//import com.dp.bigdata.taurus.frontend.client.workflow.presenter.WorkflowPresenter;
//import com.mvp4g.client.annotation.Event;
//import com.mvp4g.client.annotation.Events;
//import com.mvp4g.client.event.EventBus;
//import com.mvp4g.client.presenter.NoStartPresenter;
//
//@Events(startPresenter = NoStartPresenter.class, module = WorkflowModule.class)
//public interface WorkflowEventBus extends EventBus{
//	
//	@Event (handlers = WorkflowPresenter.class)
//	public void loadWorkflowModule();
//	
//	@Event( forwardToParent = true )
//	public void changeWorkflowTab(IsCanvas isCanvas);
//	
//}
