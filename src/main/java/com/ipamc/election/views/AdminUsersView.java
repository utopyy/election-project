package com.ipamc.election.views;

import com.ipamc.election.data.entity.User;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.RoleService;
import com.ipamc.election.services.UserService;
import com.ipamc.election.views.components.GridDetailsUsers;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;


import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.security.core.userdetails.UserDetails;

@Route(value = "userlist", layout = MainLayout.class)
@CssImport("./themes/myapp/hide-hover-grid.css")
@PageTitle("Liste des utilisateurs")


public class AdminUsersView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private SecurityUtils tools;
	
	private VerticalLayout gridLayout = new VerticalLayout();

	private List<User> users;

	public AdminUsersView(UserService userService, RoleService roleService, SecurityUtils tools) {
		this.userService = userService;
		this.tools = tools;
		setSpacing(false);
		GridDetailsUsers grid = new GridDetailsUsers(userService.getByUsername(tools.getAuthenticatedUser().getUsername()), userService, roleService);
		grid.setSizeFull();
		add(grid);
		setSizeFull();
	}


	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		UserDetails authenticatedUser = tools.getAuthenticatedUser();
		if(!(userService.getByUsername(authenticatedUser.getUsername()).isActive())) {
			beforeEnterEvent.forwardTo("registration_confirm/"+authenticatedUser.getUsername());		
		}
		if(authenticatedUser.getAuthorities().iterator().next().toString().equals("ROLE_USER")) {
			beforeEnterEvent.forwardTo("jury");
		}

	}

}
