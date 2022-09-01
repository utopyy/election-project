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
import com.ipamc.election.services.PropositionService;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.UserService;
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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;

public class UserVoteDetails extends VerticalLayout {

	private Grid<Jure> grid;
	private Question question;
	private VoteService voteService;
	private UserService userService;
	private QuestionService questionService;
	
	Registration broadcasterRegistration;

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = attachEvent.getUI();
		broadcasterRegistration = Broadcaster.register(newMessage -> {
			if(newMessage.equals("VOTE_SENDED")) {
				ui.access(() -> {
					updateQuestion();
					refreshGrid();
				});
			}
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		broadcasterRegistration.remove();
		broadcasterRegistration = null;
	}

	public UserVoteDetails(Session sess, Question question, VoteService voteService, UserService userService, QuestionService questionService) {
		this.question = sess.getQuestion(question);
		this.voteService = voteService;
		this.userService = userService;
		this.questionService = questionService;
		initGrid();
		setPadding(false);
		setMargin(false);
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
		setMaxWidth("550px");
	}

	private void initGrid() {
		grid = new Grid<Jure>();
		grid.setMaxHeight("300px");
		grid.setItems(question.getSession().getJuresNotArchived());
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
		Vote vote = voteService.getVoteByJureAndQuestion(jure, question);
		Dialog dialog = new Dialog();
		List<QuestionModule> questionsModule = new ArrayList<>();
		VerticalLayout dialogLayout = createDialogLayout(jure, vote, questionsModule);
		dialogLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		dialogLayout.setAlignItems(Alignment.CENTER);
		dialog.add(dialogLayout);
		TextField pseudo = new TextField("Pseudo");
		pseudo.setValue(jure.getPseudo());
		dialog.getHeader().add(pseudo);
		Button saveButton = createSaveButton(dialog, pseudo, jure, vote, questionsModule);
		Button cancelButton = new Button("Annuler", e -> dialog.close());
		dialog.getFooter().add(cancelButton);
		dialog.getFooter().add(saveButton);
		dialog.setMaxHeight("800px");
		dialog.setMaxWidth("600px");
		dialog.setSizeFull();
		dialog.open();
	}


	private VerticalLayout createDialogLayout(Jure jure, Vote vote, List<QuestionModule> questionsModule) {
		VerticalLayout dialog = new VerticalLayout();
		dialog.setJustifyContentMode(JustifyContentMode.START);
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

	private Button createSaveButton(Dialog dialog, TextField pseudo, Jure jure, Vote vote, List<QuestionModule> questionsModule) {
		Button saveButton = new Button("Modifier");
		sendVoteChecker(pseudo, jure, questionsModule, saveButton);
		saveButton.addClickListener(event -> {
			Set<VoteCategorie> vc = new HashSet<>();
			for(QuestionModule questionMod : questionsModule) {
				switch(questionMod.getLibelle()) {
				case "commentaire":
					if(!questionMod.getCommentaireValue().isEmpty())
						vc.add(new VoteCategorie(vote, question.getCategorieByLibelle("Commentaire"), questionMod.getCommentaireValue().toString()));
					break;
				case "note":
					if(questionMod.getNoteValue() != null && !questionMod.getNoteValue().toString().isEmpty())
						vc.add(new VoteCategorie(vote, question.getCategorieByLibelle("Note"), questionMod.getNoteValue().toString()));
					break;
				case "propositions":
					vote.setPropositions(questionMod.getPropositionsSelected());
					break;
				default:
				}
			}
			vote.setVotesCategories(vc);
			voteService.updateVote(vote);
			if(jure.getPseudo()!=pseudo.getValue()) {
				userService.updatePseudo(jure.getUser().getUsername(), pseudo.getValue());
				jure.getUser().setPseudo(pseudo.getValue());
			}
			refreshGrid();
			dialog.close();	
			Notification notification = Notification.show("Vote mis à jour!");
			notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			notification.setPosition(Notification.Position.TOP_END);
			notification.setDuration(3000);
		});
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		return saveButton;
	}

	private void refreshGrid() {
		grid.getDataProvider().refreshAll();
	}

	private void sendVoteChecker(TextField pseudo, Jure jure, List<QuestionModule> modules, Button btn) {
		for(QuestionModule question : modules)
			switch(question.getLibelle()) {
			case "commentaire" :
				question.getCommentaire().setValueChangeMode(ValueChangeMode.EAGER);
				question.getCommentaire().addValueChangeListener(event -> {
					if(question.getIsRequired() && event.getValue().isBlank()) {
						question.getCommentaire().setInvalid(true);
						question.getCommentaireIcon().setColor("RED");
					}else {
						question.getCommentaire().setInvalid(false);
						if(question.getIsRequired())
							question.getCommentaireIcon().setColor("");
					}
					if(disableButton(pseudo, modules)) {
						btn.setEnabled(false);
					}else {
						btn.setEnabled(true);
					}
				});
				break;
			case "note" :
				question.getNote().setValueChangeMode(ValueChangeMode.EAGER);
				question.getNote().addValueChangeListener(event -> {
					if(question.getIsRequired() && event.getValue() == null) {
						question.getNote().setInvalid(true);
						question.getNoteIcon().setColor("RED");
						disableButton(pseudo, modules);
					}else if(event.getValue() != null && event.getValue() > question.getNote().getMax()){
						question.getNote().setInvalid(true);
						if(question.getIsRequired())
							question.getNoteIcon().setColor("RED");
					}else {
						question.getNote().setInvalid(false);
						if(question.getIsRequired())
							question.getNoteIcon().setColor("");
					}
					if(disableButton(pseudo, modules)) {
						btn.setEnabled(false);
					}else {
						btn.setEnabled(true);
					}
					if(question.getNote().getValue() != null && question.getNote().getValue() < 0) {
						question.getNote().setValue(0);
					}
				});
				break;
			case "propositions" : 
				question.getPropositions().addSelectionListener(event -> {
					if(question.getQuestion().getPropositionRequired() && question.getPropositions().isEmpty()) {
						question.getPropIcon().setColor("RED");
					}else {
						if(question.getIsRequired())
							question.getPropIcon().setColor("");
					}
					if(disableButton(pseudo, modules)) {
						btn.setEnabled(false);
					}else {
						btn.setEnabled(true);
					}
				});	
				if(!this.question.getMultiChoice()) {
					question.getPropositions().addValueChangeListener(event -> {
						if(event.getValue().size()>1) {
							Set<Proposition> addProps = new HashSet<>();
							Set<Proposition> removeProps = new HashSet<>();
							for(Proposition p : event.getValue()) {
								if(!event.getOldValue().contains(p)) {
									addProps.add(p);
								}else {
									removeProps.add(p);
								}
							}
							question.getPropositions().updateSelection(addProps, removeProps);
						}
					});
				}
				break;
			default : 
				break;
			}
		pseudo.setMinLength(3);
		pseudo.setMaxLength(12);
		pseudo.setHelperText("De 3 à 12 caractères");
		pseudo.setValueChangeMode(ValueChangeMode.EAGER);
		pseudo.addValueChangeListener(event ->{
			if(event.getValue().length() > 2 && event.getValue().length() < 13) {
				if(userService.pseudoExists(event.getValue()) && 
						!(userService.getByPseudo(event.getValue()).getUsername().equals(jure.getUser().getUsername()))) {
					pseudo.setInvalid(true);
					pseudo.setErrorMessage("Ce pseudo est déjà pris...");
				}else {
					pseudo.setInvalid(false);
				}
			}else {
				pseudo.setErrorMessage("");
				pseudo.setInvalid(true);
			}
			if(disableButton(pseudo, modules)) {
				btn.setEnabled(false);
			}else {
				btn.setEnabled(true);
			}
		});
	}

	private Boolean disableButton(TextField pseudo, List<QuestionModule> modules) {
		if(pseudo.isInvalid()) {
			return true;
		}
		for(QuestionModule question : modules) {
			switch(question.getLibelle()) {
			case "commentaire" :
				if(question.getIsRequired() && question.getCommentaire().getValue().isBlank()) {
					return true;
				}
				break;
			case "note" :
				if((question.getIsRequired() && question.getNote().getValue() == null) ||  (question.getNote().getValue() != null && question.getNote().getValue() > question.getNote().getMax())) {
					return true;
				}
				if(question.getNote().getValue() != null && question.getNote().isInvalid()){
					return true;
				}
				break;
			case "propositions" : 
				if(question.getQuestion().getPropositionRequired() && question.getPropositions().isEmpty()) {
					return true;
				}
				break;
			default : 
				break;
			}
		}
		return false;

	}

	private void updateQuestion() {
		question = questionService.getById(question.getId());
		grid.setItems(question.getSession().getJures());
	}
}
