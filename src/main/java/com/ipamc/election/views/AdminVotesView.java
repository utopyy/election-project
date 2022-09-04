package com.ipamc.election.views;

import com.ipamc.election.data.BroadcastMessageType;
import com.ipamc.election.data.entity.Broadcaster;
import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Jure;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.data.entity.VoteCategorie;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.ipamc.election.services.VoteService;
import com.ipamc.election.views.components.DetailsQuestionEditable;
import com.ipamc.election.views.components.QuestionModule;
import com.ipamc.election.views.components.ResultsVotes;
import com.ipamc.election.views.components.UserVoteDetails;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.model.Responsive;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;


@Route(value = "salon" , layout = MainLayout.class)
@PageTitle("Salon de votes")


public class AdminVotesView extends VerticalLayout implements BeforeEnterObserver {

	/**private UserService userService;
	private VoteService voteService;
	private VoteCategorieService voteCatService;
	private JureService jureService;
	private QuestionService questionService;
	private SecurityUtils tools;
	private Select<Question> selectQuestion;
	private Session activeSession;
	private SessionService sessionService; 
	private DetailsQuestionEditable detailsQuest;
	private VerticalLayout step1;
	private VerticalLayout step2;
	private VerticalLayout step3;
	private VerticalLayout step4;
	private Button next;
	private User authenticatedUser;
	private VerticalLayout header;
	private ProgressBar waitingUsersVotes;
	private Div progressBarSubLabel;
	Registration broadcasterRegistration;


	public AdminVotesView(UserService userService, VoteService voteService, VoteCategorieService voteCatService, JureService jureService, SecurityUtils tools, SessionService sessionService, QuestionService questionService) {
		this.userService = userService;
		this.sessionService = sessionService;
		this.questionService = questionService;
		this.voteService = voteService;
		this.voteCatService = voteCatService;
		this.jureService = jureService;

		this.tools = tools;
		authenticatedUser = userService.getByUsername(tools.getAuthenticatedUser().getUsername());
		setAlignItems(Alignment.CENTER);
		initView();
	}

	private void initView() {
		removeAll();
		setSizeFull();
		setSpacing(false);
		getStyle().set("background-color","rgb(251,253,255)");
		if(sessionService.getActiveSession() != null) {
			activeSession = sessionService.getActiveSession();
			if(sessionService.getActiveSession().getActiveQuestion() != null) {
				Button back = new Button("Retour");
				initBackButton(back);
				Jure jure = jureService.findBySessionAndUser(activeSession, authenticatedUser);
				// Si l'admin est juré
				if(jure != null && !jure.getArchived()) {
					// Si l'admin n'a pas encore voté
					if(voteService.getVoteByJureAndQuestion(jureService.findBySessionAndUser(activeSession, authenticatedUser), activeSession.getActiveQuestion()) == null) {
						initStep3(activeSession.getActiveQuestion(), back);
						add(step3);
						// Si l'admin a voté
					}else {
						initStep4(activeSession.getActiveQuestion(), back);
						add(step4);	
					}
					// Si l'admin n'est pas juré
				}else {
					initStep4(activeSession.getActiveQuestion(), back);
					add(step4);

				}
			}else {
				activeSession = sessionService.getActiveSession();
				add(new H2(activeSession.getName()));
				Label hint = new Label("Sélectionnez la question que vous voulez poser ou créez-en une nouvelle.");	
				Button ok = new Button("Ok");
				initSelectQuestions();
				initPickQuestionBtn(ok);
				initStep2();
				HorizontalLayout pickQuest = new HorizontalLayout(selectQuestion, ok);
				step1 = new VerticalLayout(hint, pickQuest);
				step1.setAlignItems(FlexComponent.Alignment.CENTER);
				step1.getStyle().set("margin-top", "50px");
				step1.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
				step1.setMaxWidth("600px");
				step1.getStyle().set("background-color","White");
				add(step1);
				setSpacing(false);
				setSizeFull();
			}
		}else {
			// AFFICHER MESSAGE : PAS DE SESSION ACTIVE, + link pour en activer une?
			// TEMPORAIRE
			add(new Label("Aucune session active"));
		}
	}

	private void initSelectQuestions() {
		selectQuestion = new Select<>();
		List<Question> questions = new ArrayList<>();
		for(Question quest : activeSession.getUnansweredQuestions()) {
			questions.add(quest);
		}
		Collections.sort(questions, new Comparator<Question>() {
			@Override
			public int compare(Question quest1, Question quest2) {
				return quest1.getIntitule().compareTo(quest2.getIntitule());
			}
		});
		selectQuestion.setItems(questions);
		selectQuestion.setEmptySelectionAllowed(true);
		selectQuestion.setItemLabelGenerator(item -> {
			if(item!=null)
				return item.getIntitule();
			else
				return "Nouvelle question";
		});
		selectQuestion.setEmptySelectionCaption("Nouvelle question");
	}

	private void initPickQuestionBtn(Button ok) {
		ok.addClickListener(event -> {
			next = new Button("Lancer le vote", new Icon(VaadinIcon.ARROW_RIGHT));
			step2.removeAll();
			if(selectQuestion.getValue() != null) {
				detailsQuest = new DetailsQuestionEditable(selectQuestion.getValue(), questionService, sessionService);
			}else {
				detailsQuest = new DetailsQuestionEditable(questionService, sessionService, next);
			}
			initNextButton(next, detailsQuest);
			step2.add(detailsQuest);
			remove(step1);
			HorizontalLayout hl = new HorizontalLayout();
			Button back = new Button("Retour", new Icon(VaadinIcon.ARROW_LEFT));
			initBackButton(back, hl);
			next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			next.setIconAfterText(true);
			hl.add(back, next);
			hl.setJustifyContentMode(JustifyContentMode.BETWEEN);
			hl.getStyle().set("margin-top", "10px");
			hl.setWidthFull();
			hl.setMaxWidth("900px");
			add(hl, step2);
		});
	}

	private void initBackButton(Button btn, HorizontalLayout hl) {
		btn.addClickListener(event -> {
			remove(hl);
			remove(step2);
			remove(header);
			initView();
		});
	}

	private void initNextButton(Button btn, DetailsQuestionEditable detailsQuest) {
		btn.addClickListener(event -> {
			if(detailsQuest.isNewQuestion()) {
				detailsQuest.saveSession();
			}
			questionService.activeQuestion(activeSession, detailsQuest.getQuestionPicked());
			Broadcaster.broadcast("CRUD_QUESTION");
			initView();
		});
	}


	private void initStep2() {
		step2 = new VerticalLayout();
		step2.setAlignItems(FlexComponent.Alignment.CENTER);
		step2.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		step2.setMaxWidth("900px");
		step2.getStyle().set("background-color","White");
	}

	private void initStep3(Question quest, Button back) {

		step3 = new VerticalLayout();
		step3.setAlignItems(FlexComponent.Alignment.CENTER);

		addHeader(quest);

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
			step3.add(register);
			questionsModule.add(register);
		}
		if(quest.getPropositions().size()>0) {
			QuestionModule register = new QuestionModule(quest);
			step3.add(register);
			questionsModule.add(register);
		}
		getStyle().set("padding_bottom","20px");
		Button sendVote = new Button("Envoyer le vote");
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.add(back, sendVote);
		buttons.setWidthFull();
		buttons.setJustifyContentMode(JustifyContentMode.CENTER);
		step3.add(buttons);
		sendVoteChecker(questionsModule, sendVote);
		sendVote.addClickListener(event -> {

			Vote vote = new Vote();

			vote.setJure(jureService.findBySessionAndUser(activeSession, authenticatedUser));
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
					if(question.getNoteValue() != null && !question.getNoteValue().toString().isEmpty()) {
						try {
							votesCategories.add(new VoteCategorie(vote, quest.getCategorieByLibelle("Note"), question.getNoteValue().toString()));
						}catch(NullPointerException ex) {}
					}break;
				case "propositions":
					vote.setPropositions(question.getPropositionsSelected());
					break;
				default:
				}
			}
			voteService.saveVote(vote, votesCategories, activeSession, authenticatedUser);
			for(VoteCategorie voteCat : votesCategories)
				voteCatService.saveVoteCategorie(voteCat);
			Broadcaster.broadcast("SEND_VOTE");
			initStep4(quest, back);
			remove(step3);
			remove(header);
			add(step4);
		});
	}

	private void initStep4(Question question, Button back) {
		addHeader(question);
		VerticalLayout menu = initProgressBar(question, activeSession, back);
		menu.setPadding(false);
		menu.getStyle().set("padding-top", "10px");
		menu.setAlignItems(Alignment.CENTER);
		menu.setSpacing(false);
		step4 = new VerticalLayout();
		step4.setSizeFull();
		step4.setAlignItems(Alignment.CENTER);

		UserVoteDetails uvd = new UserVoteDetails(activeSession, activeSession.getActiveQuestion(), voteService, userService, questionService);
		Scroller scroll = new Scroller(uvd);
		Details details = new Details("Détail des votes", scroll);
		details.getStyle().set("margin-top", "0px");
		details.setMaxWidth("550px");
		details.setSizeFull();
		details.setOpened(false);
		menu.add(details);
		menu.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		menu.setWidthFull();
		menu.getStyle().set("background-color","White");
		menu.getStyle().set("margin-top", "10px");
		menu.getStyle().set("padding-left", "40px");
		menu.getStyle().set("padding-right", "40px");
		step4.add(menu);
		step4.getStyle().set("background-color","rgb(251,253,255)");
		step4.setPadding(false);
		step4.setMargin(false);
	}

	private void initBackButton(Button back) {
		back.addClickListener(event -> {
			ConfirmDialog.create()
			.withCaption("Confirmation")
			.withMessage("Tous les votes déjà envoyés pour cette question seront supprimés!")
			.withOkButton(() -> {
				sessionService.removeActiveQuestion(sessionService.getActiveSession());
				voteService.removeVotesChildFromQuestion(activeSession.getActiveQuestion());
				voteService.removeVotesFromQuestion(activeSession.getActiveQuestion());
				Broadcaster.broadcast("CRUD_QUESTION");
				initView();
			}, ButtonOption.focus(), ButtonOption.caption("OUI"))
			.withCancelButton(ButtonOption.caption("NON")).open();
		});
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
				if(!activeSession.getActiveQuestion().getMultiChoice()) {
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

	private void addHeader(Question quest) {
		header = new VerticalLayout();
		Label sess = new Label(activeSession.getName());
		sess.getStyle().set("font-size", "35px");
		sess.getStyle().set("padding", "0px");
		sess.getStyle().set("margin", "0px");
		Label question = new Label(quest.getIntitule()); 
		question.getStyle().set("font-size", "25px");
		question.getStyle().set("padding", "0px");
		question.getStyle().set("margin", "0px");
		header.add(sess,question);
		header.setAlignItems(Alignment.CENTER);
		header.setPadding(false);
		header.setMargin(false);
		header.getStyle().set("margin-bottom","15px");
		add(header);
	}

	private VerticalLayout initProgressBar(Question quest, Session session, Button back) {
		VerticalLayout vl = new VerticalLayout();
		HorizontalLayout hl = new HorizontalLayout();
		hl.setPadding(false);	
		hl.setWidthFull();
		hl.setJustifyContentMode(JustifyContentMode.CENTER);
		hl.getStyle().set("margin-top", "10px");
		Button result = new Button("Afficher les résultats");
		result.addClickListener(event ->{
			Broadcaster.broadcast("SHOW_RESULTS");
			result.setEnabled(false);
			questionService.createResultats(quest);
			UI.getCurrent().navigate(AdminResultsView.class);
		});
		result.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		hl.add(back, result);
		vl.getStyle().set("padding-left","20px");
		vl.getStyle().set("padding-right","20px");
		waitingUsersVotes = new ProgressBar();
		waitingUsersVotes.setMaxWidth("350px");
		double value = (double) quest.getVotes().size()/session.getJures().size();
		waitingUsersVotes.setValue(value);
		progressBarSubLabel = new Div();
		progressBarSubLabel.getStyle().set("font-size", "var(--lumo-font-size-xs)");
		progressBarSubLabel.setText("Votes : "+quest.getVotes().size()+"/"+session.getJuresNotArchived().size());
		vl.add(progressBarSubLabel, waitingUsersVotes, hl);
		vl.setSpacing(false);
		return vl;
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = attachEvent.getUI();
		broadcasterRegistration = Broadcaster.register(newMessage -> {
			if(newMessage.equals("ACTIVE_SESSION")) {
				ui.access(() -> initView());
			}else if(newMessage.equals("VOTE_SENDED")) {
				ui.access(() -> {
					double value = (double) waitingUsersVotes.getValue()*activeSession.getJures().size();
					waitingUsersVotes.setValue((value+1)/activeSession.getJures().size());
					progressBarSubLabel.setText("Votes : "+((int)value+1)+"/"+activeSession.getJures().size());
				});
			}else if(newMessage.equals("RESULTS_SHOWED")) {
				ui.access(() -> UI.getCurrent().navigate(AdminResultsView.class));
			}
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		broadcasterRegistration.remove();
		broadcasterRegistration = null;
	}*/

