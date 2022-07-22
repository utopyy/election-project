package com.ipamc.election.views.components;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Jure;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
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
import com.vaadin.flow.data.value.ValueChangeMode;


public class ManageSessions extends VerticalLayout {

	private SessionService sessionService;
	
	private Button addButton;
	private Button deleteButton;
	private Button updateButton;
	private Button backButton;

	private Grid<Session> grid;
	private List<Session> sessions;
	private TextField searchBar;
	
	private CreateSession createSession;
	private EditSession editSession;
	
	private Div hint = new Div();
	
	private H4 titleTools = new H4();

	public ManageSessions(UserService userService, SessionService sessionService, QuestionService questionService) {
		this.sessionService = sessionService;
		this.sessions = sessionService.findSessionsNotArchived();
		this.createSession = new CreateSession(userService, sessionService, questionService);
		createSession.getStyle().set("padding-top", "0px");
		initLayout(userService, questionService);
		refreshGrid();
	}

	private void initLayout(UserService userService, QuestionService questionService) {
		FormLayout buttons = new FormLayout();
		
		addButton = new Button(VaadinIcon.PLUS.create());
		addButton.getElement().setAttribute("title", "Add");

		updateButton = new Button(VaadinIcon.PENCIL.create());
		updateButton.setEnabled(false);
		updateButton.getElement().setAttribute("title", "Update");

		deleteButton = new Button(VaadinIcon.TRASH.create());
		deleteButton.setEnabled(false);
		deleteButton.getElement().setAttribute("title", "Delete");
		HorizontalLayout hl = new HorizontalLayout();

		backButton = new Button(new Icon("lumo","arrow-left"));
		hl.add(backButton);
		hl.add(titleTools);
		backButton.setVisible(false);
		titleTools.setVisible(false);
		titleTools.getStyle().set("margin-top", "6px");
		hl.setAlignItems(Alignment.CENTER);
		
		createSession.setVisible(false);
		createGrid();		
		GridListDataView<Session> dataView = grid.setItems(sessions);
		createSearchBar(dataView);
		Span spacing = new Span();
		buttons.add(searchBar, addButton, updateButton, deleteButton, hl);
		addButton.setMaxWidth("15px");
		updateButton.setMaxWidth("15px");
		deleteButton.setMaxWidth("15px");
		buttons.setResponsiveSteps(
				new ResponsiveStep("50px", 1, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("100px", 2, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("200px", 3, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("300px", 4, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("400px", 5, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("500px", 6, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("600px", 7, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("700px", 8, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("800px", 9, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("900px", 10, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("1000px", 11, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("1100px", 12, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("1200px", 13, ResponsiveStep.LabelsPosition.TOP));	
		buttons.setColspan(searchBar, 3);
		buttons.setColspan(spacing, 8);
		buttons.setColspan(addButton, 1);
		buttons.setColspan(updateButton, 1);
		buttons.setColspan(deleteButton, 1);
		buttons.setColspan(hl, 4);
		HorizontalLayout mainComponents = new HorizontalLayout();
		
		mainComponents.add(grid, createSession);
		createSession.setWidth("100%");
		mainComponents.setSizeFull();
		add(buttons, hint, mainComponents);
		setSpacing(false);
		initAddButton(searchBar);
		initBackButton(searchBar);
		initUpdateButton(userService, questionService, searchBar);
		updateButtons();
	}



	private void updateButtons() {
		grid.addItemClickListener(event -> {
			boolean rowSelected = !grid.asSingleSelect().isEmpty();
			updateButton.setEnabled(rowSelected);
			deleteButton.setEnabled(rowSelected);
		});
	}

	private void createGrid() {
		grid = new Grid<>(Session.class, false);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addColumn(Session::getName).setHeader("Nom").setResizable(true).setSortable(true);
		grid.addComponentColumn(session -> new Button(Integer.toString(session.getJures().size()), click -> {
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
		grid.setWidth("100%");
	}
	

	private VerticalLayout dialogShowJury(Session session) {
		Grid<Jure> grid = setupJuryGrid();
		GridListDataView<Jure> dataView = grid.setItems(session.getJures());
		TextField searchField = createSearchField(dataView);
		VerticalLayout fieldLayout = new VerticalLayout(searchField, grid);
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



	private static Grid<Jure> setupJuryGrid() {
		Grid<Jure> myGrid = new Grid<>(Jure.class, false);
		myGrid.addColumn(jure -> jure.getUser().getUsername()).setHeader("Nom d'utilisateur").setSortable(true);
		myGrid.addComponentColumn(userPermissions -> createPermissionIcon(userPermissions.getUser().certified())).setHeader("Certifié").setTextAlign(ColumnTextAlign.CENTER);               
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

	private TextField createSearchField(GridListDataView<Jure> jury) {
		TextField searchField = new TextField();
		searchField.setWidth("100%");
		searchField.setPlaceholder("Recherche");
		searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.addValueChangeListener(e -> jury.refreshAll());

		jury.addFilter(jure -> {
			String searchTerm = searchField.getValue().trim();

			if (searchTerm.isEmpty())
				return true;
			return matchesTerm(jure.getUser().getUsername(),searchTerm);
		});
		return searchField;
	}
	
	private void createSearchBar(GridListDataView<Session> dataView) {
		searchBar = new TextField();
		searchBar.setWidth("100%");
		searchBar.setPlaceholder("Recherche");
		searchBar.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		searchBar.setValueChangeMode(ValueChangeMode.EAGER);
		searchBar.addValueChangeListener(event -> dataView.refreshAll());

		dataView.addFilter(session -> {
			String searchTerm = searchBar.getValue().trim();
			if (searchTerm.isEmpty())
				return true;
			return matchesTerm(session.getName(),searchTerm);
		});
		searchBar.addFocusListener(event -> {
			grid.deselectAll();
			deleteButton.setEnabled(false);
			updateButton.setEnabled(false);			
		});
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
				updateButton.setEnabled(false);
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
		
	private void initAddButton(TextField searchBar) {
		addButton.addClickListener(event -> {
			createSession.setVisible(true);
			grid.setVisible(false);
			backButton.setVisible(true);
			titleTools.setText("Création d'une nouvelle session");
			titleTools.setVisible(true);
			addButton.setVisible(false);
			deleteButton.setVisible(false);
			updateButton.setVisible(false);
			searchBar.setVisible(false);
			hint.removeAll();
			grid.deselectAll();
		});
	}
	private void initUpdateButton(UserService userService, QuestionService questionService, TextField searchBar) {
		updateButton.addClickListener(event -> {
			Session sess = grid.getSelectedItems().iterator().next();
			editSession = new EditSession(userService, sessionService, questionService, sess);
			editSession.initUpdateSession(sessionService, questionService, this);
			add(editSession);
			grid.setVisible(false);
			backButton.setVisible(true);
			titleTools.setText("Modification de "+sess.getName());
			titleTools.setVisible(true);
			addButton.setVisible(false);
			deleteButton.setVisible(false);
			updateButton.setVisible(false);
			searchBar.setVisible(false);
			hint.removeAll();
			grid.deselectAll();
		});
	}
	
	private void initBackButton(TextField searchBar) {
		backButton.addClickListener(event ->{
			grid.setVisible(true);
			createSession.setVisible(false);
			try{
				remove(editSession);
			}catch(NullPointerException ex) {};
			backButton.setVisible(false);
			titleTools.setVisible(false);
			addButton.setVisible(true);
			deleteButton.setVisible(true);
			updateButton.setVisible(true);
			searchBar.setVisible(true);
			deleteButton.setEnabled(false);
			updateButton.setEnabled(false);
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
	
	public EditSession getEditSession() {
		return editSession;
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
	
	public void removeSession(Session sess) {
		sessions.remove(sess);
	}
	
	public void disableBtns() {
		updateButton.setEnabled(false);
		deleteButton.setEnabled(false);
	}
	
	
}
