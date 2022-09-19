package com.ipamc.election.views.components;

import java.util.HashSet;
import java.util.Set;

import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.data.entity.VoteCategorie;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class VoteJuryDetails extends VerticalLayout {
	
	private String pseudo;
	private String commentaire;
	private String note;
	private Set<Proposition> propositions = new HashSet<>();

	public VoteJuryDetails (Vote vote) {
		pseudo = vote.getJure().getPseudo();
		propositions = vote.getPropositions();
		for(VoteCategorie vc : vote.getVotesCategories()) {
			if(vc.isComCategory()) {
				commentaire = vc.getReponse();
			}else {
				note = vc.getReponse()+"/"+vc.getCategorie().getValeur();
			}
		}
		createLayout();
	}
	private void createLayout() {
		getStyle().set("padding-top", "2px");
		createPseudoContainer();
		createDetailsVoteContainer();
	}
	
	private void createPseudoContainer() {
		HorizontalLayout pseudoLay = new HorizontalLayout();
		pseudoLay.setPadding(false);
		pseudoLay.setWidthFull();
		pseudoLay.setMargin(false);
		pseudoLay.getStyle().set("padding-top", "0px");
		pseudoLay.getStyle().set("padding-bottom", "0px");
		H4 pseudoLab = new H4(pseudo);
		pseudoLab.getStyle().set("color","rgb(0,106,245)");
		pseudoLab.getStyle().set("margin-top", "10px");
		pseudoLab.getStyle().set("margin-bottom", "0px");
		pseudoLay.add(pseudoLab);
		pseudoLay.setJustifyContentMode(JustifyContentMode.CENTER);
		add(pseudoLay);
	}
	
	private void createDetailsVoteContainer() {
		VerticalLayout container = new VerticalLayout();
		container.setSizeFull();	
		container.setSpacing(false);
		container.setPadding(false);
		if(note != null) {
			Div label = new Div();
			label.getStyle().set("font-size", "var(--lumo-font-size-xs)");
			label.setText("Note");
			Div noteDiv = new Div();
			Label noteValue = new Label(note);
			noteValue.getStyle().set("color","rgb(0,106,245)");
			noteValue.getStyle().set("padding-top", "0px");
			noteDiv.add(noteValue);
			container.add(label, noteDiv);
			if(commentaire!= null || propositions.size()>0) {
				Hr hr = new Hr();
				hr.setMinHeight("1px");
				hr.setMaxWidth("90%");
				container.add(hr);
			}
		}		
		if(commentaire != null) {
			Div label = new Div();
			label.getStyle().set("font-size", "var(--lumo-font-size-xs)");
			label.setText("Commentaire");
			Div comLay = new Div();
			Label com = new Label(commentaire);
			com.getStyle().set("color","rgb(0,106,245)");
			com.getStyle().set("padding-top", "0px");
			comLay.add(com);
			container.add(label, comLay);
			if(propositions.size()>0) {
				Hr hr = new Hr();
				hr.setMinHeight("1px");
				hr.setMaxWidth("90%");
				container.add(hr);
			}
		}
		if(propositions.size() > 0) {
			Div label = new Div();
			label.getStyle().set("font-size", "var(--lumo-font-size-xs)");
			if(propositions.size() == 1)
			label.setText("Proposition");
			else
				label.setText("Propositions");
			container.add(label, addProps());
		}
		Scroller wrapper = new Scroller(container);
		wrapper.getStyle().set("padding-top", "3px");
		wrapper.setSizeFull();
		add(wrapper);
	}
	
	private VerticalLayout addProps() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(false);
		vl.setPadding(false);
		vl.setSizeFull();
		for(Proposition prop : propositions) {
			Label proposition = new Label(prop.getLibelle());
			proposition.getStyle().set("color","rgb(0,106,245)");
			vl.add(proposition);
		}
		return vl;
	}
}
