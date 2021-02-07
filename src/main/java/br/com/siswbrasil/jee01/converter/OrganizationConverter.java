package br.com.siswbrasil.jee01.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.siswbrasil.jee01.model.Organization;

@FacesConverter("organizationConverter")
public class OrganizationConverter implements Converter<Object>{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String id) {
		if (id == null || id.trim().isEmpty())
			return null;

		Organization organization = new Organization();
		organization.setId(Long.valueOf(id));

		return organization;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null)
			return null;

		Organization organization = (Organization) value;
		return organization.getId().toString();
	}

	

}
