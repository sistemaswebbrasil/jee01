package br.com.siswbrasil.jee01.exception;

import java.sql.SQLException;

import br.com.siswbrasil.jee01.util.MessageUtil;

public class CustomExceptions {

	public static Throwable converterException(Throwable t) {

		Throwable rootCause = com.google.common.base.Throwables.getRootCause(t);
		if (rootCause instanceof SQLException) {
			String sqlErrorMessage = rootCause.getMessage();
			if ("23505".equals(((SQLException) rootCause).getSQLState())) {
				String[] lines = sqlErrorMessage.split("\\n");
				return new DatabaseException(MessageUtil.getMsg("error.sql.generic"),
						MessageUtil.getMsg("error.sql.uniqueViolation") + "." + lines[1], t);
			} else {
				return new DatabaseException(MessageUtil.getMsg("error.sql.generic"),
						MessageUtil.getMsg("error.sql.unknown.state"), t);
			}
		}

		return new BusinessException(t.getMessage(), t.getLocalizedMessage(), t);
	}

}
