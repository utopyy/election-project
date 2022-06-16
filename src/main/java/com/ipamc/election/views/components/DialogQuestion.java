package com.ipamc.election.views.components;

import java.util.ArrayList;
import java.util.List;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Vote;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
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
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

public class DialogQuestion extends Div {

		private Dialog dialog;

	    public DialogQuestion() {
	        dialog = new Dialog();
	        dialog.getElement().setAttribute("aria-label", "Question");

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

	        TextField name = new TextField("Intitulé");
	        name.setPlaceholder("Quel est le ... ?");
	        name.setValueChangeMode(ValueChangeMode.EAGER);
	        name.addValueChangeListener(event ->{
	        	if(name.isEmpty() || name.getValue().isBlank()) {
	        		name.setInvalid(true);
	        	}else {
	        		name.setInvalid(false);
	        	}
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
	        HorizontalLayout commentLayout = new HorizontalLayout(commentaire, comIsRequired);
	        commentLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
	        Checkbox note = new Checkbox("Note");
	        IntegerField noteValue = new IntegerField();
	        noteValue.setPrefixComponent(new Label("Note / "));
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
	        HorizontalLayout noteLayout = new HorizontalLayout(note, noteValue, noteIsRequired);
	        noteLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
	        noteLayout.setAlignItems(Alignment.BASELINE);
	        List<String> propositionsList = new ArrayList<>();
	        List<Label> propositionsLabel = new ArrayList<>();
	  
	        Checkbox propositions = new Checkbox("Propositions");
	        Select<String> select = new Select<>();
	        select.setWidth("120px");
	        select.setItems("Une réponse", "QCM");
	        select.setPlaceholder("Type");
	        select.setVisible(false);
	        VerticalLayout propsLayout = new VerticalLayout();
	        propsLayout.setSizeFull();
	        HorizontalLayout propsHeader = new HorizontalLayout(propositions, select, new Label("         "));
	        propsHeader.setAlignItems(Alignment.BASELINE);
	        propsHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
	        propsHeader.getThemeList().add("spacing-xl");
	        propsLayout.add(propsHeader);

	        Button addProposition = new Button();
	        addProposition.setIcon(new Icon(VaadinIcon.PLUS));
		    TextField newProposition = new TextField("Ajouter une proposition");
		    UnorderedList content = new UnorderedList();
		    HorizontalLayout createPropLayout = new HorizontalLayout(newProposition, addProposition);
		    createPropLayout.setAlignItems(Alignment.BASELINE);
		    createPropLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
    		propsLayout.add(createPropLayout);
    		newProposition.setVisible(false);
    		addProposition.setVisible(false);

	        
		    addProposition.addClickListener(event -> {
		    	Button removeButton = new Button();
		    	TextField readOnly = new TextField();
		    	readOnly.setReadOnly(true);
		    	readOnly.setValue(newProposition.getValue());
		    	HorizontalLayout hl = new HorizontalLayout(readOnly, removeButton);
		        hl.setAlignItems(Alignment.BASELINE);
		        hl.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		        hl.setSizeFull();
		    	ListItem li = new ListItem(hl);
		    	setUpRemoveButton(content, li, removeButton);
		    	li.setSizeFull();
		    	content.add(li);
		    	propsLayout.add(content);		    	
		    });
	        
	        propositions.addValueChangeListener(event ->{
	        	if(event.getValue()) {
	        		newProposition.setVisible(true);
	        		addProposition.setVisible(true);
	        		select.setVisible(true);
	        		content.setVisible(true);
	        	}else {
	        		newProposition.setVisible(false);
	        		addProposition.setVisible(false);
	        		content.setVisible(false);
	        		select.setVisible(false);
	        	}
	        }); 
	        propsLayout.setPadding(false);
	        addProposition.setWidth("25%");
	        H4 titre = new H4("Type de réponses");
	        VerticalLayout fieldLayout = new VerticalLayout(name, titre, commentLayout, noteLayout, propsLayout);
	        name.getStyle().set("padding-bottom", "25px");
	        commentLayout.getStyle().set("padding-bottom", "20px");
	        noteLayout.getStyle().set("padding-bottom", "20px");
	        titre.getStyle().set("padding-bottom", "15px");
	        propsLayout.getStyle().set("padding-bottom", "20px");
	        
	        fieldLayout.setSpacing(false);
	        fieldLayout.setPadding(false);
	        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

	        Button cancelButton = new Button("Annuler", e -> dialog.close());
	        Button saveButton = new Button("Enregistrer");
	        setUpSave(saveButton, name);
	        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton,
	                saveButton);
	        buttonLayout
	                .setJustifyContentMode(FlexComponent.JustifyContentMode.END);

	        VerticalLayout dialogLayout = new VerticalLayout(headline, fieldLayout,
	                buttonLayout);
	        dialogLayout.setPadding(false);
	        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
	        dialogLayout.getStyle().set("width", "700px").set("max-width", "100%");

	        return dialogLayout;
	    }
	    
	    private void setUpSave(Button btn, TextField intituleQuestion) {
	    	btn.addClickListener(event -> {
	    		if(!intituleQuestion.getValue().isBlank()) {
		    		ConfirmDialog.create()
		  		      .withCaption("Confirmation")
		  		      .withMessage("Création de la question: "+intituleQuestion.getValue())
		  		      .withOkButton(() -> {
		  				// SAVE METHOD
		  		      }, ButtonOption.focus(), ButtonOption.caption("OUI"))
		  		      .withCancelButton(ButtonOption.caption("NON")).open();
		    		dialog.close();
	    		}else {
	    			intituleQuestion.setInvalid(true);
	    			intituleQuestion.setErrorMessage("Ce champ est obligatoire");
	    		}	
	    	});
	    }
	    
	    private Button setUpRemoveButton(UnorderedList uli, ListItem li, Button remove) {
	    	remove.setIcon(new Icon(VaadinIcon.TRASH));
	    	remove.addClickListener(event -> {
	    		uli.remove(li);
	    	});
	    	return remove;
	    }
	    
	    public void open() {
	    	dialog.open();
	    } 
}
