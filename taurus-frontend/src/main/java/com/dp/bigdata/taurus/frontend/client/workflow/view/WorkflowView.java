//package com.dp.bigdata.taurus.frontend.client.workflow.view;
//
//import java.util.ArrayList;
//
//import org.restlet.client.resource.Result;
//import org.synthful.smartgwt.client.widgets.UIButtonItem;
//import org.synthful.smartgwt.client.widgets.UIHiddenItem;
//import org.synthful.smartgwt.client.widgets.UIIButton;
//import org.synthful.smartgwt.client.widgets.UIListGrid;
//import org.synthful.smartgwt.client.widgets.UITextItem;
//
//import com.dp.bigdata.taurus.common.dto.TaskDTO;
//import com.dp.bigdata.taurus.frontend.client.service.ServiceApi;
//import com.dp.bigdata.taurus.frontend.client.service.TaskService;
//import com.dp.bigdata.taurus.frontend.client.view.widget.ReverseComposite;
//import com.dp.bigdata.taurus.frontend.client.workflow.AddWorkflowListField;
//import com.dp.bigdata.taurus.frontend.client.workflow.WorkflowListField;
//import com.dp.bigdata.taurus.frontend.client.workflow.view.interfaces.IWorkflowView;
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.uibinder.client.UiBinder;
//import com.google.gwt.uibinder.client.UiField;
//import com.smartgwt.client.types.ListGridEditEvent;
//import com.smartgwt.client.widgets.Canvas;
//import com.smartgwt.client.widgets.events.ClickEvent;
//import com.smartgwt.client.widgets.events.ClickHandler;
//import com.smartgwt.client.widgets.grid.ListGridField;
//import com.smartgwt.client.widgets.grid.ListGridRecord;
//import com.smartgwt.client.widgets.layout.VLayout;
//
//public class WorkflowView extends
//		ReverseComposite<IWorkflowView.IWorkflowPresenter> implements
//		IWorkflowView {
//	interface WorkflowViewUiBinder extends UiBinder<VLayout, WorkflowView> {
//	};
//
//	private static WorkflowViewUiBinder uiBinder = GWT
//			.create(WorkflowViewUiBinder.class);
//	private final TaskService taskService = GWT.create(TaskService.class);
//
//	@UiField(provided = true)
//	UIListGrid workflowListGrid;
//	@UiField(provided = true)
//	UIListGrid addWorkflowListGrid;
//	@UiField
//	VLayout workflowLayout;
//	@UiField
//	UIIButton addWorkflow;
//	@UiField
//	UIIButton saveWorkflow;
//	@UiField
//	UIHiddenItem workflowId;
//	@UiField
//	UITextItem taskName;
//	@UiField
//	UIButtonItem addTask;
//
//	public WorkflowView() {
//		workflowListGrid = new UIListGrid();
//		ListGridField workflowIdField = new ListGridField(
//				WorkflowListField.WORKFLOWID.getValue(), "工作流ID");
//		ListGridField workflowNameField = new ListGridField(
//				WorkflowListField.WORKFLOWNAME.getValue(), "工作流名称");
//		ListGridField creatorField = new ListGridField(
//				WorkflowListField.CREATOR.getValue(), "创建人");
//		ListGridField creationTime = new ListGridField(
//				WorkflowListField.CREATIONTIME.getValue(), "创建时间");
//		ListGridField deleteField = new ListGridField(
//				WorkflowListField.DELETE.getValue(), "删除");
//		workflowListGrid.setFields(workflowIdField, workflowNameField,
//				creatorField, creationTime, deleteField);
//
//		setupAddWorkflowListGrid();
//		uiBinder.createAndBindUi(this);
//		bindActionHandler();
//	}
//
//	private void bindActionHandler() {
//		addTask.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
//
//			@Override
//			public void onClick(
//					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
//				ListGridRecord rec = new ListGridRecord();
//				addWorkflowListGrid.addData(rec);
//			}
//		});
//	}
//
//	private void setupAddWorkflowListGrid() {
//		addWorkflowListGrid = new UIListGrid();
//		addWorkflowListGrid.setCanEdit(true);
//		addWorkflowListGrid.setEditEvent(ListGridEditEvent.CLICK);
//		addWorkflowListGrid.setCanRemoveRecords(true);
//		addWorkflowListGrid.setShowAllRecords(true);
//		addWorkflowListGrid.setShowRowNumbers(true);
//
//		final ListGridField taskField = new ListGridField(
//				AddWorkflowListField.TASK.getValue(), "作业");
//		final ListGridField dependentTaskStatExprField = new ListGridField(
//				AddWorkflowListField.DEPENDENTTASKSTATEXPR.getValue(),
//				"依赖作业状态表达式");
//
//		taskService.getClientResource().setReference(ServiceApi.TASKSERVICE);
//		taskService.retrieve(new Result<ArrayList<TaskDTO>>() {
//
//			@Override
//			public void onSuccess(ArrayList<TaskDTO> result) {
//				System.out.println("get task dto successfully " + result.size());
//				String[] taskNames = new String[result.size()];
//				for (int i = 0; i < result.size(); i++) {
//					taskNames[i] = result.get(i).getName();
//					System.out.println(taskNames[i]);
//				}
//				taskField.setValueMap(taskNames);
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//				System.out.println("fail to get task dto");
//			}
//		});
//
//		addWorkflowListGrid.setFields(taskField, dependentTaskStatExprField);
//	}
//
//	@Override
//	public Canvas asCanvas() {
//		return workflowLayout;
//	}
//
//	@Override
//	public UIListGrid getWorkflowListGrid() {
//		return workflowListGrid;
//	}
//
//	@Override
//	public UIListGrid getAddWorkflowListGrid() {
//		return addWorkflowListGrid;
//	}
//
//	@Override
//	public UIIButton getSaveWorkflow() {
//		return saveWorkflow;
//	}
//
//	@Override
//	public UITextItem getTaskName() {
//		return taskName;
//	}
//
//	@Override
//	public UIIButton getAddWorkflow() {
//		return addWorkflow;
//	}
//
//	@Override
//	public UIHiddenItem getWorkflowId() {
//		return workflowId;
//	}
//}
