package br.com.siswbrasil.jee01.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import javax.annotation.PostConstruct;

import br.com.siswbrasil.jee01.exception.DatabaseException;
import br.com.siswbrasil.jee01.util.MessageUtil;
import br.com.siswbrasil.jee01.model.Address;
import br.com.siswbrasil.jee01.service.AddressService;


@Getter
@Setter
@Named
@RequestScoped
public class AddressBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private AddressService service;
	
    @Inject
    private FacesContext facesContext;
    	
	private Long addressId;
	private Address address;
    
    @PostConstruct
    public void init() throws IOException {    
        if (addressId == null) {
            address = new Address();
        } else {
			address = service.findById(addressId);
			if (address == null) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("register_not_found"));
				FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
			}			
        }
    }
    
	public List<Address> listAll() throws DatabaseException {
		return service.findAll();
	}    
    
    public String save() throws Throwable {
    	if (address.getId() == null) {
            service.create(address);
        } else {
            service.update(address);
        }
        
        MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("create_success"));        
        return "index.xhtml?faces-redirect=true";
    }
    
	public void delete(Long addressId) {
		try {
			service.deleteById(addressId);
			MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("delete_success"));
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("delete_fail"));							
		}
	}

}
