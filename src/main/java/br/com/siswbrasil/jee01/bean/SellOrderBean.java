package br.com.siswbrasil.jee01.bean;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import br.com.siswbrasil.jee01.exception.DatabaseException;
import br.com.siswbrasil.jee01.model.SellOrder;
import br.com.siswbrasil.jee01.model.SellOrderPK;
import br.com.siswbrasil.jee01.service.SellOrderService;
import br.com.siswbrasil.jee01.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Named
@RequestScoped
public class SellOrderBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private SellOrderService service;

	@Inject
	private FacesContext facesContext;
	
	@Inject
	private Logger LOG;

	private SellOrderPK sellOrderId;
	private SellOrder sellOrder;	
	private String orderId;
	private Long organizationId;

	@PostConstruct
	public void init() throws IOException {		
		if (orderId == null && organizationId == null) {
//			sellOrder = new SellOrder();
			sellOrder = new SellOrder(new SellOrderPK(),"Adriano","XXXX",new Date(),new Date(),new BigDecimal(55L),null);
		} else {
			LOG.info("OrderId "+orderId);
			LOG.info("OrganizationId "+organizationId);
			sellOrderId = new SellOrderPK(organizationId,orderId);
			sellOrder = service.findById(new SellOrderPK(organizationId,orderId));
			System.out.println("##########################################################");
			System.out.println(sellOrder);
			System.out.println("##########################################################");
			if (sellOrder == null) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("register_not_found"));
				FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
			}
		}
	}

	public List<SellOrder> listAll() throws DatabaseException {
		return service.findAll();
	}

	public String save() throws Throwable {

		try {
			
			System.out.println("-------------------------------------------------------------------");
			System.out.println(sellOrder);
			System.out.println("-------------------------------------------------------------------");
			System.out.println(sellOrder.getPk().getOrganizationId());
			System.out.println("-------------------------------------------------------------------");

			if (StringUtils.isEmpty(sellOrder.getPk().getOrderId())) {
				sellOrder.getPk().setOrderId(UUID.randomUUID().toString());
				System.out.println("Vou criar");
				System.out.println(sellOrder);
				service.create(sellOrder);
				MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("create_success"));
			} else {
				System.out.println("Vou atualizar");
				service.update(sellOrder);
				MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("update_success"));
			}


			return "index.xhtml?faces-redirect=true";

		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("create_fail"));
		}

		return "";
	}

	public void delete(SellOrderPK sellOrderId) {
		try {
			service.deleteById(sellOrderId);
			MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("delete_success"));
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("delete_fail"));
		}
	}

}
