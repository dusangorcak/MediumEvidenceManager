package manager1;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dusan
 */
public class IllegalEntityException extends Exception {

    public IllegalEntityException(Throwable cause) {
        super(cause);
    }

    public IllegalEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalEntityException(String message) {
        super(message);
    }

    public IllegalEntityException() {
    }
    
}
