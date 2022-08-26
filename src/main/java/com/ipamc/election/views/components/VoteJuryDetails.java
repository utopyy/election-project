package com.ipamc.election.views.components;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.data.entity.VoteCategorie;
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
		createPseudoContainer();
		createDetailsVoteContainer();
	}
	
	private void createPseudoContainer() {
		HorizontalLayout pseudoLay = new HorizontalLayout();
		pseudoLay.setWidthFull();
		Label pseudoLab = new Label(pseudo);
		pseudoLab.getStyle().set("padding-bottom", "6px");
		pseudoLab.getStyle().set("margin-left", "4px");
		pseudoLab.getStyle().set("margin-right", "4px");
		pseudoLay.add(pseudoLab);
		pseudoLay.setJustifyContentMode(JustifyContentMode.CENTER);
		pseudoLay.getStyle().set("border-bottom","1px solid black");
		add(pseudoLay);
	}
	
	private void createDetailsVoteContainer() {
		VerticalLayout container = new VerticalLayout();
		container.setSizeFull();	
		if(note != null) {
			HorizontalLayout noteLay = new HorizontalLayout();
			noteLay.setSizeFull();
			noteLay.add(new Label("Note: "));
			noteLay.add(new Label(note));
			noteLay.setJustifyContentMode(JustifyContentMode.BETWEEN);
			container.add(noteLay);
		}
		
		if(commentaire != null) {
			HorizontalLayout comLay = new HorizontalLayout();
			comLay.setSizeFull();
			comLay.add(new Label(commentaire));
			container.add(comLay);
		}
		if(propositions.size() > 0) {
			container.add(addProps());
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
			vl.add(proposition);
		}
		return vl;
	}
}
