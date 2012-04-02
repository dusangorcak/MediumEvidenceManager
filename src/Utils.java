

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
    
    private  static TypeOfMedium getEnum(String type) {
        if(type.equals("BOOK")) return TypeOfMedium.BOOK;
        if(type.equals("DVD")) return TypeOfMedium.DVD;
        if(type.equals("CD")) return TypeOfMedium.CD;
        return null;        
    }
    
    public static DataSource prepareDataSource() throws SQLException{        
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:EvidenceManagerTest;create=true");
        return ds;        
    }
    
    private static String[] readSQLStatement(URL sqlUrl){
        try{
            char[] buffer = new char[256];
            StringBuilder result = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(sqlUrl.openStream(), "UTF-8");
            while(true){
                int count = reader.read(buffer);
                if(count < 0) break;
                result.append(buffer, 0, count);
            }
            return result.toString().split(";");
        }catch(IOException ex){
            throw new RuntimeException("Cannot read" + sqlUrl, ex);
        }
    }
    
    public static void executeSqlScript(DataSource dataSource, URL url) throws SQLException{ 
        Connection conn= null;
        try{
            conn = dataSource.getConnection();
            for(String sqlStatement : readSQLStatement(url)){
                if(!sqlStatement.trim().isEmpty()){
                    conn.prepareStatement(sqlStatement).executeUpdate();
                }
            }
            
        } finally{
            Utils.closeQuietly(conn);
        }
    }
    
    public static void tryCreateTables(DataSource datasource,URL scriptUrl) throws SQLException{
        try{
            executeSqlScript(datasource,scriptUrl);
            logger.warning("Tables created");
        }catch(SQLException ex){
            if("X0Y32".equals(ex.getSQLState())){ /// databazovo specificke pre derby hodi ak su tabulky uz vytvorene
                return;
            }else{
                throw ex;
            }
        }
    }
}