	private UserService userService;
	private SessionService sessionService;
	private VoteService voteService;
	private QuestionService questionService;
	private SecurityUtils tools;

	private Session activeSession;
	private User authenticatedUser;

	private Registration broadcasterRegistration;

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = attachEvent.getUI();
		broadcasterRegistration = Broadcaster.register(newMessage -> {
			if(newMessage.startsWith("ADMIN_EDIT_QUESTION") && !newMessage.equals("ADMIN_EDIT_QUESTION"+authenticatedUser.getId())) {
				if(activeSession != null && activeSession.getActiveQuestion()== null || activeSession != null && activeSession.getActiveQuestion() != null
						&& activeSession.getActiveQuestion().getDateVotes() != null) {
					ui.access(() -> {
						loadData();
						loadComponents();
					});
				}
			}else if((newMessage.startsWith(BroadcastMessageType.CRUD_QUESTION.getLabel()) &&
					!newMessage.equals(BroadcastMessageType.CRUD_QUESTION.getLabel()+authenticatedUser.getId())) || 
					(newMessage.startsWith(BroadcastMessageType.SEND_VOTE.getLabel()) && !newMessage.equals(BroadcastMessageType.SEND_VOTE.getLabel()+authenticatedUser.getId()))) {
				ui.access(() -> {
					loadData();
					loadComponents();
				});				
			}else if(newMessage.startsWith(BroadcastMessageType.SHOW_RESULTS.getLabel()) && !newMessage.equals(BroadcastMessageType.SHOW_RESULTS.getLabel()+authenticatedUser.getId())) {
				ui.access(() -> {
					loadData();
					loadComponents();
				});	
			}
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		broadcasterRegistration.remove();
		broadcasterRegistration = null;
	}


	public AdminVotesView(UserService userService, SessionService sessionService, QuestionService questionService, VoteService voteService, SecurityUtils tools) {
		this.userService = userService;
		this.sessionService = sessionService;
		this.voteService = voteService;
		this.questionService = questionService;
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
				// Si le jure a rejoint la session
				if(jure.getHasJoined()) {
					// Si il y a une question active
					if(activeSession.getActiveQuestion()!= null) {
						// Si le jure a deja vote
						if(jure.hasVoted(activeSession.getActiveQuestion())) {
							// Si les resultats ont ete affichés
							if(activeSession.getActiveQuestion().getDateVotes() != null) {
								printShowResults();
								// Si les resultats n'ont pas ete affichés
							}else {
								// J'affiche une page d'attente avec résumé des votes 
								printWaitResults();
							}
							// Si il n'a pas encore envoyé son vote
						}else {
							// J'affiche le questionnaire
							printShowQuestion();
						}
						// Si il n'y a pas de question active
					}else {
						printPickQuestion();
					}
				}else{// Je demande son pseudo
					printPickPseudo();

				}
				// Si le jure n'est pas dans cette session
			}else {
				//Si les resultats ont été affichés
				if(activeSession.getActiveQuestion().getDateVotes() != null) {
					// J'affiche le choix des questions
					printPickQuestion();
					// Si les resultats n'ont pas ete affichés
				}else {
					// J'affiche une page d'attente avec résumé des votes 
					printWaitResults();
				}	
			}
			//Si pas de session active
		}else {
			// j'affiche la page pour activer les sessions

		}
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
		Button back = new Button("Retour");
		initBackButton(back);
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.add(back, sendVote);
		buttons.setWidthFull();
		buttons.setJustifyContentMode(JustifyContentMode.CENTER);
		add(buttons);
		sendVoteChecker(questionsModule, quest, sendVote);
		setMargin(false);
		setPadding(false);
		getStyle().set("padding-bottom","10px");

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
			Broadcaster.broadcast("SEND_VOTE"+authenticatedUser.getId());
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
		add(new H3(activeSession.getActiveQuestion().getIntitule()));
		Button back = new Button("Retour");
		initBackButton(back);
		VerticalLayout menu = initProgressBar(activeSession.getActiveQuestion(), activeSession, back);
		menu.setPadding(false);
		menu.getStyle().set("padding-top", "10px");
		menu.setAlignItems(Alignment.CENTER);
		menu.setSpacing(false);
		UserVoteDetails uvd = new UserVoteDetails(activeSession, activeSession.getActiveQuestion(), voteService, userService, questionService);
		uvd.setMaxWidth("550px");
		Scroller scroll = new Scroller(uvd);
		Details details = new Details("Détail des votes", scroll);
		details.getStyle().set("margin-top", "0px");
		details.setMaxWidth("550px");
		details.setSizeFull();
		details.setOpened(false);
		menu.add(details);
		menu.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		menu.setWidthFull();
		menu.getStyle().set("background-color","White");
		menu.getStyle().set("margin-top", "10px");
		menu.getStyle().set("padding-left", "40px");
		menu.getStyle().set("padding-right", "40px");
		menu.getStyle().set("margin-left", "10px");
		menu.getStyle().set("margin-right", "10px");
		add(menu);
	}

	private VerticalLayout initProgressBar(Question quest, Session session, Button back) {
		VerticalLayout vl = new VerticalLayout();
		HorizontalLayout hl = new HorizontalLayout();
		hl.setPadding(false);	
		hl.setWidthFull();
		hl.setJustifyContentMode(JustifyContentMode.CENTER);
		hl.getStyle().set("margin-top", "10px");
		Button result = new Button("Afficher les résultats");
		result.addClickListener(event ->{
			questionService.createResultats(quest);
			Broadcaster.broadcast("SHOW_RESULTS"+authenticatedUser.getId());
			loadData();
			loadComponents();
		});
		result.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		hl.add(back, result);
		vl.getStyle().set("padding-left","20px");
		vl.getStyle().set("padding-right","20px");
		ProgressBar waitingUsersVotes = new ProgressBar();
		waitingUsersVotes.setMaxWidth("350px");
		double value = (double) quest.getVotes().size()/session.getJures().size();
		waitingUsersVotes.setValue(value);
		Div progressBarSubLabel = new Div();
		progressBarSubLabel.getStyle().set("font-size", "var(--lumo-font-size-xs)");
		progressBarSubLabel.setText("Votes : "+quest.getVotes().size()+"/"+session.getJuresNotArchived().size());
		vl.add(progressBarSubLabel, waitingUsersVotes, hl);
		vl.setSpacing(false);
		return vl;
	}

	private void printPickQuestion() {
		removeAll();
		setJustifyContentMode(JustifyContentMode.START);
		add(new H2(activeSession.getName()));
		Label hint = new Label("Sélectionnez la question que vous voulez poser ou créez-en une nouvelle.");	
		Select<Question> selectQuestion = createQuestionSelector();
		Button ok = initPickQuestionBtn(selectQuestion);
		HorizontalLayout pickQuest = new HorizontalLayout(selectQuestion, ok);
		VerticalLayout pickLayout = new VerticalLayout(hint, pickQuest);
		pickLayout.setAlignItems(FlexComponent.Alignment.CENTER);
		pickLayout.getStyle().set("margin-top", "50px");
		pickLayout.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		pickLayout.setMaxWidth("600px");
		pickLayout.getStyle().set("background-color","White");
		add(pickLayout);
		setSpacing(true);
		setSizeFull();		
	}


	private Select<Question> createQuestionSelector() {
		Select<Question> selectQuestion = new Select<>();
		List<Question> questions = new ArrayList<>();
		for(Question quest : activeSession.getUnansweredQuestions()) {
			questions.add(quest);
		}
		Collections.sort(questions, new Comparator<Question>() {
			@Override
			public int compare(Question quest1, Question quest2) {
				return quest1.getIntitule().compareTo(quest2.getIntitule());
			}
		});
		selectQuestion.setItems(questions);
		selectQuestion.setEmptySelectionAllowed(true);
		selectQuestion.setItemLabelGenerator(item -> {
			if(item!=null)
				return item.getIntitule();
			else
				return "Nouvelle question";
		});
		selectQuestion.setEmptySelectionCaption("Nouvelle question");
		return selectQuestion;
	}

	private Button initPickQuestionBtn(Select<Question> selectQuestion) {
		Button ok = new Button("Ok");
		ok.addClickListener(event -> {
			VerticalLayout showDetailsQuest = new VerticalLayout();
			showDetailsQuest = new VerticalLayout();
			showDetailsQuest.setAlignItems(FlexComponent.Alignment.CENTER);
			showDetailsQuest.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
			showDetailsQuest.setMaxWidth("900px");
			showDetailsQuest.getStyle().set("background-color","White");
			removeAll();
			DetailsQuestionEditable detailsQuest;
			Button next = new Button("Lancer le vote", new Icon(VaadinIcon.ARROW_RIGHT));
			if(selectQuestion.getValue() != null) {
				detailsQuest = new DetailsQuestionEditable(selectQuestion.getValue(), questionService, sessionService, authenticatedUser);
			}else {
				detailsQuest = new DetailsQuestionEditable(questionService, sessionService, next, activeSession);
			}
			showDetailsQuest.add(detailsQuest);
			initNextButton(next, detailsQuest);
			removeAll();
			HorizontalLayout hl = new HorizontalLayout();
			Button back = new Button("Retour", new Icon(VaadinIcon.ARROW_LEFT));
			back.addClickListener(eventBack -> {
				loadData();
				printPickQuestion();
			});
			next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			next.setIconAfterText(true);
			hl.add(back, next);
			hl.setJustifyContentMode(JustifyContentMode.BETWEEN);
			hl.getStyle().set("margin-top", "10px");
			hl.setWidthFull();
			hl.setMaxWidth("900px");
			add(new H2(activeSession.getName()));
			add(hl, showDetailsQuest);
		});
		return ok;
	}

	private void initNextButton(Button next, DetailsQuestionEditable detailsQuest) {
		next.addClickListener(event -> {
			if(detailsQuest.isNewQuestion()) {
				detailsQuest.saveSession();
			}
			questionService.activeQuestion(activeSession, detailsQuest.getQuestionPicked());
			Broadcaster.broadcast("CRUD_QUESTION"+authenticatedUser.getId());
			loadData();
			loadComponents();
		});
	}

	private void initBackButton(Button back) {
		back.addClickListener(event -> {
			ConfirmDialog.create()
			.withCaption("Confirmation")
			.withMessage("Tous les votes déjà envoyés pour cette question seront supprimés!")
			.withOkButton(() -> {
				sessionService.removeActiveQuestion(sessionService.getActiveSession());
				voteService.removeVotesChildFromQuestion(activeSession.getActiveQuestion());
				voteService.removeVotesFromQuestion(activeSession.getActiveQuestion());
				Broadcaster.broadcast("CRUD_QUESTION"+authenticatedUser.getId());
				loadData();
				loadComponents();
			}, ButtonOption.focus(), ButtonOption.caption("OUI"))
			.withCancelButton(ButtonOption.caption("NON")).open();
		});
	}

	private void printShowResults() {
		removeAll();
		ResultsVotes rv = new ResultsVotes(activeSession.getActiveQuestion(), false);
		rv.getRedirection().setText("Question suivante");
		rv.getRedirection().addClickListener(event -> {
			printPickQuestion();
		});
		add(rv);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if(!(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).isActive())) {
			beforeEnterEvent.forwardTo("registration_confirm/"+tools.getAuthenticatedUser().getUsername());		
		}

	}

}
