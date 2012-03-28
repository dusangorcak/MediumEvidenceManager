

/**
 *
 * @author Tomas
 */
public class RunTimeFailureException extends RuntimeException{
    
    public RunTimeFailureException(String msg){
        super(msg);
    }
    
    public RunTimeFailureException(Throwable cause){
        super(cause);
    }
    
    public RunTimeFailureException(String msg, Throwable cause){
        super(msg,cause);
    }
}
