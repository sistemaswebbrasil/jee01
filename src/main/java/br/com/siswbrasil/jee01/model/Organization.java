package br.com.siswbrasil.jee01.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true,callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Organizations")
public class Organization extends Audit {
	private static final long serialVersionUID = 1L;

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	@NotNull
	private String initials;

	@NotEmpty
	@NotNull
	private String companyName;

	@NotNull
	private String tradingName;

}
