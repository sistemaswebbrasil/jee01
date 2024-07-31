package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import br.com.siswbrasil.jee01.domain.RepositoryResponse;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
@Getter
@Setter
public class ConsultaGitHubBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String username = "sistemaswebbrasil";
	
	private String resposta = null ;
	
	private RepositoryResponse repositoryResponse = new RepositoryResponse();
	private List<Object> list = new ArrayList<Object>();

	public void buscar() {		
		resposta = "Pesquisando";		
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(String.format("https://api.github.com/users/%s/repos", username));
        Response response = target.request().get();
        String value = response.readEntity(String.class);
        resposta = value;
        
        response = target.request().get();        
        repositoryResponse = response.readEntity(RepositoryResponse.class);
        System.out.println(repositoryResponse);
        //System.out.println(cepResponse.getLogradouro());        
        response.close();
	}
	
	public void limpar() {
		resposta = null;
	}

}
