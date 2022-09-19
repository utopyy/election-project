package com.ipamc.election.views.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ipamc.election.data.EnumRole;
import com.ipamc.election.data.entity.Role;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.services.RoleService;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

public class GridDetailsUsers extends FormLayout {

	GridListDataView<User> dataView;
	private UserService userService;
	private RoleService roleService;

	public GridDetailsUsers(User currentUser, UserService userService, RoleService roleService) {
		this.userService = userService;	
		this.roleService = roleService;
		initGrid(currentUser);
	}

	public void initGrid(User currentUser) {
		Grid<User> grid = setupGrid(currentUser);
		grid.setSizeFull();
		dataView = grid.setItems(userService.findAll());
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		Select<String> select = new Select<>();

		TextField searchGrid = createSearchField(grid, dataView);
		HorizontalLayout hl = new HorizontalLayout();
		hl.add(searchGrid);
		hl.setWidthFull();
		hl.setJustifyContentMode(JustifyContentMode.BETWEEN);
		Button removeSelected = new Button(new Icon(VaadinIcon.TRASH));
		hl.add(select);
		hl.add(removeSelected);
		add(hl);
		select.setItems("Tous les utilisateurs", "Certifiés", "Non certifiés", "Activés", "Non activés", "Membres", "Administrateurs", "Super administrateurs");
		select.addComponents("Tous les utilisateurs", new Hr());
		select.addComponents("Non certifiés", new Hr());
		select.addComponents("Non activés", new Hr());
		select.setValue("Tous les utilisateurs");
		select.addValueChangeListener(event ->{
			searchGrid.setValue("");
			switch(select.getValue()) {
			case "Tous les utilisateurs" : 
				Set<User> users = new HashSet<>();
				for (User u :  userService.findAll()) {
					users.add(u);
				}
				Iterator<User> it2 = dataView.getItems().iterator();
				List<User> removeList = new ArrayList<>();
				while(it2.hasNext()) {
					removeList.add(it2.next());
				}
				dataView.removeItems(removeList);
				dataView.addItems(users);
				break;
			case "Certifiés" : 
				users = new HashSet<>();
				for (User u :  userService.findAllByCertified(true)) {
					users.add(u);
				}
				it2 = dataView.getItems().iterator();
				removeList = new ArrayList<>();
				while(it2.hasNext()) {
					removeList.add(it2.next());
				}
				dataView.removeItems(removeList);
				dataView.addItems(users);
				break;
			case "Non certifiés" : 
				users = new HashSet<>();
				for (User u :  userService.findAllByCertified(false)) {
					users.add(u);
				}
				it2 = dataView.getItems().iterator();
				removeList = new ArrayList<>();
				while(it2.hasNext()) {
					removeList.add(it2.next());
				}
				dataView.removeItems(removeList);
				dataView.addItems(users);
				break;
			case "Activés" : 
				users = new HashSet<>();
				for (User u :  userService.findAllByActive(true)) {
					users.add(u);
				}
				it2 = dataView.getItems().iterator();
				removeList = new ArrayList<>();
				while(it2.hasNext()) {
					removeList.add(it2.next());
				}
				dataView.removeItems(removeList);
				dataView.addItems(users);
				break;
			case "Non activés" : 
				users = new HashSet<>();
				for (User u :  userService.findAllByActive(false)) {
					users.add(u);
				}
				it2 = dataView.getItems().iterator();
				removeList = new ArrayList<>();
				while(it2.hasNext()) {
					removeList.add(it2.next());
				}
				dataView.removeItems(removeList);
				dataView.addItems(users);
				break;
			case "Membres" :
				users = new HashSet<>();
				for (User u :  userService.findAllByRole(EnumRole.ROLE_USER)) {
					users.add(u);
				}
				it2 = dataView.getItems().iterator();
				removeList = new ArrayList<>();
				while(it2.hasNext()) {
					removeList.add(it2.next());
				}
				dataView.removeItems(removeList);
				dataView.addItems(users);
				break;
			case "Administrateurs" : 
				users = new HashSet<>();
				for (User u :  userService.findAllByRole(EnumRole.ROLE_ADMIN)) {
					users.add(u);
				}
				it2 = dataView.getItems().iterator();
				removeList = new ArrayList<>();
				while(it2.hasNext()) {
					removeList.add(it2.next());
				}
				dataView.removeItems(removeList);
				dataView.addItems(users);
				break;
			case "Super administrateurs" :
				users = new HashSet<>();
				for (User u :  userService.findAllByRole(EnumRole.ROLE_SUPER_ADMIN)) {
					users.add(u);
				}
				it2 = dataView.getItems().iterator();
				removeList = new ArrayList<>();
				while(it2.hasNext()) {
					removeList.add(it2.next());
				}
				dataView.removeItems(removeList);
				dataView.addItems(users);
				break;
			default:
				break;
			}
		});

		add(grid);
		setSizeFull ();
		setResponsiveSteps(
				new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("500px", 2, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("800px", 3, ResponsiveStep.LabelsPosition.TOP));
		setColspan(hl, 1);
		setColspan(grid, 3);
	}

	private Grid<User> setupGrid(User currentUser) {
		Grid<User> grid = new Grid<>(User.class, false);
		grid.setAllRowsVisible(true);
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.addColumn(User::getUsername).setHeader("Utilisateur");
		grid.addColumn(User::getEmail).setHeader("Email");
		grid.addColumn(User::getPseudo).setHeader("Pseudo");
		grid.addColumn(new ComponentRenderer<> (user -> {
			Checkbox checkBox = new Checkbox(user.certified());
			checkBox.addValueChangeListener(event -> {
				userService.setCertified(user, checkBox.getValue());
			});
			return checkBox;
		})).setHeader("Certifié").setAutoWidth(true).setFlexGrow(0);;
		grid.addComponentColumn(userPermissions -> createPermissionIcon(userPermissions.isActive())).setHeader("Activé").setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);
		if(currentUser.isSuperAdmin()) {
			grid.addColumn(new ComponentRenderer<> (user -> {
				Select<Role> select = new Select<>();
				select.setItemLabelGenerator(Role::getNameString);
				select.addValueChangeListener(event -> {
					userService.updateRole(user, select.getValue());
				});
				List<Role> roles = roleService.findAll();
				select.setItems(roles);
				select.setValue(user.getRoles().iterator().next());
				return select;
			})).setHeader("Role").setAutoWidth(true).setFlexGrow(0);
		}
		grid.setWidthFull();
		return grid;
	}

