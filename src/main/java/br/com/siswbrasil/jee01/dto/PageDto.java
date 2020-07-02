package br.com.siswbrasil.jee01.dto;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageDto {

	private ArrayList<Object> content = new ArrayList<Object>();

	private int totalElements;

}
