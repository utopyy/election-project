package com.ipamc.election.views.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ipamc.election.data.entity.Broadcaster;
import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.CategorieGridDetails;
import com.ipamc.election.data.entity.Jure;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.data.entity.VoteCategorie;
import com.ipamc.election.services.VoteService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

public class UserVoteDetails extends VerticalLayout {

	private Grid<Jure> grid;
	private Question question;
	private VoteService voteService;
	Registration broadcasterRegistration;
	
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = attachEvent.getUI();
		broadcasterRegistration = Broadcaster.register(newMessage -> {
			if(newMessage.equals("VOTE_SENDED")) {
				ui.access(() -> refreshGrid());
				System.out.println("coucou la fami");
			}
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		broadcasterRegistration.remove();
		broadcasterRegistration = null;
	}

	public UserVoteDetails(Session sess, Question question, VoteService voteService) {
		this.question = sess.getQuestion(question);
		this.voteService = voteService;
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
			return createEditButton(jure);
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

	private Button createEditButton(Jure jure) {
		Button edit = new Button(new Icon("lumo","edit"));
		if(jure.getHasVoted()) {
			edit.setEnabled(true);
		}else {
			edit.setEnabled(false);
		}
		edit.getStyle().set("padding", "var(--lumo-space-xs");
		edit.addClickListener(event -> {
			createDialog(jure);
		});
		return edit;
	}

	private void createDialog(Jure jure) {
		Dialog dialog = new Dialog();

		VerticalLayout dialogLayout = createDialogLayout(jure);
		dialogLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		dialogLayout.setAlignItems(Alignment.CENTER);
		dialog.add(dialogLayout);
		TextField pseudo = new TextField("Pseudo");
		pseudo.setValue(jure.getPseudo());
		dialog.getHeader().add(pseudo);
		Button saveButton = createSaveButton(dialog);
		Button cancelButton = new Button("Annuler", e -> dialog.close());
		dialog.getFooter().add(cancelButton);
		dialog.getFooter().add(saveButton);
		dialog.setMaxHeight("800px");
		dialog.setMaxWidth("600px");
		dialog.setSizeFull();
		dialog.open();
	}


	private VerticalLayout createDialogLayout(Jure jure) {
			VerticalLayout dialog = new VerticalLayout();
			Vote vote = voteService.getVoteByJureAndQuestion(jure, question);
			dialog.setJustifyContentMode(JustifyContentMode.START);
			List<QuestionModule> questionsModule = new ArrayList<>();
			ArrayList<Categorie> catsSorted;
			try {
				catsSorted= new ArrayList<>(question.getCategories());
			}catch(NullPointerException ex) {
				catsSorted = new ArrayList<>();
			}
			catsSorted.sort((c1,c2) -> c1.getLibelle().compareTo(c2.getLibelle()));
			for(Categorie cat : catsSorted) {
				QuestionModule register;
				if(cat.getLibelle().equals("Commentaire")) {
					register = new QuestionModule(cat.getIsRequired(), question, vote);
				}else {
					register = new QuestionModule(cat.getValeur(), cat.getIsRequired(), question, vote);							
				}
				dialog.add(register);
				questionsModule.add(register);
			}
			if(question.getPropositions().size()>0) {
				QuestionModule register = new QuestionModule(question, vote);
				dialog.add(register);
				questionsModule.add(register);
			}
			return dialog;
	}

	private static Button createSaveButton(Dialog dialog) {
		Button saveButton = new Button("Ajouter");
		saveButton.addClickListener(event -> {
			//add button checker
			// add update vote
			// add notif
			dialog.close();	
		});
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		return saveButton;
	}
	
	private void refreshGrid() {
		grid.getDataProvider().refreshAll();
	}

}
