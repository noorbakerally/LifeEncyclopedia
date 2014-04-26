package com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.events;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageRequest;

public class NewRequestEvent {

	Request request;
	public NewRequestEvent(Request request) {
		// TODO Auto-generated constructor stub
		this.request = request;
	}
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}

}
