package com.ipamc.election.views.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ipamc.election.data.entity.Jure;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.dnd.GridDragEndEvent;
import com.vaadin.flow.component.grid.dnd.GridDragStartEvent;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

public class GridDetailsUsers extends FormLayout {

	GridListDataView<User> dataView;
	private UserService userService;

	public GridDetailsUsers(UserService userService) {
		this.userService = userService;	
		initGrid();
	}

	public void initGrid() {
		Grid<User> grid = setupGrid();
		dataView = grid.setItems(userService.findAll());
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		Select<String> select = new Select<>();

		TextField searchGrid = createSearchField(grid, dataView);

		Label user = new Label("Liste des utilisateurs");
		user.getStyle().set("font-size", "16px");

		select.setItems("Tous les utilisateurs", "Certifiés", "Non certifiés", "Activés", "Non activés");
		select.setValue("Tous les utilisateurs");
		select.addValueChangeListener(event ->{
			searchGrid.setValue("");
			switch(select.getValue()) {
			case "Tous les utilisateurs" : 
				Set<User> fullUsers = new HashSet<>();
				for (User u :  userService.findAll()) {
					fullUsers.add(u);
				}
				Iterator<User> it2 = dataView.getItems().iterator();
				List<User> removeList = new ArrayList<>();
				while(it2.hasNext()) {
					removeList.add(it2.next());
				}
				dataView.removeItems(removeList);
				dataView.addItems(fullUsers);
				break;
			case "Certifiés" : 
				Set<User> users = new HashSet<>();
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
				default:
					break;
			}
    	
		});

		VerticalLayout vert1 = new VerticalLayout();
		HorizontalLayout tools = new HorizontalLayout();
		tools.setSizeFull();
		tools.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		tools.add(select);
	
		vert1.add(user, tools, grid);
		vert1.setSpacing(false);
		vert1.setMargin(false);
		vert1.setPadding(false);
		vert1.setWidth("50%");
		//vert1.setAlignItems(Alignment.CENTER);


		add(vert1);	
		//getStyle ().set ( "border" , "6px dotted DarkOrange" );  // DEBUG - Visually display the  bounds of this layout.
		setSizeFull ();
		setMaxWidth("700px");
		setResponsiveSteps(
				new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("351px", 2, ResponsiveStep.LabelsPosition.TOP));
		setColspan(vert1, 1);

	}

	private Grid<User> setupGrid() {
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
		})).setHeader("Certifié");
		grid.addComponentColumn(userPermissions -> createPermissionIcon(userPermissions.isActive())).setHeader("Activé").setTextAlign(ColumnTextAlign.CENTER);
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
		HorizontalLayout hl = new HorizontalLayout();
		searchBar.setSizeUndefined();
		hl.add(searchBar);
		hl.setWidthFull();
		hl.setJustifyContentMode(JustifyContentMode.BETWEEN);
		Button removeSelected = new Button(new Icon(VaadinIcon.TRASH));
		hl.add(removeSelected);
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
