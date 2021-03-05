package br.com.siswbrasil.jee01.converter;



import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.siswbrasil.jee01.model.SellOrderPK;

@FacesConverter("sellOrderPKConverter")
public class SellOrderPKConverter implements Converter<Object> {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String id) {
		if (id == null || id.trim().isEmpty())
			return null;
		
		System.out.println("###########################################################");
		System.out.println("Converter getAsObject");
		System.out.println(id);
		System.out.println("###########################################################");
		Object obj = id;

		SellOrderPK sellOrderPK = (SellOrderPK) obj;
		
		System.out.println();
		
		//organization.setId(Long.valueOf(id));

		return sellOrderPK;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null)
			return null;
		
		System.out.println("###########################################################");
		System.out.println("Converter getAsString");
		System.out.println("###########################################################");		

		SellOrderPK sellOrderPK = (SellOrderPK) value;
		return "Teste";
	}

}
