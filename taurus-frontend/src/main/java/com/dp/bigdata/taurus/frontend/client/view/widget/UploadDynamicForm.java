package com.dp.bigdata.taurus.frontend.client.view.widget;

import com.smartgwt.client.types.Encoding;
import com.smartgwt.client.types.FormMethod;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;

public class UploadDynamicForm extends DynamicForm {
	
	public static final String TARGET = "upload_frame";

	private UploadListener listener;
	
	public UploadDynamicForm() {
		super();
		initComplete(this);
		ValuesManager vm = new ValuesManager();
		setValuesManager(vm);
		setEncoding(Encoding.MULTIPART);
		setMethod(FormMethod.POST);
		setTarget(TARGET);
		
//		NamedFrame frame = new NamedFrame(TARGET);
//		frame.setWidth("1");
//		frame.setHeight("1");
//		frame.setVisible(false);
	}

	public void setUploadListener(UploadListener listener) {
		this.listener = listener;
	}

	public void uploadComplete(String fileName) {
		if (listener != null)
			listener.uploadComplete(fileName);
	}

	private native void initComplete(UploadDynamicForm upload) /*-{
		$wnd.uploadComplete = function(fileName) {
			upload.@com.dp.bigdata.taurus.frontend.client.view.widget.UploadDynamicForm::uploadComplete(Ljava/lang/String;)(fileName);
		};
	}-*/;
}
