package br.com.siswbrasil.jee01.util;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
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
	
    public static void addSuccessMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
        FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
    }	
    
    public static void addErrorMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }    

}
