package com.ipamc.election.views;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ipamc.election.data.BroadcastMessageType;
import com.ipamc.election.data.entity.Broadcaster;
import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.CategorieGridDetails;
import com.ipamc.election.data.entity.Jure;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.data.entity.VoteCategorie;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.CategorieService;
import com.ipamc.election.services.JureService;
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
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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

	/**
	private SessionService sessionService;
	private JureService jureService; 
	private VoteService voteService;
	private Session session;
	private TextField pickPseudo;
	private Question quest;
	private PropositionService propService;
	private CategorieService catService;
	private VoteCategorieService voteCatService;
	private UserVoteState state;
	private User authenticatedUser;
	private Button sendVote = new Button("Envoyer le vote");
	Registration broadcasterRegistration;
	ProgressBar pg;
	List<Vote> votes;

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = attachEvent.getUI();
		broadcasterRegistration = Broadcaster.register(newMessage -> {
			ui.access(() -> pg.setVisible(true));
			ui.access(() -> sendVote.setEnabled(false));
			if(newMessage.equals("ENABLE_VOTE")) {
				ui.access(() -> updateState());
			}else if(newMessage.equals("ACTIVE_SESSION") || newMessage.equals("ARCHIVE_SESSION")) {
				ui.access(() -> updateState());
			}else if(newMessage.equals("SESS_ACTIVE_UPDATED")) {
				ui.access(() -> updateState());
			}else if(newMessage.equals("SESS_DELETE")) {
				ui.access(() -> updateState());
			}
			ui.access(() -> pg.setVisible(false));
			ui.access(() -> sendVote.setEnabled(true));

		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		broadcasterRegistration.remove();
		broadcasterRegistration = null;
	}

	public UserVotesView(UserService userService, JureService jureService, SecurityUtils tools, SessionService sessionService, VoteService voteService, CategorieService catService, PropositionService propService, VoteCategorieService voteCatService) {
		this.userService = userService;
		this.jureService = jureService;
		this.propService = propService;
		this.catService = catService;
		this.voteCatService = voteCatService;
		this.tools = tools;
		this.sessionService = sessionService;
		this.voteService = voteService;
		authenticatedUser = userService.getByUsername(tools.getAuthenticatedUser().getUsername());
		initView();
		pg = new ProgressBar();
		pg.setIndeterminate(true);
		pg.setVisible(false);
		add(pg);

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
			showWaitingResults();
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
		List<QuestionModule> questionsModule = new ArrayList<>();
		ArrayList<Categorie> catsSorted;
		try {
			catsSorted= new ArrayList<>(quest.getCategories());
		}catch(NullPointerException ex) {
			catsSorted = new ArrayList<>();
		}
		catsSorted.sort((c1,c2) -> c1.getLibelle().compareTo(c2.getLibelle()));
		for(Categorie cat : catsSorted) {
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
			QuestionModule register = new QuestionModule(quest);
			add(register);
			questionsModule.add(register);
		}
		getStyle().set("padding_bottom","20px");
		add(sendVote);
		sendVoteChecker(questionsModule, sendVote);
		sendVote.addClickListener(event -> {

			Vote vote = new Vote();
			vote.setJure(jureService.findBySessionAndUser(session, authenticatedUser));
			vote.setQuestion(quest);
			Set<VoteCategorie> votesCategories = new HashSet<>();
			for(QuestionModule question : questionsModule) {
				switch(question.getLibelle()) {
				case "commentaire":
					if(!question.getCommentaireValue().isEmpty()) {
						try {
							votesCategories.add(new VoteCategorie(vote,quest.getCategorieByLibelle("Commentaire"),question.getCommentaireValue()));
						}catch(NullPointerException ex) {}
					}
					break;
				case "note":
					if(question.getNoteValue() != null) {
						try {
							votesCategories.add(new VoteCategorie(vote, quest.getCategorieByLibelle("Note"), question.getNoteValue().toString()));
						}catch(NullPointerException ex) {}
					}					
					break;
				case "propositions":
					vote.setPropositions(question.getPropositionsSelected());
					break;
				default:
				}
			}
			voteService.saveVote(vote);
			for(VoteCategorie voteCat : votesCategories)
				voteCatService.saveVoteCategorie(voteCat);
			Broadcaster.broadcast("VOTE_SENDED");
			updateState();
		});
	}

	private void pageCleaner() {
		removeAll();
		if(session!=null) {
			VerticalLayout header = new VerticalLayout();
			Label sess = new Label(session.getName());
			sess.getStyle().set("font-size", "35px");
			sess.getStyle().set("padding", "0px");
			sess.getStyle().set("margin", "0px");
			header.add(sess);
			if(session.getActiveQuestion()!= null) {
				this.quest = session.getActiveQuestion();
				Label quest = new Label(session.getActiveQuestion().getIntitule()); 
				quest.getStyle().set("font-size", "25px");
				quest.getStyle().set("padding", "0px");
				quest.getStyle().set("margin", "0px");
				header.add(quest);
			}
			header.setSpacing(false);
			header.setWidthFull();
			header.setAlignItems(Alignment.CENTER);
			add(header);
		}
	}

	private void stateChecker() {
		if(sessionService.checkSessionAccess(authenticatedUser)) {
			this.session = sessionService.getActiveSession();
			if(sessionService.jureHasJoined(authenticatedUser)) { 
				if(session.getActiveQuestion() == null) {
					state = UserVoteState.WAITING_QUEST;
				}else {
					Jure jure = new Jure();
					for(Jure jureCheck : session.getJures()) {
						if(jureCheck.getUser().equals(authenticatedUser)){
							jure = jureCheck;
							break;
						}
					}
					if(session.getActiveQuestion().jureHasVoted(jure))
						state = UserVoteState.WAITING_RESULTS;
					else
						state = UserVoteState.ANSWER_QUEST;
				}
			}else {
				state = UserVoteState.PICK_PSEUDO;
			}
		}else {
			state = UserVoteState.NOT_ALLOWED;
		}
	}

	private void showWaitingResults() {
		pageCleaner();
		setJustifyContentMode(JustifyContentMode.START);
		VerticalLayout vl = new VerticalLayout();
		ProgressBar waitingUsersVotes = new ProgressBar();
		double value = (double) quest.getVotes().size()/session.getJures().size();
		waitingUsersVotes.setValue(value);
		Div progressBarLabel = new Div();
		if(quest.getVotes().size() == session.getJures().size()) {
			progressBarLabel.setText("En attente de l'affichage des votes...");
		}else {
			progressBarLabel.setText("En attente des votes...");
		}
		Div progressBarSubLabel = new Div();
		progressBarSubLabel.getStyle().set("font-size", "var(--lumo-font-size-xs)");
		progressBarSubLabel.setText(quest.getVotes().size()+"/"+session.getJures().size());
		vl.add(progressBarLabel, waitingUsersVotes, progressBarSubLabel);
		vl.setSpacing(false);
		vl.getStyle().set("margin-top", "50px");
		vl.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		vl.setMaxWidth("300px");
		vl.getStyle().set("background-color","White");
		add(vl);
	}*/

	private UserService userService;
	private SessionService sessionService;
	private VoteService voteService;
	private SecurityUtils tools;

	private Session activeSession;
	private User authenticatedUser;
	private Registration broadcasterRegistration;

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = attachEvent.getUI();
		broadcasterRegistration = Broadcaster.register(newMessage -> {
			if(newMessage.equals(BroadcastMessageType.CRUD_QUESTION.getLabel())){
				ui.access(() -> {
					loadData();
					loadComponents();
				});				
			}
			else if(newMessage.equals(BroadcastMessageType.CRUD_SESSION.getLabel())) {
				ui.access(() -> {
					loadData();
					loadComponents();
				});
			}else if(newMessage.equals(BroadcastMessageType.SEND_VOTE.getLabel())) {
				ui.access(() -> {
				loadData();
				if(activeSession != null) {
					Jure jure = activeSession.getJureByUser(authenticatedUser);
					if(jure != null && !jure.getArchived() && activeSession.getActiveQuestion()!= null 
							&& jure.hasVoted(activeSession.getActiveQuestion()) && activeSession.getActiveQuestion().getDateVotes() == null) {
						printWaitResults();
					}
				}
				});
			}else if(newMessage.equals(BroadcastMessageType.SHOW_RESULTS.getLabel())) {
				ui.access(() -> {
					printResults();
				});
			}
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		broadcasterRegistration.remove();
		broadcasterRegistration = null;
	}

	public UserVotesView(UserService userService, SessionService sessionService, VoteService voteService, SecurityUtils tools) {
		this.userService = userService;
		this.sessionService = sessionService;
		this.voteService = voteService;
		this.tools = tools;
		this.authenticatedUser = userService.getByUsername(tools.getAuthenticatedUser().getUsername());
		initView();
	}

	private void initView() {
		setAlignItems(Alignment.CENTER);
		getStyle().set("padding-bottom","7em");
		setSizeFull();
		loadData();
		loadComponents();
	}

	private void loadData() {
		activeSession = sessionService.getActiveSession();
	}

	private void loadComponents() {
		// Si il y a une session active
		if(activeSession != null) {
			Jure jure = activeSession.getJureByUser(authenticatedUser);
			// Si le user est jure dans cette session
			if(jure != null && !jure.getArchived()) {
				// Si il y a une question active
				if(activeSession.getActiveQuestion()!= null) {
					// si le jure n'a pas rejoint la session
					if(!jure.getHasJoined()) {
						// Je demande son pseudo
						printPickPseudo();
						// Si il n'y a pas de question active
					}else if(jure.hasVoted(activeSession.getActiveQuestion())) {
						// Si les resultats ont ete affichés
						if(activeSession.getActiveQuestion().getDateVotes() != null) {
							// j'affiche les resultats
							// Si les resultats n'ont pas ete affichés
						}else {
							// J'affiche une page d'attente des résultats
							printWaitResults();
						}
						// Si il n'a pas encore envoyé son vote
					}else {
						// J'affiche le questionnaire
						printShowQuestion();
					}
					// Si il n'y a pas de question active
				}else {
					printWaitingForQuestion();		
				}
				// Si le jure n'est pas dans cette session
			}else {
				printNoAccess();		
			}
			//Si pas de session active
		}else {
			printNoActiveSession();
		}
	}

	private void printNoActiveSession() {
		removeAll();
		setJustifyContentMode(JustifyContentMode.CENTER);
		VerticalLayout box = new VerticalLayout();
		box.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		box.add(new H4("Oups... Aucune session n'est activée pour l'instant !"));
		box.add(new Label("La page se mettra automatiquement à jour en cas de changement."));
		box.setSizeUndefined();
		box.setAlignItems(Alignment.CENTER);
		add(box);
	}

	private void printNoAccess() {
		removeAll();
		setJustifyContentMode(JustifyContentMode.CENTER);
		VerticalLayout box = new VerticalLayout();
		box.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		box.add(new H4("Oups... Vous n'avez pas accès à la session de vote en cours !"));
		box.add(new Label("La page se mettra automatiquement à jour en cas de changement."));
		box.setSizeUndefined();
		box.setAlignItems(Alignment.CENTER);
		add(box);

	}

	private void printPickPseudo() {
		removeAll();
		setJustifyContentMode(JustifyContentMode.CENTER);
		H4 info = new H4();
		info.setText("Indiquez un pseudo pour rejoindre le salon");
		Button submitButton = new Button("Rejoindre");
		submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		TextField pickPseudo = createTextFieldPseudo(submitButton);
		if(authenticatedUser.getPseudo() != null) {
			pickPseudo.setValue(authenticatedUser.getPseudo());
			submitButton.setEnabled(true);
		}else {
			submitButton.setEnabled(false);
		}
		submitButton.addClickListener(event -> {
			if(userService.joinsSession(authenticatedUser, activeSession, pickPseudo.getValue())) {
				loadData();
				loadComponents();
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

	private TextField createTextFieldPseudo(Button submitButton) {
		TextField pickPseudo = new TextField();
		pickPseudo.setMinLength(3);
		pickPseudo.setMaxLength(12);
		pickPseudo.setHelperText("De 3 à 12 caractères");
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
		return pickPseudo;
	}

	private void printShowQuestion() {
		removeAll();
		setJustifyContentMode(JustifyContentMode.START);
		List<QuestionModule> questionsModule = new ArrayList<>();
		ArrayList<Categorie> catsSorted;
		Question quest = activeSession.getActiveQuestion();
		add(new H3(quest.getIntitule()));
		try {
			catsSorted= new ArrayList<>(quest.getCategories());
		}catch(NullPointerException ex) {
			catsSorted = new ArrayList<>();
		}
		catsSorted.sort((c1,c2) -> c1.getLibelle().compareTo(c2.getLibelle()));
		for(Categorie cat : catsSorted) {
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
			QuestionModule register = new QuestionModule(quest);
			add(register);
			questionsModule.add(register);
		}
		getStyle().set("padding_bottom","20px");
		Button sendVote = createSendVoteButton(quest, questionsModule);
		add(sendVote);
		sendVoteChecker(questionsModule, quest, sendVote);

	}

	private Button createSendVoteButton(Question quest, List<QuestionModule> questionsModule) {
		Button sendVote = new Button("Envoyer le vote");
		sendVote.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		sendVote.addClickListener(event -> {
			Vote vote = new Vote();
			vote.setQuestion(quest);
			Set<VoteCategorie> votesCategories = new HashSet<>();
			for(QuestionModule question : questionsModule) {
				switch(question.getLibelle()) {
				case "commentaire":
					if(!question.getCommentaireValue().isEmpty()) {
						try {
							votesCategories.add(new VoteCategorie(vote,quest.getCategorieByLibelle("Commentaire"),question.getCommentaireValue()));
						}catch(NullPointerException ex) {}
					}
					break;
				case "note":
					if(question.getNoteValue() != null) {
						try {
							votesCategories.add(new VoteCategorie(vote, quest.getCategorieByLibelle("Note"), question.getNoteValue().toString()));
						}catch(NullPointerException ex) {}
					}					
					break;
				case "propositions":
					vote.setPropositions(question.getPropositionsSelected());
					break;
				default:
				}
			}
			voteService.saveVote(vote, votesCategories, activeSession, authenticatedUser);
			Broadcaster.broadcast("SEND_VOTE");
			loadData();
			loadComponents();
		});
		return sendVote;
	}

	private void sendVoteChecker(List<QuestionModule> modules, Question activeQuest, Button btn) {
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
				if(!activeQuest.getMultiChoice()) {
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

	private void printWaitResults() {
		removeAll();
		setJustifyContentMode(JustifyContentMode.START);
		Question quest = activeSession.getActiveQuestion();
		add(new H3(quest.getIntitule()));
		VerticalLayout vl = new VerticalLayout();
		vl.getStyle().set("margin-top", "6em");
		ProgressBar waitingUsersVotes = new ProgressBar();
		double value = (double) quest.getVotes().size()/activeSession.getJuresNotArchived().size();
		waitingUsersVotes.setValue(value);
		Div progressBarLabel = new Div();
		if(quest.getVotes().size() == activeSession.getJuresNotArchived().size()) {
			progressBarLabel.setText("En attente de l'affichage des votes...");
		}else {
			progressBarLabel.setText("En attente des votes...");
		}
		Div progressBarSubLabel = new Div();
		progressBarSubLabel.getStyle().set("font-size", "var(--lumo-font-size-xs)");
		progressBarSubLabel.setText(quest.getVotes().size()+"/"+activeSession.getJuresNotArchived().size());
		vl.add(progressBarLabel, waitingUsersVotes, progressBarSubLabel);
		vl.setSpacing(false);
		vl.getStyle().set("margin-top", "50px");
		vl.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		vl.setMaxWidth("300px");
		vl.getStyle().set("background-color","White");
		add(vl);
	}

	private void printResults() {
		removeAll();
		// TODO AND TEST
	}

	private void printWaitingForQuestion() {
		removeAll();
		setJustifyContentMode(JustifyContentMode.START);
		add(new H3(activeSession.getName()));
		VerticalLayout box = new VerticalLayout();
		box.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		box.add(new H5("En attente d'une question..."));
		ProgressBar pb = new ProgressBar();
		pb.setIndeterminate(true);
		box.add(pb);
		box.setSizeUndefined();
		box.setWidthFull();
		box.setMaxWidth("500px");
		box.getStyle().set("margin-top","9em");
		box.getStyle().set("padding-left","12px");
		box.getStyle().set("padding-right","12px");
		box.setAlignItems(Alignment.CENTER);
		add(box);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if(!(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).isActive())) {
			beforeEnterEvent.forwardTo("registration_confirm/"+tools.getAuthenticatedUser().getUsername());		
		}
	}

}

