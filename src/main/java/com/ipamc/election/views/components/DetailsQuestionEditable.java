package com.ipamc.election.views.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.CategorieGridDetails;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.SessionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

public class DetailsQuestionEditable extends VerticalLayout {

	private Set<Proposition> propositionsList = new HashSet<>();
	private QuestionService questionService;
	private SessionService sessionService;
	private Grid<CategorieGridDetails> grid = new Grid<>();
	private TextField questIntitule;
	private Button update;
	private Button create;
	
	public DetailsQuestionEditable(Question quest, QuestionService questionService, SessionService sessionService) {
		this.questionService = questionService;
		this.sessionService = sessionService;
		initForm(quest);
		HorizontalLayout headerGrid = new HorizontalLayout(questIntitule, update);
		headerGrid.setSizeFull();
		headerGrid.setPadding(false);
		headerGrid.setAlignItems(Alignment.BASELINE);
		add(headerGrid, grid);
		grid.setAllRowsVisible(true);
	}
	
	public DetailsQuestionEditable(QuestionService questionService, SessionService sessionService) {
		this.questionService = questionService;
		this.sessionService = sessionService;
		initEmptyForm();
		HorizontalLayout headerGrid = new HorizontalLayout(questIntitule, create);
		headerGrid.setSizeFull();
		headerGrid.setPadding(false);
		headerGrid.setAlignItems(Alignment.BASELINE);
		add(headerGrid, grid);
		grid.setAllRowsVisible(true);
	}

	private void initEmptyForm() {
    	questIntitule = new TextField("Question");
    	create = new Button("Créer");
    	create.setEnabled(false);
    	
    	List<CategorieGridDetails> catsDet = new ArrayList<>();
    	CategorieGridDetails comment = new CategorieGridDetails(false,false);
    	CategorieGridDetails note = new CategorieGridDetails(false,false, 0);
    	CategorieGridDetails prop = new CategorieGridDetails(false,false, false);
    	
        setupTfdFilterCreator(create, comment, note, prop);
    	
    	grid.addColumn(CategorieGridDetails::getLibelleCat).setAutoWidth(true).setFlexGrow(0).setHeader("Type de question");
    	
    	grid.addComponentColumn(question -> {
    		Checkbox actif = new Checkbox(question.getIsActive());
    		actif.addValueChangeListener(event -> {
    			question.setIsActive(actif.getValue());
    			formChecker(create, comment, note, prop);
    		});
    		return actif;
    	}).setAutoWidth(true).setFlexGrow(0).setHeader("Actif");;
    	
    	grid.addComponentColumn(question -> {
    		Checkbox obligatoire = new Checkbox(question.getIsRequired());
    		obligatoire.addValueChangeListener(event -> {
    			question.setIsRequired(obligatoire.getValue());
    		});
    		return obligatoire;
    	}).setAutoWidth(true).setFlexGrow(0).setHeader("Obligatoire");
    	
     	grid.addComponentColumn(question -> {
    		if(question.getLibelleCat().equals("Propositions")) {
    			Checkbox qcm = new Checkbox(question.getQcm());
    			qcm.setLabel("Plusieurs réponses");
    			qcm.addValueChangeListener(event -> {
    				question.setQcm(qcm.getValue());
    			});
    			return qcm;
    		}else if(question.getLibelleCat().equals("Note")) {
    			IntegerField noteValue = new IntegerField();
    			noteValue.setPrefixComponent(new Label("Note / "));
    			noteValue.setValue(question.getValue());
    			noteValue.addValueChangeListener(event ->{ 
    				question.setValue(noteValue.getValue());
    			});
    			return noteValue;
    		}else
    			return new Span();
    	}).setAutoWidth(true).setFlexGrow(0).setHeader("Paramètres");
    	
     	grid.addComponentColumn(question -> {
    		if(question.getLibelleCat().equals("Propositions")) {
    			Button addProp = new Button(new Icon(VaadinIcon.PLUS_CIRCLE));
    			addProp.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    			TextField newPropTf = new TextField();
    			newPropTf.setPlaceholder("Ajouter une proposition");
    			newPropTf.setValueChangeMode(ValueChangeMode.EAGER);
    			addProp.setEnabled(false);
    			addProp.addClickListener(event -> {
    	        	Proposition newProp = new Proposition(newPropTf.getValue());
    	        	propositionsList.add(newProp);
    	        	grid.getDataProvider().refreshAll();
    	        	formChecker(create, comment, note, prop);
    	        });
    			setupAddProposition(newPropTf, addProp);
    			HorizontalLayout propLayout = new HorizontalLayout(newPropTf, addProp);
    			propLayout.setSpacing(false);
    			propLayout.setAlignItems(Alignment.BASELINE);
    			return propLayout;
    		}
    		return new Span();
    	}).setAutoWidth(true).setFlexGrow(0);
    	
     	grid.setItemDetailsRenderer(new ComponentRenderer<Component, CategorieGridDetails>(question -> {
    		if(question.getLibelleCat().equals("Propositions")) {
	        	Span propsBadges = new Span();
	    		for(Proposition propo : propositionsList) {
	    			Span propositionBadge = createPropositionBadge(propo, create, comment, note, prop);
	    			propositionBadge.getStyle().set("display", "inline-block");
	    			propositionBadge.getStyle().set("margin-right", "10px");
	    			propositionBadge.getStyle().set("margin-bottom", "10px");
	    			propsBadges.add(propositionBadge);
	    		}
	    		Scroller scroller = new Scroller(propsBadges);
	    		return scroller;
    		}else {
    			return new Span();
    		}
    		
        }));

    	grid.addCellFocusListener(event -> {
    		if(event.getItem().isPresent()) {
	    		if(event.getItem().get().getLibelleCat().equals("Propositions")) {
		        	grid.setDetailsVisibleOnClick(true);
	    		}else {
	    			grid.setDetailsVisibleOnClick(false);
	    		}
    		}
    	});
    	grid.addItemClickListener(event -> {
    		if(!grid.isDetailsVisible(prop)) {
				grid.setDetailsVisible(prop, true);
			}
    		grid.setDetailsVisible(comment, false);
    		grid.setDetailsVisible(note, false);
    		
    	});
    	grid.setDetailsVisible(prop, true);
    	grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    	catsDet.add(comment);
    	catsDet.add(note);
    	catsDet.add(prop);
    	grid.setItems(catsDet);
    	setupCreateButton(questIntitule, comment, note, prop, update);
    	grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
	}
	
