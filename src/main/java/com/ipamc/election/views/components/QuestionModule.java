package com.ipamc.election.views.components;

import java.util.Set;

import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class QuestionModule extends VerticalLayout {

	private IntegerField note;
	private TextArea commentaire;
	private MultiSelectListBox<Proposition> propositions = new MultiSelectListBox<>();;
	private Question question;
	private String libelle;
	private Boolean isRequired;
	private Icon noteIcon;
	private Icon commentaireIcon;
	private Icon propIcon;

	// Commentaire constructor
	public QuestionModule(Boolean obligatoire, Question question) {
		initLayout();
		initCommentaireForm(obligatoire);
		this.question = question;
		libelle = "commentaire";
		isRequired = obligatoire;
	}

	// Note constructor
	public QuestionModule(Integer value, Boolean obligatoire, Question question) {
		initLayout();
		initNoteForm(value, obligatoire);
		this.question = question;
		libelle = "note";
		isRequired = obligatoire;
	}

	// Propositions constructor
	public QuestionModule(Set<Proposition> propositions, Boolean qcm, Boolean obligatoire, Question question) {
		initLayout();
		initPropositionsForm(propositions, qcm, obligatoire);
		this.question = question;
		libelle = "propositions";
	}

	private void initLayout() {
		getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		getStyle().set("background-color","White");
		setSpacing(false);
		setMaxWidth("700px");
	}

	private void initCommentaireForm(Boolean obligatoire) {
		HorizontalLayout title = new HorizontalLayout();
		title.setSpacing(false);
		title.setAlignItems(Alignment.CENTER);
		if(obligatoire) {
			commentaireIcon = new Icon("lumo", "error");
			commentaireIcon.getStyle().set("padding-left", "3px");
			title.add(new Label("Commentaire"), commentaireIcon);
		}else {
			title.add(new Label("Commentaire"));
		}
		commentaire = new TextArea();
		commentaire.setWidthFull();
		commentaire.setMinHeight("100px");
		commentaire.setMaxHeight("150px");
		add(title, commentaire);
	}

	private void initPropositionsForm(Set<Proposition> propositionsList, Boolean obligatoire, Boolean qcm) {
		HorizontalLayout title = new HorizontalLayout();
		title.setSpacing(false);
		title.setAlignItems(Alignment.CENTER);
		if(obligatoire) {
			propIcon = new Icon("lumo", "error");
			propIcon.getStyle().set("padding-left", "3px");
			title.add(new Label("Propositions"), propIcon);
		}else {
			title.add(new Label("Propositions"));
		}
		propositions.setItems(propositionsList);
		propositions.setRenderer(new ComponentRenderer<>(proposition ->
		new Text(proposition.getLibelle()))
				);
		propositions.setMaxHeight("130px");
		propositions.setWidthFull();
		add(title, propositions);
		title.getStyle().set("padding-bottom", "5px");
		isRequired = obligatoire;
	}

	private void initNoteForm(Integer value, Boolean obligatoire) {
		HorizontalLayout title = new HorizontalLayout();
		title.setSpacing(false);
		title.setAlignItems(Alignment.CENTER);
		if(obligatoire) {
			noteIcon = new Icon("lumo", "error");
			noteIcon.getStyle().set("padding-left", "3px");
			title.add(new Label("Note"), noteIcon);
		}else {
			title.add(new Label("Note"));
		}
		note = new IntegerField();
		note.setSuffixComponent(new Label("/"+value));
		note.setMax(value);
		note.setMin(0);
		add(title,note);
	}
	
	

	public Icon getNoteIcon() {
		return noteIcon;
	}

	public void setNoteIcon(Icon noteIcon) {
		this.noteIcon = noteIcon;
	}

	public Icon getCommentaireIcon() {
		return commentaireIcon;
	}

	public void setCommentaireIcon(Icon commentaireIcon) {
		this.commentaireIcon = commentaireIcon;
	}

	public Icon getPropIcon() {
		return propIcon;
	}

	public void setPropIcon(Icon propIcon) {
		this.propIcon = propIcon;
	}

	public IntegerField getNote() {
		return note;
	}
	

	public TextArea getCommentaire() {
		return commentaire;
	}

	public MultiSelectListBox<Proposition> getPropositions(){
		return propositions;
	}

	public Question getQuestion() {
		return question;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public void setNote(IntegerField note) {
		this.note = note;
	}

	public void setCommentaire(TextArea commentaire) {
		this.commentaire = commentaire;
	}

	public void setPropositions(MultiSelectListBox<Proposition> propositions) {
		this.propositions = propositions;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}
}
