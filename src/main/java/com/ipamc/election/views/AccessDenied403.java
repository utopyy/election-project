package com.ipamc.election.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.History;
import com.vaadin.flow.router.Route;

@Route(value = "403_access_denied")
public class AccessDenied403 extends VerticalLayout {

	
    public AccessDenied403() {
        setSpacing(false);

        
        getStyle().set("background-image","url(images/403.png)");
        getStyle().set("background-repeat", "no-repeat");
        getStyle().set("background-size", "100% 100%");

        H2 h = new H2("Accès interdit!");
        
        h.getStyle().set("color", "white");
        h.getStyle().set("margin-top","250px");
        Paragraph p = new Paragraph("Ton compte ne te permet pas d'accéder à cette page...");
        p.getStyle().set("color", "white");
        Button b = new Button("Essayer de retrouver mon chemin");
        b.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        b.addClickListener(e -> {
        	UI.getCurrent().navigate("login");
		});
        add(h, p, new Hr(), b);
        
        

        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
}
