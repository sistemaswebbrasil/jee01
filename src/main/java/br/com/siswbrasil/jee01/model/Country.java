package br.com.siswbrasil.jee01.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Country implements Serializable {
	private static final long serialVersionUID = 1L;	
	   
	@EqualsAndHashCode.Include
	@Id
	private String iddCode;
	
	@NotNull
	private String name;
	
	@NotBlank
	private String continent;
   
}
