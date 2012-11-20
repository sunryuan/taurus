package com.dp.bigdata.taurus.frontend.client.pool.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.restlet.client.resource.Result;

import com.dp.bigdata.taurus.restlet.shared.HostDTO;
import com.dp.bigdata.taurus.restlet.shared.PoolDTO;
import com.dp.bigdata.taurus.frontend.client.pool.HostListField;
import com.dp.bigdata.taurus.frontend.client.pool.PoolEventBus;
import com.dp.bigdata.taurus.frontend.client.pool.PoolListField;
import com.dp.bigdata.taurus.frontend.client.pool.view.PoolView;
import com.dp.bigdata.taurus.frontend.client.pool.view.interfaces.IPoolView;
import com.dp.bigdata.taurus.frontend.client.service.HostService;
import com.dp.bigdata.taurus.frontend.client.service.HostsService;
import com.dp.bigdata.taurus.frontend.client.service.PoolsService;
import com.dp.bigdata.taurus.frontend.client.service.PoolService;
import com.dp.bigdata.taurus.frontend.client.service.ServiceApi;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.BasePresenter;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;

@Presenter(view = PoolView.class)
public class PoolPresenter  extends
		BasePresenter<IPoolView, PoolEventBus> implements
		IPoolView.IPoolPresenter {
	
	private Map<Integer,String> poolIdtoNameMap = new HashMap<Integer,String>();
	
	public void onLoadPoolModule() {
		System.out.println("Pool Presenter onLoadPoolModule");
		eventBus.changePoolTab(view);
	}
	
	private final PoolService poolService = GWT.create(PoolService.class);
	private final PoolsService poolsService = GWT.create(PoolsService.class);
	private final HostService hostService = GWT.create(HostService.class);
	private final HostsService hostsService = GWT.create(HostsService.class);

	private void retrievePoolNames() {
		poolsService.getClientResource().setReference(ServiceApi.POOLSSERVICE);
		poolsService.retrieve(new Result<ArrayList<PoolDTO>>() {
			public void onFailure(Throwable caught) {
				System.out.println("fail to receive pool names");
				caught.printStackTrace();

			}

			public void onSuccess(ArrayList<PoolDTO> result) {
				String[] poolNames = new String[result.size()];
				poolIdtoNameMap.clear();
				for (int i = 0; i < result.size(); i++) {
					poolNames[i] = result.get(i).getName();
					poolIdtoNameMap.put(result.get(i).getId(), poolNames[i]);
				}
				view.getPoolNameField().setValueMap(poolNames);
				view.getPoolListGrid().removeAll();
				if (result != null && result.size() > 0){
					for (int i = 0; i < result.size(); i++){
						PoolDTO poolDto = result.get(i);
						ListGridRecord rec = new ListGridRecord();
						rec.setAttribute(PoolListField.PoolId.getValue(),poolDto.getId());
						rec.setAttribute(PoolListField.PoolName.getValue(),poolDto.getName());
						view.getPoolListGrid().addData(rec);
					}
				}
			}
		});
		
	}


	@Override
	public void bind() {
		retrievePoolNames();
		addCellSavedHandler();
		addRemoveHandler();
		addClickHandler();	
	}
	
	private void addRemoveHandler() {
		view.getRemoveButton().addClickHandler(new ClickHandler() {
			@Override  
			public void onClick(ClickEvent event) {
				String poolID = view.getPoolListGrid().getSelectedRecord()
						.getAttribute(PoolListField.PoolId.getValue());
				poolService.getClientResource().setReference(ServiceApi.POOLSERVICE+poolID);
				poolService.remove(new Result<Void>(){
					@Override
					public void onFailure(Throwable caught) {
						retrievePoolNames();
						SC.say("删除pool失败！");
						caught.printStackTrace();
					}
					@Override
					public void onSuccess(Void result) {
						retrievePoolNames();
						SC.say("删除pool成功！");
					}	
				});
	        }  
		});
	}
	
	private void addCellSavedHandler(){
		//pool cell saved handler
		view.getPoolListGrid().addCellSavedHandler(new CellSavedHandler() {
			@Override
			public void onCellSaved(final CellSavedEvent event) {
				Record rec = event.getRecord();
				String poolID = rec.getAttribute(PoolListField.PoolId.getValue());
				String poolName = rec.getAttribute(PoolListField.PoolName.getValue());
				String creator = Cookies.getCookie("user");
				PoolDTO poolDto = null;
				
				if(poolID == null){
					poolDto = new PoolDTO(0, poolName, creator);
					poolsService.create(poolDto, new Result<Void>(){
						@Override
						public void onFailure(Throwable caught) {
							retrievePoolNames();
							SC.say("添加pool失败！");
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(Void result) {
							retrievePoolNames();
							SC.say("添加pool成功！");
							
						}
					});
					
				} else {
					int id = Integer.parseInt(poolID);
					poolDto = new PoolDTO(id, poolName, creator);
					poolsService.update(poolDto, new Result<Void>(){
						@Override
						public void onFailure(Throwable caught) {
							retrievePoolNames();
							SC.say("修改pool失败！");
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(Void result) {
							retrievePoolNames();
							SC.say("修改pool成功！");
						}
					});
				}

			}
			
		});
		//host cell saved handler
		view.getHostListGrid().addCellSavedHandler(new CellSavedHandler() {
			@Override
			public void onCellSaved(final CellSavedEvent event) {
				Record rec = event.getRecord();
				String poolID = rec.getAttribute(HostListField.HostIp.getValue());
				String poolName = rec.getAttribute(PoolListField.PoolName.getValue());
				String creator = Cookies.getCookie("user");
				PoolDTO poolDto = null;

				if(poolID == null){
					poolDto = new PoolDTO(0, poolName, creator);
					poolsService.create(poolDto, new Result<Void>(){
						@Override
						public void onFailure(Throwable caught) {
							retrievePoolNames();
							SC.say("添加pool失败！");
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(Void result) {
							retrievePoolNames();
							SC.say("添加pool成功！");
							
						}
					});
					
				} else {
					int id = Integer.parseInt(poolID);
					poolDto = new PoolDTO(id, poolName, creator);
					poolsService.update(poolDto, new Result<Void>(){
						@Override
						public void onFailure(Throwable caught) {
							retrievePoolNames();
							SC.say("修改pool失败！");
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(Void result) {
							retrievePoolNames();
							SC.say("修改pool成功！");
						}
					});
				}

			}
			
		});
	}
	
	private void addClickHandler() {
		view.getPoolListGrid().addRecordClickHandler(new RecordClickHandler() {

			@Override
			public void onRecordClick(RecordClickEvent event) {
				view.getHostListGrid().removeAll();
				Record rec = event.getRecord();
				String poolID = rec.getAttribute(PoolListField.PoolId.getValue());
				if (poolID != null && !"".equals(poolID)){
					hostsService.getClientResource().setReference(ServiceApi.HOSTSERVICE + poolID);
					hostsService.retrieve(new Result<ArrayList<HostDTO>>() {
						
						@Override
						public void onSuccess(ArrayList<HostDTO> result) {
							if (result != null && result.size() > 0){
								for (int i = 0; i < result.size(); i++){
									HostDTO hostDto = result.get(i);
									ListGridRecord rec = new ListGridRecord();
									rec.setAttribute(HostListField.HostId.getValue(), hostDto.getId());
									rec.setAttribute(HostListField.HostIp.getValue(), hostDto.getIp());
									rec.setAttribute(HostListField.PoolName.getValue(), poolIdtoNameMap.get(hostDto.getPoolid()));
									rec.setAttribute(HostListField.IsConnected.getValue(), hostDto.isConnected());
									if(hostDto.isConnected()) {
										rec.setAttribute("STYLE", "background-color:#FF0000;");

									}
									System.out.println("!!!"+hostDto.toString());
									view.getHostListGrid().addData(rec);
									
									
								}
							}
						}
						
						@Override
						public void onFailure(Throwable caught) {
							System.out.println("fail to get hosts");
							caught.printStackTrace();
						}
					});
				}
			}
			
		});
	}
}
