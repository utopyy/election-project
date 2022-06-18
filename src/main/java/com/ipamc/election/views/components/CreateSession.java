package com.ipamc.election.views.components;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

public class CreateSession extends VerticalLayout {

	private Button saveSession;
	private DragAndDropUsers dragAndDrop;
	private TextField sessionName;
	private CreateQuestionsShowNews questionsCreator;


	private static final String SESSION_NAME = "Nom de la session";
	private static final String QUESTIONS = "Questions";
	private static final String JURY = "Membres du jury";

	public CreateSession(UserService userService, SessionService sessionService, QuestionService questionService) {

		Accordion accordion = new Accordion();
		dragAndDrop = new DragAndDropUsers(userService);

		Binder<Session> sessionBinder = new Binder<>(Session.class);
		sessionBinder.setBean(new Session());

		Binder<Question> questionBinder = new Binder<>(Question.class);
		questionBinder.setBean(new Question());

		Binder<Categorie> categorieBinder = new Binder<>(Categorie.class);
		categorieBinder.setBean(new Categorie());
		Binder<Proposition> propositionBinder = new Binder<>(Proposition.class);
		propositionBinder.setBean(new Proposition());

		FormLayout sessionDetailsFormLayout = createFormLayout();
		AccordionPanel sessionDetailsPanel = accordion.add(SESSION_NAME,  sessionDetailsFormLayout);

		HorizontalLayout juryLayout = new HorizontalLayout();
		juryLayout.add(dragAndDrop);
		juryLayout.setSizeFull();
		dragAndDrop.getStyle().set("padding-top", "12px");
		dragAndDrop.getStyle().set("padding-bottom", "12px");
		AccordionPanel juryPanel = accordion.add(JURY, juryLayout);

		FormLayout questionsFormLayout = createFormLayout();
		AccordionPanel questionsPanel = accordion.add(QUESTIONS,  questionsFormLayout);

		juryPanel.setEnabled(false);
		questionsPanel.setEnabled(false);
		// Session name details
		sessionName = new TextField();
		sessionName.setPlaceholder("Nom du tournois");
		sessionBinder.forField(sessionName).bind("name");

		sessionDetailsPanel.addOpenedChangeListener(e -> {
			if(e.isOpened()) {
				sessionDetailsPanel.setSummaryText(SESSION_NAME);
			} else if(sessionBinder.getBean() != null) {
				Session sessionValue = sessionBinder.getBean();
				sessionDetailsPanel.setSummary(createSummary(SESSION_NAME,
						sessionValue.getName()
						));
			}
		});    

		Button sessionDetailsButton = new Button("Suivant");
		sessionDetailsButton.addClickListener(event ->{
			sessionDetailsPanel.setOpened(false);
			sessionDetailsPanel.setEnabled(false);
			juryPanel.setEnabled(true);
			juryPanel.setOpened(true); 
		});
		sessionDetailsButton.setEnabled(false);
		sessionDetailsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		HorizontalLayout hl = new HorizontalLayout(sessionName, sessionDetailsButton);
		sessionDetailsFormLayout.add(hl);

		sessionName.setValueChangeMode(ValueChangeMode.EAGER);
		sessionName.addValueChangeListener(event ->{
			if(sessionName.isEmpty() || sessionName.getValue().isBlank()) {
				sessionDetailsButton.setEnabled(false);
			}else {
				sessionDetailsButton.setEnabled(true);
			}
		});

		// Jury details
		juryPanel.addOpenedChangeListener(e -> {
			if(e.isOpened()) {
				juryPanel.setSummaryText(JURY);
			}else if(dragAndDrop.getSelectedUsers().size() != 0) {
				juryPanel.setSummary(createSummary(JURY, dragAndDrop.getSelectedUsers().size()+" membres ajoutés"));
			}else {
				juryPanel.setSummary(createSummary(JURY, "Aucun membre ajouté"));
			}
		});

		Button backJuryButton = new Button("Retour");
		backJuryButton.addClickListener(event -> {
			juryPanel.setEnabled(false);
			juryPanel.setOpened(false);
			sessionDetailsPanel.setOpened(true);
			sessionDetailsPanel.setEnabled(true);
		});
		Button juryButton = new Button("Continuer");
		juryButton.addClickListener(event -> {
			juryPanel.setEnabled(false);
			juryPanel.setOpened(false);
			questionsPanel.setEnabled(true);
			questionsPanel.setOpened(true);
		});
		juryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		juryPanel.addContent(backJuryButton, juryButton);

		// Questions details
		questionsCreator = new CreateQuestionsShowNews();
		questionsCreator.setSizeFull();
		questionsCreator.getStyle().set("padding-bottom", "10px");
		HorizontalLayout hl2 = new HorizontalLayout(questionsCreator);
		hl2.setSizeFull();
		questionsFormLayout.add(hl2);

		questionsPanel.addOpenedChangeListener(e -> {
			if(e.isOpened()) {
				questionsPanel.setSummaryText(QUESTIONS);
			} else if(questionBinder.getBean().getIntitule() != null) {
				Question questionValues = questionBinder.getBean();
				questionsPanel.setSummary(createSummary(QUESTIONS,
						questionValues.getIntitule()
						));
			}
		});

		Button backQuestionsButton = new Button("Retour");
		backQuestionsButton.addClickListener(event -> {
			questionsPanel.setEnabled(false);
			questionsPanel.setOpened(false);
			juryPanel.setOpened(true);
			juryPanel.setEnabled(true);
		});
		saveSession = new Button("Créer la session");
		saveSession.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		questionsPanel.addContent(backQuestionsButton, saveSession);
		questionsPanel.setSizeFull();
		add(accordion);
		accordion.setSizeFull();
		setSizeFull();
	}

	private FormLayout createFormLayout() {
		FormLayout billingAddressFormLayout = new FormLayout();
		billingAddressFormLayout.setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1),
				new FormLayout.ResponsiveStep("20em", 2)
				);
		return billingAddressFormLayout;
	}

	private VerticalLayout createSummary(String title, String... details) {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setPadding(false);

		layout.add(title);

		if (details.length > 0) {   
			VerticalLayout detailsLayout = new VerticalLayout();
			detailsLayout.setSpacing(false);
			detailsLayout.setPadding(false);
			detailsLayout.getStyle().set("font-size", "var(--lumo-font-size-s)");

			for (String detail: details) {
				if (detail != null && !detail.isEmpty()) {
					detailsLayout.add(new Span(detail));
				}
			}

			layout.add(detailsLayout);
		}

		return layout;
	}

	public void addButtonEvent(Tab ok, SessionService sessionService) {
		saveSession.addClickListener(event ->{
			ok.removeAll();
			Span so = createBadge(sessionService.getNumberOfSessions());
			ok.add(new Span("Gérer mes sessions"), so);
		});
	}

	private Span createBadge(Long value) {
		Span badge = new Span(String.valueOf(value));
		badge.getElement().getThemeList().add("badge small contrast");
		badge.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
		return badge;
	}
	
	public Button getSaveSession() {
		return saveSession;
	}

	public DragAndDropUsers getDragAndDrop() {
		return dragAndDrop;
	}

	public TextField getSessionName() {
		return sessionName;
	}

	public CreateQuestionsShowNews getQuestionsCreator() {
		return questionsCreator;
	}
	
	

}
