package com.ipamc.election.views;

import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.UserService;
import com.ipamc.election.views.components.ResetPasswordForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Mot de passe oublié")
@Route("forgot_password")

public class ForgetPasswordView extends VerticalLayout implements BeforeEnterObserver {

	private UserService userService;
	private SecurityUtils tools;
	
    public ForgetPasswordView(UserService userService, SecurityUtils tools) {
    	this.tools = tools;
    	this.userService = userService;
    	ResetPasswordForm form = new ResetPasswordForm(userService);
    	setHorizontalComponentAlignment(Alignment.CENTER, form);
	    add(form);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("text-align", "center");
    }
    
	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if(tools.isUserLoggedIn()) {
			beforeEnterEvent.forwardTo(ConfirmLoginRedirect.class);	
		}
	}

}
