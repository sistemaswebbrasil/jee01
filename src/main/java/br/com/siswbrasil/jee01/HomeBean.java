package br.com.siswbrasil.jee01;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Named
@RequestScoped
public class HomeBean {

	private String initialMessage = "Hello World!";

}