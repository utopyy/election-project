package com.ipamc.election.views;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.ipamc.election.views.components.CreateSession;
import com.ipamc.election.views.components.ManageSessions;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@Route(value = "gestionsalon", layout = MainLayout.class)
@PageTitle("Gestion du salon")

public class AdminRoomSettingsView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private SecurityUtils tools;
	private SessionService sessionService;
	private QuestionService questionService;
	private final VerticalLayout content;
	private ManageSessions manageSessionsView;
	private final Tab activeSession;
	private Tab manageSessions;
	private Div sp = new Div();

	public AdminRoomSettingsView(UserService userService, SecurityUtils tools, SessionService sessionService,
			QuestionService questionService) {

		this.userService = userService;
		this.sessionService = sessionService;
		this.questionService = questionService;
		this.manageSessionsView = new ManageSessions(userService, sessionService, questionService);
		this.tools = tools;

		setSpacing(false);
		activeSession = new Tab("Lancer une session");
		sp.add(createBadge(sessionService.getNumberOfSessions()));
		manageSessionsView.initDeleteButton(sp);
		manageSessions = new Tab(new Span("Gérer les sessions"), sp);

		Tabs tabs = new Tabs(activeSession, manageSessions);

		tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
		tabs.setSizeFull();
		add(tabs);

		setupSaveBtn();

		tabs.addSelectedChangeListener(event ->
		setContent(event.getSelectedTab())
				);



		content = new VerticalLayout();
		content.setSpacing(false);
		content.setSizeFull();
		setContent(tabs.getSelectedTab());

		add(tabs, content);

	}

	private void setContent(Tab tab) {
		content.removeAll();

		if (tab.equals(activeSession)) {
			content.add(new Paragraph("This is the ActiveSession tab"));
		} else {
			content.add(manageSessionsView);
		}
	}

	private Span createBadge(Long value) {
		Span badge = new Span(String.valueOf(value));
		badge.getElement().getThemeList().add("badge small contrast");
		badge.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
		return badge;
	}
	
	private void setupSaveBtn() {
		manageSessionsView.getCreateSession().getSaveSession().addClickListener(event ->{
			ConfirmDialog.create()
			.withCaption("Confirmation")
			.withMessage("Création de la session: "+manageSessionsView.getCreateSession().getSessionName().getValue()+ " ?")
			.withOkButton(() -> {
				Session sess = sessionService.createSession(manageSessionsView.getCreateSession().getSessionName().getValue(), manageSessionsView.getCreateSession().getDragAndDrop().getSelectedUsers());
				for(Question quest : manageSessionsView.getCreateSession().getQuestionsCreator().getQuestions()) {
					questionService.createQuestion(quest, sess);
				}
				setupSaveBtn();
				Session newFullSess = sessionService.getBySessionName(sess.getName());
				manageSessionsView.addSession(newFullSess);
				manageSessionsView.getBackButton().click();
				sp.removeAll();
				sp.add(createBadge(sessionService.getNumberOfSessions()));
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
		if(!(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).isActive())) {
			beforeEnterEvent.forwardTo("registration_confirm/"+tools.getAuthenticatedUser().getUsername());		
		}

	}

}
