package com.dp.bigdata.taurus.frontend.client.pool.view.interfaces;

import org.synthful.smartgwt.client.widgets.UIListGrid;

import com.dp.bigdata.taurus.frontend.client.common.IsCanvas;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface IPoolView extends IsCanvas{
	interface IPoolPresenter {
	}
	
	public UIListGrid getPoolListGrid();
	
	public UIListGrid getHostListGrid();
	
	public ListGridField getPoolNameField();

	public ToolStripButton getRemoveButton();
}
