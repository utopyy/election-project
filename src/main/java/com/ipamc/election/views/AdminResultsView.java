package com.ipamc.election.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.vaadin.addon.ewopener.EnhancedBrowserWindowOpener;

import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.data.entity.VoteCategorie;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.UserService;
import com.ipamc.election.views.components.VoteJuryDetails;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.Command;
import com.vaadin.ui.Link;

@Route(value = "results")
@CssImport("./themes/myapp/waiting-results.css")
@CssImport("./themes/myapp/circle.css")
@JsModule("./themes/myapp/full-screen.js")
@PageTitle("Résultats")

public class AdminResultsView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private QuestionService questionService;
	private SecurityUtils tools;

	private Question question;

	private VerticalLayout waitingResults;
	private VerticalLayout showResults;

	public AdminResultsView(UserService userService, QuestionService questionService, SecurityUtils tools) {

		this.userService = userService;
		this.questionService = questionService;
		this.tools = tools;
		initWaitingResults();
		initView();
		initShowResults();
	}

	private void initView() {
		setSizeFull();
		setPadding(false);
		refreshResults();
		if(question!=null) {
			add(waitingResults);
		}else {
			VerticalLayout noVotes = new VerticalLayout();
			noVotes.setAlignItems(Alignment.CENTER);
			noVotes.setJustifyContentMode(JustifyContentMode.CENTER);
			noVotes.setSizeFull();
			noVotes.add(new H2("Aucun vote n'a encore été réalisé..."));
			add(noVotes);
		}
	}

	private void initShowResults() {
		//config mainLayout section
		showResults = new VerticalLayout();
		showResults.setSizeFull();
		showResults.setPadding(false);
		showResults.setSpacing(false);
		showResults.setJustifyContentMode(JustifyContentMode.START);
		
		// top-header section
		Button fullScreen = createFullScreenBtn("blue");
		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setWidthFull();
		buttonBar.add(fullScreen);
		buttonBar.setJustifyContentMode(JustifyContentMode.END);
		buttonBar.setPadding(false);
		showResults.add(buttonBar);
		
		// title-header section
		HorizontalLayout questionTitle = new HorizontalLayout();
		questionTitle.setWidthFull();
		questionTitle.setJustifyContentMode(JustifyContentMode.CENTER);
		questionTitle.setPadding(false);
		H2 title = new H2(question.getIntitule());
		title.getStyle().set("margin-top","0px");
		questionTitle.add(title);
		showResults.add(questionTitle);

		// body-results section
		VerticalLayout bodyResults = new VerticalLayout();
		bodyResults.setWidthFull();
		createResultsSection(bodyResults);
		showResults.add(bodyResults);

		// body-detailsVotes section
		HorizontalLayout voteJuryDetails = new HorizontalLayout();
		voteJuryDetails.setPadding(false);
		voteJuryDetails.setWidthFull();
		voteJuryDetails.setJustifyContentMode(JustifyContentMode.CENTER);
		voteJuryDetails.add(createVoteJuryDetails());
		showResults.add(voteJuryDetails);

		// footer section
		HorizontalLayout hl = new HorizontalLayout();
		hl.setPadding(false);
		hl.setSpacing(false);
		hl.setJustifyContentMode(JustifyContentMode.CENTER);
		hl.setSizeFull();
		Button clearVotes = new Button("Ne plus afficher");
		clearVotes.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		clearVotes.addClickListener(event -> {
			remove(showResults);
			add(waitingResults);
		});
		hl.add(clearVotes);
		showResults.add(hl);

	}

	private void initWaitingResults() {

		waitingResults = new VerticalLayout();

		Button fullScreen = createFullScreenBtn("white");
		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.add(fullScreen);
		buttonBar.setWidthFull();
		buttonBar.setJustifyContentMode(JustifyContentMode.END);
		buttonBar.setPadding(false);

		ProgressBar progressBar = new ProgressBar();
		progressBar.setIndeterminate(true);
		Div progressBarLabel = new Div();
		progressBarLabel.setText("En attente d'un résultat...");
		VerticalLayout vl = new VerticalLayout();
		vl.setMaxWidth("400px");
		vl.add(progressBarLabel, progressBar);
		vl.setSpacing(false);
		vl.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		vl.getStyle().set("margin-left", "10px");
		vl.getStyle().set("margin-right", "10px");
		vl.getStyle().set("background-color", "rgba(255, 255, 255, 0.90)");
		vl.getStyle().set("border-radius", "10px");
		vl.setJustifyContentMode(JustifyContentMode.CENTER) ;
		vl.setAlignItems(Alignment.CENTER);


		Button showLastVotes = new Button("Voir les derniers votes");
		showLastVotes.addClickListener(event -> {
			refreshResults();
			remove(waitingResults);
			add(showResults);
		});;
		Button goBack = new Button(new Icon(VaadinIcon.ANGLE_LEFT));
		goBack.addClickListener(event -> {
			UI.getCurrent().navigate(AdminVotesView.class);
		});

		HorizontalLayout bottomBar = new HorizontalLayout();
		bottomBar.add(goBack,showLastVotes);
		bottomBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
		bottomBar.setSizeFull();
		bottomBar.setMaxWidth("400px");
		vl.add(new Hr(),bottomBar);

		VerticalLayout container = new VerticalLayout();
		container.setSizeFull();
		container.setSpacing(false);
		container.setPadding(false);
		container.add(vl);
		container.setJustifyContentMode(JustifyContentMode.CENTER);
		container.setAlignItems(Alignment.CENTER) ;
		container.getStyle().set("margin-bottom","80px");

		waitingResults.setClassName("waitingresults");
		waitingResults.setSizeFull();
		waitingResults.setPadding(false);
		waitingResults.add(buttonBar);
		waitingResults.add(container); 	
	}

	private Button createFullScreenBtn(String color) {
		String colorCode = "rgb(255,255,255)";
		if(color.equals("black")) {
			colorCode = "rgb(0,0,0)";
		}else if(color.equals("blue")) {
			colorCode = "rgb(0,106,245)";
		}
		Page page = UI.getCurrent().getPage();
		Icon fs = new Icon(VaadinIcon.EXPAND_FULL);
		fs.setColor(colorCode);
		Button fullScreen = new Button(fs, click -> {  
			page.executeJs("openFullscreen()");
		});
		fullScreen.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE);
		fullScreen.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
		fullScreen.getStyle().set("padding-top", "6px");
		fullScreen.getStyle().set("padding-right", "6px");
		return fullScreen;
	}

	private void refreshResults() {
		question = questionService.getLastResults();
	}

	private void createResultsSection(VerticalLayout bodyResults) {
		if(question.containsNotes() && question.containsPropositions()) {
			FormLayout formLayout = new FormLayout();		
			VerticalLayout note = new VerticalLayout();
			note.add(createNoteResults());		
			note.setAlignItems(Alignment.CENTER);
			note.setJustifyContentMode(JustifyContentMode.CENTER);
			note.setFlexGrow(1);
			note.getStyle().set("align-self","flex-start");
			note.getStyle().set("padding-top", "30px");
			note.getStyle().set("padding-bottom", "30px");
			VerticalLayout prop = new VerticalLayout();
			prop.add(createTopPropositions());
			prop.setAlignItems(Alignment.CENTER);
			prop.getStyle().set("align-self","flex-start");
			prop.setFlexGrow(1);
			formLayout.add(note, prop);
			formLayout.setResponsiveSteps(
					new ResponsiveStep("0",1),
					new ResponsiveStep("500px",2)
					);
			formLayout.setWidthFull();
			formLayout.setMaxWidth("800px");
			bodyResults.add(formLayout);
			bodyResults.setAlignSelf(Alignment.CENTER, formLayout);
		}else if(question.containsPropositions()) {
			HorizontalLayout hl = new HorizontalLayout();
			hl.getStyle().set("margin-top", "40px");
			hl.setWidthFull();
			hl.setJustifyContentMode(JustifyContentMode.CENTER);
			hl.add(createTopPropositions());
			bodyResults.add(hl);	
		}else if(question.containsNotes()) {
			HorizontalLayout hl = new HorizontalLayout();
			hl.getStyle().set("margin-top", "40px");
			hl.setWidthFull();
			hl.setJustifyContentMode(JustifyContentMode.CENTER);
			hl.add(createNoteResults());
			bodyResults.add(hl);
		}
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidthFull();
		hl.setJustifyContentMode(JustifyContentMode.CENTER);
		Hr hr = new Hr();
		hr.setWidthFull();
		hr.setMaxWidth("90%");
		hl.add(hr);
		bodyResults.add(hl);
	}

	private Scroller createTopPropositions() {
		Double totalPoints = 0.0;
		Map<Proposition, Integer> propsRank = question.propositionsRanked();
		for (Map.Entry<Proposition, Integer> entry : propsRank.entrySet()) {
			totalPoints+= entry.getValue();
		}
		VerticalLayout propositions = new VerticalLayout();
		for (Map.Entry<Proposition, Integer> entry : propsRank.entrySet()) {
			VerticalLayout vl = new VerticalLayout();
			vl.setSpacing(false);
			vl.setPadding(false);
			Label proposition = new Label(entry.getKey().getLibelle());
			Label scoreValue = new Label(Integer.toString(entry.getValue())+"/"+(int) Math.round(totalPoints));
			ProgressBar score = new ProgressBar();
			score.setValue((entry.getValue()/totalPoints));
			HorizontalLayout hl = new HorizontalLayout();
			hl.setWidthFull();
			hl.add(proposition, scoreValue);
			hl.setJustifyContentMode(JustifyContentMode.BETWEEN);
			vl.add(hl, score);
			vl.setSizeFull();
			propositions.add(vl);
		}
		Scroller scroll = new Scroller(propositions);
		scroll.setSizeFull();
		scroll.setMaxHeight("220px"); 
		scroll.setMaxWidth("400px"); 	
		scroll.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		scroll.getStyle().set("border-radius", "10px"); 
		return scroll;
	}

	private VerticalLayout createNoteResults() {
		VerticalLayout scoreLayout = new VerticalLayout();
		int sumNotes = question.getSumNotes();
		if(sumNotes != -1) {
			int maxValueNote = question.getMaxValueNote();
			H1 scorer = new H1(sumNotes+"/"+maxValueNote);
			scorer.setClassName("h1");
			scorer.getStyle().set("color","rgb(0,106,245)");
			scorer.getStyle().set("padding-bottom", "20px");
			scoreLayout.add(scorer);
		}else {
			H4 scorer = new H4("Pas de notes reçues");
			scorer.getStyle().set("padding-bottom", "15px");
			scoreLayout.add(scorer);
		}
		scoreLayout.getStyle().set("border-radius", "1000px"); 
		scoreLayout.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		scoreLayout.setAlignItems(Alignment.CENTER);
		scoreLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		scoreLayout.setMaxHeight("220px");
		scoreLayout.setMaxWidth("220px");
		scoreLayout.setSizeFull();
		return scoreLayout;
	}

	private Scroller createVoteJuryDetails() {		
		HorizontalLayout votesJury = new HorizontalLayout();
		votesJury.setSizeFull();
		int cpt = 0;
		for(Vote vote : question.getVotes()) {
			VoteJuryDetails voteDet = new VoteJuryDetails(vote);
			voteDet.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
			voteDet.setMaxHeight("200px");
			voteDet.setMaxWidth("170px");
			voteDet.setMinWidth("170px");
			votesJury.add(voteDet);
			cpt++;
		}
		votesJury.setJustifyContentMode(JustifyContentMode.BETWEEN);
		votesJury.setPadding(true);
		Scroller scroll = new Scroller(votesJury);
		scroll.setScrollDirection(ScrollDirection.HORIZONTAL);
		scroll.getStyle().set("margin-left", "10px");
		scroll.getStyle().set("margin-right", "10px");
		scroll.setSizeFull();
		if(cpt > 4) {
			scroll.setMaxWidth("1300px");
		}else {
			scroll.setMaxWidth("800px");
		}
		return scroll;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if(!(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).isActive())) {
			beforeEnterEvent.forwardTo("registration_confirm/"+tools.getAuthenticatedUser().getUsername());		
		}
		if(tools.getAuthenticatedUser().getAuthorities().iterator().next().toString().equals("ROLE_USER")) {
			beforeEnterEvent.forwardTo("jury");
		}
	}

}


