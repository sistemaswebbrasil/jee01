package br.com.siswbrasil.jee01.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseOrderDto {	

	private String codigo;	

	private BigDecimal subtotal;	

	private BigDecimal taxaFrete;	

	private BigDecimal valorTotal;	

	private String status;	

	private OffsetDateTime dataCriacao;
	
	private RestauranteDto restaurante;	

}
