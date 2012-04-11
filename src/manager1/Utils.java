package manager1;



import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

/**
 *
 * @author Tomas
 */
public class Utils {
    
    private static final Logger logger = Logger.getLogger(Utils.class.getName());
    
    public static void closeQuietly(Connection conn, Statement ... statements) {
        if(conn != null){
            try{
                conn.close();
            } catch(SQLException ex){
                logger.log(Level.SEVERE, "Error: when closing connection", ex);
            }
        }
        
        for(Statement st : statements){
            if(st != null){
                try{
                    st.close();
                } catch(SQLException ex){
                    logger.log(Level.SEVERE, "Error when closing statement", ex);
                }
            }
        }
    }
    
    public static Long getKey(ResultSet rs,Medium medium) throws SQLException,RunTimeFailureException{
        if(rs.next()){
            if(rs.getMetaData().getColumnCount() != 1){
                throw new RunTimeFailureException("Error: generating keys -  wrong keys count " + medium);
            }
            Long key = rs.getLong(1);
            if(rs.next()){
                throw new RunTimeFailureException("Error: generating keys - more keys found " + medium);
            }
            return key;
        } else {
            throw new RunTimeFailureException("Error: generating keys - no key found " + medium );
        }
    }
    
     public static void doRollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                if (conn.getAutoCommit()) {
                    throw new IllegalStateException("Connection is in the autocommit mode!");
                }
                conn.rollback();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error when doing rollback", ex);
            }
        }
    }
    
    public static Medium resultSetToMedium(ResultSet rs) throws SQLException {
        Medium medium = new Medium();
        medium.setId(rs.getLong("id"));
        medium.setName(rs.getString("name"));
        medium.setAuthor(rs.getString("author"));
        medium.setGenre(rs.getString("genre"));
        medium.setPrice(rs.getBigDecimal("price"));
        medium.setType(getEnum(rs.getString("type")));
        return medium;
    }
    
    public static Storage resultSetToStorage(ResultSet rs) throws SQLException {
        Storage storage = new Storage();
                
        storage.setId(rs.getLong("id"));
        storage.setCapacity(rs.getInt("capacity"));
        storage.setAddress(rs.getString("address"));
        return storage;
    }
    
    private  static TypeOfMedium getEnum(String type) {
        if(type.equals("BOOK")) return TypeOfMedium.BOOK;
        if(type.equals("DVD")) return TypeOfMedium.DVD;
        if(type.equals("CD")) return TypeOfMedium.CD;
        return null;        
    }
    
   
    
    
    
    public static void executeSqlScript(DataSource ds, URL scriptUrl) throws SQLException{        
        Connection conn = null;
        try {
            conn = ds.getConnection();
            
            for (String sqlStatement : readSqlStatements(scriptUrl)) {
                if (!sqlStatement.trim().isEmpty()) {
                    System.out.println("Executing statement: " + sqlStatement);
                    conn.prepareStatement(sqlStatement).executeUpdate();
                }
            }
        } finally {
            closeQuietly(conn);
        }
    }
    
    private static String[] readSqlStatements(URL url) {
        try {
            char buffer[] = new char[256];
            StringBuilder result = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
            while (true) {
                int count = reader.read(buffer);
                if (count < 0) {
                    break;
                }
                result.append(buffer, 0, count);
            }
            return result.toString().split(";");
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read " + url, ex);
        }
    }
    
    public static void tryCreateTables(DataSource ds, URL scriptUrl) throws SQLException {
        try {
            executeSqlScript(ds, scriptUrl);
            logger.warning("Tables created");
        } catch (SQLException ex) {
            if ("X0Y32".equals(ex.getSQLState())) {
                // This code represents "Table/View/... already exists"
                // This code is Derby specific!
                return;
            } else {
                throw ex;
            }
        }
    }
    
    
}
