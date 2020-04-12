package br.com.siswbrasil.jee01.util;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class MessageUtil {

	public static String getMsg(String messageId) {
		String msg = "";
		FacesContext facesContext = FacesContext.getCurrentInstance();		
		Locale locale = facesContext.getViewRoot().getLocale();		
		ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);				
		try {
			msg = bundle.getString(messageId);
		} catch (Exception e) {
		}
		return msg;
	}

}
