package com.ipamc.election.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ipamc.election.data.entity.User;
import com.ipamc.election.services.UserService;
import com.ipamc.election.views.components.NewPasswordForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ErrorLevel;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

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

