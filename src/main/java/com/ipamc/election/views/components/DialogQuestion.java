package com.ipamc.election.views.components;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class DialogQuestion extends Div {

	private Question quest;
	private Dialog dialog;
	private Set<Proposition> propositionsList;
	private Set<Categorie> categoriesList = new HashSet<>();
	private TextField intitule;
	private Select qcm;
	private Checkbox propIsRequired;
	private Checkbox propIsOk;
	private Checkbox commentaire;
	private Checkbox comIsRequired;
	private Checkbox note;
	private IntegerField noteValue;
	private List<Question> questions;
	Checkbox noteIsRequired;
	Button addBtn = new Button("Créer");

	public DialogQuestion(List<Question> questions) {
		dialog = new Dialog();
		dialog.getElement().setAttribute("aria-label", "Question");
		this.questions = questions;
		VerticalLayout dialogLayout = createDialogLayout(dialog);
		dialog.add(dialogLayout);
		dialog.addDialogCloseActionListener(event -> {
			ConfirmDialog.create()
			.withCaption("Attention!")
			.withMessage("Vous quittez le formulaire, confirmer?")
			.withOkButton(() -> {
				dialog.close();
			}, ButtonOption.focus(), ButtonOption.caption("OUI"))
			.withCancelButton(ButtonOption.caption("NON")).open();
		});
	}

	private VerticalLayout createDialogLayout(Dialog dialog) {
		H2 headline = new H2("Question");
		headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0")
		.set("font-size", "1.5em").set("font-weight", "bold");

		intitule = new TextField("Intitulé");
		intitule.setPlaceholder("Quel est le ... ?");
		intitule.setValueChangeMode(ValueChangeMode.EAGER);
		intitule.addValueChangeListener(event ->{
			if(intitule.isEmpty() || intitule.getValue().isBlank()) {
				intitule.setInvalid(true);
				intitule.setErrorMessage("Ce champ est obligtoire");
				checkForm();
			}else if(questionExists(intitule.getValue())) {
				intitule.setInvalid(true);
				intitule.setErrorMessage("Une question avec le même nom existe déjà");
				checkForm();
			}else {
				intitule.setInvalid(false);
				intitule.setErrorMessage("");
				checkForm();
			}
		});
		commentaire = new Checkbox("Commentaire");
		comIsRequired = new Checkbox("Obligatoire");
		comIsRequired.setEnabled(false);
		commentaire.addValueChangeListener(event ->{
			if(event.getValue()) {
				comIsRequired.setEnabled(true);
				checkForm();
			}else {
				comIsRequired.setEnabled(false);
				checkForm();
			}
		});   
		HorizontalLayout commentLayout = new HorizontalLayout(commentaire, comIsRequired);
		commentLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

		note = new Checkbox("Note");
		noteValue = new IntegerField();
		noteValue.getStyle().set("padding-left", "50px");
		noteValue.setPrefixComponent(new Label("Note / "));
		noteIsRequired = new Checkbox("Obligatoire");
		noteValue.setVisible(false);
		noteIsRequired.setEnabled(false);
		note.addValueChangeListener(event ->{
			if(event.getValue()) {
				noteValue.setVisible(true);
				noteIsRequired.setEnabled(true);
				checkForm();
			}else {
				noteValue.setVisible(false);
				noteIsRequired.setEnabled(false);
				checkForm();
			}
		});
		HorizontalLayout noteLayout = new HorizontalLayout(note, noteValue, noteIsRequired);
		noteLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		noteLayout.setAlignItems(Alignment.BASELINE);

		propIsOk = new Checkbox("Propositions");
		propIsRequired = new Checkbox("Obligatoire");
		propIsRequired.setEnabled(false);
		qcm = new Select<String>();
		qcm.setWidth("140px");
		qcm.setItems("Une réponse", "QCM");
		qcm.setValue("Une réponse");
		qcm.setVisible(false);
		VerticalLayout propsLayout = new VerticalLayout();
		propsLayout.setSizeFull();
		HorizontalLayout propsHeader = new HorizontalLayout(propIsOk, qcm, propIsRequired);
		propsHeader.setAlignItems(Alignment.BASELINE);
		propsHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		propsHeader.setSizeFull();
		propsLayout.add(propsHeader);

		Button addProposition = new Button();
		addProposition.setIcon(new Icon(VaadinIcon.PLUS));
		addProposition.setEnabled(false);
		TextField newProposition = new TextField("Ajouter une proposition");
		UnorderedList content = new UnorderedList();
		HorizontalLayout createPropLayout = new HorizontalLayout(newProposition, addProposition);
		createPropLayout.setAlignItems(Alignment.BASELINE);
		createPropLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		propsLayout.add(createPropLayout);
		newProposition.setVisible(false);
		addProposition.setVisible(false);

		newProposition.setValueChangeMode(ValueChangeMode.EAGER);
		newProposition.addValueChangeListener(event ->{
			if(newProposition.isEmpty() || newProposition.getValue().isBlank()) {
				addProposition.setEnabled(false);
				newProposition.setInvalid(false);
			}else if(propositionsList.contains(new Proposition(newProposition.getValue()))) {
				addProposition.setEnabled(false);
				newProposition.setInvalid(true);
				newProposition.setErrorMessage("Cette proposition existe déjà!");
			}else {
				addProposition.setEnabled(true);
				newProposition.setInvalid(false);
			}
		});
		propositionsList = new HashSet<>();
		addProposition.addClickListener(event -> {
			Button removeButton = new Button();
			TextField readOnly = new TextField();
			readOnly.setReadOnly(true);
			readOnly.setValue(newProposition.getValue());
			HorizontalLayout hl = new HorizontalLayout(readOnly, removeButton);
			propositionsList.add(new Proposition(newProposition.getValue()));
			hl.setAlignItems(Alignment.BASELINE);
			hl.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
			hl.setSizeFull();
			ListItem li = new ListItem(hl);
			setUpRemoveButton(content, li, removeButton, newProposition.getValue());
			li.setSizeFull();
			content.add(li);
			propsLayout.add(content);
			newProposition.setValue("");
			checkForm();
		});

		propIsOk.addValueChangeListener(event ->{
			if(event.getValue()) {
				newProposition.setVisible(true);
				addProposition.setVisible(true);
				propIsRequired.setEnabled(true);
				qcm.setVisible(true);
				content.setVisible(true);
				checkForm();
			}else {
				newProposition.setVisible(false);
				addProposition.setVisible(false);
				content.setVisible(false);
				qcm.setVisible(false);
				propIsRequired.setEnabled(false);
				checkForm();
			}
		}); 
		propsLayout.setPadding(false);
		addProposition.setWidth("25%");
		H4 titre = new H4("Type de réponses");
		VerticalLayout fieldLayout = new VerticalLayout(intitule, titre, commentLayout, noteLayout, propsLayout);
		intitule.getStyle().set("padding-bottom", "25px");
		commentLayout.getStyle().set("padding-bottom", "20px");
		noteLayout.getStyle().set("padding-bottom", "20px");
		titre.getStyle().set("padding-bottom", "15px");
		propsLayout.getStyle().set("padding-bottom", "20px");

		fieldLayout.setSpacing(false);
		fieldLayout.setPadding(false);
		fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

		Button cancelButton = new Button("Annuler", e -> dialog.close());


		addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		addBtn.setEnabled(false);
		HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton,
				addBtn);
		buttonLayout
		.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

		VerticalLayout dialogLayout = new VerticalLayout(headline, fieldLayout,
				buttonLayout);
		dialogLayout.setPadding(false);
		dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
		dialogLayout.getStyle().set("width", "700px").set("max-width", "100%");

		return dialogLayout;
	}

	/*public Q setUpSave() {
	    	addBtn.addClickListener(event -> {
	    		if(!intitule.getValue().isBlank()) {
	    			createCategorieList(commentaire, comIsRequired, note, noteValue, noteIsRequired);
		    		ConfirmDialog.create()
		  		      .withCaption("Confirmation")
		  		      .withMessage("Création de la question: "+intitule.getValue())
		  		      .withOkButton(() -> {
		  		    	  saveQuestion();
		  		    	dialog.close();
		  		      }, ButtonOption.focus(), ButtonOption.caption("OUI"))
		  		      .withCancelButton(ButtonOption.caption("NON")).open();

	    		}else {
	    			intitule.setInvalid(true);
	    			intitule.setErrorMessage("Ce champ est obligatoire");
	    		}	
	    	});
	    	return quest;
	    }**/

	private Button setUpRemoveButton(UnorderedList uli, ListItem li, Button remove, String value) {
		remove.setIcon(new Icon(VaadinIcon.TRASH));
		remove.addClickListener(event -> {
			uli.remove(li);
			propositionsList.remove(new Proposition(value));
			checkForm();
		});
		return remove;
	}

	public void open() {
		dialog.open();
	} 

	public Question saveQuestion() {
		quest = new Question();
		quest.setIntitule(intitule.getValue());
		quest.setCategories(categoriesList);
		if(propIsOk.getValue() && propositionsList.size() > 0) {
			quest.setPropositionRequired(propIsRequired.getValue());
			quest.setPropositions(propositionsList);
			if(qcm.getValue().equals("QCM")) {
				quest.setMultiChoice(true);
			}else {
				quest.setMultiChoice(false);
			}
		}else {
			quest.setPropositionRequired(false);
			quest.setMultiChoice(false);
		}

		return quest;
	}

	public Question getQuestion() {
		return quest;
	}
	
	private Boolean questionExists(String questionIntitule) {
		for(Question quest : questions) {
			if(quest.getIntitule().equals(questionIntitule)) {
				return true;
			}
		}
		return false;
	}

	private void checkForm() {
		if(!intitule.getValue().isBlank() && !questionExists(intitule.getValue())) {
			if(commentaire.getValue() || note.getValue() || propIsOk.getValue()) {
				if(propIsOk.getValue()) {
					if(propositionsList.size()>0) {
						addBtn.setEnabled(true); 
					}else{
						addBtn.setEnabled(false);
					}
				}else {
					addBtn.setEnabled(true);
				}
			}else {
				addBtn.setEnabled(false);
			}
		}else {
			addBtn.setEnabled(false);
		}
	}

	public void createCategorieList() {
		if(commentaire.getValue()) {
			categoriesList.add(new Categorie("Commentaire", -1, comIsRequired.getValue()));
		}
		if(note.getValue()) {
			if(noteValue.getValue() == null) {
				noteValue.setValue(20);
			}
			categoriesList.add(new Categorie("Note", noteValue.getValue() ,noteIsRequired.getValue()));
		}
	}

	public Button getAddBtn() {
		return addBtn;
	}

	public TextField getIntitule() {
		return intitule;
	}

	public void close() {
		dialog.close();
	}
}
