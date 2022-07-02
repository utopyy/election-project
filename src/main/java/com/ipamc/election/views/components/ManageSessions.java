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
import com.ipamc.election.services.SessionService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
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

	private Grid<Session> grid;
	private List<Session> sessions;

	public ManageSessions(SessionService sessionService) {
		this.sessionService = sessionService;
		this.sessions = sessionService.findAllSessions();
		initLayout();
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

		createGrid();
		GridListDataView<Session> dataView = grid.setItems(sessions);
		TextField searchBar = searchBarSession(dataView);
		buttons.add(searchBar, addButton, updateButton, deleteButton);

		add(buttons, grid);

		initDeleteButton(dataView);
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

	private void initDeleteButton(GridListDataView<Session> sessionsView) {
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
				grid.getDataProvider().refreshAll();
				Notification notification = Notification.show("Session supprimée!");
				notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				notification.setDuration(1500);
				notification.setPosition(Position.TOP_END);
			}, ButtonOption.focus(), ButtonOption.caption("OUI"))
			.withCancelButton(ButtonOption.caption("NON")).open();
		});
	}



}
