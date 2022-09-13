package com.ipamc.election.views.components;

import java.util.Map;

import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Vote;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.progressbar.ProgressBar;

public class ResultsVotes extends VerticalLayout {

	private Question question;
	private Button redirection;

	public ResultsVotes(Question question, Boolean fullScreenOption) {
		this.question = question;
		initShowResults(fullScreenOption);
	}
	
	public ResultsVotes(Question question, Boolean fullScreenOption, Boolean isOldVote) {
		this.question = question;
		initShowResults(fullScreenOption);
		redirection.setVisible(false);
	}

	private void initShowResults(Boolean fullScreenOption) {
		//config mainLayout section
		setSizeFull();
		setPadding(false);
		setSpacing(false);

		// top-header section
		if(fullScreenOption) {
			Button fullScreen = createFullScreenBtn("blue");
			HorizontalLayout buttonBar = new HorizontalLayout();
			buttonBar.setWidthFull();
			buttonBar.add(fullScreen);
			buttonBar.setJustifyContentMode(JustifyContentMode.END);
			buttonBar.setPadding(false);
			add(buttonBar);
		}

		// title-header section
		HorizontalLayout questionTitle = new HorizontalLayout();
		questionTitle.setWidthFull();
		questionTitle.setJustifyContentMode(JustifyContentMode.CENTER);
		questionTitle.setPadding(false);
		H2 title = new H2(question.getIntitule());
		title.getStyle().set("margin-top","0px");
		title.getStyle().set("color","rgb(0,106,245)");
		questionTitle.add(title);
		add(questionTitle);

		// body-results section
		VerticalLayout bodyResults = new VerticalLayout();
		bodyResults.setWidthFull();
		createResultsSection(bodyResults);
		add(bodyResults);

		// body-detailsVotes section
		HorizontalLayout voteJuryDetails = new HorizontalLayout();
		voteJuryDetails.setPadding(false);
		voteJuryDetails.setWidthFull();
		voteJuryDetails.setJustifyContentMode(JustifyContentMode.CENTER);
		voteJuryDetails.add(createVoteJuryDetails());
		add(voteJuryDetails);

		// footer section
		HorizontalLayout hl = new HorizontalLayout();
		hl.getStyle().set("margin-top", "25px");
		hl.setSpacing(false);
		hl.setJustifyContentMode(JustifyContentMode.CENTER);
		hl.setSizeFull();
		redirection = new Button("");
		redirection.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		hl.add(redirection);
		add(hl);

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
	
	private void createResultsSection(VerticalLayout bodyResults) {
		if(question.containsNotes() && question.containsPropositions()) {
			FormLayout formLayout = new FormLayout();		
			VerticalLayout note = new VerticalLayout();
			note.add(createNoteResults());		
			note.setAlignItems(Alignment.CENTER);
			note.setJustifyContentMode(JustifyContentMode.CENTER);
			note.getStyle().set("align-self","flex-start");
			note.getStyle().set("padding-top", "30px");
			note.getStyle().set("padding-bottom", "30px");
			VerticalLayout prop = new VerticalLayout();
			prop.add(createTopPropositions());
			prop.setAlignItems(Alignment.CENTER);
			prop.getStyle().set("align-self","flex-start");
			formLayout.add(note, prop);
			formLayout.setResponsiveSteps(
					new ResponsiveStep("0",1),
					new ResponsiveStep("500px",2)
					);
			formLayout.setWidthFull();
			formLayout.setMaxWidth("620px");
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
		int amountNotes = question.amountNotes();
		if(amountNotes != -1) {
			int maxValueNote = question.getMaxValueNote();
			H1 scorer = new H1(amountNotes+"/"+maxValueNote);
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
		VerticalLayout emptyLay1= new VerticalLayout();
		emptyLay1.setPadding(false);
		emptyLay1.setMinHeight("1px");
		emptyLay1.setMinWidth("1px");
		emptyLay1.setMaxWidth("1px");
		votesJury.add(emptyLay1);
		for(Vote vote : question.getVotes()) {
			VoteJuryDetails voteDet = new VoteJuryDetails(vote);
			voteDet.getStyle().set("box-shadow", " rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
			voteDet.setMaxHeight("220px");
			voteDet.setMaxWidth("190px");
			voteDet.setMinWidth("190px");
			votesJury.add(voteDet);
			cpt++;
		}
		VerticalLayout emptyLay= new VerticalLayout();
		emptyLay.setPadding(false);
		emptyLay.setMinHeight("1px");
		emptyLay.setMinWidth("1px");
		emptyLay.setMaxWidth("1px");
		votesJury.add(emptyLay);
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
	
	public Button getRedirection() {
		return redirection;
	}



}
