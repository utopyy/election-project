package com.ipamc.election.views;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.ipamc.election.data.entity.Broadcaster;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.ipamc.election.views.components.ManageSessions;
import com.ipamc.election.views.components.StartSession;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;


@Route(value = "gestionsalon", layout = MainLayout.class)
@PageTitle("Gestion du salon")

public class AdminRoomSettingsView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private SecurityUtils tools;
	private SessionService sessionService;
	private QuestionService questionService;
	private final VerticalLayout content;
	private ManageSessions manageSessionsView;
	private StartSession startSession;
	private final Tab activeSession;
	private Tab manageSessions;
	private Div sp = new Div();
	Registration broadcasterRegistration;
	ProgressBar pg;
	
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = attachEvent.getUI();
		broadcasterRegistration = Broadcaster.register(newMessage -> {
			ui.access(() -> pg.setVisible(true));
			if(newMessage.equals("ARCHIVE_SESSION")) {
				ui.access(() -> manageSessionsView.archiveSess());
			}
			ui.access(() -> pg.setVisible(false));
		});
	}
	
	public AdminRoomSettingsView(UserService userService, SecurityUtils tools, SessionService sessionService,
			QuestionService questionService) {

		this.userService = userService;
		this.sessionService = sessionService;
		this.questionService = questionService;
		this.manageSessionsView = new ManageSessions(userService, sessionService, questionService);
		this.startSession = new StartSession(sp, sessionService);
		this.tools = tools;

		setSpacing(false);
		activeSession = new Tab("Lancer une session");
		sp.add(createBadge(Long.valueOf(sessionService.findSessionsNotArchived().size())));
		manageSessionsView.initDeleteButton(sp);
		manageSessions = new Tab(new Span("Gérer les sessions"), sp);

		Tabs tabs = new Tabs(activeSession, manageSessions);
		tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
		tabs.setSizeFull();
		tabs.setMaxHeight("20px");
		add(tabs);

		setupSaveBtn(userService);

		tabs.addSelectedChangeListener(event ->
		setContent(event.getSelectedTab())
				);
		content = new VerticalLayout();
		content.setSpacing(false);
		content.setSizeFull();
		setContent(tabs.getSelectedTab());
		add(tabs, content);
		getStyle().set("background-color","rgb(251,253,255)");
		setSizeFull();
		pg = new ProgressBar();
		pg.setIndeterminate(true);
		pg.setVisible(false);
		add(pg);
	}

	private void setContent(Tab tab) {
		content.removeAll();

		if (tab.equals(activeSession)) {
			startSession.refreshSelect(sessionService);
			content.add(startSession);
		} else {
			//manageSessionsView.refreshGrid();
			manageSessionsView.disableBtns();
			content.add(manageSessionsView);
		}
	}

	private Span createBadge(Long value) {
		Span badge = new Span(String.valueOf(value));
		badge.getElement().getThemeList().add("badge small contrast");
		badge.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
		return badge;
	}
	
	private void setupSaveBtn(UserService userService) {
		manageSessionsView.getCreateSession().getSaveSession().addClickListener(event ->{
			ConfirmDialog.create()
			.withCaption("Confirmation")
			.withMessage("Création de la session: "+manageSessionsView.getCreateSession().getSessionName().getValue()+ " ?")
			.withOkButton(() -> {
				Session sess = sessionService.createSession(manageSessionsView.getCreateSession().getSessionName().getValue(), manageSessionsView.getCreateSession().getDragAndDrop().getSelectedUsers());
				for(Question quest : manageSessionsView.getCreateSession().getQuestionsCreator().getQuestions()) {
					questionService.createQuestion(quest, sess);
				}
				Session newFullSess = sessionService.getBySessionName(sess.getName());
				manageSessionsView.addSession(newFullSess);
				manageSessionsView.getBackButton().click();
				manageSessionsView.getCreateSession().clearForm(userService);
				sp.removeAll();
				sp.add(createBadge(sessionService.getNumberOfSessions()));
				setupSaveBtn(userService);
				Notification notification = Notification.show("Session créée avec succès!");
				notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				notification.setDuration(3000);
				notification.setPosition(Position.TOP_END);
			}, ButtonOption.focus(), ButtonOption.caption("OUI"))
			.withCancelButton(ButtonOption.caption("NON")).open();
		});
	}
	
	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		try { 
			if(!(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).isActive())) {
				beforeEnterEvent.forwardTo("registration_confirm/"+tools.getAuthenticatedUser().getUsername());	
			}
		}catch(NullPointerException ex) {
		 UI.getCurrent().navigate(AccessDenied403.class);
		};

	}

}
