package com.ipamc.election.views;

import java.util.List;
import java.util.Map;

import com.ipamc.election.security.services.UserDetailsServiceImpl;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
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

        add(new H2("Activation du compte"));
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
			add(new H3("Féliciations!"));
			add(new Text("Ton compte est activé"));
			add(new RouterLink("Login", LoginView.class));
		} catch (Exception e) {
			add(new Text("Lien invalide."));
		}
    }

}
