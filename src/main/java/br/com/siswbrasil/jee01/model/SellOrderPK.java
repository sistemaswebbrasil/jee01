package br.com.siswbrasil.jee01.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SellOrderPK implements Serializable {

	private static final long serialVersionUID = 1L;	
		
	@Column(name = "organization_id")
	private Long organizationId;	
	
	@Column(name = "order_id")
	private String orderId;	

}
