package com.ipamc.election.views;

import com.ipamc.election.data.EnumRole;
import com.ipamc.election.data.entity.User;
import com.ipamc.election.security.SecurityUtils;
import com.ipamc.election.services.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    /**
     * A simple navigation item component, based on ListItem element.
     */
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames("menu-item-link");
            link.setRoute(view);

            Span text = new Span(menuTitle);
            text.addClassNames("menu-item-text");

            link.add(new LineAwesomeIcon(iconClass), text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }

        /**
         * Simple wrapper to create icons using LineAwesome iconset. See
         * https://icons8.com/line-awesome
         */
        @NpmPackage(value = "line-awesome", version = "1.3.0")
        public static class LineAwesomeIcon extends Span {
            public LineAwesomeIcon(String lineawesomeClassnames) {
                addClassNames("menu-item-icon");
                if (!lineawesomeClassnames.isEmpty()) {
                    addClassNames(lineawesomeClassnames);
                }
            }
        }

    }

    private H1 viewTitle;

    private AccessAnnotationChecker accessChecker;
    private SecurityUtils tools;
    private UserService userService;
    
    public MainLayout(AccessAnnotationChecker accessChecker, SecurityUtils tools, UserService userService) {
    	this.userService = userService;
    	this.tools = tools;
    	this.accessChecker = accessChecker;
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("view-title");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("view-header");
        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("Election");
        appName.addClassNames("app-name");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation(), createFooter());
        section.addClassNames("drawer-section");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("menu-item-container");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("navigation-list");
        nav.add(list);

        for (MenuItemInfo menuItem : createMenuItems()) {
                list.add(menuItem);
        }
        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("footer");

        UserDetails user = tools.getAuthenticatedUser();
        if (user != null) {

            Avatar avatar = new Avatar(user.getUsername()/*, user.getProfilePictureUrl()**/);
            avatar.addClassNames("me-xs");

            ContextMenu userMenu = new ContextMenu(avatar);
            userMenu.setOpenOnClick(true);
            userMenu.addItem("Se déconnecter", e -> {
                tools.logout();
            });

            Span name = new Span(user.getUsername());
            name.addClassNames("font-medium", "text-s", "text-secondary");

            layout.add(avatar, name);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }



    private MenuItemInfo[] createMenuItems() {
    	if(tools.getAuthenticatedUser() != null) {
	    	String role = tools.getAuthenticatedUser().getAuthorities().iterator().next().getAuthority();
	    	if(userService.getByUsername(tools.getAuthenticatedUser().getUsername()).isActive()) {
		    	if(role.equals(EnumRole.ROLE_USER.toString())) {
		    		return new MenuItemInfo[]{
		        			new MenuItemInfo("Votes", "la la-vote-yea", UserVotesView.class),
		        			new MenuItemInfo("Mon compte", "la la-file", ProfilView.class)
		    		};
		        }else if(role.equals(EnumRole.ROLE_ADMIN.toString()) || role.equals(EnumRole.ROLE_SUPER_ADMIN.toString())) {
		        	return new MenuItemInfo[]{        
		        			new MenuItemInfo("Salon de votes", "la la-vote-yea", AdminVotesView.class),
			                new MenuItemInfo("Gestion du salon", "la la-tools", AdminRoomSettingsView.class),
			                new MenuItemInfo("Résultats", "la la-columns", AdminResultsView.class),
			                new MenuItemInfo("Liste des utilisateurs", "la la-columns", AdminUsersView.class),
			                new MenuItemInfo("Historique", "la la-columns", AdminLogsView.class),
			                new MenuItemInfo("Mon compte", "la la-user-circle", ProfilView.class)
		        	};
		        }
	    	}
    	}
	    return new MenuItemInfo[]{        
	    		new MenuItemInfo("S'inscrire", "la la-user-cog", RegistrationView.class),
	    		new MenuItemInfo("Connexion", "la la-user", LoginView.class)
	    	};
    	}

 
}
