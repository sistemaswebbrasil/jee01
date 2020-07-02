package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.siswbrasil.jee01.dto.RestauranteDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Named
@ViewScoped
public class RestaurantBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private RestauranteDto restaurant;

	public List<RestauranteDto> list() {		

		Client client = ClientBuilder.newClient();
		String target = "http://localhost:8081/restaurantes";

		Response resp = client.target(target).request().get();

		System.out.println(resp);
		System.out.println(resp.getEntity());
		System.out.println(resp.getStatus());
		System.out.println(resp.getClass());
		System.out.println(resp.getDate());
		System.out.println(resp.getEntityTag());
		System.out.println(resp.getHeaders());
		System.out.println(resp.getLocation());
		System.out.println(resp.getMediaType());
		System.out.println(resp.getMetadata());

		List<RestauranteDto> list = resp.readEntity(new GenericType<List<RestauranteDto>>() {});



		return list;
	}

}
