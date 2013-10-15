package org.apodhrad.tools.github.client.rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

/**
 * 
 * @author Andrej Podhradsky (andrej.podhradsky@gmail.com)
 * 
 */
public class RESTClient {

	public static String get(String url) {
		Client client = ResteasyClientBuilder.newClient();
		WebTarget target = client.target(url);

		Response response = target.request(MediaType.APPLICATION_JSON).get();
		String value = response.readEntity(String.class);
		response.close();

		return value;
	}

	public static String post(String url, String body) {
		return post(url, body, null, null);
	}

	public static String post(String url, String body, String user, String password) {
		Client client = ResteasyClientBuilder.newClient();
		if (user != null && password != null) {
			client.register(new Authenticator(user, password));
		}
		WebTarget target = client.target(url);

		Entity<String> entity = Entity.entity(body, MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON).post(entity);
		String value = response.readEntity(String.class);
		response.close();

		return value;
	}
}
