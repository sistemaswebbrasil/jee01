package br.com.siswbrasil.jee01.model;

import java.io.Serializable;

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
		
	private Long organizationId;		
	private String orderId;	

}
