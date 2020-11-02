package br.com.siswbrasil.jee01.exception;

import java.sql.SQLException;

import br.com.siswbrasil.jee01.util.MessageUtil;

public class CustomExceptions {
	
	public static Throwable converterException(Throwable t) {

		Throwable rootCause = com.google.common.base.Throwables.getRootCause(t);
		System.out.println("1#############################################");
		System.out.println(rootCause);
		System.out.println("1#############################################");
		if (rootCause instanceof SQLException) {
			
			String sqlErrorMessage = rootCause.getMessage();
			
			System.out.println("2#############################################");
			System.out.println(((SQLException) rootCause).getSQLState());
			System.out.println("2#############################################");			
			
			if ("23505".equals(((SQLException) rootCause).getSQLState())) {
				
				System.out.println("3#############################################");
				System.out.println(((SQLException) rootCause).getSQLState());
				System.out.println("3#############################################");
				System.out.println(((SQLException) rootCause).getErrorCode() );
				System.out.println("3#############################################");
				System.out.println(((SQLException) rootCause).getLocalizedMessage() );
				System.out.println("3#############################################");
				System.out.println(((SQLException) rootCause).getMessage() );
				System.out.println("3#############################################");
				//System.out.println(((SQLException) rootCause).getCause().getMessage() );
				System.out.println("3#############################################");	
				String[] lines = sqlErrorMessage.split("\\n");

				return new DatabaseException(MessageUtil.getMsg("error.sql.generic"),
						MessageUtil.getMsg("error.sql.uniqueViolation") + "."+lines[1], t);
			} else {
				System.out.println("4#############################################");
				System.out.println(((SQLException) rootCause).getSQLState());
				System.out.println("4#############################################");				

				return new DatabaseException(MessageUtil.getMsg("error.sql.generic"),
						MessageUtil.getMsg("error.sql.unknown.state"), t);
			}
		}

		return new BusinessException(t.getMessage(), t.getLocalizedMessage(), t);
	}	

}
