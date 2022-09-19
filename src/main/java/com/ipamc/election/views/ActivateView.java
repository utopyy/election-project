package com.ipamc.election.views;

import java.util.List;
import java.util.Map;

import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;


@Route(value = "activate")
public class ActivateView extends VerticalLayout implements BeforeEnterObserver {

	private final UserService tools;
	
    public ActivateView(UserService tools) {
    	this.tools = tools;
        setSpacing(false);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
    	try {
	    	Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();
	    	String code = params.get("code").get(0);
			tools.activate(code);
	        add(new H2("Félicitations"));
			add(new H3("Ton compte est activé !"));
			add(new RouterLink("Login", LoginView.class));
		} catch (Exception e) {
			add(new H2("Problème..."));
			add(new H3("Lien invalide."));
			add(new RouterLink("Retourner à l'accueil", LoginView.class));
		}
    }

}
