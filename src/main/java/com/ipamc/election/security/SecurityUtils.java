package com.ipamc.election.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.HandlerHelper.RequestType;
import com.vaadin.flow.shared.ApplicationConstants;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.stream.Stream;

@Component
public final class SecurityUtils {

    private SecurityUtils() {
        // Util methods only
    }

    static boolean isFrameworkInternalRequest(HttpServletRequest request) { 


        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
            && Stream.of(RequestType.values())
            .anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    public static boolean isUserLoggedIn() { 


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
            && !(authentication instanceof AnonymousAuthenticationToken)
            && authentication.isAuthenticated();
    }
    
    public UserDetails getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        }
        // Anonymous or no authentication.
        return null;
    }
    
    
    public void logout() {
	    UI.getCurrent().getPage().setLocation("/login");
	    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
	    logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }
}