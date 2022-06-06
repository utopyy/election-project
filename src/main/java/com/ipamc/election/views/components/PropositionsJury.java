package com.ipamc.election.views.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class PropositionsJury extends VerticalLayout {
	
	private Question quest;
	private ListBox simpleList;
	private MultiSelectListBox multiList;
	
	public PropositionsJury(Question quest) {
		this.quest = quest;
		multiList = new MultiSelectListBox<>();
		simpleList = new ListBox<>();
		if(quest.getPropositions().size()>0) {
			List<String> items = new ArrayList<>();
			for(Proposition prop : quest.getPropositions()) {
				items.add(prop.getLibelle());
			}
			if(quest.getMultiChoice()) {
				multiList.setItems(items);
				multiList.select(items.get(0));
				multiList.setHeight("200px");
				add(multiList);
			}else {
				simpleList.setItems(items);
				simpleList.setValue(items.get(0));
				add(simpleList);
			}
		}
	}
	
	public List<String> getMultiResult() {
		List<String> responses = new ArrayList<>();
		for (Object i : multiList.getSelectedItems()) { 
				responses.add((String)i);
	    }
	    return responses;
	}
	
	public String getSimpleResult() {
			return simpleList.getValue().toString();
	}

}