	private void setupTfdFilter(Question quest, Button btn, CategorieGridDetails comment, 
    		CategorieGridDetails note, CategorieGridDetails prop) {
		questIntitule.setValueChangeMode(ValueChangeMode.EAGER);
		questIntitule.addValueChangeListener(event ->{
			if(questionNameExistsUpdate(quest)){
				questIntitule.setInvalid(true);
				questIntitule.setErrorMessage("Une question avec le même nom existe déjà pour cette session");
				btn.setEnabled(false);
			}else if(questIntitule.getValue().isBlank()){
				questIntitule.setInvalid(true);
				questIntitule.setErrorMessage("La question ne peut pas être vide");
				btn.setEnabled(false);
			}else {
				questIntitule.setInvalid(false);
				if(!formChecker(btn, comment, note, prop)) {
					btn.setEnabled(false);
				}else {
					btn.setEnabled(true);
				}
			}
		});
	}
	
	private void setupTfdFilterCreator(Button btn, CategorieGridDetails comment, 
    		CategorieGridDetails note, CategorieGridDetails prop) {
		questIntitule.setValueChangeMode(ValueChangeMode.EAGER);
		questIntitule.addValueChangeListener(event ->{
			if(questIntitule.getValue().isBlank()){
				questIntitule.setInvalid(true);
				questIntitule.setErrorMessage("La question ne peut pas être vide");
				btn.setEnabled(false);
			}else if(questionNameExistsCreate()){
				questIntitule.setInvalid(true);
				questIntitule.setErrorMessage("Une question avec le même nom existe déjà pour cette session");
				btn.setEnabled(false);
			}else {
				questIntitule.setInvalid(false);
				if(!formChecker(btn, comment, note, prop)) {
					btn.setEnabled(false);
				}else {
					btn.setEnabled(true);
				}
			}
		});
	}
	
	private Boolean questionNameExistsUpdate(Question quest) {
		Session sess = quest.getSession();
		if(questIntitule.getValue().equals(quest.getIntitule())) {
			return false;
		}
		for(Question question : sess.getQuestions()) {
			if(question.getIntitule().equals(questIntitule.getValue())) {
				return true;
			}
		}
		return false;
	}
	
	private Boolean questionNameExistsCreate() {
		Session sess = sessionService.getActiveSession();
		for(Question question : sess.getQuestions()) {
			if(question.getIntitule().equals(questIntitule.getValue())) {
				return true;
			}
		}
		return false;
	}
	
    private Span createPropositionBadge(Proposition proposition, Button btn, CategorieGridDetails comment,
    		CategorieGridDetails note, CategorieGridDetails prop) {
        Button clearButton = new Button(VaadinIcon.CLOSE_SMALL.create());
        clearButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE);
        clearButton.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");

        Span badge = new Span(new Span(proposition.getLibelle()), clearButton);
        badge.getElement().getThemeList().add("badge contrast pill");

