package com.ipamc.election.views;

import com.ipamc.election.data.entity.Broadcaster;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.ipamc.election.views.components.CategoriesJury;
import com.ipamc.election.views.components.DetailsQuestionEditable;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.model.Responsive;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Route(value = "salon" , layout = MainLayout.class)
@PageTitle("Salon de votes")


public class AdminVotesView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private QuestionService questionService;
	private SecurityUtils tools;
	private Select<Question> selectQuestion;
	private Session activeSession;
	private Question activeQuestion;
	private SessionService sessionService;
	private Button pickQuest; 
	private DetailsQuestionEditable detailsQuest;
	private VerticalLayout step1;
	private VerticalLayout step2;
	Registration broadcasterRegistration;


	public AdminVotesView(UserService userService, SecurityUtils tools, SessionService sessionService, QuestionService questionService) {
		this.userService = userService;
		this.sessionService = sessionService;
		this.questionService = questionService;
		this.tools = tools;
		setAlignItems(Alignment.CENTER);
		initView();
		getStyle().set("background-color","rgb(251,253,255)");
	}

	private void initView() {
		removeAll();
		if(sessionService.getActiveSession() != null) {
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
			/**
            Button openVotes = new Button("Lancer la phase de votes");
            openVotes.addClickListener(event -> {
            		Broadcaster.broadcast("ENABLE_VOTE");
            });
            add(openVotes);*/
			setSpacing(false);
			setSizeFull();
		}else {
			// AFFICHER MESSAGE : PAS DE SESSION ACTIVE, + link pour en activer une?
		}
	}

	private void initSelectQuestions() {
		selectQuestion = new Select<>();
		List<Question> questions = new ArrayList<>();
		for(Question quest : activeSession.getQuestions()) {
			questions.add(quest);
			if(quest.getIsActive()) {
				activeQuestion = quest;
			}
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
		//setupQuestion();
	}

	private void setupQuestion() {
		selectQuestion.addValueChangeListener(event -> {
			if(selectQuestion.getValue() != null) {
				activeQuestion = selectQuestion.getValue();
			}
			questionService.activeQuestion(activeSession, selectQuestion.getValue()); 
			initView();
			detailsQuest = new DetailsQuestionEditable(activeQuestion, questionService, sessionService);
			add(detailsQuest);
		});
	}

	private void initPickQuestionBtn(Button ok) {
		ok.addClickListener(event -> {
			step2.removeAll();
			if(selectQuestion.getValue() != null) {
				activeQuestion = selectQuestion.getValue();
				detailsQuest = new DetailsQuestionEditable(activeQuestion, questionService, sessionService);
			}else {
				detailsQuest = new DetailsQuestionEditable(questionService, sessionService);
			}
			step2.add(detailsQuest);
			remove(step1);
			HorizontalLayout hl = new HorizontalLayout();
			Button back = new Button("Retour", new Icon(VaadinIcon.ARROW_LEFT));
			initBackButton(back, hl);
			Button next = new Button("Lancer le vote", new Icon(VaadinIcon.ARROW_RIGHT));
			initNextButton(next);
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
			initView();
		});
	}
	
	private void initNextButton(Button btn) {
		btn.addClickListener(event -> {
			questionService.activeQuestion(activeSession, activeQuestion);
		});
	}
	
	private void initLaunchVoteBtn(Button btn) {
		btn.addClickListener(event -> {
			Broadcaster.broadcast("ENABLE_VOTE");
		});
	}


	private void initStep2() {
		step2 = new VerticalLayout();
		step2.setAlignItems(FlexComponent.Alignment.CENTER);
		step2.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		step2.setMaxWidth("900px");
		step2.getStyle().set("background-color","White");
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = attachEvent.getUI();
		broadcasterRegistration = Broadcaster.register(newMessage -> {
			if(newMessage.equals("ACTIVE_SESSION")) {
				ui.access(() -> initView());
			}
		});
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if(!(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).isActive())) {
			beforeEnterEvent.forwardTo("registration_confirm/"+tools.getAuthenticatedUser().getUsername());		
		}

	}

}
