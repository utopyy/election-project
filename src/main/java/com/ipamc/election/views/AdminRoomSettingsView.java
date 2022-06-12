package com.ipamc.election.views;

import com.helger.commons.dimension.IHasDimensionDouble;
import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.CategorieService;
import com.ipamc.election.services.QuestionService;
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
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Route(value = "gestionsalon", layout = MainLayout.class)
@PageTitle("Gestion du salon")

public class AdminRoomSettingsView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private SecurityUtils tools;
	private SessionService sessionService;
	private CategorieService categorieService;
	private QuestionService questService;
	@Autowired private EntityManager entityManager;
	
    public AdminRoomSettingsView(UserService userService, SecurityUtils tools, SessionService sessionService, CategorieService categorieService,
    		QuestionService questService) {
    	this.userService = userService;
    	this.sessionService = sessionService;
    	this.categorieService = categorieService;
    	this.questService = questService;
    	this.tools = tools;
        setSpacing(false);

        Button createSession = new Button("Créer une session");
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

        Button saveSession = new Button("Sauvegarder");
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
        	
        	questService.createQuestion(cats, props, intituleQuestion.getValue(), qcm.getValue(), sess);


        });
        
        add(nomSession, intituleQuestion, commentaire, comIsRequired, note, noteValue, noteIsRequired, propositions, proposition1, proposition2, qcm, usersBox, saveSession);
        

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
