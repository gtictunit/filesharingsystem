package com.gtict.app.ui;

import com.gtict.app.models.Archive_log;
import com.gtict.app.models.FileInfo;
import com.gtict.app.services.ArchiveLogService;
import com.gtict.app.services.FileInfoService;
import com.gtict.app.utilities.AppUtils;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.vaadin.dialogs.ConfirmDialog;

@Theme("gtictthem")
@SuppressWarnings({ "serial", "deprecation" })
@Widgetset("com.gtict.app.filesharingsystem.AppWidgetSet")
@Component(service = UI.class, property = { "com.liferay.portlet.display-category=category.gtict",
		"javax.portlet.name=GTFileSharingApp", "javax.portlet.display-name=File Storage and Sharing App",
		"javax.portlet.security-role-ref=power-user,user",
		"com.vaadin.osgi.liferay.portlet-ui=true" }, scope = ServiceScope.PROTOTYPE)
public class UserDashboard extends UI {

	private static String HOME_CONTEXT = "";
	private static String SHARED_CONTEXT = "";
	private static String CURRENT_CONTEXT = "";
	private static String OVERVIEW_CURRENT_CONTEXT = "";
	private static int CONTEXT_COUNT = 0;
	private static int OVERVIEW_CONTEXT_COUNT = 0;
//    private static Log log = LogFactoryUtil.getLog(UserDashboard.class);
	VerticalLayout home = new VerticalLayout();
	VerticalLayout shared = new VerticalLayout();
	VerticalLayout OVERVIEW = new VerticalLayout();
	TabSheet mainTab = new TabSheet();
	User user;
	String searchKey = "";
	String searchKeyShared = "";
	long roleId = 0L;
	List<User> getUsers = new ArrayList<User>();

