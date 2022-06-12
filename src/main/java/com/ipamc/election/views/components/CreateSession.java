package com.ipamc.election.views.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class CreateSession extends VerticalLayout {
	
	private UserService userService;
	private SessionService sessionService;
	private QuestionService questionService;
	private Button saveSession;

	public CreateSession(UserService userService, SessionService sessionService, QuestionService questionService) {
		
        TextField nomSession = new TextField("Nom de la session");
        TextField intituleQuestion = new TextField("Question");
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
        Checkbox qcm = new Checkbox("QCM");
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
        
        MultiSelectListBox<User> usersBox = new MultiSelectListBox<>();  
        List<User> users = new ArrayList<>();
        users = userService.findAll();
        usersBox.setItems(users);
        usersBox.setRenderer(new ComponentRenderer<Component, User>(user -> {
            Span name = new Span(user.getUsername());
            return name;
        }));

        saveSession = new Button("Sauvegarder");
        saveSession.addClickListener(event ->{
        	Set<Categorie> cats = new HashSet<>();
        	if(commentaire.getValue()) {
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
        	}
        	Set<User> jury = new HashSet<>();
        	for (Object i : usersBox.getSelectedItems()) { 
				jury.add((User)i);
        	}
        	Session sess = sessionService.createSession(nomSession.getValue(), jury);
        	
        	questionService.createQuestion(cats, props, intituleQuestion.getValue(), qcm.getValue(), sess);


        });
        
        add(nomSession, intituleQuestion, commentaire, comIsRequired, note, noteValue, noteIsRequired, propositions, proposition1, proposition2, qcm, usersBox, saveSession);
        
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

	public void setSaveSession(Button saveSession) {
		this.saveSession = saveSession;
	}
	
	
}
