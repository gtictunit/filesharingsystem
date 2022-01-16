package com.gtict.app.ui;

import com.gtict.app.models.Archive_log;
import com.gtict.app.models.FileInfo;
import com.gtict.app.services.ArchiveLogService;
import com.gtict.app.services.FileInfoService;
import com.gtict.app.utilities.SplittableInputStream;
//import com.gtict.app.ui.UploadComponent.SlowUpload;
//import com.gtict.app.ui.UploadComponent.SlowUploadStatePanel;
import com.gtict.app.utilities.AppUtils;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamVariable;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.AllUploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStatePanel;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class UploadInterface extends ResponsiveLayout {

	boolean isShared = false;
	
	private String savedFileName = "";
	/**
	 * 
	 */
	private static final long serialVersionUID = 2225524318218992648L;
	
	private static final Logger log = LoggerFactory.getLogger(UploadComponent.class);
	List<com.gtict.app.models.User> userShare = new ArrayList<com.gtict.app.models.User>();

	UploadInterface(String userPath, User userId, long roleid) {
		setResponsive(true);
		System.out.printf("userid:: " + userId);
		System.out.printf("roleid:: " + roleid);
		List<User> getUsers = UserLocalServiceUtil.getRoleUsers(roleid);
	
		//BUILD MAIN LAYOUT
		ResponsiveRow main = addRow().withMargin(true);
		main.setSpacing(true);		
		ResponsiveLayout first = new ResponsiveLayout();
		ResponsiveLayout second = new ResponsiveLayout();		
		main.addColumn().withDisplayRules(6, 0, 0, 0).withComponent(first);
		main.addColumn().withDisplayRules(6, 0, 0, 0).withComponent(second);
		
		
		//RESPONSIVE ROWS
		ResponsiveRow row3 = first.addRow().withMargin(true);
 		row3.setSpacing(true);
		ResponsiveRow row4 = first.addRow().withMargin(true);
 		row4.setSpacing(true);
		ResponsiveRow row5 = first.addRow().withMargin(true);
		row5.setSpacing(true);
		ResponsiveRow row6 = first.addRow().withMargin(true);
		row6.setSpacing(true);	
		
 		//BUILD THE FUNCTIONAL INTERFACE
		ComboBox<User> sharedUsers = new ComboBox<User>("Share with:");
		CheckBox isPrivate = new CheckBox("Private");
		VerticalLayout lUsrs = new VerticalLayout();
		lUsrs.setCaption("File Shared With:");		
		Button addFiles = new Button("Upload Files");
		addFiles.setIcon(FontAwesome.PLUS_CIRCLE);

		
		//ADD FUNCTIONALITY		
		row3.addColumn().withDisplayRules(12, 0, 0, 0).withComponent(isPrivate);
		isPrivate.addValueChangeListener(listener->{
			sharedUsers.setEnabled(!listener.getValue());
			sharedUsers.clear();
			lUsrs.removeAllComponents();
			userShare.clear();
		});



		
		sharedUsers.setItems(getUsers);
		sharedUsers.setItemCaptionGenerator(User::getFullName);
		sharedUsers.addSelectionListener(u -> {
			if (u.getSelectedItem().isPresent()) {
				System.out.println("GET Share User value "+ u.getValue().getFullName());
				com.gtict.app.models.User usr = AppUtils.maptoKeyUser(u.getValue());
				if (!userShare.contains(usr)) {
					System.out.println("ADDING USER");
					HorizontalLayout lUsr = new HorizontalLayout();
					lUsr.setSizeFull();
					lUsr.setSpacing(true);
					Button clearUsrs = new Button();
					clearUsrs.setWidth("50px");
					clearUsrs.setHeight("25px");
					lUsr.addComponent(new Label(u.getValue().getFullName().toUpperCase()));
					lUsr.addComponent(clearUsrs);
					clearUsrs.setCaptionAsHtml(true);
					clearUsrs.setCaption("<div class=\"iconblock\"><img src=\"https://img.icons8.com/glyph-neue/20/000000/close-window.png\"/></div>");
					clearUsrs.addClickListener(listener -> {
						userShare.remove(usr);
						System.out.println("NO. Of Users After removing"+userShare.size());
						lUsrs.removeComponent(lUsr);
					});
					userShare.add(usr);
					System.out.println("NO. Of Users "+userShare.size());
					lUsrs.addComponent(lUsr);
				}
			}
		});
		row4.addColumn().withDisplayRules(4, 0, 0, 0).withComponent(sharedUsers);
		row5.addColumn().withDisplayRules(8, 0, 0, 0).withComponent(lUsrs);



		addFiles.addClickListener(listener -> {
			System.out.println("Size of Users::::>  "+userShare.size());
			if(!userShare.isEmpty())
				isShared = true;
			isPrivate.setValue(false);
			ResponsiveRow row = second.addRow().withMargin(true);
			row.setSpacing(true);	
			row.addColumn().withDisplayRules(12, 0, 0, 0).withComponent(UploadComponent(userPath, userId.getFullName(), userId.getUserId(), userShare, isPrivate.getValue(),isShared));
			addFiles.setEnabled(false);
			sharedUsers.setEnabled(false);

			Button finished = new Button("Finish");
			finished.addClickListener(l->{
				second.removeAllComponents();
				userShare.clear();
				isPrivate.setValue(false);
				sharedUsers.clear();
				addFiles.setEnabled(true);
				sharedUsers.setEnabled(true);
				lUsrs.removeAllComponents();
			});
			row.addColumn().withDisplayRules(12, 0, 0, 0).withComponent(finished);
		});
		row6.addColumn().withDisplayRules(12, 0, 0, 0).withComponent(addFiles);
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
	
	public void auditTrail(String userPath, String owner, long userId, boolean isPrivate,List<com.gtict.app.models.User> users, long length, int filesLeftInQueue) {
		FileInfo fileinfo = new FileInfo();
		fileinfo.setSharedWithUsers(users);
		Archive_log archive_log = new Archive_log();
		
		fileinfo.setFilename(savedFileName);
		fileinfo.setFilepath(userPath);
		fileinfo.setUserId(userId);
		fileinfo.setPrivate(isPrivate);
		fileinfo.setOwner(owner);	
		fileinfo.setCreatedDate(new Date());
//		System.out.println("Users to share with (before Persisting - Stage 2)::   " + fileinfo.getSharedWithUsers().size());
		
		archive_log.setUserId(userId);
		archive_log.setFilename(savedFileName);
		archive_log.setFilepath(userPath);
		archive_log.setLog_date(new Date());
		archive_log.setAction_info("UPLOADED_NEW_FILE");

		FileInfoService.save(fileinfo);
		ArchiveLogService.save(archive_log);
		Notification.show(
				savedFileName + " uploaded (" + length + " bytes). " + filesLeftInQueue + " files left.",
				Type.HUMANIZED_MESSAGE);

	}
	
	
	
	VerticalLayout UploadComponent(String userPath, String owner, long userId, List<com.gtict.app.models.User> userList, boolean isPrivate, boolean isShared) {
		VerticalLayout VerticalLayout = new VerticalLayout();
		System.out.println("Users to share with::   " + userList.size());
//		auditTrail(userList);

		UploadStateWindow uploadStateWindow = new UploadStateWindow();
		UploadFinishedHandler uploadFinishedHandler = (InputStream stream, String fileName, String mimeType,
				long length, int filesLeftInQueue) -> {
			System.out.println("IS UPLOADING FILES");
			SplittableInputStream is1 = new SplittableInputStream(stream);
			SplittableInputStream stream2 = is1.split();
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
				while ((bytesRead = is1.read(buffer)) != -1) {
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
				IOUtils.closeQuietly(is1);
				IOUtils.closeQuietly(stream2);
				IOUtils.closeQuietly(outStream);
				IOUtils.closeQuietly(outStream2);
				auditTrail(userPath, owner, userId, isPrivate,userList, length, filesLeftInQueue);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.debug(e.getLocalizedMessage());
			}


		};

		AllUploadFinishedHandler all = (() -> {
//			Notification.show("File(s) upload completed", Type.HUMANIZED_MESSAGE);
	        Notification notification = new Notification("File(s) upload completed", Type.HUMANIZED_MESSAGE);
	        notification.setDelayMsec(2000);
	        notification.show(UI.getCurrent().getPage());
//			UI.getCurrent().removeWindow(thisWindow);
		});

		SlowUpload m = new SlowUpload(uploadFinishedHandler, uploadStateWindow);
		m.setCaption("Add Files/Zip Folder");
		m.setPanelCaption("File Upload List");
		m.getSmartUpload().setUploadButtonCaptions("Upload File", "Upload Files");
//		m.getSmartUpload().setUploadButtonIcon(FontAwesome.UPLOAD);
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

		VerticalLayout.addComponentsAndExpand(m,dragAndDropWrapper);
		return VerticalLayout;
	}
	
}
