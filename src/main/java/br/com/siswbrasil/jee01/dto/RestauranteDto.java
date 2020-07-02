package br.com.siswbrasil.jee01.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestauranteDto {

	private Long id;

	private String nome;

	private BigDecimal taxaFrete;

	private Boolean ativo;

	private Boolean aberto;

}
