package com.ipamc.election.views.components;

import java.util.List;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.vaadin.crudui.crud.impl.GridCrud;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;


public class ManageSessions extends VerticalLayout {

	private SessionService sessionService;
	
	private Button findAllButton;
	private Button addButton;
	private Button deleteButton;
	private Button updateButton;
	private TextField searchBar;
	private Button backButton;

	private Grid<Session> grid;
	private List<Session> sessions;
	
	private CreateSession createSession;
	
	private Div hint = new Div();

	public ManageSessions(UserService userService, SessionService sessionService, QuestionService questionService) {
		this.sessionService = sessionService;
		this.sessions = sessionService.findAllSessions();
		this.createSession = new CreateSession(userService, sessionService, questionService);
		createSession.getStyle().set("padding-top", "0px");
		initLayout();
		refreshGrid();
	}

	private void initLayout() {
		HorizontalLayout buttons = new HorizontalLayout();

		addButton = new Button(VaadinIcon.PLUS.create());
		addButton.getElement().setAttribute("title", "Add");

		updateButton = new Button(VaadinIcon.PENCIL.create());
		updateButton.setEnabled(false);
		updateButton.getElement().setAttribute("title", "Update");

		deleteButton = new Button(VaadinIcon.TRASH.create());
		deleteButton.setEnabled(false);
		deleteButton.getElement().setAttribute("title", "Delete");
		
		backButton = new Button("Retour");
		backButton.setVisible(false);
		
		createSession.setVisible(false);
		createGrid(createSession);
		
		GridListDataView<Session> dataView = grid.setItems(sessions);
		searchBar = searchBarSession(dataView);
		buttons.add(searchBar, addButton, updateButton, deleteButton, backButton);
		HorizontalLayout mainComponents = new HorizontalLayout();
		
		mainComponents.add(grid, createSession);
		createSession.setWidth("50em");
		mainComponents.setSizeFull();
		add(buttons, hint, mainComponents);
		setSpacing(false);
		initAddButton();
		initBackButton();
		updateButtons();
	}



	private void updateButtons() {
		grid.addItemClickListener(event -> {
			boolean rowSelected = !grid.asSingleSelect().isEmpty();
			updateButton.setEnabled(rowSelected);
			deleteButton.setEnabled(rowSelected);
		});
	}

	private void createGrid(CreateSession createSession) {
		grid = new Grid<>(Session.class, false);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addColumn(Session::getName).setHeader("Nom");
		grid.addComponentColumn(session -> new Button(Integer.toString(session.getUsers().size()), click -> {
			Dialog dialog = new Dialog();
			dialog.getElement().setAttribute("aria-label", "Add note");
			VerticalLayout dialogLayout = dialogShowJury(session);
			dialog.add(dialogLayout);
			dialog.setHeaderTitle(session.getName());
			Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
			closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			dialog.getHeader().add(closeButton);
			dialog.open();
		})).setHeader("Membres du jury");
		grid.addComponentColumn(session -> new Button(Integer.toString(session.getQuestions().size()), click -> {
			Dialog dialog = new Dialog();
			dialog.getElement().setAttribute("aria-label", "Add note");
			VerticalLayout dialogLayout = dialogShowQuestion(session);
			dialog.add(dialogLayout);
			dialog.setHeaderTitle(session.getName());
			Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
			closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			dialog.getHeader().add(closeButton);
			dialog.open();
		})).setHeader("Questions");
		grid.setItems(sessionService.findAllSessions());
		grid.setWidth("75%");
		grid.addItemClickListener(event -> {
			createSession.setVisible(false);
		});
	}

	private VerticalLayout dialogShowJury(Session session) {
		Grid<User> grid = setupJuryGrid();
		GridListDataView<User> dataView = grid.setItems(session.getUsers());
		TextField searchField = createSearchField(dataView);
		VerticalLayout fieldLayout = new VerticalLayout();
		fieldLayout.add(searchField, grid);
		fieldLayout.setSpacing(false);
		fieldLayout.setPadding(false);
		fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
		fieldLayout.getStyle().set("width", "400px").set("max-width", "100%");
		return fieldLayout;
	}

