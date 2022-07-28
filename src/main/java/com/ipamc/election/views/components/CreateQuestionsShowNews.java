package com.ipamc.election.views.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
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
	}

	public void setupAddQuestion(Button saveBtn) {
		Button addQuestion = new Button("Ajouter une question");
		addQuestion.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		addQuestion.addClickListener(event -> {
			DialogQuestion diag = new DialogQuestion(questions);
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
						addQuestion.scrollIntoView();
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
		grid.addComponentColumn(question -> createCommentaireButton(question)).setHeader("Commentaire").setTextAlign(ColumnTextAlign.CENTER);		
		grid.addComponentColumn(question -> createNoteButton(question)).setHeader("Note").setTextAlign(ColumnTextAlign.CENTER);
		grid.addComponentColumn(question -> createPropositionsButton(question)).setHeader("Propositions").setTextAlign(ColumnTextAlign.CENTER);

		grid.addColumn(new ComponentRenderer<>(Button::new, (button, question) -> {
			button.addThemeVariants(ButtonVariant.LUMO_ICON,
					ButtonVariant.LUMO_ERROR,
					ButtonVariant.LUMO_TERTIARY);
			button.addClickListener(e -> {
				if(question.getIsActive() && question.getSession().getIsActive()) {
					ConfirmDialog.create()
					.withCaption("Confirmation")
					.withMessage("Cette question est actuellement activée dans le salon de vote, voulez-vous vraiment la supprimer?")
					.withOkButton(() -> {
						this.removeQuestion(question);
						if(questions.size()==0) {
							saveBtn.setEnabled(false);
						}
					}, ButtonOption.focus(), ButtonOption.caption("OUI"))
					.withCancelButton(ButtonOption.caption("NON")).open();
				}else {
					this.removeQuestion(question);
					if(questions.size()==0) {
						saveBtn.setEnabled(false);
					}
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

	public void fillGrid(Session sess) {
		for(Question quest : sess.getQuestions()) {
			questions.add(quest);
		}
		questions.sort((q1,q2) -> q1.getIntitule().compareTo(q2.getIntitule()));
		refreshGrid();	
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

	private static Button createCommentaireButton(Question question) {
		Button commentaire = new Button(new Icon("lumo","checkmark"));
		commentaire.addThemeVariants(ButtonVariant.LUMO_ICON);
		commentaire.setVisible(false);
		for(Categorie cat : question.getCategories()) {
			if(cat.getLibelle().equals("Commentaire")) {
				commentaire.setVisible(true);
				break;
			}
		}
		commentaire.addClickListener(event -> {
			Dialog dialog = new Dialog();
			dialog.getElement().setAttribute("aria-label", "Add note");
			VerticalLayout dialogLayout = new VerticalLayout();
			String obligatoire = "Obligatoire";
			for(Categorie cat : question.getCategories()) {
				if(cat.getLibelle().equals("Commentaire")) {
					if(!cat.getIsRequired()) {
						obligatoire = "Pas obligatoire";
					}
					break;
				}
			}
			dialogLayout.add(new Label("- "+obligatoire));
			dialog.add(dialogLayout);
			dialog.setHeaderTitle("Détail");
			dialogLayout.getStyle().set("width", "300px").set("max-width", "100%");
			dialogLayout.getStyle().set("height", "200px").set("max-height", "100%");
			dialogLayout.setSpacing(false);
			Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
			closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			dialog.getHeader().add(closeButton);
			dialog.open();
		});
		return commentaire;
	}

	private static Button createNoteButton(Question question) {
		Button note = new Button(new Icon("lumo","checkmark"));
		note.addThemeVariants(ButtonVariant.LUMO_ICON);
		note.setVisible(false);
		for(Categorie cat : question.getCategories()) {
			if(cat.getLibelle().equals("Note")) {
				note.setVisible(true);
				break;
			}
		}
		note.addClickListener(event -> {
			Dialog dialog = new Dialog();
			dialog.getElement().setAttribute("aria-label", "Add note");
			VerticalLayout dialogLayout = new VerticalLayout();
			String valeur = "";
			String obligatoire = "Obligatoire";
			for(Categorie cat : question.getCategories()) {
				if(cat.getLibelle().equals("Note")) {
					if(!cat.getIsRequired()) {
						obligatoire = "Pas obligatoire";
					}
					valeur = Integer.toString(cat.getValeur());
					break;
				}
			}
			dialogLayout.add(new Label("- Note /"+valeur));
			dialogLayout.add(new Label("- "+obligatoire));
			dialogLayout.setPadding(false);
			dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
			dialogLayout.getStyle().set("width", "300px").set("max-width", "100%");
			dialogLayout.getStyle().set("height", "200px").set("max-height", "100%");
			dialogLayout.setSpacing(false);
			dialog.add(dialogLayout);
			dialog.setHeaderTitle("Détail");
			Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
			closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			dialog.getHeader().add(closeButton);
			dialog.open();
		});
		return note;
	}

	private static Button createPropositionsButton(Question question) {
		Button props = new Button(new Icon("lumo","checkmark"));
		props.addThemeVariants(ButtonVariant.LUMO_ICON);
		props.setVisible(false);
		if(question.getPropositions().size()>0) {
			props.setVisible(true);
		}
		props.addClickListener(event -> {
			Dialog dialog = new Dialog();
			dialog.getElement().setAttribute("aria-label", "Add note");
			VerticalLayout dialogLayout = new VerticalLayout();
			String answer = "Plusieurs réponses possibles";
			if(!question.getMultiChoice()) {
				answer = "Une seule réponse";
			}
			dialogLayout.add(new Label("- "+answer));
			String obligatoire = "Obligatoire";
			if(!question.getPropositionRequired()) {
				obligatoire = "Pas obligatoire";
			}
			dialogLayout.add(new Label("- "+obligatoire));
			dialogLayout.add(new Label("- Liste des propositions: "));
			VerticalLayout listProps = new VerticalLayout();
			listProps.setSpacing(false);
			listProps.setPadding(false);
			for(Proposition p : question.getPropositions()) {
				listProps.add(new Span("   -> "+p.getLibelle()));
			}
			dialogLayout.add(listProps);
			dialogLayout.setSpacing(false);
			dialogLayout.getStyle().set("width", "300px").set("max-width", "100%");
			dialogLayout.getStyle().set("height", "200px").set("max-height", "100%");
			dialog.add(dialogLayout);
			dialog.setHeaderTitle("Détail");
			Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
			closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			dialog.getHeader().add(closeButton);
			dialog.open();
		});
		return props;
	}

}
