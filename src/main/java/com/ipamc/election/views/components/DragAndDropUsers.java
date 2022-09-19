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
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.dnd.GridDragEndEvent;
import com.vaadin.flow.component.grid.dnd.GridDragStartEvent;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

public class DragAndDropUsers extends FormLayout {

	private User draggedItem;
	private UserService userService;
	GridListDataView<User> dataView1;
	GridListDataView<User> dataView2;

	public DragAndDropUsers(UserService userService) {
		initDragAndDrop(userService);
	}

	public void initDragAndDrop(UserService userService) {
		this.userService = userService;
		List<User> people = userService.findAll();

		ArrayList<User> people1 = new ArrayList<>(people);
		ArrayList<User> people2 = new ArrayList<>();    

		Grid<User> grid1 = setupGrid();
		Grid<User> grid2 = setupGrid();

		dataView1 = grid1.setItems(people1);
		dataView2 = grid2.setItems(people2);
		grid1.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid2.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		dataView2.getItems();
		Select<String> select = new Select<>();
		grid1.setDropMode(GridDropMode.ON_GRID);
		grid1.setRowsDraggable(true);
		grid1.addDragStartListener(this::handleDragStart);
		grid1.addDropListener(e -> {
			dataView2.removeItem(draggedItem);
			dataView1.addItem(draggedItem);
		});
		grid1.addDragEndListener(this::handleDragEnd);
		grid1.setHeight("320px");
		grid2.setDropMode(GridDropMode.ON_GRID);
		grid2.setRowsDraggable(true);
		grid2.addDragStartListener(this::handleDragStart);
		grid2.addDropListener(e -> {
			dataView1.removeItem(draggedItem);
			dataView2.addItem(draggedItem);
		});
		grid2.addDragEndListener(this::handleDragEnd);

		grid2.setHeight("320px");

		grid1.addItemDoubleClickListener(event ->{
			dataView1.removeItem(event.getItem());
			dataView2.addItem(event.getItem());
		});

		grid2.addItemDoubleClickListener(event ->{
			dataView2.removeItem(event.getItem());
			if(select.getValue().equals("Certifiés uniquement")) {
				if(event.getItem().certified()) {
					dataView1.addItem(event.getItem());
				}
			}else {
				dataView1.addItem(event.getItem());
			}
		});

		TextField searchGrid1 = createSearchField(dataView1);
		TextField searchGrid2 = createSearchField(dataView2);


		VerticalLayout vert1 = new VerticalLayout();
		VerticalLayout vert2 = new VerticalLayout();

		Label user = new Label("Liste des utilisateurs");
		user.getStyle().set("font-size", "16px");

		select.setItems("Tous les utilisateurs", "Certifiés uniquement");
		select.setValue("Tous les utilisateurs");
		select.addValueChangeListener(event ->{
			searchGrid1.setValue("");
			searchGrid2.setValue("");
			if(select.getValue().equals("Tous les utilisateurs")) {   		
				Set<User> fullUsers = new HashSet<>();
				for (User u :  userService.findAll()) {
					fullUsers.add(u);
				}
				Iterator<User> it = dataView2.getItems().iterator();
				while(it.hasNext()) {
					User u = it.next();
					fullUsers.remove(u);
				}
				Iterator<User> it2 = dataView1.getItems().iterator();
				List<User> removeList = new ArrayList<>();
				while(it2.hasNext()) {
					removeList.add(it2.next());
				}
				dataView1.removeItems(removeList);
				dataView1.addItems(fullUsers);
			}else {
				Iterator<User> it = dataView1.getItems().iterator();
				List<User> removeList = new ArrayList<>();
				while(it.hasNext()) {
					User u = it.next();
					if(!u.certified()) {
						removeList.add(u);
					} 
				}
				dataView1.removeItems(removeList);
			}	    	
		});

		Button moveToRight = new Button(new Icon(VaadinIcon.ARROW_RIGHT));
		moveToRight.addClickListener(event ->{
			Set<User> usersToMove = new HashSet<>();
			if(select.getValue().equals("Tous les utilisateurs")) {
				for(User u : userService.findAll()) {
					usersToMove.add(u);
				}
			}else {
				for(User u : userService.findCertifiedUsers()) {
					usersToMove.add(u);
				}
			}
			List<User> usersView1 = new ArrayList<>();
			List<User> usersView2 = new ArrayList<>();
			Iterator<User> it = dataView1.getItems().iterator();
			while(it.hasNext()) {
				usersView1.add(it.next());
			}
			it = dataView2.getItems().iterator();
			while(it.hasNext()) {
				User u = it.next();
				usersView2.add(u);
				if(!select.getValue().equals("Tous les utilisateurs") && !u.certified()) {
					usersToMove.add(u);
				}	
			}
			dataView1.removeItems(usersView1);
			dataView2.removeItems(usersView2);
			dataView2.addItems(usersToMove);
		});

		Button moveToLeft = new Button(new Icon(VaadinIcon.ARROW_LEFT));
		moveToLeft.addClickListener(event ->{
			Set<User> usersToMove = new HashSet<>();
			if(select.getValue().equals("Tous les utilisateurs")){
				usersToMove.addAll(userService.findAll());
			}else {
				usersToMove.addAll(userService.findCertifiedUsers());
			}
			clearGrids();
			dataView1.addItems(usersToMove);
		});

		HorizontalLayout tools = new HorizontalLayout();
		tools.setSizeFull();
		tools.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		tools.add(select, moveToRight);
		HorizontalLayout tools2 = new HorizontalLayout();
		tools2.setSizeFull();
		tools2.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
		tools2.add(moveToLeft);
		vert1.add(user, searchGrid1, grid1, tools);
		vert1.setSpacing(false);
		vert1.setMargin(false);
		vert1.setPadding(false);
		vert1.setWidth("50%");
		//vert1.setAlignItems(Alignment.CENTER);
		Label jury = new Label("Jury");
		jury.getStyle().set("font-size", "16px");
		vert2.add(jury, searchGrid2, grid2, tools2);
		vert2.setSpacing(false);
		vert2.setMargin(false);
		vert2.setPadding(false);
		vert2.setWidth("50%");
		vert2.setAlignItems(Alignment.CENTER);

		add(vert1,vert2);	
		//getStyle ().set ( "border" , "6px dotted DarkOrange" );  // DEBUG - Visually display the  bounds of this layout.
		setSizeFull ();
		setMaxWidth("700px");
		setResponsiveSteps(
				new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("351px", 2, ResponsiveStep.LabelsPosition.TOP));
		setColspan(vert1, 1);
		setColspan(vert2, 1);

	}
	
