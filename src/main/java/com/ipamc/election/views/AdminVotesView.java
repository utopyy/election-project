package com.ipamc.election.views;

import com.ipamc.election.data.BroadcastMessageType;
import com.ipamc.election.data.entity.BroadcastMessage;
import com.ipamc.election.data.entity.Broadcaster;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.CategorieService;
import com.ipamc.election.services.PropositionService;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.SessionService;
import com.ipamc.election.services.UserService;
import com.ipamc.election.services.VoteCategorieService;
import com.ipamc.election.services.VoteService;
import com.ipamc.election.views.components.CategoriesJury;
import com.ipamc.election.views.components.DetailsQuestionEditable;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;


@Route(value = "salon" , layout = MainLayout.class)
@PageTitle("Salon de votes")


public class AdminVotesView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private QuestionService questionService;
	private SecurityUtils tools;
	private CategoriesJury categoriesJury;
	private Select<Question> selectQuestion;
	private Session activeSession;
	private Question activeQuestion;
	private SessionService sessionService;
	private Button pickQuest; 
	Registration broadcasterRegistration;

	
    public AdminVotesView(UserService userService, SecurityUtils tools, SessionService sessionService, QuestionService questionService) {
    	this.userService = userService;
    	this.sessionService = sessionService;
    	this.questionService = questionService;
    	this.tools = tools;
    	setAlignItems(Alignment.CENTER);
        initView();
    }
    
    private void initView() {
    	removeAll();
    	if(sessionService.getActiveSession() != null) {
    		activeSession = sessionService.getActiveSession();
    		add(new H2(activeSession.getName()));
    		initSelectQuestions();
            Button openVotes = new Button("Lancer la phase de votes");
            openVotes.addClickListener(event -> {
            		Broadcaster.broadcast("ENABLE_VOTE");
            });
            initDetailsQuestion();
            add(openVotes);
            setSizeFull();
            setSpacing(false);
            //setDefaultHorizontalComponentAlignment(Alignment.CENTER);
            //getStyle().set("text-align", "center");	
    	}else {
    		// AFFICHER MESSAGE : PAS DE SESSION ACTIVE, + link pour en activer une?
    	}
    }

    private void initSelectQuestions() {
    	selectQuestion = new Select<>();
		selectQuestion.setItemLabelGenerator(Question::getIntitule);
		List<Question> questions = new ArrayList<>();
		for(Question quest : activeSession.getQuestions()) {
			questions.add(quest);
			if(quest.getIsActive()) {
				activeQuestion = quest;
			}
		}
		selectQuestion.setItems(questions);
		if(activeQuestion != null) {
			selectQuestion.setValue(activeQuestion);
		}
		pickQuest = new Button("Ok");
		pickQuest.addClickListener(event ->{
			activeQuestion = selectQuestion.getValue();
			questionService.activeQuestion(activeSession, activeQuestion);
			initView();
		});
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.add(selectQuestion, pickQuest);
		add(buttons);
    }
    
    private void initDetailsQuestion() {
    	if(selectQuestion.getValue()!=null) {	
    		Details details = new Details("Details de la question", new DetailsQuestionEditable(activeQuestion));
    		add(details);
    	}
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
