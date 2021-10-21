package com.gtict.app.ui;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;

import com.gtict.app.utilities.AppUtils;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WrappedPortletSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import java.io.File;

import com.vaadin.ui.HorizontalLayout;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Theme("gtictthem")
@StyleSheet({"filefolder.css"})
@SuppressWarnings("serial")
@Widgetset("com.gtict.app.filesharingsystem.AppWidgetSet")
@Component(service = UI.class, property = {
        "com.liferay.portlet.display-category=category.gtict",
        "javax.portlet.name=GTFileSharingApp",
        "javax.portlet.display-name=File Storage and Sharing App",
        "javax.portlet.security-role-ref=power-user,user",
        "com.vaadin.osgi.liferay.portlet-ui=true"}, scope = ServiceScope.PROTOTYPE)
public class UserDashboard extends UI {

    private static Log log = LogFactoryUtil.getLog(UserDashboard.class);
    HorizontalLayout layout = new HorizontalLayout();
    User user;

    @Override
    protected void init(VaadinRequest request) {
    	PortletRequest pRequest = VaadinPortletService.getCurrentPortletRequest();
		HttpSession portletSession = PortalUtil.getHttpServletRequest(pRequest).getSession();
		try {
			user = PortalUtil.getUser(pRequest);
			System.out.println("User StaffID:: " + user.getJobTitle());
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResponsiveLayout respLayout = new ResponsiveLayout();
		ResponsiveRow row3 = respLayout.addRow().withMargin(true);
		row3.setSpacing(true);
		
		
		layout.addComponent(respLayout);
        layout.setSpacing(true);
        setContent(layout);
        
        File folder = new File(AppUtils.getProperty("gt.ict.filesystemapp.rootpath"));
        File[] listOfFiles = folder.listFiles();
        
        for (File i : listOfFiles) {
        	if(i.isDirectory()) {
        	VerticalLayout fl = new VerticalLayout();
        	Label l = new Label("<span><div class=\"folder\"></div></span><br><b>"+i.getName()+"</b>");
                	l.setContentMode(ContentMode.HTML);
//                	Label lb = new Label(i.getName());        	
                	// Create a context menu for 'someComponent'
                	ContextMenu contextMenu = new ContextMenu(l, true);
                	MenuItem item = contextMenu.addItem("Checkable", e -> {
                	            Notification.show("checked: " + e.isChecked());
                	        });
                	item.setEnabled(true);        	
//                	fl.addComponents(l,lb);
            		row3.addColumn().withDisplayRules(4, 0, 0, 0).withComponent(l);
    		}        	
        }
        
        for (File i : listOfFiles) {
        	if(i.isFile()) {
        	VerticalLayout fl = new VerticalLayout();
        	Label l = new Label("<span><div class=\"file\"></div></span><br>"+i.getName()+"");
        	l.setContentMode(ContentMode.HTML);
//        	Label lb = new Label(i.getName());        	
        	// Create a context menu for 'someComponent'
        	ContextMenu contextMenu = new ContextMenu(l, true);
        	MenuItem item = contextMenu.addItem("Checkable", e -> {
        	            Notification.show("checked: " + e.isChecked());
        	        });
        	item.setEnabled(true);        	
//        	fl.addComponents(l,lb);
    		row3.addColumn().withDisplayRules(4, 0, 0, 0).withComponent(l);
    		}        	
        }
    }
    
    
    private HorizontalLayout addFile() {
    	HorizontalLayout hl =  new HorizontalLayout();
    	Upload upload =  new Upload();
    	
    	hl.addComponent(upload);
    	return hl;    	
    }
    
}