	@Override
	protected void init(VaadinRequest request) {
		PortletRequest pRequest = VaadinPortletService.getCurrentPortletRequest();
//		HttpSession portletSession = PortalUtil.getHttpServletRequest(pRequest).getSession();
		try {
			user = PortalUtil.getUser(pRequest);
			for (Role role : user.getRoles()) {
				if (role.getName().equalsIgnoreCase("APP_USER_ROLE")) {
					roleId = role.getRoleId();
				}
			}
			getUsers = UserLocalServiceUtil.getRoleUsers(roleId);
			System.out.println("User StaffID:: " + user.getFullName() + " :: ROLE_ID :: " + roleId);
			HOME_CONTEXT = AppUtils.getProperty("gt.ict.filesystemapp.rootpath") + user.getUserId() + "\\";
			SHARED_CONTEXT = AppUtils.getProperty("gt.ict.filesystemapp.sharedpath") + "\\";
			System.out.println("Main Folder path::  " + HOME_CONTEXT);
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (CONTEXT_COUNT == 0) {
			CURRENT_CONTEXT = HOME_CONTEXT;
		}

		populateDashboard(HOME_CONTEXT);
		sharedFilesLayout();
		OVERVIEW();

		mainTab.addTab(home, "HOME");
		mainTab.addTab(shared, "SHARED WITH ME");
		if(user.getUserId()==20130) {
			mainTab.addTab(OVERVIEW, "OVERVIEW");
		}

		
		setContent(mainTab);
	}

	@Override
	protected void refresh(VaadinRequest request) {
		home.removeAllComponents();
		shared.removeAllComponents();
		mainTab.removeAllComponents();
//		for(Window w : UI.getCurrent().getWindows()) {
//			removeWindow(w);
//		}
		PortletRequest pRequest = VaadinPortletService.getCurrentPortletRequest();
//		HttpSession portletSession = PortalUtil.getHttpServletRequest(pRequest).getSession();
		try {
			user = PortalUtil.getUser(pRequest);
			for (Role role : user.getRoles()) {
				if (role.getName().equalsIgnoreCase("APP_USER_ROLE")) {
					roleId = role.getRoleId();
				}
			}
			getUsers = UserLocalServiceUtil.getRoleUsers(roleId);
			System.out.println("User ID:: " + user.getUserId() + " :: ROLE_ID :: " + roleId);
			HOME_CONTEXT = AppUtils.getProperty("gt.ict.filesystemapp.rootpath") + user.getUserId() + "\\";
			SHARED_CONTEXT = AppUtils.getProperty("gt.ict.filesystemapp.sharedpath") + "\\";
			System.out.println("Main Folder path::  " + HOME_CONTEXT);
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (CONTEXT_COUNT > 0) {
			populateFileViewWindow(CURRENT_CONTEXT);
		}

		if (CONTEXT_COUNT == 0) {
			populateDashboard(HOME_CONTEXT);
		}
		sharedFilesLayout();
		OVERVIEW();
		mainTab.addTab(home, "HOME");
		mainTab.addTab(shared, "SHARED WITH ME");
		if(user.getUserId()==20130) {
			mainTab.addTab(OVERVIEW, "OVERVIEW");
		}
		setContent(mainTab);
	}

	void populateDashboard(String CONTEXT_URI) {
		home.removeAllComponents();
		File folder = new File(CONTEXT_URI);
		File[] listOfFiles = folder.listFiles();
		List<File> files = new ArrayList<File>();
		if (listOfFiles.length > 0) {
			files = Arrays.asList(listOfFiles);
			if (!Objects.isNull(searchKey.trim()) && !searchKey.trim().isEmpty()) {
				files = files.stream().filter(p -> p.getName().contains(searchKey)).collect(Collectors.toList());
				searchKey = "";
			}
		}
		ResponsiveLayout respLayout = new ResponsiveLayout();
		ResponsiveRow row3 = respLayout.addRow().withMargin(true);
		row3.setSpacing(true);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);

		Button refresh = new Button();
		refresh.setCaptionAsHtml(true);
		refresh.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/ios-glyphs/20/000000/refresh--v1.png\"/>&nbsp; Refresh</div>");
		refresh.addClickListener(listener -> {
			populateDashboard(CONTEXT_URI);
		});

		Button newFile = new Button();
		newFile.setCaptionAsHtml(true);
		newFile.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/glyph-neue/20/000000/plus.png\"/>&nbsp; File</div>");
		newFile.addClickListener(listener -> {
			Window window = new Window();
			window.setClosable(true);
			window.center();
			window.setWidth("80%");
			window.setHeight("80%");
			window.setResponsive(true);
			window.setModal(true);
			UploadInterface u = new UploadInterface(CONTEXT_URI, user, roleId);
			u.setSizeFull();
			window.setContent(u);
			window.addCloseListener(closer -> {
				populateDashboard(CONTEXT_URI);
			});
			window.getContent().setWidth("100%");
			window.getContent().setHeight("100%");
			UI.getCurrent().addWindow(window);
		});

