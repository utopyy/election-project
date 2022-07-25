package com.ipamc.election.views;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ipamc.election.data.UserVoteState;
import com.ipamc.election.data.entity.Broadcaster;
import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.CategorieService;
import com.ipamc.election.services.PropositionService;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.ipamc.election.services.VoteCategorieService;
import com.ipamc.election.services.VoteService;
import com.ipamc.election.views.components.QuestionModule;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

@Route(value = "jury", layout = MainLayout.class)
@PageTitle("Salon de votes")

public class UserVotesView extends VerticalLayout implements BeforeEnterObserver  {

	private UserService userService;
	private SecurityUtils tools;
	private SessionService sessionService;
	private VoteService voteService;
	private Session session;
	private TextField pickPseudo;
	private Question quest;
	private PropositionService propService;
	private CategorieService catService;
	private VoteCategorieService voteCatService;
	private UserVoteState state;
	private User authenticatedUser;
	Registration broadcasterRegistration;

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = attachEvent.getUI();
		broadcasterRegistration = Broadcaster.register(newMessage -> {
			if(newMessage.equals("ENABLE_VOTE")) {
				ui.access(() -> updateState());
			}else if(newMessage.equals("ACTIVE_SESSION")) {
				ui.access(() -> updateState());
			}else if(newMessage.equals("SESS_ACTIVE_UPDATED")) {
				ui.access(() -> updateState());
			}else if(newMessage.equals("SESS_DELETE")) {
				ui.access(() -> updateState());
			}
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		broadcasterRegistration.remove();
		broadcasterRegistration = null;
	}

	public UserVotesView(UserService userService, SecurityUtils tools, SessionService sessionService, VoteService voteService, CategorieService catService, PropositionService propService, VoteCategorieService voteCatService) {
		this.userService = userService;
		this.propService = propService;
		this.catService = catService;
		this.voteCatService = voteCatService;
		this.tools = tools;
		this.sessionService = sessionService;
		this.voteService = voteService;
		authenticatedUser = userService.getByUsername(tools.getAuthenticatedUser().getUsername());
		initView();
	}

	private void initView() {
		setJustifyContentMode(JustifyContentMode.CENTER);
		setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		updateState();
		setSizeFull();
		getStyle().set("text-align", "center");

	}

	private void sendVoteChecker(List<QuestionModule> modules, Button btn) {
		for(QuestionModule question : modules) {
			if(question.getIsRequired()) {
				btn.setEnabled(false);
				break;
			}
		}
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
					if(disableButton(modules)) {
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
						disableButton(modules);
					}else if(event.getValue() != null && event.getValue() > question.getNote().getMax()){
						question.getNote().setInvalid(true);
						if(question.getIsRequired())
							question.getNoteIcon().setColor("RED");
					}else {
						question.getNote().setInvalid(false);
						if(question.getIsRequired())
							question.getNoteIcon().setColor("");
					}
					if(disableButton(modules)) {
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
					if(disableButton(modules)) {
						btn.setEnabled(false);
					}else {
						btn.setEnabled(true);
					}
				});	
				if(!quest.getMultiChoice()) {
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
	}


	private Boolean disableButton(List<QuestionModule> modules) {
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

	private void updateState() {
		if(sessionService.getActiveSession()!=null) {
			session = sessionService.getActiveSession();
		}
		stateChecker();
		switch(state) {
		case NOT_ALLOWED:
			showNotAllowed();
			break;
		case PICK_PSEUDO:
			showPickPseudo();
			break;
		case WAITING_QUEST:
			showWaitingQuest();
			break;
		case ANSWER_QUEST:
			showAnswerQuest();
			break;
		case WAITING_RESULTS:
			break;
		case SHOW_RESULTS:
			break;
		default:
		}
	}

	private void showNotAllowed() {
		session = null;
		pageCleaner();
		H4 info = new H4();
		info.setText("Aucun salon de votes n'est disponible pour vous.");
		add(info);
	}

	private void showPickPseudo() {
		pageCleaner();
		H4 info = new H4();
		info.setText("Indiquez un pseudo pour rejoindre le salon");
		pickPseudo = new TextField();
		pickPseudo.setMinLength(3);
		pickPseudo.setMaxLength(12);
		pickPseudo.setHelperText("De 3 à 12 caractères");

		Button submitButton = new Button("Rejoindre");
		submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		if(authenticatedUser.getPseudo() != null) {
			pickPseudo.setValue(authenticatedUser.getPseudo());
			submitButton.setEnabled(true);
		}else {
			submitButton.setEnabled(false);
		}

		pickPseudo.setValueChangeMode(ValueChangeMode.EAGER);
		pickPseudo.addValueChangeListener(event ->{
			if(event.getValue().length() > 2 && event.getValue().length() < 13) {
				if(userService.pseudoExists(event.getValue()) && 
						!(userService.getByPseudo(event.getValue()).getUsername().equals(authenticatedUser.getUsername()))) {
					pickPseudo.setInvalid(true);
					pickPseudo.setErrorMessage("Ce pseudo est déjà pris...");
					submitButton.setEnabled(false);
				}else {
					submitButton.setEnabled(true);
				}
			}else {
				pickPseudo.setErrorMessage("");
				submitButton.setEnabled(false);
			}
		});

		submitButton.addClickListener(event -> {
			if(!userService.pseudoExists(pickPseudo.getValue()) || (userService.pseudoExists(pickPseudo.getValue()) && userService.getByPseudo(pickPseudo.getValue()).equals(authenticatedUser))) {
				userService.updatePseudo(tools.getAuthenticatedUser().getUsername(),pickPseudo.getValue()); 
				userService.joinsSession(authenticatedUser, session);
				UI.getCurrent().getPage().reload();
			}else {
				pickPseudo.setInvalid(true);
				pickPseudo.setErrorMessage("Oups, quelqu'un vient juste de prendre ce pseudo...");
			}
		});

		HorizontalLayout layout = new HorizontalLayout();
		layout.setPadding(true);
		layout.add(pickPseudo);
		layout.add(submitButton);

		add(info, layout);
	}

	private void showWaitingQuest() {
		pageCleaner();
		add(new Label("En attente d'une question...."));
	}

	private void showAnswerQuest() {
		pageCleaner();
		setJustifyContentMode(JustifyContentMode.START);
		quest = session.getActiveQuestion(); 
		add(new H3(quest.getIntitule()));
		List<QuestionModule> questionsModule = new ArrayList<>();
		for(Categorie cat : quest.getCategories()) {
			QuestionModule register;
			if(cat.getLibelle().equals("Commentaire")) {
				register = new QuestionModule(cat.getIsRequired(), quest);
			}else {
				register = new QuestionModule(cat.getValeur(), cat.getIsRequired(), quest);							
			}
			add(register);
			questionsModule.add(register);
		}
		if(quest.getPropositions().size()>0) {
			QuestionModule register = new QuestionModule(quest.getPropositions(), quest.getPropositionRequired(), quest.getMultiChoice(), quest);
			add(register);
			questionsModule.add(register);
		}
		Button sendVote = new Button("Envoyer le vote");
		add(sendVote);
		sendVoteChecker(questionsModule, sendVote);
	}

	private void pageCleaner() {
		removeAll();
		if(session!=null) {
			add(new H2(session.getName()));
		}
	}

	private void stateChecker() {
		if(sessionService.checkSessionAccess(authenticatedUser)) {
			this.session = sessionService.getActiveSession();
			if(sessionService.jureHasJoined(authenticatedUser)) {
				if(session.getActiveQuestion() == null) {
					state = UserVoteState.WAITING_QUEST;
				}else {
					state = UserVoteState.ANSWER_QUEST;
				}
			}else {
				state = UserVoteState.PICK_PSEUDO;
			}
		}else {
			state = UserVoteState.NOT_ALLOWED;
		}
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if(!(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).isActive())) {
			beforeEnterEvent.forwardTo("registration_confirm/"+tools.getAuthenticatedUser().getUsername());		
		}
	}

}

