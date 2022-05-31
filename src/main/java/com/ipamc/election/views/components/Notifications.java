package com.ipamc.election.views.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.Route;

@Route("notification-link")
public class Notifications extends Div {

  public Notifications() {
    
  }

  public Notification show(String texte, String url, String urlName) {

    Notification notification = new Notification();

    Div text = new Div(
      new Text(texte),
      new Anchor(url,urlName)
    );

    Button closeButton = new Button(new Icon("lumo", "cross"));
    closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
    closeButton.getElement().setAttribute("aria-label", "Close");
    closeButton.addClickListener(event -> {
      notification.close();
    });

    HorizontalLayout layout = new HorizontalLayout(text, closeButton);
    layout.setAlignItems(Alignment.CENTER);

    notification.add(layout);
    notification.open();

    notification.setPosition(Notification.Position.MIDDLE);

    return notification;
  }

}

