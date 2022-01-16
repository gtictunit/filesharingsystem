package com.gtict.app.ui;

import com.gtict.app.models.Archive_log;
import com.gtict.app.models.FileInfo;
import com.gtict.app.services.ArchiveLogService;
import com.gtict.app.services.FileInfoService;
import com.gtict.app.utilities.AppUtils;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.liferay.portal.kernel.model.User;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamVariable;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.AllUploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStatePanel;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.IOUtils;

@SuppressWarnings("deprecation")
public class UploadComponent extends ResponsiveLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7533341957982355099L;
	private String savedFileName = "";
//	private List<User> users = new ArrayList<User>();
	FileInfo fileinfo = new FileInfo();

	private static final Logger log = LoggerFactory.getLogger(UploadComponent.class);

	UploadComponent(String userPath, String owner, long userId, List<com.gtict.app.models.User> userList, boolean isPrivate, boolean isShared) {
		ResponsiveRow row2 = addRow().withMargin(true);
		row2.setSpacing(true);
		ResponsiveRow row3 = addRow().withMargin(true);
		row3.setSpacing(true);
		System.out.println("Users to share with::   " + userList.size());
		auditTrail(userList);

		UploadStateWindow uploadStateWindow = new UploadStateWindow();
		UploadFinishedHandler uploadFinishedHandler = (InputStream stream, String fileName, String mimeType,
				long length, int filesLeftInQueue) -> {
			System.out.println("IS UPLOADING FILES");
			InputStream stream2 = stream;
			File file = new File(userPath + "\\" + fileName);
			File fileShare = new File(AppUtils.getProperty("gt.ict.filesystemapp.sharedpath") + "\\" + fileName);
			savedFileName = fileName;
			Date date = new Date(Instant.now().toEpochMilli());
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_hhmmss");
			if (file.exists()) {
				savedFileName = sdf.format(date) + "_" + fileName;
				file = new File(userPath + "\\" + savedFileName);
			}
			if (fileShare.exists()) {
				savedFileName = sdf.format(date) + "_" + fileName;
				fileShare = new File(AppUtils.getProperty("gt.ict.filesystemapp.sharedpath") + "\\" + savedFileName);
			}

			try {
				OutputStream outStream = new FileOutputStream(file);
				OutputStream outStream2 = new FileOutputStream(fileShare);
				byte[] buffer = new byte[8 * 1024];
				int bytesRead;
				while ((bytesRead = stream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				if (isShared) {
					System.out.println("IS SHARED FILE");
					
					buffer = new byte[8 * 1024];
					while ((bytesRead = stream2.read(buffer)) != -1) {
						outStream2.write(buffer, 0, bytesRead);
					}
				}
				IOUtils.closeQuietly(stream);
				IOUtils.closeQuietly(stream2);
				IOUtils.closeQuietly(outStream);
				IOUtils.closeQuietly(outStream2);
				auditTrail(userPath, userId, isPrivate);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.debug(e.getLocalizedMessage());
			}
			UI.getCurrent().showNotification(
					fileName + " uploaded (" + length + " bytes). " + filesLeftInQueue + " files left.",
					Type.HUMANIZED_MESSAGE);

		};

		AllUploadFinishedHandler all = (() -> {
			Notification.show("File(s) upload completed", Type.HUMANIZED_MESSAGE);
//			UI.getCurrent().removeWindow(thisWindow);
		});

		SlowUpload m = new SlowUpload(uploadFinishedHandler, uploadStateWindow);
		m.setCaption("Add Files/Zip Folder");
		m.setPanelCaption("File Upload List");
//		List<String> mimeTypes = new ArrayList<String>();
//		mimeTypes.add("application/octet-stream");
//		mimeTypes.add("audio/*");
//		mimeTypes.add("video/*");
//		mimeTypes.add(".pdf");
//		mimeTypes.add(".doc");
//		mimeTypes.add(".docx");
//		mimeTypes.add(".txt");
//		mimeTypes.add("application/msword");
//		mimeTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
//		m.setAcceptedMimeTypes(mimeTypes);

		m.getSmartUpload().setUploadButtonCaptions("Upload File", "Upload Files");
		m.setMaxFileCount(20);
		m.setResponsive(true);
		m.setAllUploadFinishedHandler(all);
		Label dropLabel = new Label("Drop files here...");
		dropLabel.addStyleName(ValoTheme.LABEL_HUGE);
		Panel dropArea = new Panel(dropLabel);
		dropArea.setWidth("100%");
		dropArea.setHeight("400px");

		DragAndDropWrapper dragAndDropWrapper = m.createDropComponent(dropArea);
		dragAndDropWrapper.setResponsive(true);
		dragAndDropWrapper.setWidth("100%");
		dragAndDropWrapper.setHeight("400px");

		row2.addColumn().withDisplayRules(8, 0, 0, 0).withComponent(m);
		row3.addColumn().withDisplayRules(12, 0, 0, 0).withComponent(dragAndDropWrapper);
	}

	private class SlowUpload extends MultiFileUpload {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5282219851989552135L;

		public SlowUpload(UploadFinishedHandler uploadFinishedHandler, UploadStateWindow uploadStateWindow) {
			super(uploadFinishedHandler, uploadStateWindow);
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
				Thread.sleep((int) 100);
			} catch (InterruptedException ex) {
				log.error(ex.getMessage());
			}
			super.onProgress(event);
		}
	}

	public void auditTrail(List<com.gtict.app.models.User> users) {
			System.out.println("Users to share with (before Persisting)::   " + users.size());
			fileinfo.setSharedWithUsers(users);
		System.out.println("Users to share with (before Persisting - Stage 1)::   " + fileinfo.getSharedWithUsers().size());


	}
	
	public void auditTrail(String userPath, long userId, boolean isPrivate) {

		Archive_log archive_log = new Archive_log();
		fileinfo.setFilename(savedFileName);
		fileinfo.setFilepath(userPath);
		fileinfo.setUserId(userId);
		fileinfo.setPrivate(isPrivate);
		System.out.println("Users to share with (before Persisting - Stage 2)::   " + fileinfo.getSharedWithUsers().size());
		archive_log.setUserId(userId);
		archive_log.setFilename(savedFileName);
		archive_log.setFilepath(userPath);
		archive_log.setLog_date(new Date());
		archive_log.setAction_info("UPLOADED_NEW_FILE");

		FileInfoService.save(fileinfo);
		ArchiveLogService.save(archive_log);

	}
}
