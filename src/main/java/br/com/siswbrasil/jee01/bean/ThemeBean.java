package br.com.siswbrasil.jee01.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.component.themeswitcher.ThemeSwitcher;

import br.com.siswbrasil.jee01.service.Theme;
import br.com.siswbrasil.jee01.service.ThemeService;
import lombok.Getter;
import lombok.Setter;



@Named
@Getter
@Setter
@SessionScoped
public class ThemeBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Inject
    private ThemeService service;
    
    private String theme = "nova-light";
    
    private List<Theme> themes ;
    


	@PostConstruct
	public void init() {
        themes = service.getThemes(); 

	}

	public void saveTheme(AjaxBehaviorEvent ajax) {
		setTheme((String) ((ThemeSwitcher) ajax.getSource()).getValue());
		
	}
}
