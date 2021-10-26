package com.gtict.app.ui;

import com.gtict.app.utilities.AppUtils;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStatePanel;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

@SuppressWarnings("deprecation")
public class UploadComponent extends ResponsiveLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7533341957982355099L;
	
	UploadComponent(String userPath) {
		ResponsiveRow row2 = addRow().withMargin(true);
		row2.setSpacing(true);
		
        UploadStateWindow uploadStateWindow = new UploadStateWindow();
        UploadFinishedHandler uploadFinishedHandler = (InputStream stream, String fileName, String mimeType, long length,
				int filesLeftInQueue) -> {

			File file = new File(AppUtils.getProperty("gt.ict.filesystemapp.rootpath")+userPath+"\\" +fileName);
			try {
				OutputStream outStream = new FileOutputStream(file);
				byte[] buffer = new byte[8 * 1024];
				int bytesRead;
				while ((bytesRead = stream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				IOUtils.closeQuietly(stream);
				IOUtils.closeQuietly(outStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Notification.show(fileName + " uploaded (" + length + " bytes). " + filesLeftInQueue + " files left.");
		};

		SlowUpload m = new SlowUpload(uploadFinishedHandler,uploadStateWindow);
        m.setCaption("Add Files/Zip Folder");
        m.setPanelCaption("File Upload List");
        m.getSmartUpload().setUploadButtonCaptions("Upload File", "Upload Files");
        m.getSmartUpload().setUploadButtonIcon(FontAwesome.UPLOAD);
        m.setMaxFileCount(20);
		Label dropLabel = new Label("Drop files here...");
		dropLabel.addStyleName(ValoTheme.LABEL_HUGE);
		Panel dropArea = new Panel(dropLabel);
		dropArea.setWidth(300, Unit.PIXELS);
		dropArea.setHeight(150, Unit.PIXELS);

		DragAndDropWrapper dragAndDropWrapper = m.createDropComponent(dropArea);
		dragAndDropWrapper.setSizeUndefined();
		
		row2.addColumn().withDisplayRules(4, 0, 0, 0).withComponent(m);
		row2.addColumn().withDisplayRules(8, 0, 0, 0).withComponent(dragAndDropWrapper);
	}
	
	private class SlowUpload extends MultiFileUpload {

		public SlowUpload(UploadFinishedHandler uploadFinishedHandler, UploadStateWindow uploadStateWindow) {
			super(uploadFinishedHandler, uploadStateWindow);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected UploadStatePanel createStatePanel(UploadStateWindow uploadStateWindow) {
			return new SlowUploadStatePanel(uploadStateWindow);
		}
	}
	
	private class SlowUploadStatePanel extends UploadStatePanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5918188829512314671L;

		public SlowUploadStatePanel(UploadStateWindow window) {
			super(window);
		}

		@Override
		public void onProgress(StreamVariable.StreamingProgressEvent event) {
			try {
				Thread.sleep((int) 200);
			} catch (InterruptedException ex) {
				Logger.getLogger(UploadComponent.class.getName()).log(Level.SEVERE, null, ex);
			}
			super.onProgress(event);
		}
	}
}