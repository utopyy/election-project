package com.ipamc.election.views;

import java.util.Optional;

import org.vaadin.addon.ewopener.EnhancedBrowserWindowOpener;

import com.ipamc.election.data.entity.ResultatsJury;
import com.ipamc.election.data.entity.Vote;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.ResultatsJuryService;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
@JsModule("./themes/myapp/full-screen.js")
@PageTitle("Résultats")

public class AdminResultsView extends VerticalLayout implements BeforeEnterObserver {
	
	private UserService userService;
	private ResultatsJuryService resultsService;
	private SecurityUtils tools;

	private ResultatsJury results;
	
	private VerticalLayout waitingResults;
	private VerticalLayout showResults;

    public AdminResultsView(UserService userService, ResultatsJuryService resultsService, SecurityUtils tools) {
 
    	this.userService = userService;
    	this.resultsService = resultsService;
    	this.tools = tools;
    	initWaitingResults();
    	initView();
    	initShowResults();
    }
    
    private void initView() {
    	setSizeFull();
    	setPadding(false);
    	results = resultsService.getLastResults();
    	if(results!=null) {
    		add(waitingResults);
    	}else {
    		add(new Label("No votes"));
    		// show UI "No votes are available"
    	}
    }
    
    private void initShowResults() {
    	showResults = new VerticalLayout();
    	showResults.setSizeFull();
    	Button clearVotes = new Button("Ne plus afficher");
    	clearVotes.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    	clearVotes.addClickListener(event -> {
    		remove(showResults);
    		add(waitingResults);
    	});
    	Button fullScreen = createFullScreenBtn();
    	HorizontalLayout buttonBar = new HorizontalLayout();
    	buttonBar.setWidthFull();
    	buttonBar.add(fullScreen);
    	buttonBar.setJustifyContentMode(JustifyContentMode.END);
    	buttonBar.setPadding(false);
    	showResults.add(buttonBar);
    	showResults.add(clearVotes);
    	showResults.setPadding(false);
    	showResults.setJustifyContentMode(JustifyContentMode.START);
    }
    
    private void initWaitingResults() {
    	
    	waitingResults = new VerticalLayout();
    	
    	Button fullScreen = createFullScreenBtn();
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
    	vl.getStyle().set("background-color", "rgba(255, 255, 255, 0.85)");
    	vl.getStyle().set("border-radius", "10px");
    	vl.setJustifyContentMode(JustifyContentMode.CENTER) ;
    	vl.setAlignItems(Alignment.CENTER);
    	
    	
    	Button showLastVotes = new Button("Voir les derniers votes");
		showLastVotes.addClickListener(event -> {
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

    private Button createFullScreenBtn() {
    	Page page = UI.getCurrent().getPage();
    	Icon fs = new Icon(VaadinIcon.EXPAND_FULL);
    	fs.setColor("rgb(255,255,255)");
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


