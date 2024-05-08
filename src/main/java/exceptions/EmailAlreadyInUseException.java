package exceptions;

import org.hibernate.exception.ConstraintViolationException;

import java.sql.SQLException;

public class EmailAlreadyInUseException extends ConstraintViolationException {
    public EmailAlreadyInUseException(String errorMesage, SQLException e){
        super(errorMesage, e, "email");
    }
}