        clearButton.addClickListener(event -> {
            badge.getElement().removeFromParent();
            propositionsList.remove(proposition);
            formChecker(btn, comment, note, prop);
        });
        return badge;
    }
    
    private void setupAddProposition(TextField propTf, Button ok) {
    	propTf.addValueChangeListener(event -> {
    		propTf.setInvalid(false);
    		ok.setEnabled(true);
    		String prop = event.getValue();
	    	if(prop.isBlank()) {
	    		ok.setEnabled(false);
	    	}
	    	for(Proposition p : propositionsList) {
	    		if(p.getLibelle().equals(prop)) {
	    			ok.setEnabled(false);
	    			propTf.setErrorMessage("Cette proposition existe déjà");
	    			propTf.setInvalid(true);
	    		}
	    	}
    	});
    }
    
    private void setupUpdateButton(Question quest, TextField questionName, CategorieGridDetails comment, 
    		CategorieGridDetails note, CategorieGridDetails prop, Button update) {
    	update.addClickListener(event -> {
    		Long idQuest = quest.getId();
    		Question newQuest = new Question(questionName.getValue(), prop.getQcm(), prop.getIsRequired());
	    	newQuest.setIntitule(questionName.getValue());
	    	newQuest.setPropositions(propositionsList);
	    	Set<Categorie> catList = new HashSet<>();
	    	if(comment.getIsActive()) {
	    		catList.add(new Categorie("Commentaire", -1, comment.getIsRequired()));
	    	}
	    	if(note.getIsActive()) {
	    		if(note.getValue() == null)
	    			note.setValue(20);
	    		catList.add(new Categorie("Note", note.getValue(), note.getIsRequired()));
	    	}
	    	newQuest.setCategories(catList);
	    	questionService.updateQuestion(idQuest, newQuest);
	    	Notification notification = Notification.show("Modifications enregistrées!");
	    	notification.setDuration(2000);
	    	notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    	});
    }
    
    private void setupCreateButton(TextField questionName, CategorieGridDetails comment, 
    		CategorieGridDetails note, CategorieGridDetails prop, Button update) {
    	create.addClickListener(event -> {
    		Question newQuest = new Question(questionName.getValue(), prop.getQcm(), prop.getIsRequired());
	    	newQuest.setIntitule(questionName.getValue());
	    	newQuest.setPropositions(propositionsList);
	    	Set<Categorie> catList = new HashSet<>();
	    	if(comment.getIsActive()) {
	    		catList.add(new Categorie("Commentaire", -1, comment.getIsRequired()));
	    	}
	    	if(note.getIsActive()) {
	    		if(note.getValue() == null)
	    			note.setValue(20);
	    		catList.add(new Categorie("Note", note.getValue(), note.getIsRequired()));
	    	}
	    	newQuest.setCategories(catList);
	    	questionService.createQuestion(newQuest, sessionService.getActiveSession());
	    	Notification notification = Notification.show("Question créée!");
	    	notification.setDuration(2000);
	    	notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    	});
    }
    
    private void initForm(Question quest) {
    	questIntitule = new TextField("Question");
    	questIntitule.setValue(quest.getIntitule());
    	update = new Button("Mettre à jour");
    	List<CategorieGridDetails> catsDet = new ArrayList<>();
    	CategorieGridDetails comment = new CategorieGridDetails(false,false);
    	CategorieGridDetails note = new CategorieGridDetails(false,false, 0);
    	CategorieGridDetails prop = new CategorieGridDetails(false,false, false);
        
    	setupTfdFilter(quest, update, comment, note, prop);	
    	grid.addColumn(CategorieGridDetails::getLibelleCat).setAutoWidth(true).setFlexGrow(0).setHeader("Type de question");
    	
    	grid.addComponentColumn(question -> {
    		Checkbox actif = new Checkbox(question.getIsActive());
    		actif.addValueChangeListener(event -> {
    			question.setIsActive(actif.getValue());
    			formChecker(update, comment, note, prop);
    		});
    		return actif;
    	}).setAutoWidth(true).setFlexGrow(0).setHeader("Actif");;
    	
    	grid.addComponentColumn(question -> {
    		Checkbox obligatoire = new Checkbox(question.getIsRequired());
    		obligatoire.addValueChangeListener(event -> {
    			question.setIsRequired(obligatoire.getValue());
    		});
    		return obligatoire;
    	}).setAutoWidth(true).setFlexGrow(0).setHeader("Obligatoire");
    	
     	grid.addComponentColumn(question -> {
    		if(question.getLibelleCat().equals("Propositions")) {
    			Checkbox qcm = new Checkbox(question.getQcm());
    			qcm.setLabel("Plusieurs réponses");
    			qcm.addValueChangeListener(event -> {
    				System.out.println("yoProp");
    				question.setQcm(qcm.getValue());
    			});
    			return qcm;
    		}else if(question.getLibelleCat().equals("Note")) {
    			IntegerField noteValue = new IntegerField();
    			noteValue.setPrefixComponent(new Label("Note / "));
    			noteValue.setValue(question.getValue());
    			noteValue.addValueChangeListener(event ->{ 
    				question.setValue(noteValue.getValue());
    			});
    			return noteValue;
    		}else
    			return new Span();
    	}).setAutoWidth(true).setFlexGrow(0).setHeader("Paramètres");
    	
     	grid.addComponentColumn(question -> {
    		if(question.getLibelleCat().equals("Propositions")) {
    			Button addProp = new Button(new Icon(VaadinIcon.PLUS_CIRCLE));
    			addProp.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    			TextField newPropTf = new TextField();
    			newPropTf.setPlaceholder("Ajouter une proposition");
    			newPropTf.setValueChangeMode(ValueChangeMode.EAGER);
    			addProp.setEnabled(false);
    			addProp.addClickListener(event -> {
    	        	Proposition newProp = new Proposition(newPropTf.getValue());
    	        	propositionsList.add(newProp);
    	        	quest.getPropositions().add(newProp);
    	        	formChecker(update, comment, note, prop);
    	        	grid.getDataProvider().refreshAll();
    	        });
    			setupAddProposition(newPropTf, addProp);
    			HorizontalLayout propLayout = new HorizontalLayout(newPropTf, addProp);
    			propLayout.setSpacing(false);
    			propLayout.setAlignItems(Alignment.BASELINE);
    			return propLayout;
    		}
    		return new Span();
    	}).setAutoWidth(true).setFlexGrow(0);
    	
     	grid.setItemDetailsRenderer(new ComponentRenderer<Component, CategorieGridDetails>(question -> {
    		if(question.getLibelleCat().equals("Propositions")) {
	        	Span propsBadges = new Span();
	    		propositionsList = quest.getPropositions();
	    		for(Proposition propo : propositionsList) {
	    			Span propositionBadge = createPropositionBadge(propo, update, comment, note, prop);
	    			propositionBadge.getStyle().set("display", "inline-block");
	    			propositionBadge.getStyle().set("margin-right", "10px");
	    			propositionBadge.getStyle().set("margin-bottom", "10px");
	    			propsBadges.add(propositionBadge);
	    		}
	    		Scroller scroller = new Scroller(propsBadges);
	    		return scroller;
    		}else {
    			return new Span();
    		}
    		
        }));

    	grid.addCellFocusListener(event -> {
    		if(event.getItem().isPresent()) {
	    		if(event.getItem().get().getLibelleCat().equals("Propositions")) {
		        	grid.setDetailsVisibleOnClick(true);
	    		}else {
	    			grid.setDetailsVisibleOnClick(false);
	    		}
    		}
    	});
    	grid.addItemClickListener(event -> {
    		if(!grid.isDetailsVisible(prop)) {
				grid.setDetailsVisible(prop, true);
			}
    		grid.setDetailsVisible(comment, false);
    		grid.setDetailsVisible(note, false);
    		
    	});
    	grid.setDetailsVisible(prop, true);
    	grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    	for(Categorie cat : quest.getCategories()) {
    		if(cat.getLibelle().equals("Commentaire")) {
    			comment.setIsActive(true);
    			comment.setIsRequired(cat.getIsRequired());
    		}else {
    			note.setIsActive(true);
    			note.setIsRequired(cat.getIsRequired());
    			note.setValue(cat.getValeur());
    		}
    	}
    	if(quest.getPropositions().size()>0) {
    		prop.setIsActive(true);
    		prop.setIsRequired(quest.getPropositionRequired());
    		prop.setQcm(quest.getMultiChoice());
    	}
    	catsDet.add(comment);
    	catsDet.add(note);
    	catsDet.add(prop);
    	grid.setItems(catsDet);
    	setupUpdateButton(quest, questIntitule, comment, note, prop, update);
    	grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    }
    
    private Boolean formChecker(Button btn, CategorieGridDetails comment, CategorieGridDetails note, CategorieGridDetails proposition) {
    	if(proposition.getIsActive() && propositionsList.size() == 0) {
    		btn.setEnabled(false);
    		return false;
    	}
    	if(!comment.getIsActive() && !note.getIsActive() && !proposition.getIsActive()) {
    		btn.setEnabled(false);
    		return false;
    	}
    	btn.setEnabled(true);
    	return true;   	
    }
    
    //AJOUTER FORM CHECKER DANS LE REMOVE PROPOSITION ET DANS LE CHANGE NOOM SESSION TEXTFIELD
    //VERIFIER SI LES MAJ SE FONT CORRECTEMENT QUAND "ACTIF" N'EST PAS CHECK
    
    //IDEM AVEC CREATE
    
    //STEP3
    
}
   
