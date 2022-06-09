package com.ipamc.election.views.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.data.entity.VoteCategorie;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.CategorieService;
import com.ipamc.election.services.PropositionService;
import com.ipamc.election.services.UserService;
import com.ipamc.election.services.VoteCategorieService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.Command;

public class CategoriesJury extends VerticalLayout {
	
	private IntegerField note;
	private TextField com;
	private Boolean comEnabled;
	private Boolean noteEnabled;
	private Button submit;

	
	public CategoriesJury(Question quest, PropositionsJury props, PropositionService propService, UserService userService, SecurityUtils tools, CategorieService catService, VoteCategorieService voteCatService) {
		add(new H3(quest.getIntitule()));
		com = new TextField("Commentaire");
		note = new IntegerField();
		submit = new Button("Envoyer vote");
		if(quest.getVoteEnabled()) {
			submit.setEnabled(true);
		}else {
			submit.setEnabled(false);
		}
		for(Categorie cat : quest.getCategories()) {
			if(cat.getLibelle().equals("Note")) {
				if(cat.getIsRequired()) {
					submit.setEnabled(false);
				}
				noteEnabled = true;
				note.setSuffixComponent(new Span("/"+cat.getValeur()));
				note.setLabel("Note");
				note.setWidth("100px");
				note.setMin(0);
				note.setMax(cat.getValeur());
				note.setValueChangeMode(ValueChangeMode.EAGER);
				note.addValueChangeListener(event ->{
					if(cat.getIsRequired() && (note.isInvalid() || note.isEmpty())) {
						note.setErrorMessage("Ce champ est obligatoire");
						note.setInvalid(true);
						submit.setEnabled(false);
					}else {
						note.setInvalid(false);
						if(!com.isInvalid()) {
							submit.setEnabled(true);
						}
					}
					try {
						if(event.getValue()>cat.getValeur()) {
							note.setValue(event.getOldValue());
						}
					}catch(NullPointerException ex) {}	
				});
				
				add(note);
			}else if(cat.getLibelle().equals("Commentaire")) {
				com.setValueChangeMode((ValueChangeMode.EAGER));
				com.addValueChangeListener(event ->{
					if(cat.getIsRequired() && (com.getValue().isEmpty() || com.getValue().isBlank())) {
						com.setErrorMessage("Ce champ est obligatoire");
						com.setInvalid(true);
						submit.setEnabled(false);
					}else {
						com.setInvalid(false);
						if(!note.isInvalid()) {
							submit.setEnabled(true);
						}
					}
				});
				comEnabled = true;
				add(com);
			}
		}
		
		submit.addClickListener(event -> {
			Set<Proposition> propositions = new HashSet<>();
			if(quest.getMultiChoice()) {
				List<String> propsList = props.getMultiResult();
				for(String rep : propsList) {
					propositions.add(propService.findByLibelle(rep));
				}
			}else {
				propositions.add(propService.findByLibelle(props.getSimpleResult()));
			}
			ConfirmDialog.create()
		      .withCaption("Confirmation")
		      .withMessage("Vous ne pourrez pas modifier votre vote, confirmer ?")
		      .withOkButton(() -> {
				Vote vote = userService.createVote(userService.getByUsername(tools.getAuthenticatedUser().getUsername()), quest, propositions); 
				List<String> valueList = new ArrayList<String>(); // trick to make ConfirmDialogBox working, String value don't work, but with array it's okay
				valueList.add("");
				for(Categorie cat : quest.getCategories()) {
						if(cat.getLibelle().equals("Commentaire")) {
							if(com.getValue().isBlank()) {
								valueList.set(0,null);
							}else {
								valueList.set(0, com.getValue());	
							}
						}else{
	    					try {
	    						valueList.set(0, note.getValue().toString());
	    					}catch(NullPointerException ex) {
	    							valueList.set(0, null);
	    					};
	    				}
				}
		      }, ButtonOption.focus(), ButtonOption.caption("OUI"))
		      .withCancelButton(ButtonOption.caption("NON")).open();
		});
	}
	
    public void enableVotes() {
    	        submit.setEnabled(true);
    }

	public IntegerField getNote() {
		return note;
	}


	public TextField getCom() {
		return com;
	}

	public Boolean getComEnabled() {
		return comEnabled;
	}

	public void setComEnabled(Boolean comEnabled) {
		this.comEnabled = comEnabled;
	}

	public Boolean getNoteEnabled() {
		return noteEnabled;
	}

	public void setNoteEnabled(Boolean noteEnabled) {
		this.noteEnabled = noteEnabled;
	}

	public void setNote(IntegerField note) {
		this.note = note;
	}

	public void setCom(TextField com) {
		this.com = com;
	}

	public Button getSubmit() {
		return submit;
	}

	public void setSubmit(Button submit) {
		this.submit = submit;
	}
	
	
	
	
}
