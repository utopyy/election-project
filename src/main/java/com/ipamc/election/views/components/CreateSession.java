package com.ipamc.election.views.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

public class CreateSession extends VerticalLayout {
	
	private UserService userService;
	private SessionService sessionService;
	private QuestionService questionService;
	private Button saveSession;
	private DragAndDropUsers dragAndDrop;

	
	
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

	        FormLayout questionsFormLayout = createFormLayout();
	        AccordionPanel questionsPanel = accordion.add(QUESTIONS,  questionsFormLayout);
	        
	        HorizontalLayout juryLayout = new HorizontalLayout();
	        juryLayout.add(dragAndDrop);
	        juryLayout.setSizeFull();
	        juryLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
	        AccordionPanel juryPanel = accordion.add(JURY, juryLayout);
	        
	        juryPanel.setEnabled(false);
	        questionsPanel.setEnabled(false);
	        // Session name details
	        TextField sessionName = new TextField();
	        sessionName.setPlaceholder("RLeague - Round2");
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
	        	questionsPanel.setEnabled(true);
	        	questionsPanel.setOpened(true); 
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
	        
	        // Questions details
	        List<Question> questions = new ArrayList<>();
	        Button addQuestion = new Button("Créer une question");
	        addQuestion.addClickListener(event -> {
	        	DialogQuestion diag = new DialogQuestion();
	        	diag.open();
	        });
	        TextField name = new TextField("Intitulé");
	        questionBinder.forField(name).bind(question -> {
	            if  (question.getIntitule() != null) {
	                return question.getIntitule();
	            }
	            return "";
	        }, (question, value) -> {
	            if (question.getIntitule() == null) {
	                question.setIntitule("");
	            }
	            question.setIntitule(value);
	        });

	        Checkbox commentaire = new Checkbox("Commentaire");
	        Checkbox comIsRequired = new Checkbox("Obligatoire");
	        comIsRequired.setEnabled(false);
	        commentaire.addValueChangeListener(event ->{
	        	if(event.getValue()) {
	        		comIsRequired.setEnabled(true);
	        	}else {
	        		comIsRequired.setEnabled(false);
	        	}
	        });    
	        Checkbox note = new Checkbox("Note");
	        IntegerField noteValue = new IntegerField("Note sur");
	        Checkbox noteIsRequired = new Checkbox("Obligatoire");
	        noteValue.setVisible(false);
	        noteIsRequired.setEnabled(false);
	        note.addValueChangeListener(event ->{
	        	if(event.getValue()) {
	        		noteValue.setVisible(true);
	        		noteIsRequired.setEnabled(true);
	        	}else {
	        		noteValue.setVisible(false);
	        		noteIsRequired.setEnabled(false);
	        	}
	        });
	        Checkbox propositions = new Checkbox("Propositions");
	        TextField proposition1 = new TextField("Proposition");
	        TextField proposition2 = new TextField("Proposition");
	        Checkbox qcm = new Checkbox("Choix multiples");
	        qcm.setVisible(false);
			proposition1.setVisible(false);
			proposition2.setVisible(false);
	        propositions.addValueChangeListener(event ->{
	        	if(event.getValue()) {
	        		proposition1.setVisible(true);
	        		proposition2.setVisible(true);
	        		qcm.setVisible(true);
	        	}else {
	        		proposition1.setVisible(false);
	        		proposition2.setVisible(false);
	        		qcm.setVisible(false);
	        	}
	        }); 
	        HorizontalLayout hl2 = new HorizontalLayout(addQuestion);
	        questionsFormLayout.add(hl2);/*
	        questionsFormLayout.add(addQuestion, name, new Label(""));
	        questionsFormLayout.add(commentaire, comIsRequired);
	        questionsFormLayout.add(note, noteValue, noteIsRequired);
	        questionsFormLayout.add(propositions, qcm);
	        questionsFormLayout.add(proposition1, 2);
	        questionsFormLayout.add(proposition2, 2);**/
	        

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
	        Button backQuestButton = new Button("Retour");
	        backQuestButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
	        backQuestButton.addClickListener(event -> {
	        	questionsPanel.setEnabled(false);
	        	questionsPanel.setOpened(false);
	        	sessionDetailsPanel.setOpened(true);
	        	sessionDetailsPanel.setEnabled(true);
	        });
	        Button questionButton = new Button("Continuer");
	        questionButton.addClickListener(event -> {
	        	questionsPanel.setEnabled(false);
	        	questionsPanel.setOpened(false);
	        	juryPanel.setEnabled(true);
	        	juryPanel.setOpened(true);
	        });
	        questionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	        questionButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
	        questionsPanel.addContent(backQuestButton, questionButton);

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
	        	questionsPanel.setOpened(true);
	        	questionsPanel.setEnabled(true);
	        });
	        saveSession = new Button("Créer la session", (e) -> juryPanel.setOpened(false));
	        saveSession.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	        saveSession.addThemeVariants(ButtonVariant.LUMO_SMALL);
	        backJuryButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
	        juryPanel.addContent(backJuryButton, saveSession);
	        juryPanel.setSizeFull();
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

	
	

        /**Button addQuestion = new Button("Ajouter une question", new Icon(VaadinIcon.PLUS));
        saveSession = new Button("Sauvegarder");
        saveSession.addClickListener(event ->{
        	Set<Categorie> cats = new HashSet<>();
        	/**if(commentaire.getValue()) {
        		cats.add(new Categorie("Commentaire", -1, comIsRequired.getValue()));
        	}
        	if(note.getValue()) {
        		cats.add(new Categorie("Note", noteValue.getValue() ,noteIsRequired.getValue()));
        	}
        	Set<Proposition> props = new HashSet<>();
        	if(qcm.getValue()) {
        			props.add(new Proposition(proposition1.getValue()));
        			props.add(new Proposition(proposition2.getValue()));
        	}else {
        		props.add(new Proposition(proposition1.getValue()));
        	}*/
        	//Session sess = sessionService.createSession(nomSession.getValue(), dragAndDrop.getSelectedUsers());    	
        	//questionService.createQuestion(cats, props, intituleQuestion.getValue(), qcm.getValue(), sess);
        //});
        //Html h = new Html("<em>Déplace les utilisateurs dans le compartiment Jury afin de leur donner accès à cette session.");
        /**h.getElement().getStyle().set("padding-top", "30px");
        h.getElement().getStyle().set("padding-bottom", "20px");
        h.getElement().getStyle().set("font-size", "13px");*/
        //HorizontalLayout hL = new HorizontalLayout();
        //hL.add(nomSession, addQuestion);
        //hL.setAlignItems(FlexComponent.Alignment.BASELINE);
        //add(hL, /*intituleQuestion, comLayout, noteLayout, propositions, proposition1, proposition2, qcm, h*/ dragAndDrop, saveSession);
        //setHorizontalComponentAlignment(Alignment.CENTER, dragAndDrop);
        //setSpacing(false);


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
/*
	public void setSaveSession(Button saveSession) {
		this.saveSession = saveSession;
	}**/	
}
