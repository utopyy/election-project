package com.ipamc.election.views.components;

import java.util.ArrayList;
import java.util.List;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Question;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class CreateQuestionsShowNews extends VerticalLayout {

	private List<Question> questions = new ArrayList<>();
	private Grid<Question> grid;
	private Div hint;
	
	public CreateQuestionsShowNews() {
		//this.setupAddQuestion();
		//this.setupGrid();
		//this.refreshGrid();
	}
	
	public void setupAddQuestion(Button saveBtn) {
		Button addQuestion = new Button("Ajouter une question");
		addQuestion.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		addQuestion.addClickListener(event -> {
			DialogQuestion diag = new DialogQuestion();
			diag.getAddBtn().addClickListener(e -> {
	    		if(!diag.getIntitule().getValue().isBlank()) {
	    			diag.createCategorieList();
		    		ConfirmDialog.create()
		  		      .withCaption("Confirmation")
		  		      .withMessage("Création de la question: "+diag.getIntitule().getValue())
		  		      .withOkButton(() -> {
		  		    	 Question question = diag.saveQuestion();
		  		    	 questions.add(question);
		  		    	 this.refreshGrid();
		  		    	 diag.close();
		  		    	 saveBtn.setEnabled(true);
		  		      }, ButtonOption.focus(), ButtonOption.caption("OUI"))
		  		      .withCancelButton(ButtonOption.caption("NON")).open();
	
	    		}else {
	    			diag.getIntitule().setInvalid(true);
	    			diag.getIntitule().setErrorMessage("Ce champ est obligatoire");
	    		}	
	    	});
			diag.open();
		});

		HorizontalLayout layout = new HorizontalLayout(addQuestion);
		layout.getStyle().set("padding-bottom", "15px");
		add(layout);
	}
	
	public void setupGrid(Button saveBtn) {
		grid = new Grid<>(Question.class, false);
		grid.setAllRowsVisible(true);
		grid.addColumn(Question::getIntitule).setHeader("Intitulé").setResizable(true);
		grid.addColumn(new ComponentRenderer<>(Button::new, (button, question) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> {
            	this.removeQuestion(question);
            	if(questions.size()==0) {
            		saveBtn.setEnabled(false);
            	}
            });
            button.setIcon(new Icon(VaadinIcon.TRASH));
        })).setHeader("Supprimer").setTextAlign(ColumnTextAlign.END);
		
		grid.setItems(questions);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.setSizeFull();
		hint = new Div();
		hint.setText("Aucune question n'a été créée");
		hint.getStyle().set("padding", "var(--lumo-size-l)")
        .set("text-align", "center").set("font-style", "italic")
        .set("color", "var(--lumo-contrast-70pct)");
		
		add(hint, grid);
		this.refreshGrid();
	}
	
	private void refreshGrid() {
        if (questions.size() > 0) {
            grid.setVisible(true);
            hint.setVisible(false);
            grid.getDataProvider().refreshAll();
        } else {
            grid.setVisible(false);
            hint.setVisible(true);
        }
	}
	
    private void removeQuestion(Question question) {
        if (question == null)
            return;
        questions.remove(question);
        this.refreshGrid();
    }	
    
    public List<Question> getQuestions(){
    	return questions;
    }
    
    public Grid<Question> getGrid(){
    	return grid;
    }
    
    public void clear() {
    	questions.clear();
    	refreshGrid();
    }
}
