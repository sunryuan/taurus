package com.dp.bigdata.taurus.frontend.client.pool.view;

import org.synthful.smartgwt.client.widgets.UIListGrid;

import com.dp.bigdata.taurus.frontend.client.pool.HostListField;
import com.dp.bigdata.taurus.frontend.client.pool.PoolListField;
import com.dp.bigdata.taurus.frontend.client.pool.view.interfaces.IPoolView;
import com.dp.bigdata.taurus.frontend.client.view.widget.ReverseComposite;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.smartgwt.client.types.ListGridComponent;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PoolView extends
	ReverseComposite<IPoolView.IPoolPresenter> implements IPoolView{

	interface PoolViewUiBinder extends UiBinder<HLayout, PoolView> {
	};

	private static PoolViewUiBinder uiBinder = GWT
			.create(PoolViewUiBinder.class);

	@UiField 
	HLayout poolLayout;
	@UiField 
	VLayout leftLayout;
	@UiField 
	VLayout rightLayout;

	UIListGrid hostListGrid;
	
	UIListGrid poolListGrid;

	ListGridField poolNameField;
	
	ToolStripButton removeButton;

	public PoolView() {
		uiBinder.createAndBindUi(this);
		setupPoolListGrid();
		setuphostListGrid();
	}
	
	private void setupPoolListGrid() {
		poolListGrid = new UIListGrid();
		poolListGrid.setWidth(200);
		ListGridField poolId = new ListGridField(
				PoolListField.PoolId.getValue(), "poolId");
		poolId.setHidden(true);
		ListGridField poolField = new ListGridField(
				PoolListField.PoolName.getValue(), "poolName");
		poolListGrid.setFields(poolId,poolField);
		poolListGrid.setCanEdit(true);
		poolListGrid.setEditByCell(true);
		poolListGrid.setEditEvent(ListGridEditEvent.DOUBLECLICK);  
		poolListGrid.setModalEditing(true); 
		ToolStrip gridEditControls = new ToolStrip();  
		gridEditControls.setWidth100();  
     	gridEditControls.setHeight(24);   
        
     	LayoutSpacer spacer = new LayoutSpacer();  
     	spacer.setWidth("*");
     	ToolStripButton addButton = new ToolStripButton();  
        addButton.setIcon("[SKIN]/actions/add.png");  
        addButton.setPrompt("add new pool");  
        addButton.addClickHandler(new ClickHandler() {  
            @Override  
            public void onClick(ClickEvent event) {  
                poolListGrid.startEditingNew(); 
            }  
        });  
          
        removeButton = new ToolStripButton();  
        removeButton.setIcon("[SKIN]/actions/remove.png");  
        removeButton.setPrompt("Remove selected record");  
        gridEditControls.setMembers(spacer,addButton,removeButton);
        
        poolListGrid.setGridComponents(new Object[] {  
                ListGridComponent.HEADER,   
                ListGridComponent.FILTER_EDITOR,   
                ListGridComponent.BODY,   
                gridEditControls  
        });  
        leftLayout.addMember(poolListGrid);
    }

	private void setuphostListGrid() {
		hostListGrid = new UIListGrid();
		hostListGrid.setHeight("80%");
		hostListGrid.setWidth(400);
		
		ListGridField idField = new ListGridField(
				HostListField.HostId.getValue(), "hostid");
		ListGridField connectField = new ListGridField(
				HostListField.IsConnected.getValue(), "isConnected");
		idField.setHidden(true);
		connectField.setHidden(true);
		ListGridField ipField = new ListGridField(
				HostListField.HostIp.getValue(), "hostip");
		poolNameField = new ListGridField(
				HostListField.PoolName.getValue(), "poolname");
		hostListGrid.setFields(idField,ipField, poolNameField,connectField);
		hostListGrid.setCanEdit(true);
		ipField.setCanEdit(false);
		//		poolListGrid.setAutoFetchData(true);  
		hostListGrid.setEditEvent(ListGridEditEvent.DOUBLECLICK);  
		hostListGrid.setModalEditing(true);  
		rightLayout.addMember(hostListGrid);
	}
	
	public ListGridField getPoolNameField(){
		return poolNameField;
	}

	@Override
	public Canvas asCanvas() {
		return poolLayout;
	}

	@Override
	public UIListGrid getPoolListGrid() {
		return poolListGrid;
	}

	@Override
	public UIListGrid getHostListGrid() {
		return hostListGrid;
	}

	@Override
	public ToolStripButton getRemoveButton() {
		return removeButton;
	}
	
}

