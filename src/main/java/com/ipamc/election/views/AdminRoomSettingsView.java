package com.ipamc.election.views;

import com.helger.commons.dimension.IHasDimensionDouble;
import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;

@Route(value = "gestionsalon", layout = MainLayout.class)
@PageTitle("Gestion du salon")

public class AdminRoomSettingsView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private SecurityUtils tools;
	private SessionService sessionService;
	
    public AdminRoomSettingsView(UserService userService, SecurityUtils tools, SessionService sessionService) {
    	this.userService = userService;
    	this.sessionService = sessionService;
    	this.tools = tools;
        setSpacing(false);

        Button createSession = new Button("Créer une session");
        TextField nomSession = new TextField("Nom de la session");
        TextField intituleQuestion = new TextField("Question");
        Checkbox commentaire = new Checkbox("Commentaire");
        Checkbox comIsRequired = new Checkbox("Obligatoire");
        Checkbox note = new Checkbox("Note");
        IntegerField noteValue = new IntegerField("Note sur");
        noteValue.setVisible(false);
        note.addValueChangeListener(event ->{
        	if(event.getValue()) {
        		noteValue.setVisible(true);
        	}else {
        		noteValue.setVisible(false);
        	}
        });
        Checkbox noteIsRequired = new Checkbox("Obligatoire");
        Checkbox qcm = new Checkbox("QCM");
        TextField proposition1 = new TextField("Proposition");
        TextField proposition2 = new TextField("Proposition");
		proposition1.setVisible(false);
		proposition2.setVisible(false);
        qcm.addValueChangeListener(event ->{
        	if(event.getValue()) {
        		proposition1.setVisible(true);
        		proposition2.setVisible(true);
        	}else {
        		proposition1.setVisible(false);
        		proposition2.setVisible(false);
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

        Button saveSession = new Button("Sauvegarder");
        saveSession.addClickListener(event ->{
        	Set<Categorie> cats = new HashSet<>();
        	if(commentaire.getValue()) {
        		cats.add(new Categorie("Commentaire", 0, comIsRequired.getValue()));
        	}
        	if(note.getValue()) {
        		cats.add(new Categorie("Note", noteValue.getValue() ,noteIsRequired.getValue()));
        	}
        	Set<Proposition> props = new HashSet<>();
        	if(qcm.getValue()) {
        			props.add(new Proposition(proposition1.getValue()));
        			props.add(new Proposition(proposition2.getValue()));
        	}
        	Set<User> jury = new HashSet<>();
        	for (Object i : usersBox.getSelectedItems()) { 
				jury.add((User)i);
        	}
        	Question quest = new Question(intituleQuestion.getValue(), cats, props, qcm.getValue());
        	Set<Question> questions = new HashSet<>();
        	questions.add(quest);
        	sessionService.createSession(nomSession.getValue(), jury, questions);
        });
        
        add(nomSession, intituleQuestion, commentaire, comIsRequired, note, noteValue, noteIsRequired, qcm, proposition1, proposition2, usersBox, saveSession);
        

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
    
	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
   		if(!(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).isActive())) {
   			beforeEnterEvent.forwardTo("registration_confirm/"+tools.getAuthenticatedUser().getUsername());		
   		}

	}

}
