package com.ipamc.election.views;

import com.ipamc.election.data.entity.Jure;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.ipamc.election.views.components.ResultsVotes;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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

@Route(value = "oldvotes", layout = MainLayout.class)
@CssImport("./themes/myapp/hide-hover-grid.css")
@PageTitle("Anciens votes")


public class AdminLogsView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private SecurityUtils tools;
	private SessionService sessionService;
	
	private VerticalLayout gridLayout = new VerticalLayout();
	private VerticalLayout detailQuestLayout;

	private List<Session> sessions;

	public AdminLogsView(UserService userService, SessionService sessionService , SecurityUtils tools) {
		this.userService = userService;
		this.sessionService = sessionService;
		this.tools = tools;
		setSpacing(false);

		initMainLayout();
		loadData();
		setUpGrid();
	}

	private void initMainLayout() {
		setSizeFull();
		setDefaultHorizontalComponentAlignment(Alignment.CENTER);

	}

	private void loadData() {
		sessions = sessionService.loadArchivedSessions();
	}
	
	private void createTopToolBar(Grid<Session> grid) {
		//Searchfield, delete unlocked sessions
		GridListDataView<Session> dataView = grid.setItems(sessions);
		createSearchBar(dataView, grid);
	}
	
	private void createSearchBar(GridListDataView<Session> dataView, Grid<Session> grid) {
		TextField searchBar = new TextField();
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
		});
		HorizontalLayout hl = new HorizontalLayout();
		searchBar.setSizeUndefined();
		hl.add(searchBar);
		hl.setWidthFull();
		hl.setJustifyContentMode(JustifyContentMode.BETWEEN);
		gridLayout.add(hl);
	}

	private void setUpGrid() {
		Grid<Session> grid = new Grid<>(Session.class, false);
		grid.setAllRowsVisible(true);
		grid.addColumn(Session::getName).setHeader("Nom");
		grid.addComponentColumn(session -> new Button(Integer.toString(session.getJuresNotArchived().size()), click -> {
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
		grid.addComponentColumn(session -> new Label(Integer.toString(session.getQuestions().size()))).setHeader("Nombre de questions");
		grid.addColumn(new ComponentRenderer<> (session -> {
			Checkbox checkBox = new Checkbox(session.getIsLocked());
			checkBox.addValueChangeListener(event -> {
				sessionService.lockSession(session, checkBox.getValue());
			});
			return checkBox;
		})).setHeader("Verrouiller la session");
		grid.addComponentColumn(session -> new Button(new Icon(VaadinIcon.TRASH), click -> {
			ConfirmDialog.create()
			.withCaption("Action irréversible")
			.withMessage(new VerticalLayout(new Label("Supprimer définitivement la session  \" "+session.getName() +" \" ?"), new Label("Toutes les questions et les votes de cette session seront définitivement supprimés."))
					).withOkButton(() -> {
						sessionService.removeSession(session.getId());
						sessions.remove(session);
						grid.getDataProvider().refreshAll();
					}, ButtonOption.focus(), ButtonOption.caption("OUI"))
			.withCancelButton(ButtonOption.caption("NON")).open();
		}));

		grid.setItemDetailsRenderer(createSessionDetailsRenderer());
		createTopToolBar(grid);
		gridLayout.add(grid);
		gridLayout.setSizeFull();
		add(gridLayout);
	}

	private ComponentRenderer<SessionDetailsFormLayout, Session> createSessionDetailsRenderer() {
		return new ComponentRenderer<>(SessionDetailsFormLayout::new,
				SessionDetailsFormLayout::setQuestions);
	}

	private class SessionDetailsFormLayout extends VerticalLayout {
		private Grid<Question> grid = new Grid<>(Question.class, false);
		public SessionDetailsFormLayout() {
			setPadding(false);
			setSpacing(false);
			grid.setClassName("v-grid-cell-focused");
			grid.addComponentColumn(question -> new Label(question.getIntitule())).setAutoWidth(true).setFlexGrow(0);
			grid.addComponentColumn(question -> {
				Button votes = new Button("Afficher les votes");
				configVoteBtn(votes, question);
				return votes;
			}).setAutoWidth(true).setFlexGrow(0);
			grid.setAllRowsVisible(true);
			grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
			grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);		
		}

		public void setQuestions(Session sess) {
			grid.setItems(sess.getQuestions());
			add(grid);
		}
	}

	private VerticalLayout dialogShowJury(Session session) {
		Grid<Jure> grid = setupJuryGrid();
		GridListDataView<Jure> dataView = grid.setItems(session.getJuresNotArchived());
		TextField searchField = createSearchField(dataView);
		VerticalLayout fieldLayout = new VerticalLayout(searchField, grid);
		fieldLayout.setSpacing(false);
		fieldLayout.setPadding(false);
		fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
		fieldLayout.getStyle().set("width", "400px").set("max-width", "100%");
		return fieldLayout;
	}

	private static Grid<Jure> setupJuryGrid() {
		Grid<Jure> myGrid = new Grid<>(Jure.class, false);
		myGrid.addColumn(jure -> jure.getUser().getUsername()).setHeader("Nom d'utilisateur").setSortable(true);
		myGrid.addComponentColumn(userPermissions -> createPermissionIcon(userPermissions.getUser().certified())).setHeader("Certifié").setTextAlign(ColumnTextAlign.CENTER);               
		return myGrid;
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
	
	private void configVoteBtn(Button btn, Question question) {
		btn.addClickListener(event -> {
			gridLayout.setVisible(false);
			HorizontalLayout hl = new HorizontalLayout();
			hl.setWidthFull();
			hl.setPadding(false);
			hl.setAlignItems(Alignment.START);
			Button back = new Button(new Icon(VaadinIcon.ARROW_LEFT));
			back.addClickListener(event2 -> {
				remove(detailQuestLayout);
				gridLayout.setVisible(true);
			});
			hl.add(back);
			detailQuestLayout = new VerticalLayout();
			detailQuestLayout.add(hl);
			detailQuestLayout.add(new ResultsVotes(question, false, false));
			detailQuestLayout.setSpacing(false);
			detailQuestLayout.setPadding(false);
			add(detailQuestLayout);
		});
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
