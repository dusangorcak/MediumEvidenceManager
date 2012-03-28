

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tomas
 */
public class Utils {
    
    private static final Logger logger = Logger.getLogger(Utils.class.getName());
    
    public static void closeQuietly(Connection conn) {
        if(conn != null){
            try{
                conn.close();
            } catch(SQLException ex){
                logger.log(Level.SEVERE, "Error: when closing connection", ex);
            }
        }
    }
}
