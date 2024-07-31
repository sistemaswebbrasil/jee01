package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import br.com.siswbrasil.jee01.domain.CepResponse;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
@Getter
@Setter
public class ConsultaCepBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String cep = "21221460";
	
	private String resposta = null ;
	
	private CepResponse cepResponse = new CepResponse();

	public void buscar() {		
		resposta = "Pesquisando";		
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(String.format("https://viacep.com.br/ws/%s/json/", cep));

        Response response = target.request().get();
        String value = response.readEntity(String.class);
        resposta = value;
        response = target.request().get();        
        cepResponse = response.readEntity(CepResponse.class);
        System.out.println(cepResponse.getLogradouro());        
        response.close();
	}
	
	public void limpar() {
		resposta = null;
	}

}
