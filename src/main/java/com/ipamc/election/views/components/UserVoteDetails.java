package com.ipamc.election.views.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ipamc.election.data.entity.CategorieGridDetails;
import com.ipamc.election.data.entity.Jure;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.data.entity.VoteCategorie;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class UserVoteDetails extends VerticalLayout {

	/*private String pseudo;
	private Vote vote;
	

	public UserVoteDetails(Jure jure, Vote vote) {
		pseudo = jure.getUser().getUsername();
		this.vote = vote;
		HorizontalLayout hl = new HorizontalLayout();
		hl.add(new Text(pseudo));
		hl.setWidthFull();
		hl.setJustifyContentMode(JustifyContentMode.CENTER);
		hl.getStyle().set("margin-bottom", "15px");
		hl.setSpacing(false);
		add(hl);
		VerticalLayout details = new VerticalLayout();
		if(vote.getQuestion().getCategorieByLibelle("Commentaire") != null) {
			Details commentaire = createCommentaireDetails("Commentaire", vote);
			details.add(commentaire);
		}
		if(vote.getQuestion().getCategorieByLibelle("Note") != null) {
			Details note = createNoteDetails("Note", vote);
			details.add(note);
		}
		if(vote.getQuestion().getPropositions() != null) {
			Details propositions = createPropositionDetails("Propositions",vote);
			details.add(propositions);
		}
		details.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		details.setMaxWidth("280px");
		details.setWidth("280px");
		details.getStyle().set("background-color","White");
		details.getStyle().set("overflow-y", "auto");
		add(details);
		setSpacing(false);
		getStyle ().set ( "border" , "6px dotted DarkOrange" ); 
	}

	// Si on veut éditer on passe à vote.getQuestion().getPropositions() et on les affiche et select les propositions pick
	private Details createPropositionDetails(String summary, Vote vote) {
		Details details = new Details(summary, createPropositionContent(vote.getPropositions()));
		details.setOpened(false);
		return details;
	}

	private VerticalLayout createPropositionContent(Set<Proposition> propositions) {
		VerticalLayout content = new VerticalLayout();
		content.setPadding(false);
		content.setSpacing(false);
		for(Proposition proposition : propositions) {
			Label libelle = new Label(proposition.getLibelle());
			content.add(libelle);
		}
		return content;
	}

	private Details createNoteDetails(String summary, Vote vote) {
		Integer note = null;
		Integer maxValue = null;
		for(VoteCategorie cat : vote.getVotesCategories()) {
			if(cat.getCategorie().getLibelle().equals("Note")) {
				note = Integer.valueOf(cat.getReponse());
				maxValue = cat.getCategorie().getValeur();
			}
		}
		Details details = new Details(summary, createNoteContent(note, maxValue));
		details.setOpened(false);
		return details;
	}

	private VerticalLayout createNoteContent(Integer note, Integer maxValue) {
		VerticalLayout content = new VerticalLayout();
		content.setPadding(false);
		content.setSpacing(false);
		content.add(new Label(note.toString()+"/"+maxValue.toString()));
		return content;
	}

	private Details createCommentaireDetails(String summary, Vote vote) {
		String commentaire = null;
		for(VoteCategorie cat : vote.getVotesCategories()) {
			if(cat.getCategorie().getLibelle().equals("Commentaire"))
				commentaire = cat.getReponse();
		}
		Details details = new Details(summary, createCommentaireContent(commentaire));
		details.setOpened(false);
		return details;
	}

	private VerticalLayout createCommentaireContent(String commentaire) {
		VerticalLayout content = new VerticalLayout();
		content.setPadding(false);
		content.setSpacing(false);
		content.add(new Label(commentaire.toString()));
		return content;
	}**/
	
	private Grid<Jure> grid;
	private Question question;
	
	public UserVoteDetails(Session sess, Question question) {
		this.question = sess.getQuestion(question);
		initGrid(sess);
		setPadding(false);
		setMargin(false);
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
		setMaxWidth("550px");
	}
	
	private void initGrid(Session sess) {
		grid = new Grid<Jure>();
		grid.setMaxHeight("300px");
		Set<Jure> jures = sess.getJures();
		for(Jure jure : sess.getJures()) {
			jures.add(jure);
		}
		for(Jure jure : sess.getJures()) {
			jures.add(jure);
		}
		grid.setItems(jures);
		grid.addColumn(Jure::getPseudo).setResizable(true).setAutoWidth(true).setFlexGrow(0).setHeader("Pseudo").setSortable(true);
    	grid.addComponentColumn(jure -> {
    		return createPermissionIcon(jure.hasVoted(question));
    	}).setHeader("Statut").setSortable(true).setComparator(Jure::getHasVoted);
    	grid.addComponentColumn(jure -> {
    		return createEditButton(jure.hasVoted(question));
    	}).setAutoWidth(true).setFlexGrow(0);
    	grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_WRAP_CELL_CONTENT);
    	grid.setAllRowsVisible(true);
    	add(grid);
    	grid.setSizeUndefined();
	}
	
	

	private Span createPermissionIcon(boolean hasPermission) {
		Span permission;
	    if (hasPermission) {
	    	permission = new Span("A voté!");
	    	permission.getElement().getThemeList().add("badge success primary pill");
	    } else {
	    	permission = new Span("En attente...");
	    	permission.getElement().getThemeList().add("badge pill");
	    }
	    return permission;
	}
	
	private Icon createIcon(VaadinIcon vaadinIcon, String label) {
	    Icon icon = vaadinIcon.create();
	    icon.getStyle().set("padding", "var(--lumo-space-xs");
	    // Accessible label
	    icon.getElement().setAttribute("aria-label", label);
	    // Tooltip
	    icon.getElement().setAttribute("title", label);
	    return icon;
	}
	
	private Button createEditButton(Boolean permission) {
		Button edit = new Button(new Icon("lumo","edit"));
			if(permission) {
				edit.setEnabled(true);
			}else {
				edit.setEnabled(false);
			}
			edit.getStyle().set("padding", "var(--lumo-space-xs");
			return edit;
	}

}
