//package com.dp.bigdata.taurus.frontend.client.workflow.presenter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.restlet.client.resource.Result;
//
//import com.dp.bigdata.taurus.common.dto.DependencyDTO;
//import com.dp.bigdata.taurus.common.dto.WorkflowDTO;
//import com.dp.bigdata.taurus.frontend.client.common.DependencyExpressionUtils;
//import com.dp.bigdata.taurus.frontend.client.service.ServiceApi;
//import com.dp.bigdata.taurus.frontend.client.service.WorkflowService;
//import com.dp.bigdata.taurus.frontend.client.service.WorkflowsService;
//import com.dp.bigdata.taurus.frontend.client.workflow.AddWorkflowListField;
//import com.dp.bigdata.taurus.frontend.client.workflow.WorkflowEventBus;
//import com.dp.bigdata.taurus.frontend.client.workflow.WorkflowListField;
//import com.dp.bigdata.taurus.frontend.client.workflow.view.WorkflowView;
//import com.dp.bigdata.taurus.frontend.client.workflow.view.interfaces.IWorkflowView;
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.user.client.Cookies;
//import com.mvp4g.client.annotation.Presenter;
//import com.mvp4g.client.presenter.BasePresenter;
//import com.smartgwt.client.util.SC;
//import com.smartgwt.client.widgets.events.ClickEvent;
//import com.smartgwt.client.widgets.events.ClickHandler;
//import com.smartgwt.client.widgets.grid.ListGridRecord;
//import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
//import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
//
//@Presenter(view = WorkflowView.class)
//public class WorkflowPresenter extends
//		BasePresenter<IWorkflowView, WorkflowEventBus> implements
//		IWorkflowView.IWorkflowPresenter {
//
//	private final WorkflowsService workflowsService = GWT
//			.create(WorkflowsService.class);
//	private final WorkflowService workflowService = GWT
//			.create(WorkflowService.class);
//	
//	public Boolean modifyWorkflowFlag =  false;
//
//	public void onLoadWorkflowModule() {
//		System.out.println("workflowpresenter on onLoadWorkflowModule");
//		eventBus.changeWorkflowTab(view);
//	}
//
//	@Override
//	public void bind() {
//		workflowsService.getClientResource().setReference(
//				ServiceApi.WORKFLOWSERVICE);
//		view.getAddWorkflow().addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				modifyWorkflowFlag = false;
//				view.getTaskName().setValue("workflow name");
//				view.getAddWorkflowListGrid().removeAll();
//			}
//		});
//		
//		retrieveWorkflows();
//		
//		view.getWorkflowListGrid().addRecordClickHandler(
//				new RecordClickHandler() {
//
//					@Override
//					public void onRecordClick(RecordClickEvent event) {
//						modifyWorkflowFlag = true;
//						view.getTaskName().setValue(event.getRecord().getAttribute(WorkflowListField.WORKFLOWNAME.getValue()));
//						final String workflowId = event.getRecord().getAttribute(
//										WorkflowListField.WORKFLOWID.getValue());
//						view.getWorkflowId().setValue(workflowId);
//						workflowService.getClientResource().setReference(
//								ServiceApi.WORKFLOWSERVICE + "/" + workflowId);
//						workflowService
//								.getDependency(new Result<ArrayList<DependencyDTO>>() {
//
//									@Override
//									public void onSuccess(
//											ArrayList<DependencyDTO> result) {
//										view.getAddWorkflowListGrid()
//												.removeAll();
//
//										for (DependencyDTO dependencyDTO : result) {
//											ListGridRecord rec = new ListGridRecord();
//											rec.setAttribute(
//													AddWorkflowListField.TASK
//															.getValue(),
//													dependencyDTO.getTaskName());
//											rec.setAttribute(AddWorkflowListField.DEPENDENTTASKSTATEXPR
//															.getValue(),
//													dependencyDTO
//															.getDependencyExpr());
//											view.getAddWorkflowListGrid()
//													.addData(rec);
//										}
//									}
//
//									@Override
//									public void onFailure(Throwable caught) {
//										System.out
//												.println("fail to get workflowid:"
//														+ workflowId
//														+ " dependency list");
//									}
//								});
//					}
//				});
//
//		view.getSaveWorkflow().addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				WorkflowDTO workflowDto = new WorkflowDTO();
//				if (view.getTaskName().getDisplayValue().equals("")) {
//					SC.say("请输入工作流名称");
//				}
//				workflowDto.setName(view.getTaskName().getDisplayValue());
//				workflowDto.setUserName(Cookies.getCookie("user"));
//				List<DependencyDTO> dependecies = new ArrayList<DependencyDTO>();
//				ListGridRecord[] recs = view.getAddWorkflowListGrid()
//						.getRecords();
//				for (int i = 0; i < recs.length; i++) {
//					DependencyDTO dep = new DependencyDTO();
//					String taskName = recs[i].getAttributeAsString(AddWorkflowListField.TASK.getValue());
//					String exp = recs[i].getAttributeAsString(AddWorkflowListField.DEPENDENTTASKSTATEXPR.getValue());
//					
//					if (taskName == null || exp == null || taskName.equals("") || !DependencyExpressionUtils.isLegalDependencyExp(exp)){
//						SC.say("不合法的作业名或作业依赖表达式");
//						return;
//					}
//					dep.setTaskName(taskName);
//					dep.setDependencyExpr(exp);
//					dependecies.add(dep);
//				}
//				workflowDto.setDependecies(dependecies);
//				if (modifyWorkflowFlag){
//					String workflowID = (String) view.getWorkflowId().getValue();
//					workflowService.getClientResource().setReference(
//							ServiceApi.WORKFLOWSERVICE + "/" + workflowID);
//					workflowDto.setWorkflowID(workflowID);
//					workflowService.update(workflowDto, new Result<Void>() {
//						
//						@Override
//						public void onSuccess(Void result) {
//							retrieveWorkflows();
//							SC.say("更新工作流成功");
//						}
//						
//						@Override
//						public void onFailure(Throwable caught) {
//							SC.say("更新工作流失败");
//						}
//					});
//					
//				}else{
//					workflowsService.create(workflowDto, new Result<Void>() {
//
//						@Override
//						public void onSuccess(Void result) {
//							retrieveWorkflows();
//							SC.say("上传工作流成功");
//						}
//
//						@Override
//						public void onFailure(Throwable caught) {
//							SC.say("上传工作流失败");
//						}
//					});
//				}
//			}
//		});
//	}
//
//	private void retrieveWorkflows() {
//		view.getWorkflowListGrid().removeAll();
//		workflowsService.retrieve(new Result<ArrayList<WorkflowDTO>>() {
//
//			@Override
//			public void onSuccess(ArrayList<WorkflowDTO> result) {
//				for (WorkflowDTO wf : result) {
//					ListGridRecord rec = new ListGridRecord();
//					rec.setAttribute(WorkflowListField.WORKFLOWID.getValue(),
//							wf.getWorkflowID());
//					rec.setAttribute(WorkflowListField.WORKFLOWNAME.getValue(),
//							wf.getName());
//					rec.setAttribute(WorkflowListField.CREATOR.getValue(),
//							wf.getUserName());
//					rec.setAttribute(WorkflowListField.CREATIONTIME.getValue(),
//							wf.getAddTime());
//					view.getWorkflowListGrid().addData(rec);
//				}
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//				System.out.println("fail to receive workflows");
//			}
//		});
//	}
//}
