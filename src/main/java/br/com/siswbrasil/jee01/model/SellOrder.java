package br.com.siswbrasil.jee01.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class SellOrder extends Audit {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private SellOrderPK pk = new SellOrderPK();


	private String user;

	@NotEmpty
	private String customer;	

    @Temporal(TemporalType.DATE)
    private Date sellDate = new Date();	

	@Temporal(TemporalType.DATE)
	private Date deliveryDate = new Date();
	
	@NotNull
	private BigDecimal total;

}