		Button newFolder = new Button();
		newFolder.setWidth("120");
		newFolder.setCaptionAsHtml(true);
		newFolder.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/glyph-neue/20/000000/plus.png\"/>&nbsp; Folder</div>");
		newFolder.addClickListener(listener -> {
			TextField eFolder = new TextField("Enter Folder Name");
			Button createFolder = new Button("Create");
			createFolder.addClickListener(evnt -> {
				AppUtils.createFolder(CONTEXT_URI, eFolder.getValue(), user);
				Notification.show("Folder created successfully", Type.TRAY_NOTIFICATION);
				populateDashboard(CONTEXT_URI);
			});
			Window window = new Window();
			window.setClosable(true);
			window.center();
			window.setWidth("50%");
			window.setHeight("50%");
			window.setModal(true);
			VerticalLayout u = new VerticalLayout();
			u.setSizeFull();
			u.addComponentsAndExpand(eFolder, createFolder);
			window.setContent(u);
			UI.getCurrent().addWindow(window);
		});

		buttonLayout.addComponent(newFile);
		buttonLayout.addComponent(newFolder);
		buttonLayout.addComponent(refresh);

		HorizontalLayout searchBar = new HorizontalLayout();
		searchBar.setSpacing(true);
		Button searchBtn = new Button();
		searchBtn.setWidth("40px");
		searchBtn.setHeight("25px");
		
		searchBtn.setCaptionAsHtml(true);
		searchBtn.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/ios-filled/20/000000/search--v1.png\"/>&nbsp; Search</div>");
		searchBtn.addClickListener(l -> {
			populateDashboard(CONTEXT_URI);
		});
		TextField searchBox = new TextField();
		searchBox.setPlaceholder("Search for file/folder");
		searchBox.addValueChangeListener(evt -> {
			searchKey = evt.getValue();
		});
		searchBar.addComponentsAndExpand(searchBox, searchBtn);

		home.addComponent(searchBar);
		home.addComponent(buttonLayout);
		home.addComponent(respLayout);
		home.setSpacing(true);

		System.out.println("No Of Files:: " + listOfFiles.length);
		for (File i : files) {
			if (i.isDirectory()) {
				HorizontalLayout fl = new HorizontalLayout();
				Label l = new Label("<span><div class=\"folder\"></div></span><br><div class\"titlename\"><b>"
						+ i.getName() + "</b></div>");
				l.setContentMode(ContentMode.HTML);
				fl.addComponent(l);
				fl.addLayoutClickListener(evt -> {
					if (evt.isDoubleClick()) {
						// ...
						CURRENT_CONTEXT = CONTEXT_URI + "\\" + i.getName();
						CONTEXT_COUNT = CONTEXT_COUNT + 1;
						populateFileViewWindow(CURRENT_CONTEXT);
					} else if (evt.getButton() == MouseEvents.ClickEvent.BUTTON_RIGHT) {
						try {
							rightClick(i);
						} catch (PortalException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				row3.addColumn().withDisplayRules(3, 0, 0, 0).withComponent(fl);
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
						viewFile(i);
					} else if (evt.getButton() == MouseEvents.ClickEvent.BUTTON_RIGHT) {
						try {
							rightClick(i);
						} catch (PortalException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				row3.addColumn().withDisplayRules(3, 0, 0, 0).withComponent(fl);
			}
		}
	}

	void sharedFilesLayout() {
		shared.removeAllComponents();
		ResponsiveLayout respLayout = new ResponsiveLayout();
		ResponsiveRow row = respLayout.addRow().withMargin(true);
		row.setSpacing(true);
		ResponsiveRow row3 = respLayout.addRow().withMargin(true);
		row3.setSpacing(true);

		shared.addComponent(respLayout);
		shared.setSpacing(true);

		File folder = new File(SHARED_CONTEXT);
		File[] listOfFiles = folder.listFiles();
		System.out.println("Files:::>>>  " + listOfFiles.length);
		List<File> files = Arrays.asList(listOfFiles);
		boolean isEmpty = false;
		if (listOfFiles.length == 0)
			isEmpty = true;
		if (!Objects.isNull(searchKeyShared.trim()) && !searchKeyShared.trim().isEmpty()) {
			files = files.stream().filter(p -> p.getName().contains(searchKeyShared)).collect(Collectors.toList());
			searchKeyShared = "";
		}
		Button refresh = new Button();
		refresh.setCaptionAsHtml(true);
		refresh.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/ios-glyphs/20/000000/refresh--v1.png\"/>&nbsp; Refresh</div>");
		refresh.addClickListener(listener -> {
			sharedFilesLayout();
		});
		HorizontalLayout searchBar = new HorizontalLayout();
		searchBar.setSpacing(true);
		Button searchBtn = new Button();
		searchBtn.setCaptionAsHtml(true);
		searchBtn.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/ios-filled/20/000000/search--v1.png\"/>&nbsp; Search</div>");
		searchBtn.addClickListener(l -> {
			sharedFilesLayout();
		});
		TextField searchBox = new TextField();
		searchBox.setPlaceholder("Search for file/folder");
		searchBox.addValueChangeListener(evt -> {
			searchKeyShared = evt.getValue();
		});
		searchBar.addComponentsAndExpand(searchBox, searchBtn, refresh);
		row.addColumn().withDisplayRules(12, 0, 0, 0).withComponent(searchBar);
		if (!isEmpty) {
			for (File i : files) {
				if (i.isFile()) {
					if (AppUtils.isFileShared(i.getName(), user.getUserId())) {
						HorizontalLayout fl = new HorizontalLayout();
						Label l = new Label("<span><div class=\"file\"></div></span><br><div class=\"titlename\">"
								+ i.getName() + "</div>");
						l.setContentMode(ContentMode.HTML);
						fl.addComponent(l);
						fl.addLayoutClickListener(evt -> {
							if (evt.isDoubleClick()) {
								// ...
								viewFile(i);
							} else if (evt.getButton() == MouseEvents.ClickEvent.BUTTON_RIGHT) {
								try {
									rightClick(i);
								} catch (PortalException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						row3.addColumn().withDisplayRules(4, 0, 0, 0).withComponent(fl);
					}
				}
			}
		}
	}

	void populateFileViewWindow(String CTX_URI) {
		home.removeAllComponents();
		File folder = new File(CTX_URI);
		File[] listOfFiles = folder.listFiles();

		List<File> files = Arrays.asList(listOfFiles);
		if (!Objects.isNull(searchKey.trim()) && !searchKey.trim().isEmpty()) {
			files = files.stream().filter(p -> p.getName().contains(searchKey)).collect(Collectors.toList());
			searchKey = "";
		}

		System.out.println("No Of Files:: " + listOfFiles.length);

		ResponsiveLayout respLayout = new ResponsiveLayout();
		ResponsiveRow row3 = respLayout.addRow().withMargin(true);
		row3.setSpacing(true);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);

		Button refresh = new Button();
		refresh.setCaptionAsHtml(true);
		refresh.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/ios-glyphs/20/000000/refresh--v1.png\"/>&nbsp; Refresh</div>");
		refresh.addClickListener(listener -> {
			populateFileViewWindow(CTX_URI);
		});

		Button homeBtn = new Button();
		homeBtn.setCaptionAsHtml(true);
		homeBtn.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/ios-glyphs/20/000000/home.png\"/>&nbsp; Home</div>");
		homeBtn.addClickListener(evx -> {
			populateDashboard(HOME_CONTEXT);
		});

		Button back = new Button();
		back.setCaptionAsHtml(true);
		back.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/external-flatart-icons-outline-flatarticons/20/000000/external-back-arrow-flatart-icons-outline-flatarticons-1.png\"/>&nbsp; Back</div>");
		back.addClickListener(evx -> {
			CONTEXT_COUNT = CONTEXT_COUNT - 1;
			System.out.println("CONTEXT COUNT:::> "+CONTEXT_COUNT);
			if (CONTEXT_COUNT > 0) {
				CURRENT_CONTEXT = CTX_URI.substring(0, CTX_URI.lastIndexOf("\\"));
				populateFileViewWindow(CURRENT_CONTEXT);
			}

			if (CONTEXT_COUNT == 0) {
				populateDashboard(HOME_CONTEXT);
			}
		});

		Button newFile = new Button();
		newFile.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/glyph-neue/20/000000/plus.png\"/>&nbsp;  File</div>");
		newFile.setCaptionAsHtml(true);
		newFile.addClickListener(listener -> {
			Window window = new Window();
			window.setClosable(true);
			window.center();
			window.setWidth("80%");
			window.setHeight("80%");
			window.setResponsive(true);
			window.setModal(true);
			UploadInterface u = new UploadInterface(CTX_URI, user, roleId);
			u.setSizeFull();
			window.setContent(u);
			window.addCloseListener(closer -> {
				populateFileViewWindow(CTX_URI);
			});
			window.getContent().setWidth("100%");
			window.getContent().setHeight("100%");
			UI.getCurrent().addWindow(window);
		});
		Button newFolder = new Button();
		newFolder.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/glyph-neue/20/000000/plus.png\"/>&nbsp;  Folder</div>");
		newFolder.setCaptionAsHtml(true);
		newFolder.addClickListener(l -> {
			TextField eFolder = new TextField("Enter Folder Name");
			Button createFolder = new Button("Create");
			createFolder.addClickListener(evnt -> {
				AppUtils.createFolder(CTX_URI, eFolder.getValue(), user);
				Notification.show("Folder created successfully", Type.TRAY_NOTIFICATION);
				populateFileViewWindow(CTX_URI);
			});
			Window window = new Window();
			window.setClosable(true);
			window.center();
			window.setWidth("50%");
			window.setHeight("50%");
			window.setModal(true);
			VerticalLayout u = new VerticalLayout();
			u.setSizeFull();
			u.addComponentsAndExpand(eFolder, createFolder);
			window.setContent(u);
			UI.getCurrent().addWindow(window);
		});

		buttonLayout.addComponent(back);
		buttonLayout.setComponentAlignment(back, Alignment.TOP_LEFT);
		buttonLayout.addComponent(homeBtn);

		buttonLayout.addComponent(newFile);
		buttonLayout.addComponent(newFolder);
		buttonLayout.addComponent(refresh);

		HorizontalLayout searchBar = new HorizontalLayout();
		searchBar.setSpacing(true);
		Button searchBtn = new Button();
		searchBtn.setCaptionAsHtml(true);
		searchBtn.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/ios-filled/20/000000/search--v1.png\"/>&nbsp; Search</div>");
		searchBtn.addClickListener(l -> {
			populateDashboard(CTX_URI);
		});
		TextField searchBox = new TextField();
		searchBox.setPlaceholder("Search for file/folder");
		searchBox.addValueChangeListener(evt -> {
			searchKey = evt.getValue();
		});
		searchBar.addComponentsAndExpand(searchBox, searchBtn);

		home.addComponent(searchBar);
		home.addComponent(buttonLayout);
		home.addComponent(respLayout);
		home.setSpacing(true);

		for (File i : files) {
			if (i.isDirectory()) {
				HorizontalLayout fl = new HorizontalLayout();
				Label l = new Label("<span><div class=\"folder\"></div></span><br><div class\"titlename\"><b>"
						+ i.getName() + "</b></div>");
				l.setContentMode(ContentMode.HTML);
				fl.addComponent(l);
				fl.addLayoutClickListener(evt -> {
					if (evt.isDoubleClick()) {
						// ...
						CURRENT_CONTEXT = CTX_URI + "\\" + i.getName();
						CONTEXT_COUNT = CONTEXT_COUNT + 1;
						populateFileViewWindow(CURRENT_CONTEXT);
					} else if (evt.getButton() == MouseEvents.ClickEvent.BUTTON_RIGHT) {
						try {
							rightClick(i);
						} catch (PortalException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
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
						// Use default program to view/execute file
						viewFile(i);
					} else if (evt.getButton() == MouseEvents.ClickEvent.BUTTON_RIGHT) {
						try {
							rightClick(i);
						} catch (PortalException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				row3.addColumn().withDisplayRules(4, 0, 0, 0).withComponent(fl);
			}
		}
	}

	void OVERVIEW(){
		OVERVIEW_CURRENT_CONTEXT = AppUtils.getProperty("gt.ict.filesystemapp.rootpath") + "\\";
				OVERVIEW.removeAllComponents();
				File folder = new File(OVERVIEW_CURRENT_CONTEXT);
				File[] listOfFiles = folder.listFiles();
				List<File> files = new ArrayList<File>();
				if (listOfFiles.length > 0) {
					files = Arrays.asList(listOfFiles);
					if (!Objects.isNull(searchKey.trim()) && !searchKey.trim().isEmpty()) {
						files = files.stream().filter(p -> p.getName().contains(searchKey)).collect(Collectors.toList());
						searchKey = "";
					}
				}
				ResponsiveLayout respLayout = new ResponsiveLayout();
				ResponsiveRow row3 = respLayout.addRow().withMargin(true);
				row3.setSpacing(true);

				HorizontalLayout buttonLayout = new HorizontalLayout();
				buttonLayout.setSpacing(true);

				Button refresh = new Button();
				refresh.setCaptionAsHtml(true);
				refresh.setCaption(
						"<div class=\"iconblock\"><img src=\"https://img.icons8.com/ios-glyphs/20/000000/refresh--v1.png\"/>&nbsp; Refresh</div>");
				refresh.addClickListener(listener -> {
					OVERVIEW();
				});

				buttonLayout.addComponent(refresh);

				HorizontalLayout searchBar = new HorizontalLayout();
				searchBar.setSpacing(true);
				Button searchBtn = new Button();
				searchBtn.setWidth("40px");
				searchBtn.setHeight("25px");
				
				searchBtn.setCaptionAsHtml(true);
				searchBtn.setCaption(
						"<div class=\"iconblock\"><img src=\"https://img.icons8.com/ios-filled/20/000000/search--v1.png\"/>&nbsp; Search</div>");
				searchBtn.addClickListener(l -> {
					populateDashboard(OVERVIEW_CURRENT_CONTEXT);
				});
				TextField searchBox = new TextField();
				searchBox.setPlaceholder("Search for file/folder");
				searchBox.addValueChangeListener(evt -> {
					searchKey = evt.getValue();
				});
				searchBar.addComponentsAndExpand(searchBox, searchBtn);

				OVERVIEW.addComponent(searchBar);
				OVERVIEW.addComponent(buttonLayout);
				OVERVIEW.addComponent(respLayout);
				OVERVIEW.setSpacing(true);

				System.out.println("No Of Files:: " + listOfFiles.length);
				for (File i : files) {
					if (i.isDirectory()) {
						HorizontalLayout fl = new HorizontalLayout();
						Label l = new Label("<span><div class=\"folder\"></div></span><br><div class\"titlename\"><b>"
								+ i.getName() + "</b></div>");
						l.setContentMode(ContentMode.HTML);
						fl.addComponent(l);
						fl.addLayoutClickListener(evt -> {
							if (evt.isDoubleClick()) {
								// ...
								OVERVIEW_CURRENT_CONTEXT = OVERVIEW_CURRENT_CONTEXT + "\\" + i.getName();
								OVERVIEW_CONTEXT_COUNT = OVERVIEW_CONTEXT_COUNT + 1;
								populateFileViewWindowOverView(OVERVIEW_CURRENT_CONTEXT);
							} else if (evt.getButton() == MouseEvents.ClickEvent.BUTTON_RIGHT) {
								try {
									rightClick(i);
								} catch (PortalException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						row3.addColumn().withDisplayRules(3, 0, 0, 0).withComponent(fl);
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
								if (AppUtils.isFileShared(i.getName(), user.getUserId())) {
								viewFile(i);
								}else {
									Notification.show("Permission Denied! (File not Shared)", Type.ERROR_MESSAGE);
								}
							} else if (evt.getButton() == MouseEvents.ClickEvent.BUTTON_RIGHT) {
								try {
									rightClick(i);
								} catch (PortalException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						row3.addColumn().withDisplayRules(3, 0, 0, 0).withComponent(fl);
					}
				}
			
		}
	
	void populateFileViewWindowOverView(String CTX_URI) {
		OVERVIEW_CURRENT_CONTEXT = CTX_URI;
		OVERVIEW.removeAllComponents();
		File folder = new File(OVERVIEW_CURRENT_CONTEXT);
		File[] listOfFiles = folder.listFiles();

		List<File> files = Arrays.asList(listOfFiles);
		if (!Objects.isNull(searchKey.trim()) && !searchKey.trim().isEmpty()) {
			files = files.stream().filter(p -> p.getName().contains(searchKey)).collect(Collectors.toList());
			searchKey = "";
		}

		System.out.println("No Of Files:: " + listOfFiles.length);

		ResponsiveLayout respLayout = new ResponsiveLayout();
		ResponsiveRow row3 = respLayout.addRow().withMargin(true);
		row3.setSpacing(true);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);

		Button refresh = new Button();
		refresh.setCaptionAsHtml(true);
		refresh.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/ios-glyphs/20/000000/refresh--v1.png\"/>&nbsp; Refresh</div>");
		refresh.addClickListener(listener -> {
			populateFileViewWindowOverView(OVERVIEW_CURRENT_CONTEXT);
		});

		Button homeBtn = new Button();
		homeBtn.setCaptionAsHtml(true);
		homeBtn.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/ios-glyphs/20/000000/home.png\"/>&nbsp; Home</div>");
		homeBtn.addClickListener(evx -> {
			OVERVIEW();
		});

		Button back = new Button();
		back.setCaptionAsHtml(true);
		back.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/external-flatart-icons-outline-flatarticons/20/000000/external-back-arrow-flatart-icons-outline-flatarticons-1.png\"/>&nbsp; Back</div>");
		back.addClickListener(evx -> {
			OVERVIEW_CONTEXT_COUNT = OVERVIEW_CONTEXT_COUNT - 1;
			System.out.println("CONTEXT COUNT:::> "+OVERVIEW_CONTEXT_COUNT);
			if (OVERVIEW_CONTEXT_COUNT > 0) {
				OVERVIEW_CURRENT_CONTEXT = CTX_URI.substring(0, CTX_URI.lastIndexOf("\\"));
				populateFileViewWindowOverView(OVERVIEW_CURRENT_CONTEXT);
			}

			if (CONTEXT_COUNT == 0) {
				OVERVIEW();
			}
		});

		buttonLayout.addComponent(back);
		buttonLayout.setComponentAlignment(back, Alignment.TOP_LEFT);
		buttonLayout.addComponent(homeBtn);

		buttonLayout.addComponent(refresh);

		HorizontalLayout searchBar = new HorizontalLayout();
		searchBar.setSpacing(true);
		Button searchBtn = new Button();
		searchBtn.setCaptionAsHtml(true);
		searchBtn.setCaption(
				"<div class=\"iconblock\"><img src=\"https://img.icons8.com/ios-filled/20/000000/search--v1.png\"/>&nbsp; Search</div>");
		searchBtn.addClickListener(l -> {
			populateDashboard(CTX_URI);
		});
		TextField searchBox = new TextField();
		searchBox.setPlaceholder("Search for file/folder");
		searchBox.addValueChangeListener(evt -> {
			searchKey = evt.getValue();
		});
		searchBar.addComponentsAndExpand(searchBox, searchBtn);

		OVERVIEW.addComponent(searchBar);
		OVERVIEW.addComponent(buttonLayout);
		OVERVIEW.addComponent(respLayout);
		OVERVIEW.setSpacing(true);

		for (File i : files) {
			if (i.isDirectory()) {
				HorizontalLayout fl = new HorizontalLayout();
				Label l = new Label("<span><div class=\"folder\"></div></span><br><div class\"titlename\"><b>"
						+ i.getName() + "</b></div>");
				l.setContentMode(ContentMode.HTML);
				fl.addComponent(l);
				fl.addLayoutClickListener(evt -> {
					if (evt.isDoubleClick()) {
						// ...
						OVERVIEW_CURRENT_CONTEXT = CTX_URI + "\\" + i.getName();
						OVERVIEW_CONTEXT_COUNT = OVERVIEW_CONTEXT_COUNT + 1;
						populateFileViewWindowOverView(OVERVIEW_CURRENT_CONTEXT);
					} else if (evt.getButton() == MouseEvents.ClickEvent.BUTTON_RIGHT) {
						try {
							rightClick(i);
						} catch (PortalException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
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
						// Use default program to view/execute file
						if (AppUtils.isFileShared(i.getName(), user.getUserId())) {
						viewFile(i);
						}else {
							Notification.show("Permission Denied! (File not Shared)", Type.ERROR_MESSAGE);
						}
					} else if (evt.getButton() == MouseEvents.ClickEvent.BUTTON_RIGHT) {
						try {
							rightClick(i);
						} catch (PortalException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
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
			Desktop.getDesktop().open(i);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void rightClick(File i) throws PortalException {
		if (i.isFile()) {
			FileInfo f = FileInfoService.findOne(i);
			ResponsiveLayout rc = new ResponsiveLayout();

			ResponsiveRow row3 = rc.addRow().withMargin(true);
			row3.setSpacing(true);

			// FILE_INFORMATION_COLUMN
			VerticalLayout info = new VerticalLayout();
			info.setCaptionAsHtml(true);
			info.setCaption("<h2><b>FILE INFORMATION</b></h2>");
			Label fileNamee = new Label("<b>File Name: </b>" + f.getFilename());
			fileNamee.setContentMode(ContentMode.HTML);
			Label owner = new Label("<b>Owner: </b>" + f.getOwner());
			owner.setContentMode(ContentMode.HTML);
			Label uploaded = new Label("<b>Date Uploaded: </b>" + f.getCreatedDate());
			uploaded.setContentMode(ContentMode.HTML);
			Label modified = new Label("<b>Date Modified: </b>" + f.getModifiedDate());
			modified.setContentMode(ContentMode.HTML);
			info.addComponents(fileNamee, owner, uploaded, modified);

			// FILE_ACTION_OPERATIONS_COLUMN
			VerticalLayout action = new VerticalLayout();

			Button deleteFile = new Button("Delete File");
			ComboBox<User> sharedUsers = new ComboBox<User>("Share with:");
			VerticalLayout usersSharedWith = new VerticalLayout();
			action.addComponentsAndExpand(deleteFile, sharedUsers, usersSharedWith);

			deleteFile.addClickListener(l -> {
				File file = new File(f.getFilepath() + f.getFilename());
				System.out.println("File to Delete:: ::> "+f.getFilepath() + f.getFilename());
				if (file.exists()) {
					ConfirmDialog.show(UI.getCurrent(), "Are you sure you want to delete file?",
							new ConfirmDialog.Listener() {
								@Override
								public void onClose(ConfirmDialog dialog) {
									// TODO Auto-generated method stub
									if (dialog.isConfirmed()) {
										// Confirmed to continue
										if (file.delete()) {
											f.setDeleted(true);
											FileInfoService.update(f);

											Archive_log archive_log = new Archive_log();
											archive_log.setUserId(user.getUserId());
											archive_log.setLog_date(new Date());
											archive_log.setAction_info("DELETED_FILE_INFO");
											archive_log.setFilename(f.getFilename());
											archive_log.setFilepath(f.getFilepath());
											ArchiveLogService.save(archive_log);

											Notification.show(f.getFilename() + " has been deleted successfully");
										}

									}
								}
							});
				}
			});

			sharedUsers.setItems(getUsers);
			sharedUsers.setItemCaptionGenerator(User::getFullName);
			sharedUsers.addSelectionListener(u -> {
				System.out.println("GET Share User value " + u.getValue());
				if (u.getSelectedItem().isPresent()) {
					com.gtict.app.models.User usr = AppUtils.maptoKeyUser(u.getValue());
					if (!f.getSharedWithUsers().contains(usr)) {
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
						clearUsrs.setCaption(
								"<div class=\"iconblock\"><img src=\"https://img.icons8.com/glyph-neue/20/000000/close-window.png\"/></div>");
						clearUsrs.addClickListener(listener -> {
							f.getSharedWithUsers().remove(usr);
							usersSharedWith.removeComponent(lUsr);
							FileInfoService.update(f);
						});
						f.getSharedWithUsers().add(usr);
						usersSharedWith.addComponent(lUsr);
						FileInfoService.update(f);
					}
				}
			});

			for (com.gtict.app.models.User id : f.getSharedWithUsers()) {
				HorizontalLayout usr = new HorizontalLayout();
				Button r = new Button();
				r.setWidth("50px");
				r.setHeight("25px");
				r.setCaptionAsHtml(true);
				r.setCaption(
						"<div class=\"iconblock\"><img src=\"https://img.icons8.com/glyph-neue/20/000000/close-window.png\"/></div>");
				r.addClickListener(listener -> {
					f.getSharedWithUsers().remove(id);
					usersSharedWith.removeComponent(usr);
					FileInfoService.update(f);

					Archive_log archive_log = new Archive_log();
					archive_log.setUserId(user.getUserId());
					archive_log.setLog_date(new Date());
					archive_log.setAction_info("UPDATED_FILE_INFO_REMOVED_SHARED_USERID_" + id);
					archive_log.setFilename(f.getFilename());
					archive_log.setFilepath(f.getFilepath());
					ArchiveLogService.save(archive_log);
				});
				usr.addComponentsAndExpand(new Label(id.getFullname()), r);
				usersSharedWith.addComponent(usr);
			}

			row3.addColumn().withDisplayRules(6, 0, 0, 0).withComponent(info);
			row3.addColumn().withDisplayRules(6, 0, 0, 0).withComponent(action);

			Window window = new Window();
			window.setScrollTop(50);
			window.setClosable(true);
			window.center();
			window.setWidth("50%");
			window.setHeight("40%");
			window.setModal(true);
			window.setContent(rc);
			UI.getCurrent().addWindow(window);
		} else {
			Notification.show("Double-click the Folder", Type.TRAY_NOTIFICATION);
		}
	}
}
