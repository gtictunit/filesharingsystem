package com.gtict.app.ui;

import javax.portlet.PortletRequest;
import com.gtict.app.utilities.AppUtils;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.PortalUtil;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.vaadin.ui.HorizontalLayout;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Theme("gtictthem")
@StyleSheet({ "filefolder.css" })
@SuppressWarnings("serial")
@Widgetset("com.gtict.app.filesharingsystem.AppWidgetSet")
@Component(service = UI.class, property = { "com.liferay.portlet.display-category=category.gtict",
		"javax.portlet.name=GTFileSharingApp", "javax.portlet.display-name=File Storage and Sharing App",
		"javax.portlet.security-role-ref=power-user,user",
		"com.vaadin.osgi.liferay.portlet-ui=true" }, scope = ServiceScope.PROTOTYPE)
public class UserDashboard extends UI {

//    private static Log log = LogFactoryUtil.getLog(UserDashboard.class);
	VerticalLayout layout = new VerticalLayout();
	User user;
	String searchKey = "";
	
	

	@Override
	protected void init(VaadinRequest request) {
		PortletRequest pRequest = VaadinPortletService.getCurrentPortletRequest();
//		HttpSession portletSession = PortalUtil.getHttpServletRequest(pRequest).getSession();
		try {
			user = PortalUtil.getUser(pRequest);
			System.out.println("User StaffID:: " + user.getJobTitle());
			System.out.println("Main Folder path::  " + AppUtils.getProperty("gt.ict.filesystemapp.rootpath")
					+ user.getJobTitle() + "\\");
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		populateDashboard();
		setContent(layout);
	}

	@Override
	protected void refresh(VaadinRequest request) {
		layout.removeAllComponents();
		PortletRequest pRequest = VaadinPortletService.getCurrentPortletRequest();
//		HttpSession portletSession = PortalUtil.getHttpServletRequest(pRequest).getSession();
		try {
			user = PortalUtil.getUser(pRequest);
			System.out.println("User StaffID:: " + user.getJobTitle());
			System.out.println("Main Folder path::  " + AppUtils.getProperty("gt.ict.filesystemapp.rootpath")
					+ user.getJobTitle() + "\\");
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		populateDashboard();
		setContent(layout);
	}

	void populateDashboard() {
		layout.removeAllComponents();
		ResponsiveLayout respLayout = new ResponsiveLayout();
		ResponsiveRow row3 = respLayout.addRow().withMargin(true);
		row3.setSpacing(true);

		ResponsiveLayout buttonLayout = new ResponsiveLayout();
		ResponsiveRow row2 = buttonLayout.addRow().withMargin(true);
		row2.setSpacing(true);

		Button newFile = new Button("Add New File");
		newFile.addClickListener(listener -> {
			Window window = new Window();
			window.setClosable(true);
			window.center();
			window.setWidth("50%");
			window.setHeight("50%");
			window.setModal(true);
			UploadComponent u = new UploadComponent(user.getJobTitle());
			u.setSizeFull();
			window.setContent(u);
			UI.getCurrent().addWindow(window);
		});
		Button newFolder = new Button("Create New Folder");
		TextField searchBar = new TextField();
		searchBar.setPlaceholder("Search for file/folder");
		searchBar.addValueChangeListener(evt -> {
//			evt.getValue();
		});

		row2.addColumn().withDisplayRules(2, 0, 0, 0).withComponent(newFile);
		row2.addColumn().withDisplayRules(2, 0, 0, 0).withComponent(newFolder);
		row2.addColumn().withDisplayRules(4, 0, 0, 0).withComponent(searchBar);

		layout.addComponent(buttonLayout);
		layout.addComponent(respLayout);
		layout.setSpacing(true);

		File folder = new File(AppUtils.getProperty("gt.ict.filesystemapp.rootpath") + user.getJobTitle() + "\\");
		File[] listOfFiles = folder.listFiles();
		System.out.println("No Of Files:: " + listOfFiles.length);

		if (!searchKey.isEmpty()) {
			for (File i : listOfFiles) {
				if (i.isDirectory()) {
					HorizontalLayout fl = new HorizontalLayout();
					Label l = new Label("<span><div class=\"folder\"></div></span><br><div class\"titlename\"><b>"
							+ i.getName() + "</b></div>");
					l.setContentMode(ContentMode.HTML);
					fl.addComponent(l);
					fl.addLayoutClickListener(evt -> {
						if (evt.isDoubleClick()) {
							// ...
							populateFileViewWindow(i.getName());
						}
					});
//					ContextMenu contextMenu = new ContextMenu(l, true);
//					MenuItem item = contextMenu.addItem("Checkable", e -> {
//						Notification.show("checked: " + e.isChecked());
//					});
//					item.setEnabled(true);
					row3.addColumn().withDisplayRules(3, 0, 0, 0).withComponent(fl);
				}
			}

			for (File i : listOfFiles) {
				if (i.isFile()) {
					HorizontalLayout fl = new HorizontalLayout();
					Label l = new Label("<span><div class=\"file\"></div></span><br><div class=\"titlename\">"
							+ i.getName() + "</div>");
					l.setContentMode(ContentMode.HTML);
					fl.addComponent(l);
					fl.addLayoutClickListener(evt -> {
						if (evt.isDoubleClick()) {
							// ...
							populateFileViewWindow(i.getName());
						}
					});
//			ContextMenu contextMenu = new ContextMenu(l, true);
//			MenuItem item = contextMenu.addItem("Checkable", e -> {
//				Notification.show("checked: " + e.isChecked());
//			});
//			item.setEnabled(true);
					row3.addColumn().withDisplayRules(3, 0, 0, 0).withComponent(fl);
				}
			}
		} else {
			for (File i : listOfFiles) {
				if (i.getName().contains(searchKey)) {
					if (i.isDirectory()) {
						HorizontalLayout fl = new HorizontalLayout();
						Label l = new Label("<span><div class=\"folder\"></div></span><br><div class\"titlename\"><b>"
								+ i.getName() + "</b></div>");
						l.setContentMode(ContentMode.HTML);
						fl.addComponent(l);
						fl.addLayoutClickListener(evt -> {
							if (evt.isDoubleClick()) {
								// ...
								populateFileViewWindow(i.getName());
							}
						});
//					ContextMenu contextMenu = new ContextMenu(l, true);
//					MenuItem item = contextMenu.addItem("Checkable", e -> {
//						Notification.show("checked: " + e.isChecked());
//					});
//					item.setEnabled(true);
						row3.addColumn().withDisplayRules(3, 0, 0, 0).withComponent(fl);
					}
				}
			}

			for (File i : listOfFiles) {
				if (i.isFile()) {
					if (i.getName().contains(searchKey)) {
						HorizontalLayout fl = new HorizontalLayout();
						Label l = new Label("<span><div class=\"file\"></div></span><br><div class=\"titlename\">"
								+ i.getName() + "</div>");
						l.setContentMode(ContentMode.HTML);
						fl.addComponent(l);
						fl.addLayoutClickListener(evt -> {
							if (evt.isDoubleClick()) {
								// ...
								viewFile(i);
							}
						});
						row3.addColumn().withDisplayRules(3, 0, 0, 0).withComponent(fl);
					}
				}
			}
		}
	}

	void populateFileViewWindow(String folderPath) {
		layout.removeAllComponents();
		ResponsiveLayout respLayout = new ResponsiveLayout();
		ResponsiveRow row3 = respLayout.addRow().withMargin(true);
		row3.setSpacing(true);

		ResponsiveLayout buttonLayout = new ResponsiveLayout();
		ResponsiveRow row2 = buttonLayout.addRow().withMargin(true);
		row2.setSpacing(true);

		Button back = new Button("Dashboard");
		back.setIcon(FontAwesome.DASHBOARD);
		back.addClickListener(evx -> {
			populateDashboard();
		});

		Button newFile = new Button("Add New File");
		newFile.addClickListener(listener -> {
			Window window = new Window();
			window.setClosable(true);
			window.center();
			window.setWidth("50%");
			window.setHeight("50%");
			window.setModal(true);
			UploadComponent u = new UploadComponent(user.getJobTitle() + "\\" + folderPath);
			u.setSizeFull();
			window.setContent(u);
			UI.getCurrent().addWindow(window);
		});
		Button newFolder = new Button("Create New Folder");
		TextField searchBar = new TextField();
		searchBar.setPlaceholder("Search for file/folder");
		searchBar.addValueChangeListener(evt -> {
//			evt.getValue();
		});

		row2.addColumn().withDisplayRules(2, 0, 0, 0).withComponent(back);
		row2.addColumn().withDisplayRules(2, 0, 0, 0).withComponent(newFile);
		row2.addColumn().withDisplayRules(2, 0, 0, 0).withComponent(newFolder);
		row2.addColumn().withDisplayRules(6, 0, 0, 0).withComponent(searchBar);

		layout.addComponent(buttonLayout);
		layout.addComponent(respLayout);
		layout.setSpacing(true);

		File folder = new File(
				AppUtils.getProperty("gt.ict.filesystemapp.rootpath") + user.getJobTitle() + "\\" + folderPath + "\\");
		File[] listOfFiles = folder.listFiles();
		System.out.println("No Of Files:: " + listOfFiles.length);

		for (File i : listOfFiles) {
			if (i.isDirectory()) {
				HorizontalLayout fl = new HorizontalLayout();
				Label l = new Label("<span><div class=\"folder\"></div></span><br><div class\"titlename\"><b>"
						+ i.getName() + "</b></div>");
				l.setContentMode(ContentMode.HTML);
				fl.addComponent(l);
				fl.addLayoutClickListener(evt -> {
					if (evt.isDoubleClick()) {
						// ...
						populateFileViewWindow(i.getName());
					}
				});
//				ContextMenu contextMenu = new ContextMenu(l, true);
//				MenuItem item = contextMenu.addItem("Checkable", e -> {
//					Notification.show("checked: " + e.isChecked());
//				});
//				item.setEnabled(true);
				row3.addColumn().withDisplayRules(4, 0, 0, 0).withComponent(fl);
			}
		}

		for (File i : listOfFiles) {
			if (i.isFile()) {
				HorizontalLayout fl = new HorizontalLayout();
				Label l = new Label("<span><div class=\"file\"></div></span><br><div class=\"titlename\">" + i.getName()
						+ "</div>");
				l.setContentMode(ContentMode.HTML);
				fl.addComponent(l);
				fl.addLayoutClickListener(evt -> {
					if (evt.isDoubleClick()) {
						// ...
						try {
							byte[] bytes = Files.readAllBytes(i.toPath());
							Path tempFile = Files.createTempFile(null, null);
							Files.write(tempFile, bytes);
							Desktop.getDesktop().open(tempFile.toFile());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
//		ContextMenu contextMenu = new ContextMenu(l, true);
//		MenuItem item = contextMenu.addItem("Checkable", e -> {
//			Notification.show("checked: " + e.isChecked());
//		});
//		item.setEnabled(true);
				row3.addColumn().withDisplayRules(4, 0, 0, 0).withComponent(fl);
			}
		}
	}

	void viewFile(File i) {
		try {
			System.setProperty("java.awt.headless", "false");
			com.liferay.portal.kernel.util.SystemProperties.set("java.awt.headless", "false");
			boolean headless = GraphicsEnvironment.isHeadless();
			System.out.println("Headless: " + headless);
			byte[] bytes = Files.readAllBytes(i.toPath());
			Path tempFile = Files.createTempFile(null, null);
			Files.write(tempFile, bytes);
			Desktop.getDesktop().open(tempFile.toFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class ImageReceiver implements Receiver, SucceededListener, FailedListener {
		private static final long serialVersionUID = -1276759102490466761L;

		public File file;

		public OutputStream receiveUpload(String filename, String mimeType) {
			// Create upload stream
			FileOutputStream fos = null; // Stream to write to
			try {
				// Open the file for writing.
				file = new File(
						AppUtils.getProperty("gt.ict.filesystemapp.rootpath") + user.getJobTitle() + "\\" + filename);
				fos = new FileOutputStream(file);
				Notification.show("File Uploaded Successfully! ", Type.WARNING_MESSAGE);
			} catch (final java.io.FileNotFoundException e) {
				new Notification("Could not open file<br/>", e.getMessage(), Notification.Type.ERROR_MESSAGE)
						.show(UI.getCurrent().getPage());
				return null;
			}
			return fos; // Return the output stream to write to
		}

		public void uploadSucceeded(SucceededEvent event) {
			// Show the uploaded file in the image viewer
			Notification.show("File Uploaded Successfully! ", Type.WARNING_MESSAGE);
		}

		@Override
		public void uploadFailed(FailedEvent event) {
			// TODO Auto-generated method stub
			Notification.show(
					"File Failed to Upload! Kindly check the file or file path! If issue persists notify the system administrator",
					Type.ERROR_MESSAGE);
		}
	};

	ImageReceiver receiver = new ImageReceiver();

}
