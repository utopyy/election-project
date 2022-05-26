package com.ipamc.election.data.entity;

import com.vaadin.flow.component.Component;

public class AuthorizedRoute {
	
	private String route;
	private String name;
	private Class<? extends Component> view;
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Class<? extends Component> getView() {
		return view;
	}
	public void setView(Class<? extends Component> view) {
		this.view = view;
	}
	public AuthorizedRoute(String route, String name, Class<? extends Component> view) {
		super();
		this.route = route;
		this.name = name;
		this.view = view;
	}
	
	
}
