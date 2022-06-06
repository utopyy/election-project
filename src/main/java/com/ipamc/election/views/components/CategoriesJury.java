package com.ipamc.election.views.components;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Question;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class CategoriesJury extends VerticalLayout {
	
	private IntegerField note;
	private TextField com;
	private Boolean comEnabled;
	private Boolean noteEnabled;
	
	public CategoriesJury(Question quest) {
		add(new H3(quest.getIntitule()));
		com = new TextField("Commentaire");
		for(Categorie cat : quest.getCategories()) {
			if(cat.getLibelle().equals("Note")) {
				noteEnabled = true;
				note = new IntegerField();
				note.setSuffixComponent(new Span("/"+cat.getValeur()));
				note.setLabel("Note");
				note.setWidth("100px");
				note.setMin(0);
				note.setMax(cat.getValeur());
				note.setValueChangeMode(ValueChangeMode.EAGER);
				note.addValueChangeListener(event ->{
					try {
						if(event.getValue()>cat.getValeur()) {
							note.setValue(event.getOldValue());
						}
					}catch(NullPointerException ex) {}	
				});
				add(note);
			}else if(cat.getLibelle().equals("Commentaire")) {
				comEnabled = true;
				add(com);
			}
		}
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
	
	
}
