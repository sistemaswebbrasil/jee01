package br.com.siswbrasil.jee01.util.teste;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class MyFilter implements Filter {

    public void destroy() {
        // TODO Auto-generated method stub

     }

     public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        
        System.out.println("Estou dentro do doFilter");
        
        
        try {
            chain.doFilter(request,response);
        } catch (Exception e) {
            System.out.println("Deu erro qualquer aqui");
            throw new RuntimeException(e);
        }        
        
        
        

        //chain.doFilter(request, response);

     }

     public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub
    	 System.out.println("Olá , estou aqui");

     }


}