	public void fillDragAndDrop(Session session) {
		clearGrids();
		List<User> usersSelected = new ArrayList<>();
		for(Jure jure : session.getJuresNotArchived()) {
			usersSelected.add(jure.getUser());
		}
		List<User>usersLeftList = new ArrayList<>();
		for(User u : userService.findAll()) {
			if(!usersSelected.contains(u)) {
				usersLeftList.add(u);
			}
		}
		dataView1.addItems(usersLeftList);
		dataView2.addItems(usersSelected);
	}

	private static Grid<User> setupGrid() {
		Grid<User> grid = new Grid<>(User.class, false);
		grid.addColumn(User::getUsername).setHeader("Nom d'utilisateur").setSortable(true);;
		grid.addComponentColumn(userPermissions -> createPermissionIcon(userPermissions.certified())).setHeader("Certifié").setTextAlign(ColumnTextAlign.CENTER);               
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

	private void handleDragStart(GridDragStartEvent<User> e) {
		draggedItem = e.getDraggedItems().get(0);
	}

	private void handleDragEnd(GridDragEndEvent<User> e) {
		draggedItem = null;
	}


	public Set<User> getSelectedUsers(){
		Set<User> users = new HashSet<>();
		Iterator<User> it = dataView2.getItems().iterator();
		while(it.hasNext()) {
			users.add(it.next());
		}
		return users;

	}

	private TextField createSearchField(GridListDataView<User> users) {
		TextField searchField = new TextField();
		searchField.setWidth("100%");
		searchField.setPlaceholder("Recherche");
		searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.addValueChangeListener(e -> users.refreshAll());

		users.addFilter(user -> {
			String searchTerm = searchField.getValue().trim();

			if (searchTerm.isEmpty())
				return true;
			return matchesTerm(user.getUsername(),searchTerm);
		});
		return searchField;
	}
	
	private void clearGrids() {
		List<User> usersView1 = new ArrayList<>();
		List<User> usersView2 = new ArrayList<>();
		Iterator<User> it = dataView1.getItems().iterator();
		while(it.hasNext()) {
			usersView1.add(it.next());
		}
		it = dataView2.getItems().iterator();
		while(it.hasNext()) {
			usersView2.add(it.next());
		}
		dataView1.removeItems(usersView1);
		dataView2.removeItems(usersView2);
	}
}
