package br.com.siswbrasil.jee01.util;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MessageUtil implements Serializable{

	private static final long serialVersionUID = 1L;

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
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

		FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", msg);
		FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
	}

	public static void addErrorMessage(String msg) {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

		FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha", msg);
		FacesContext.getCurrentInstance().addMessage(null, facesMsg);
	}

	public static void addSuccessMessage(String msg, String detail) {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

		FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, detail);
		FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
	}

	public static void addErrorMessage(String msg, String detail) {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

		FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, detail);
		FacesContext.getCurrentInstance().addMessage(null, facesMsg);
	}

}
