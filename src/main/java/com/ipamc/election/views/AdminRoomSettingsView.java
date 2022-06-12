package com.ipamc.election.views;

import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.ipamc.election.views.components.CreateSession;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
	private CreateSession createSessionView;
	private final Tab createSession;
	private final Tab activeSession;
	private Tab manageSessions;
	private Span sp;
	
    public AdminRoomSettingsView(UserService userService, SecurityUtils tools, SessionService sessionService,
    		QuestionService questionService) {

    	this.userService = userService;
    	this.sessionService = sessionService;
    	this.questionService = questionService;
    	this.tools = tools;
    	createSessionView = new CreateSession(userService, sessionService, questionService);
        setSpacing(false);
        createSession = new Tab("Créer une session");
		activeSession = new Tab("Lancer une session");
		sp = createBadge(sessionService.getNumberOfSessions());
		manageSessions = new Tab(new Span("Gérer mes sessions"), sp);

		Tabs tabs = new Tabs(createSession, activeSession, manageSessions);
		tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
		tabs.setSizeFull();
		add(tabs);
		
		tabs.addSelectedChangeListener(event ->
		setContent(event.getSelectedTab())
		);
		
		content = new VerticalLayout();
		content.setSpacing(false);
		setContent(tabs.getSelectedTab());

		add(tabs, content);
		
		createSessionView.addButtonEvent(manageSessions, sessionService);
    }
    
    private void setContent(Tab tab) {
		content.removeAll();

		if (tab.equals(activeSession)) {
			content.add(new Paragraph("This is the ActiveSession tab"));
		} else if (tab.equals(manageSessions)) {
			content.add(new Paragraph("This is the ManageSessions tab"));
		} else {
			content.add(createSessionView);
		}
	}
    
    private Span createBadge(Long value) {
		Span badge = new Span(String.valueOf(value));
		badge.getElement().getThemeList().add("badge small contrast");
		badge.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
		return badge;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
   		if(!(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).isActive())) {
   			beforeEnterEvent.forwardTo("registration_confirm/"+tools.getAuthenticatedUser().getUsername());		
   		}

	}

}
