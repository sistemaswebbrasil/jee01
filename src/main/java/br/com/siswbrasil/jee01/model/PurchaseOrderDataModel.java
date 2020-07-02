package br.com.siswbrasil.jee01.model;

import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.siswbrasil.jee01.dto.PageDto;
import br.com.siswbrasil.jee01.dto.PurchaseOrderDto;

@ViewScoped
public class PurchaseOrderDataModel extends LazyDataModel<PurchaseOrderDto> {

	private static final long serialVersionUID = 1L;

	@Override
	public List<PurchaseOrderDto> load(
			int first, 
			int pageSize, 
			String sortField, 
			SortOrder sortOrder,
			Map<String, FilterMeta> filterBy) {		
		
		try {
			
			return getDataFromApi(
					first, 
					pageSize, 
					sortField, 
					sortOrder,
					filterBy					
			);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
		

	}

	private List<PurchaseOrderDto> getDataFromApi(
			int first, 
			int pageSize, 
			String sortField, 
			SortOrder sortOrder,
			Map<String, FilterMeta> filterBy			
		) {

		String target = "http://localhost:8081/pedidos";
		
		Client client = ClientBuilder.newClient();		
		WebTarget webTarget = client.target(target)
		.queryParam("size", pageSize) 
		.queryParam("page", first / pageSize);		
		
		if (sortField != null) {
			String sort = "asc";
			if(sortOrder == SortOrder.DESCENDING) {
				sort = "desc";
			}			
			webTarget = webTarget.queryParam("sort", sortField + "," + sort);
		}		
		Response resp = webTarget.request().get();
		PageDto page = resp.readEntity(new GenericType<PageDto>() {});	

		
		List<PurchaseOrderDto> list = (List<PurchaseOrderDto>)(Object)    page.getContent();
		super.setRowCount(page.getTotalElements());
		return list;
	}

}
