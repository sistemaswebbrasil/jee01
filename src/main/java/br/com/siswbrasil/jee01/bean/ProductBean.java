package br.com.siswbrasil.jee01.bean;

import java.io.IOException;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import javax.annotation.PostConstruct;
import br.com.siswbrasil.jee01.util.MessageUtil;
import br.com.siswbrasil.jee01.model.Product;
import br.com.siswbrasil.jee01.service.ProductService;
import br.com.siswbrasil.jee01.datamodel.ProductDataModel;


@Getter
@Setter
@Named
@RequestScoped
public class ProductBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private ProductService service;
	
    @Inject
    private FacesContext facesContext;
    
    @Inject
    private ProductDataModel dataModel;    
    	
	private Long productId;
	private Product product;
    
    @PostConstruct
    public void init() throws IOException {    
        if (productId == null) {
            product = new Product();
        } else {
			product = service.findById(productId);
			if (product == null) {
				MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("register_not_found"));
				FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
			}			
        }
    }  
    
    public String save() throws Throwable {
    	if (product.getId() == null) {
            service.create(product);
        } else {
            service.update(product);
        }
        
        MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("create_success"));        
        return "index.xhtml?faces-redirect=true";
    }
    
	public void delete(Long productId) {
		try {
			service.deleteById(productId);
			MessageUtil.addSuccessMessage(MessageUtil.getMsg("success"), MessageUtil.getMsg("delete_success"));
		} catch (Exception e) {
			MessageUtil.addErrorMessage(MessageUtil.getMsg("error"), MessageUtil.getMsg("delete_fail"));							
		}
	}

}
