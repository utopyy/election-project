package com.ipamc.election.views.components;


import java.util.ArrayList;
import java.util.List;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.ipamc.election.data.entity.Broadcaster;
import com.ipamc.election.data.entity.Question;
import com.ipamc.election.data.entity.Session;
import com.ipamc.election.services.SessionService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;

public class StartSession extends VerticalLayout {

	Select<Session> selectSession;
	Button setActive = new Button("Activer la session");
	Session activeSession;
	VerticalLayout details = new VerticalLayout();
	VerticalLayout infos = new VerticalLayout();
	Button archive;
	Button pause;
	
	public StartSession(Div sp, SessionService sessionService) {
		initArchive(sp, sessionService);
		initPause(sessionService);
		initSelectSession(sessionService);
		initSetActive(sessionService);
		infosBtn();
		HorizontalLayout hl = new HorizontalLayout(selectSession, setActive);
		hl.setAlignItems(Alignment.CENTER);
		add(hl, details);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        details.setJustifyContentMode(JustifyContentMode.CENTER);
        details.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	}
	
	private void initSelectSession(SessionService sessionService) {
		selectSession = new Select<>();
		selectSession.setItemLabelGenerator(Session::getName);
		setActive.setEnabled(false);
		activeSession = null;
		List<Session> sessions = new ArrayList<>();
		for(Session sess : sessionService.findSessionsNotArchived()) {
			sessions.add(sess);
			if(sess.getIsActive()) {
				activeSession = sess;
				setActive.setText("Session activée");
				addDetailsSess(sess);
				setActive.setEnabled(false);
				
			}
		}
		selectSession.setItems(sessions);
		selectSession.setValue(activeSession);
		selectSession.addValueChangeListener(event -> {
			if(selectSession.getValue()!=null) {
				if(!selectSession.getValue().equals(activeSession)) {
					setActive.setEnabled(true);
					setActive.setText("Activer la session");
				}else {
					setActive.setText("Session activée");
					setActive.setEnabled(false);
				}
			}else {
				setActive.setEnabled(false);
				setActive.setText("Activer la session");
			}
		});
	}
	
	private void initSetActive(SessionService sessionService) {
		setActive.addClickListener(event -> {
			sessionService.updateActiveSession(selectSession.getValue());
			activeSession = selectSession.getValue();
			setActive.setText("Session activée");
	        addDetailsSess(activeSession);
			setActive.setEnabled(false);
			Broadcaster.broadcast("ACTIVE_SESSION");
		});
	}
	
	private void addDetailsSess(Session sess) {
		details.removeAll();
		details.add(new Hr());
		details.add(new H4("Session en cours : "+sess.getName()));
		String jure;
		String question;
		if(sess.getJures().size()>1) {
			jure = "jurés";
		}else {
			jure = "juré";
		}
		if(sess.getQuestions().size()>1) {
			question = "questions";
		}else {
			question = "question";
		}
		details.add(new Label(Integer.toString(sess.getJures().size())+" "+jure));
		details.add(new Label(Integer.toString(sess.getQuestions().size())+" "+question));
		HorizontalLayout buttons = new HorizontalLayout(pause, archive);
		details.add(buttons);
		details.add(new Hr());
		details.add(infos);
	}
	
	private void initArchive(Div sp, SessionService sessionService) {
		archive = new Button("Archiver");
		archive.addClickListener(event -> {
			ConfirmDialog.create()
			.withCaption("Action irréversible")
			.withMessage("Voulez vous vraiment archiver: "+activeSession.getName()+ " ?")
			.withOkButton(() -> {
				sessionService.archive(activeSession);
				sp.removeAll();
				sp.add(createBadge(sessionService.getNumberOfSessions()));
				refreshSelect(sessionService);
				selectSession.setPlaceholder("Sessions");
				Notification notification = Notification.show("Session archivée!");
				Broadcaster.broadcast("ACTIVE_SESSION");
				notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				notification.setDuration(3000);
				notification.setPosition(Position.TOP_END);
			}, ButtonOption.focus(), ButtonOption.caption("OUI"))
			.withCancelButton(ButtonOption.caption("NON")).open();
		});
	}
	
	private void initPause(SessionService sessionService) {
		pause = new Button("Mettre en pause");
		pause.addClickListener(event -> {
			ConfirmDialog.create()
			.withCaption("Mettre en pause")
			.withMessage("Voulez vous vraiment mettre \""+activeSession.getName()+ "\" en pause ?")
			.withOkButton(() -> {
				sessionService.pauseSession(activeSession);
				refreshSelect(sessionService);
				selectSession.setPlaceholder("Sessions");
				Notification notification = Notification.show("Session mise en pause!");
				Broadcaster.broadcast("ACTIVE_SESSION");
				notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				notification.setDuration(3000);
				notification.setPosition(Position.TOP_END);
			}, ButtonOption.focus(), ButtonOption.caption("OUI"))
			.withCancelButton(ButtonOption.caption("NON")).open();
		});
	}
	
	public void refreshSelect(SessionService sessionService) {
		selectSession.removeAll();
		setActive.setEnabled(false);
		activeSession = null;
		List<Session> sessions = new ArrayList<>();
		for(Session sess : sessionService.findSessionsNotArchived()) {
			sessions.add(sess);
			if(sess.getIsActive()) {
				activeSession = sess;
				setActive.setText("Session activée");
				addDetailsSess(sess);
				setActive.setEnabled(false);
			}
		}
		if(activeSession == null) {
			details.removeAll();
			details.add(new Hr());
			details.add(new H4("Aucune session n'est en cours"));
			details.add(new Icon(VaadinIcon.FROWN_O));
		}
		selectSession.setItems(sessions);
		selectSession.setValue(activeSession);
		selectSession.addValueChangeListener(event -> {
			if(selectSession.getValue()!=null) {
				if(!selectSession.getValue().equals(activeSession)) {
					setActive.setEnabled(true);
					setActive.setText("Activer la session");
				}else {
					setActive.setText("Session activée");
					setActive.setEnabled(false);
				}
			}else {
				setActive.setEnabled(false);
				setActive.setText("Activer la session");
			}
		});
	}
	
	private Span createBadge(Long value) {
		Span badge = new Span(String.valueOf(value));
		badge.getElement().getThemeList().add("badge small contrast");
		badge.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
		return badge;
	}
	
	private void infosBtn() {
		HorizontalLayout archiveLayout = new HorizontalLayout();
		HorizontalLayout pauseLayout = new HorizontalLayout();
		Label bold1 = new Label("Archiver : "); 
		bold1.getStyle().set("padding-right", "2px");
		bold1.getStyle().set("font-weight", "600");
		bold1.getStyle().set("font-style", "italic");
		Label archive = new Label(" Permet de cloturer la session. Elle ne sera plus utilisable mais reste visible dans la partie \" Historique \".");
		archive.getStyle().set("font-style", "italic");
		archiveLayout.add(bold1, archive);
		Label bold2 = new Label("Mettre en pause : ");
		bold2.getStyle().set("font-style", "italic");
		bold2.getStyle().set("font-weight", "600");
		bold2.getStyle().set("padding-right", "2px");
		Label pause = new Label("  Permet de désactiver la session tout en la conservant si vous souhaitez la relancer plus tard.");
		pause.getStyle().set("font-style", "italic");
		pauseLayout.add(bold2, new Span(" "), pause);
		pauseLayout.setSpacing(false);
		archiveLayout.setSpacing(false);
		infos.add(pauseLayout, archiveLayout);
		infos.setSpacing(false);
	}

}
