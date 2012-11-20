package com.dp.bigdata.taurus.frontend.client.pool;

/* 
 * Smart GWT (GWT for SmartClient) 
 * Copyright 2008 and beyond, Isomorphic Software, Inc. 
 * 
 * Smart GWT is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License version 3 
 * as published by the Free Software Foundation.  Smart GWT is also 
 * available under typical commercial license terms - see 
 * http://smartclient.com/license 
 * 
 * This software is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * Lesser General Public License for more details. 
 */  
  
import com.smartgwt.client.data.RecordList;  
import com.smartgwt.client.data.ResultSet;  
import com.smartgwt.client.data.events.DataChangedEvent;  
import com.smartgwt.client.data.events.DataChangedHandler;  
import com.smartgwt.client.types.ListGridComponent;  
import com.smartgwt.client.types.ListGridEditEvent;  
import com.smartgwt.client.widgets.Canvas;  
import com.smartgwt.client.widgets.Label;  
import com.smartgwt.client.widgets.events.ClickEvent;  
import com.smartgwt.client.widgets.events.ClickHandler;  
import com.smartgwt.client.widgets.grid.ListGrid;  
import com.smartgwt.client.widgets.grid.ListGridField;  
import com.smartgwt.client.widgets.grid.ListGridRecord;  
import com.smartgwt.client.widgets.layout.LayoutSpacer;  
import com.smartgwt.client.widgets.toolbar.ToolStrip;  
import com.smartgwt.client.widgets.toolbar.ToolStripButton;  
//import com.smartgwt.sample.showcase.client.data.CountryXmlDS;  
  
import com.google.gwt.core.client.EntryPoint;  
  
public class GridComponentsSample implements EntryPoint {  
  
      
    private ListGrid countryGrid;  
    private Label totalsLabel;  
    
    public static void main(String []args) {
    	for(int i = 1; i< 100; i++){
    		cc(i);
    	}
    	
    }
    private static void cc(  int num)
    { 
	      System.out.print(num+":\t");

      
      while (num!=1) {
    	  if (num%2==0) {
    	      num/=2;
    	      System.out.print(num);

    	  } else {
    	      num=num*3+1;
    	      System.out.print(num);
    	  }
	      System.out.print("\t");

      }
      System.out.println();

    }
    
    public void onModuleLoad() {  
        ToolStrip gridEditControls = new ToolStrip();  
        gridEditControls.setWidth100();  
        gridEditControls.setHeight(24);  
          
        totalsLabel = new Label();  
        totalsLabel.setPadding(5);  
          
        LayoutSpacer spacer = new LayoutSpacer();  
        spacer.setWidth("*");  
          
        ToolStripButton editButton = new ToolStripButton();  
        editButton.setIcon("[SKIN]/actions/edit.png");  
        editButton.setPrompt("Edit selected record");  
        editButton.addClickHandler(new ClickHandler() {  
              
            @Override  
            public void onClick(ClickEvent event) {  
                ListGridRecord record = countryGrid.getSelectedRecord();  
                if (record == null) return;  
                countryGrid.startEditing(countryGrid.getDataAsRecordList().indexOf(record), 0, false);  
                  
            }  
        });  
          
        ToolStripButton removeButton = new ToolStripButton();  
        removeButton.setIcon("[SKIN]/actions/remove.png");  
        removeButton.setPrompt("Remove selected record");  
        removeButton.addClickHandler(new ClickHandler() {  
              
            @Override  
            public void onClick(ClickEvent event) {  
                countryGrid.removeSelectedData();  
                  
            }  
        });  
          
        gridEditControls.setMembers(totalsLabel, spacer, editButton, removeButton);  
          
        ListGridField countryName = new ListGridField("countryName");  
        ListGridField capital = new ListGridField("capital");  
        ListGridField continent = new ListGridField("continent");  
        ListGridField independence = new ListGridField("independence");  
  
        countryGrid = new ListGrid();  
        countryGrid.setFields(new ListGridField[] { countryName, capital, continent, independence });  
        countryGrid.setWidth(500);  
        countryGrid.setHeight100();  
//        countryGrid.setDataSource(CountryXmlDS.getInstance());  
        countryGrid.setAutoFetchData(true);  
        countryGrid.setShowFilterEditor(true);  
        countryGrid.setCanEdit(true);  
        countryGrid.setEditEvent(ListGridEditEvent.NONE);  
          
        ResultSet dataProperties = new ResultSet();  
        dataProperties.addDataChangedHandler(new DataChangedHandler() {  
              
            @Override  
            public void onDataChanged(DataChangedEvent event) {  
                RecordList data = countryGrid.getDataAsRecordList();  
                  
                if (data != null && data instanceof ResultSet && ((ResultSet)data).lengthIsKnown() && data.getLength() > 0) {  
                    totalsLabel.setContents(data.getLength() + " Records");  
                } else {  
                    totalsLabel.setContents(" ");  
                }  
            }  
        });  
        countryGrid.setDataProperties(dataProperties);  
          
        countryGrid.setGridComponents(new Object[] {  
                ListGridComponent.HEADER,   
                ListGridComponent.FILTER_EDITOR,   
                ListGridComponent.BODY,   
                gridEditControls  
        });  
          
        countryGrid.draw();  
    }  
  
}  