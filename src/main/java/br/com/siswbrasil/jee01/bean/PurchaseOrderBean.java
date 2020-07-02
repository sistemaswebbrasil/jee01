package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import br.com.siswbrasil.jee01.dto.PageDto;
import br.com.siswbrasil.jee01.dto.PurchaseOrderDto;
import br.com.siswbrasil.jee01.model.PurchaseOrderDataModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Named
@ViewScoped
public class PurchaseOrderBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private PurchaseOrderDto dto;
	
	@Inject
	private PurchaseOrderDataModel dataModel;	
	
	public List<PurchaseOrderDto> list() {	
		System.out.println("##############################");

		Client client = ClientBuilder.newClient();
		String target = "http://localhost:8081/pedidos";

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

		//List<PurchaseOrderDto> list = resp.readEntity(new GenericType<List<PurchaseOrderDto>>() {});

		PageDto page = resp.readEntity(new GenericType<PageDto>() {});
		
		System.out.println("####################################################");
		System.out.println(page.getTotalElements());
		System.out.println(page.getContent() );
		
		List<PurchaseOrderDto> list = (List<PurchaseOrderDto>)(Object)    page.getContent();
		

		return list;
	}	
	
	
	
	

}