	private VerticalLayout dialogShowQuestion(Session session) {
		VerticalLayout mainLayout = new VerticalLayout();
		for(Question quest : session.getQuestions()) {
			VerticalLayout detailsLayout = new VerticalLayout();
			detailsLayout.setSpacing(false);
			detailsLayout.setPadding(false);
			if(!quest.getCategories().isEmpty()) {
				for(Categorie cat : quest.getCategories()) {
					String isRequired = "Pas obligatoire";
					if(cat.getIsRequired()) {
						isRequired = "Obligatoire";
					}
					String libelle = cat.getLibelle();
					if(libelle.equals("Note")) {
						libelle += " (/"+cat.getValeur()+")";
					}
					detailsLayout.add(new Label(libelle + " : "+ isRequired));
				}
			}
			if(!quest.getPropositions().isEmpty()) {
				String isRequired = "Pas obligatoire";
				String isQcm = "Une seule réponse possible";
				VerticalLayout propositions = new VerticalLayout();
				propositions.setPadding(false);
				detailsLayout.setSpacing(false);
				if(quest.getMultiChoice()) {
					isQcm = "Plusieurs réponses possibles";
				}
				if(quest.getPropositionRequired()) {
					isRequired = "Obligatoire";
				}
				detailsLayout.add(new Label("Propositions: "+isRequired +" - "+ isQcm));
				int cpt = 1;
				for(Proposition prop : quest.getPropositions()) {
					propositions.add(new Label(+cpt+": "+prop.getLibelle()));
					cpt++;
				}
				Details propDetails = new Details("Liste des propositions", propositions);
				propDetails.getStyle().set("padding-left","20px");
				propDetails.getStyle().set("padding-top","0px");
				detailsLayout.add(propDetails);
			}
			detailsLayout.setSpacing(false);
			detailsLayout.setPadding(false);
			detailsLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
			detailsLayout.getStyle().set("width", "450px").set("max-width", "100%");
			mainLayout.add(new Details(quest.getIntitule(), detailsLayout));
			mainLayout.getStyle().set("width", "450px").set("max-width", "100%");
		}
		return mainLayout;
	}



	private static Grid<User> setupJuryGrid() {
		Grid<User> myGrid = new Grid<>(User.class, false);
		myGrid.addColumn(User::getUsername).setHeader("Nom d'utilisateur").setSortable(true);
		myGrid.addComponentColumn(userPermissions -> createPermissionIcon(userPermissions.certified())).setHeader("Certifié").setTextAlign(ColumnTextAlign.CENTER);               
		return myGrid;
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

	private boolean matchesTerm(String value, String searchTerm) {
		return value.toLowerCase().contains(searchTerm.toLowerCase());
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

	private TextField searchBarSession(GridListDataView<Session> sessions) {
		TextField searchField = new TextField();
		searchField.setWidth("100%");
		searchField.setPlaceholder("Recherche");
		searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.addValueChangeListener(e -> sessions.refreshAll());

		sessions.addFilter(session -> {
			String searchTerm = searchField.getValue().trim();

			if (searchTerm.isEmpty())
				return true;
			return matchesTerm(session.getName(),searchTerm);
		});
		return searchField;
	}

	public void initDeleteButton(Div sp) {
		deleteButton.addClickListener(event ->{
			Session sess = grid.getSelectedItems().iterator().next();
			ConfirmDialog.create()
			.withCaption("Confirmation")
			.withMessage("Voulez vous supprimer la session "+sess.getName() +" ?")
			.withOkButton(() -> {
				sessionService.removeSession(sess.getId());
				sessions.remove(sess);
				grid.deselectAll();
				deleteButton.setEnabled(false);
				refreshGrid();
				sp.removeAll();
				sp.add(createBadge(sessionService.getNumberOfSessions()));
				Notification notification = Notification.show("Session supprimée!");
				notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				notification.setDuration(1500);
				notification.setPosition(Position.TOP_END);
			}, ButtonOption.focus(), ButtonOption.caption("OUI"))
			.withCancelButton(ButtonOption.caption("NON")).open();
		});
	}
		
	private void initAddButton() {
		addButton.addClickListener(event -> {
			createSession.setVisible(true);
			grid.setVisible(false);
			backButton.setVisible(true);
			addButton.setVisible(false);
			deleteButton.setVisible(false);
			updateButton.setVisible(false);
			searchBar.setVisible(false);
			hint.removeAll();
			hint.add(new H4("Nouvelle session:"));
			grid.deselectAll();
		});
	}
	
	private void initBackButton() {
		backButton.addClickListener(event ->{
			grid.setVisible(true);
			createSession.setVisible(false);
			backButton.setVisible(false);
			addButton.setVisible(true);
			deleteButton.setVisible(true);
			updateButton.setVisible(true);
			searchBar.setVisible(true);
			refreshGrid();
		});
	}
	
	public void refreshGrid() {
        if (sessions.size() > 0) {
            grid.setVisible(true);
            hint.setVisible(false);
            grid.getDataProvider().refreshAll();
        } else {
            grid.setVisible(false);
            hint.removeAll();
            hint.add(new H4("Aucune session disponible..."));
            hint.setVisible(true);
        }
	}
	
	public CreateSession getCreateSession() {
		return createSession;
	}
	
	public void setCreateSession(CreateSession createSession) {
		this.createSession = createSession;
	}

	public Button getBackButton() {
		return backButton;
	}

	public Grid<Session> getGrid() {
		return grid;
	}

	public void addSession(Session session) {
		sessions.add(session);
	}
	
	private Span createBadge(Long value) {
		Span badge = new Span(String.valueOf(value));
		badge.getElement().getThemeList().add("badge small contrast");
		badge.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
		return badge;
	}
	
}
