package com.gtict.app.ui;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class UploadInterface extends ResponsiveLayout {

	boolean isShared;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2225524318218992648L;

	UploadInterface(String userPath, long userId, long roleid) {
		System.out.printf("userid:: " + userId);
		System.out.printf("roleid:: " + roleid);
		List<User> getUsers = UserLocalServiceUtil.getRoleUsers(roleid);
		List<Long> userShare = new ArrayList<Long>();

		ResponsiveRow row3 = addRow().withMargin(true);
// 		row3.setSpacing(true);
		CheckBox isPrivate = new CheckBox("Private or Shared");
		row3.addColumn().withDisplayRules(12, 0, 0, 0).withComponent(isPrivate);

		ResponsiveRow row4 = addRow().withMargin(true);
// 		row4.setSpacing(true);
		ResponsiveRow row5 = addRow().withMargin(true);
		row5.setSpacing(true);

		VerticalLayout lUsrs = new VerticalLayout();
		lUsrs.setCaption("File Shared With:");

		ComboBox<User> sharedUsers = new ComboBox<User>("Share with:");
		sharedUsers.setItems(getUsers);
		sharedUsers.setItemCaptionGenerator(User::getFullName);
		sharedUsers.addSelectionListener(u -> {
			if (!Objects.isNull(u.getValue())) {
				if (userShare.contains(u.getValue().getUserId())) {
					HorizontalLayout lUsr = new HorizontalLayout();
					Button clearUsrs = new Button();
					lUsr.addComponentsAndExpand(new Label(u.getValue().getFullName().toUpperCase()), clearUsrs);
					clearUsrs.setIcon(FontAwesome.REMOVE);
					clearUsrs.addClickListener(listener -> {
						userShare.remove(u.getValue().getUserId());
						lUsrs.removeComponent(lUsr);
					});
					userShare.add(u.getValue().getUserId());
					lUsrs.addComponent(lUsr);
				}
			}
		});
		row4.addColumn().withDisplayRules(12, 0, 0, 0).withComponent(sharedUsers);
		row5.addColumn().withDisplayRules(12, 0, 0, 0).withComponent(lUsrs);

		ResponsiveRow row6 = addRow().withMargin(true);
		row6.setSpacing(true);
		Button addFiles = new Button("Upload Files");
		addFiles.setIcon(FontAwesome.PLUS_CIRCLE);
		addFiles.addClickListener(listener -> {
			isPrivate.setValue(false);
			sharedUsers.setSelectedItem(null);
			userShare.clear();
			lUsrs.removeAllComponents();
			Window window = new Window();
			window.setClosable(true);
			window.center();
			window.setWidth("30%");
			window.setHeight("50%");
			window.setModal(true);
			if(!userShare.isEmpty())
				isShared = true;
			UploadComponent u = new UploadComponent(userPath, userId, userShare, isPrivate.getValue(),isShared,window);
			u.setSizeFull();
			window.setContent(u);
			UI.getCurrent().addWindow(window);
		});
		row6.addColumn().withDisplayRules(12, 0, 0, 0).withComponent(addFiles);
	}
}
