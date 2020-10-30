package br.com.siswbrasil.jee01.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.siswbrasil.jee01.exception.DataBaseException;
import br.com.siswbrasil.jee01.model.Country;

@Stateless
public class CountryService extends GenericServiceImpl<Country> {
//
//	@Inject
//	private CountryService service;
//
//	@Override
//	public void create(Country entity) {
//		System.out.println("Entrei aqui");
//		try {
//			service.create(entity);
//		} catch (Exception e) {  
//			System.out.println("Aqui deu erro!");
//			throw new DataBaseException("Falha ao processar requisição no banco de dados", e);
//		}
//	}

}
