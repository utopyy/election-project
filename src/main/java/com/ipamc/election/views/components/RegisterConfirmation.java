package com.ipamc.election.views.components;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.UserService;
import com.ipamc.election.views.ConfirmLoginRedirect;
import com.ipamc.election.views.LoginView;
import com.ipamc.election.views.MainLayout;
import com.ipamc.election.views.UserVotesView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;

@Route(value = "registration_confirm", layout = MainLayout.class)

public class RegisterConfirmation extends VerticalLayout implements HasUrlParameter<String>{

	private H2 h2 = new H2();
	private UserService userService;
	private SecurityUtils tools;
    
	public RegisterConfirmation(UserService userService, SecurityUtils tools) {
    	this.userService = userService;
    	this.tools = tools;
    	
        setSpacing(false);

        add(h2);
        add(new Paragraph("Tu n'as pas reçu de mail? Clique ici pour recevoir un nouveau lien."));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

	@Override
	protected void onAttach(AttachEvent event){
		Location uriString = UI.getCurrent().getInternals().getActiveViewLocation();
		List<String> uriList = new ArrayList<>();
		uriList = uriString.getSegments();
		String uri = uriList.get(uriList.size()-1);
		//si le compte est actif ou que le compte n'a pas le meme nom que l'url encodé on le redirige vers la page dispatch (confirmloginredirect)
		if(tools.isUserLoggedIn()) {
			if(!tools.getAuthenticatedUser().getUsername().equals(uri)||(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).isActive())) {
				event.getUI().getCurrent().navigate(ConfirmLoginRedirect.class);
			}
			h2.setText("Bienvenue " + tools.getAuthenticatedUser().getUsername() +", vérifie tes mails pour activer ton compte!");
		}else if(userService.usernameExist(uri)) {
			h2.setText("Bienvenue " + uri +", vérifie tes mails pour activer ton compte!");
		}else {
			event.getUI().getCurrent().navigate(ConfirmLoginRedirect.class);
		}
	}

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		
	}

}