	private boolean matchesTerm(String value, String searchTerm) {
		return value.toLowerCase().contains(searchTerm.toLowerCase());
	}

	private static Icon createPermissionIcon(boolean isCertified) {
		Icon icon;
		if (isCertified) {
			icon = createIcon(VaadinIcon.CHECK, "Yes");
			icon.getElement().getThemeList().add("badge success");
		} else {
			icon = createIcon(VaadinIcon.CLOSE_SMALL, "No");
			icon.getElement().getThemeList().add("badge error");
		}
		return icon;
	}

	private static Icon createIcon(VaadinIcon vaadinIcon, String label) {
		Icon icon = vaadinIcon.create();
		icon.getStyle().set("padding", "var(--lumo-space-xs");
		icon.getElement().setAttribute("aria-label", label);
		icon.getElement().setAttribute("title", label);
		return icon;
	}


	private TextField createSearchField(Grid grid, GridListDataView<User> users) {
		TextField searchBar = new TextField();
		searchBar.setWidth("100%");
		searchBar.setPlaceholder("Recherche");
		searchBar.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		searchBar.setValueChangeMode(ValueChangeMode.EAGER);
		searchBar.addValueChangeListener(event -> dataView.refreshAll());

		dataView.addFilter(user -> {
			String searchTerm = searchBar.getValue().trim();
			if (searchTerm.isEmpty())
				return true;
			boolean matchesUsername = matchesTerm(user.getUsername(),searchTerm);
			boolean matchesEmail = matchesTerm(user.getEmail(), searchTerm);
			boolean matchesPseudo;
			try { 
				matchesPseudo = matchesTerm(user.getPseudo(), searchTerm);
			}catch(NullPointerException ex) {
				matchesPseudo = false;
			}
			return matchesUsername | matchesEmail | matchesPseudo;
		});
		searchBar.addFocusListener(event -> {
			grid.deselectAll();		
		});
		searchBar.setSizeUndefined();
		return searchBar;
	}

	private void clearGrids() {
		List<User> usersView1 = new ArrayList<>();
		List<User> usersView2 = new ArrayList<>();
		Iterator<User> it = dataView.getItems().iterator();
		while(it.hasNext()) {
			usersView1.add(it.next());
		}
		it = dataView.getItems().iterator();
		while(it.hasNext()) {
			usersView2.add(it.next());
		}
		dataView.removeItems(usersView1);
	}

}
