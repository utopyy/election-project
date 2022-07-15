package com.ipamc.election.views.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.hibernate.boot.spi.SessionFactoryBuilderService;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.CategorieGridDetails;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.SessionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
		//initEmptyForm();
	}
	
	/**private void initFilledForm(Question quest) {
		TextField questionName = new TextField("Question");
		questionName.setValue(quest.getIntitule());
		questionName.setWidth("200px");
		Checkbox commentaire = new Checkbox("Activer");
		commentaire.setValue(quest.getIsActive());
		Checkbox comOblig = new Checkbox("Obligatoire");

		Label comText = new Label("Commentaire");
		comText.setWidth("100px");
		comText.getStyle().set("font-size", "13px");
		comText.getStyle().set("color", "rgb(68,137,227)");
		HorizontalLayout comHl = new HorizontalLayout(comText, commentaire, comOblig);
		comHl.setAlignItems(Alignment.BASELINE);
		Checkbox note = new Checkbox("Activer");
		Checkbox noteOblig = new Checkbox("Obligatoire");
		IntegerField noteValue = new IntegerField();
		noteValue.setPrefixComponent(new Label("Note / "));
		Label noteText = new Label("Note");
		//noteText.setWidth("100px");
		noteText.getStyle().set("font-size", "13px");
		noteText.getStyle().set("color", "rgb(68,137,227)");
		HorizontalLayout noteHl = new HorizontalLayout(noteText, note, noteOblig, noteValue);
		noteHl.setAlignItems(Alignment.BASELINE);
		Checkbox propositions = new Checkbox("Activer");
		Checkbox propOblig = new Checkbox("Obligatoire");
		Checkbox qcm = new Checkbox("QCM");
		Button addProp = new Button(new Icon(VaadinIcon.PLUS_CIRCLE));
		addProp.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		Label propText = new Label("Propositions");
		propText.setWidth("100px");
		propText.getStyle().set("font-size", "13px");
		propText.getStyle().set("color", "rgb(68,137,227)");
		TextField newPropTf = new TextField();
		newPropTf.setPlaceholder("Ajouter une proposition");
		newPropTf.setValueChangeMode(ValueChangeMode.EAGER);
		addProp.setEnabled(false);
		setupAddProposition(newPropTf, addProp);
		HorizontalLayout propLayout = new HorizontalLayout(newPropTf, addProp);
		propLayout.setSpacing(false);
		propLayout.getStyle().set("margin-left", "70px");
		HorizontalLayout propHl = new HorizontalLayout(propText, propositions, propOblig, qcm, propLayout);
		propHl.setAlignItems(Alignment.BASELINE);
		for(Categorie cat : quest.getCategories()) {
			if(cat.getLibelle().equals("Commentaire")) {
				commentaire.setValue(true);
				if(cat.getIsRequired().equals(true)) {
					comOblig.setValue(true);
				}
			}else {
				note.setValue(true);
				noteValue.setValue(cat.getValeur());
				if(cat.getIsRequired().equals(true)) {
					noteOblig.setValue(true);
				}
			}
		}
		if(quest.getPropositions().size()>0) {
			propositions.setValue(true);
			if(quest.getMultiChoice()) {
				qcm.setValue(true);
			}
			if(quest.getPropositionRequired()) {
				propOblig.setValue(true);
			}
		}
		HorizontalLayout propsBadges = new HorizontalLayout();
		propositionsList = quest.getPropositions();
		for(Proposition prop : propositionsList) {
			Span propositionBadge = createPropositionBadge(prop);
			propsBadges.add(propositionBadge);
		}
        propsBadges.getStyle().set("flex-wrap", "wrap");
        propsBadges.setAlignItems(Alignment.BASELINE);
        
        addProp.addClickListener(event -> {
        	Proposition newProp = new Proposition(newPropTf.getValue());
        	propositionsList.add(newProp);
        	Span propositionBadge = createPropositionBadge(newProp);
        	newPropTf.setValue("");
        	propsBadges.add(propositionBadge);
        });
        Button updateBtn = new Button("Modifier");
        setupUpdateButton(quest, questionName, commentaire, comOblig, note, noteOblig, noteValue, qcm, propOblig, updateBtn);
        setupTfdFilter(quest, updateBtn, questionName);
		add(questionName, new Hr(), comHl, new Hr(), noteHl, new Hr(), propHl, propsBadges, updateBtn);
		setSpacing(false);
		setPadding(false);
		setAlignItems(FlexComponent.Alignment.STRETCH);
		getStyle().set("width", "800px").set("max-width", "100%");
	}*/
	
	/**private void initEmptyForm() {
		TextField questionName = new TextField("Question");
		questionName.setWidth("200px");
		Checkbox commentaire = new Checkbox("Activer");
		Checkbox comOblig = new Checkbox("Obligatoire");
		Label comText = new Label("Commentaire");
		comText.setWidth("100px");
		comText.getStyle().set("font-size", "13px");
		comText.getStyle().set("color", "rgb(68,137,227)");
		HorizontalLayout comHl = new HorizontalLayout(comText, commentaire, comOblig);
		comHl.setAlignItems(Alignment.BASELINE);
		Checkbox note = new Checkbox("Activer");
		Checkbox noteOblig = new Checkbox("Obligatoire");
		IntegerField noteValue = new IntegerField();
		noteValue.setPrefixComponent(new Label("Note / "));
		Label noteText = new Label("Note");
		noteText.setWidth("100px");
		noteText.getStyle().set("font-size", "13px");
		noteText.getStyle().set("color", "rgb(68,137,227)");
		HorizontalLayout noteHl = new HorizontalLayout(noteText, note, noteOblig, noteValue);
		noteHl.setAlignItems(Alignment.BASELINE);
		Checkbox propositions = new Checkbox("Activer");
		Checkbox propOblig = new Checkbox("Obligatoire");
		Checkbox qcm = new Checkbox("QCM");
		Button addProp = new Button(new Icon(VaadinIcon.PLUS_CIRCLE));
		addProp.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		Label propText = new Label("Propositions");
		propText.setWidth("100px");
		propText.getStyle().set("font-size", "13px");
		propText.getStyle().set("color", "rgb(68,137,227)");
		TextField newPropTf = new TextField();
		newPropTf.setPlaceholder("Ajouter une proposition");
		newPropTf.setValueChangeMode(ValueChangeMode.EAGER);
		addProp.setEnabled(false);
		setupAddProposition(newPropTf, addProp);
		HorizontalLayout propLayout = new HorizontalLayout(newPropTf, addProp);
		propLayout.setSpacing(false);
		propLayout.getStyle().set("margin-left", "70px");
		HorizontalLayout propHl = new HorizontalLayout(propText, propositions, propOblig, qcm, propLayout);
		propHl.setAlignItems(Alignment.BASELINE);
		HorizontalLayout propsBadges = new HorizontalLayout();
        propsBadges.getStyle().set("flex-wrap", "wrap");
        propsBadges.setAlignItems(Alignment.BASELINE);
        addProp.addClickListener(event -> {
        	Proposition newProp = new Proposition(newPropTf.getValue());
        	propositionsList.add(newProp);
        	Span propositionBadge = createPropositionBadge(newProp);
        	newPropTf.setValue("");
        	propsBadges.add(propositionBadge);
        });
        Button createBtn = new Button("Créer");
        setupCreateButton(questionName, commentaire, comOblig, note, noteOblig, noteValue, qcm, propOblig, createBtn);
        setupTfdFilterCreator(createBtn, questionName);
		add(questionName, new Hr(), comHl, new Hr(), noteHl, new Hr(), propHl, propsBadges, createBtn);
		setSpacing(false);
		setPadding(false);
		setSizeFull();
//		setAlignItems(FlexComponent.Alignment.STRETCH);
//		::getStyle().set("width", "800px").set("max-width", "100%");
	}*/
	
	private void setupTfdFilter(Question quest, Button btn) {
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
				btn.setEnabled(true);
			}
		});
	}
	
	private void setupTfdFilterCreator(Button btn) {
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
				btn.setEnabled(true);
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
	
    private Span createPropositionBadge(Proposition proposition) {
        Button clearButton = new Button(VaadinIcon.CLOSE_SMALL.create());
        clearButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE);
        clearButton.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");

        Span badge = new Span(new Span(proposition.getLibelle()), clearButton);
        badge.getElement().getThemeList().add("badge contrast pill");

        clearButton.addClickListener(event -> {
            badge.getElement().removeFromParent();
            propositionsList.remove(proposition);
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
    
    private void setupUpdateButton(Question quest, TextField questionName, Boolean commentaire, Boolean comOblig, Boolean note, Boolean noteOblig,
    		Integer noteValue, Boolean qcm, Boolean propOblig, Button update) {
    	update.addClickListener(event -> {
    		Long idQuest = quest.getId();
    		Question newQuest = new Question(questionName.getValue(), qcm, propOblig);
	    	newQuest.setIntitule(questionName.getValue());
	    	newQuest.setPropositions(propositionsList);
	    	Set<Categorie> catList = new HashSet<>();
	    	if(commentaire) {
	    		catList.add(new Categorie("Commentaire", -1, comOblig));
	    	}
	    	if(note) {
	    		catList.add(new Categorie("Note", noteValue, noteOblig));
	    	}
	    	newQuest.setCategories(catList);
	    	questionService.updateQuestion(idQuest, newQuest);
    	});
    }
    
    private void setupCreateButton(TextField questionName, Checkbox commentaire, Checkbox comOblig, Checkbox note, Checkbox noteOblig,
    		IntegerField noteValue, Checkbox qcm, Checkbox propOblig, Button create) {
    	create.addClickListener(event -> {
    		Question newQuest = new Question(questionName.getValue(), qcm.getValue(), propOblig.getValue());
	    	newQuest.setIntitule(questionName.getValue());
	    	newQuest.setPropositions(propositionsList);
	    	Set<Categorie> catList = new HashSet<>();
	    	if(commentaire.getValue()) {
	    		catList.add(new Categorie("Commentaire", -1, comOblig.getValue()));
	    	}
	    	if(note.getValue()) {
	    		catList.add(new Categorie("Note", noteValue.getValue(), noteOblig.getValue()));
	    	}
	    	newQuest.setCategories(catList);
	    	questionService.createQuestion(newQuest, sessionService.getActiveSession());
    	});
    }
    
    private void initForm(Question quest) {
    	questIntitule = new TextField("Question");
    	questIntitule.setValue(quest.getIntitule());
    	update = new Button("Mettre à jour");
        setupTfdFilter(quest, update);
    	
    	grid.addColumn(CategorieGridDetails::getLibelleCat).setAutoWidth(true).setFlexGrow(0).setHeader("Type de question");
    	grid.addComponentColumn(question -> new Checkbox(question.getIsActive())).setAutoWidth(true).setFlexGrow(0).setHeader("Actif");
    	grid.addComponentColumn(question -> new Checkbox(question.getIsRequired())).setAutoWidth(true).setFlexGrow(0).setHeader("Obligatoire");
     	grid.addComponentColumn(question -> {
    		if(question.getLibelleCat().equals("Propositions")) {
    			Checkbox qcm = new Checkbox();
    			qcm.setLabel("Plusieurs réponses");
    			return qcm;
    		}else if(question.getLibelleCat().equals("Note")) {
    			IntegerField noteValue = new IntegerField();
    			noteValue.setPrefixComponent(new Label("Note / "));
    			noteValue.setValue(question.getValue());
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
	    		for(Proposition prop : propositionsList) {
	    			Span propositionBadge = createPropositionBadge(prop);
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

    	List<CategorieGridDetails> catsDet = new ArrayList<>();
    	CategorieGridDetails comment = new CategorieGridDetails(false,false);
    	CategorieGridDetails note = new CategorieGridDetails(false,false, 0);
    	CategorieGridDetails prop = new CategorieGridDetails(false,false, false);
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
    	setupUpdateButton(quest, questIntitule, comment.getIsActive(), comment.getIsRequired(), note.getIsActive(), note.getIsRequired(), note.getValue(), prop.getQcm(), prop.getIsRequired(), update);
    	grid.setItems(catsDet);
    	grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
 
    	// POUR LE SETUP : JE DOIS TROUVER UN MOYEN DE RECUPERER LES VALEURS DES CHECKBOX ET REMPLACER LES PARAMETRES DANS LA METHODE SETUPUPDATEBUTTON;
    }

}
   
