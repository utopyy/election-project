package com.ipamc.election.views;

import com.ipamc.election.services.UserService;
import com.ipamc.election.views.components.NewPasswordForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route(value = "reset_password")
public class ResetPasswordView extends VerticalLayout implements BeforeEnterObserver {

	private final UserService tools;
	private String token;
	private NewPasswordForm form;
	 
    public ResetPasswordView(UserService tools) {
    	this.tools = tools;
    	form = new NewPasswordForm(tools);
    	setHorizontalComponentAlignment(Alignment.CENTER, form);
	    add(form);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("text-align", "center");
    }
    
    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {   
    	token = event.getLocation().getQueryParameters().getParameters().get("token").get(0);
    	form.setToken(token);
    	if(tools.getByResetPasswordToken(token) == null) {
    		event.forwardTo("login");
    	}
    }

}

