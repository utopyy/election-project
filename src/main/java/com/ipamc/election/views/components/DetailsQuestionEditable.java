package com.ipamc.election.views.components;

import com.ipamc.election.data.entity.Categorie;
import com.ipamc.election.data.entity.Proposition;
import com.ipamc.election.data.entity.Question;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;

public class DetailsQuestionEditable extends VerticalLayout {

	public DetailsQuestionEditable(Question quest) {
		initForm(quest);
	}
	
	private void initForm(Question quest) {
		TextField questionName = new TextField("Question");
		questionName.setValue(quest.getIntitule());
		questionName.setWidth("200px");
		Checkbox commentaire = new Checkbox("Activer");
		commentaire.setValue(quest.getIsActive());
		Checkbox comOblig = new Checkbox("Obligatoire");

		Label comText = new Label("Commentaire");
		comText.setWidth("100px");
		comText.getStyle().set("font-size", "13px");
		comText.getStyle().set("color", "rgb(68,137,227)");
		HorizontalLayout comHl = new HorizontalLayout(comText, commentaire, comOblig);
		comHl.setAlignItems(Alignment.BASELINE);
		Checkbox note = new Checkbox("Activer");
		Checkbox noteOblig = new Checkbox("Obligatoire");
		IntegerField noteValue = new IntegerField();
		noteValue.setPrefixComponent(new Label("Note / "));
		Label noteText = new Label("Note");
		noteText.setWidth("100px");
		noteText.getStyle().set("font-size", "13px");
		noteText.getStyle().set("color", "rgb(68,137,227)");
		HorizontalLayout noteHl = new HorizontalLayout(noteText, note, noteOblig, noteValue);
		noteHl.setAlignItems(Alignment.BASELINE);
		Checkbox propositions = new Checkbox("Activer");
		Checkbox propOblig = new Checkbox("Obligatoire");
		Checkbox qcm = new Checkbox("QCM");
		Button addProp = new Button(new Icon(VaadinIcon.PLUS_CIRCLE));
		addProp.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		Label propText = new Label("Propositions");
		propText.setWidth("100px");
		propText.getStyle().set("font-size", "13px");
		propText.getStyle().set("color", "rgb(68,137,227)");
		TextField newPropTf = new TextField();
		newPropTf.setPlaceholder("Ajouter une proposition");
		HorizontalLayout propLayout = new HorizontalLayout(newPropTf, addProp);
		propLayout.setSpacing(false);
		propLayout.getStyle().set("margin-left", "70px");
		HorizontalLayout propHl = new HorizontalLayout(propText, propositions, propOblig, qcm, propLayout);
		propHl.setAlignItems(Alignment.BASELINE);
		for(Categorie cat : quest.getCategories()) {
			if(cat.getLibelle().equals("Commentaire")) {
				commentaire.setValue(true);
				if(cat.getIsRequired().equals(true)) {
					comOblig.setValue(true);
				}
			}else {
				note.setValue(true);
				noteValue.setValue(cat.getValeur());
				if(cat.getIsRequired().equals(true)) {
					noteOblig.setValue(true);
				}
			}
		}
		HorizontalLayout propsBadges = new HorizontalLayout();
        propsBadges.getStyle().set("flex-wrap", "wrap");
        propsBadges.setAlignItems(Alignment.BASELINE);
        addProp.addClickListener(event -> {
        	Span propositionBadge = createPropositionBadge(new Proposition(newPropTf.getValue()));
        	newPropTf.setValue("");
        	propsBadges.add(propositionBadge);
        });
		add(questionName, new Hr(), comHl, new Hr(), noteHl, new Hr(), propHl, propsBadges);
		setSpacing(false);
		setPadding(false);
		setAlignItems(FlexComponent.Alignment.STRETCH);
		getStyle().set("width", "800px").set("max-width", "100%");
	}
	
    private Span createPropositionBadge(Proposition proposition) {
        Button clearButton = new Button(VaadinIcon.CLOSE_SMALL.create());
        clearButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE);
        clearButton.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
        // Accessible button name
        clearButton.getElement().setAttribute("aria-label", "Clear filter: " + proposition.getLibelle());
        // Tooltip
        clearButton.getElement().setAttribute("title", "Clear filter: " + proposition.getLibelle());

        Span badge = new Span(new Span(proposition.getLibelle()), clearButton);
        badge.getElement().getThemeList().add("badge contrast pill");

        // Add handler for removing the badge
        clearButton.addClickListener(event -> {
            badge.getElement().removeFromParent();
        });

        return badge;
    }
}
