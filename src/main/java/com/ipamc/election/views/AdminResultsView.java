package com.ipamc.election.views;

import com.ipamc.election.data.BroadcastMessageType;
import com.ipamc.election.data.entity.Broadcaster;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.QuestionService;
import com.ipamc.election.services.UserService;
import com.ipamc.election.views.components.ResultsVotes;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

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

	private Registration broadcasterRegistration;

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = attachEvent.getUI();
		broadcasterRegistration = Broadcaster.register(newMessage -> {
			if(newMessage.startsWith(BroadcastMessageType.SHOW_RESULTS.getLabel())) {
				ui.access(() -> {
					initShowResults();
				});
			}
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		broadcasterRegistration.remove();
		broadcasterRegistration = null;
	}


	public AdminResultsView(UserService userService, QuestionService questionService, SecurityUtils tools) {

		this.userService = userService;
		this.questionService = questionService;
		this.tools = tools;
		initWaitingResults();
		initView();
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
		refreshResults();
		removeAll();
		showResults = new ResultsVotes(question, true);
		add(showResults);
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
			initShowResults();
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

	
	private void refreshResults() {
		question = questionService.getLastResults();
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


