
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author Tomas
 */
public class MediumManagerImpl implements MediumManager {    
    
    public static final Logger logger = Logger.getLogger(MediumManagerImpl.class.getName());
    private DataSource datasource;
    
    void SetDataSource(DataSource ds) {
        this.datasource = ds;
    }
    
    
        @Override
        public void createMedium(Medium medium) throws IllegalArgumentException {
        if(medium == null){
            throw new IllegalArgumentException("medium is null");
        }        
        if(medium.getId() != null){
            throw new IllegalArgumentException("medium has allready set id");
        }        
        if(medium.getAuthor() == null){
            throw new IllegalArgumentException("Author has to be set.");
        }        
        if(medium.getName() == null){
            throw new IllegalArgumentException("Name has to be set.");
        }
        
        if(medium.getGenre() == null){
            throw new IllegalArgumentException("Genre has to be set.");
        }
        
        if(medium.getPrice() == null){
            throw new IllegalArgumentException("Price has to be set.");
        }
        
        if(medium.getPrice().signum() == -1 || medium.getPrice().signum() == 0){
            throw new IllegalArgumentException("Price must be greather than zero.");
        }
        
        if(medium.getType() != TypeOfMedium.BOOK || 
                medium.getType() != TypeOfMedium.CD||
                  medium.getType() != TypeOfMedium.DVD){
            throw new IllegalArgumentException("Wrong type of medium.");
        }
        
        PreparedStatement st = null;
        Connection conn = null;
        try{
            conn = datasource.getConnection();
            st = conn.prepareStatement("INSERT INTO MEDIUM(NAME, AUTHOR, GENRE, PRICE, TYPE) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, medium.getName());
            st.setString(2, medium.getAuthor());
            st.setString(3, medium.getGenre());
            st.setBigDecimal(4, medium.getPrice());
            st.setString(5, medium.getType().toString());

            int addedRows = st.executeUpdate();
            if(addedRows != 1){
                throw new RunTimeFailureException("Error:  when inserting a medium into DB - " + medium);
            }
            
            ResultSet rs = st.getGeneratedKeys();
            medium.setId(getKey(rs,medium));
        } catch(SQLException ex){
            logger.log(Level.SEVERE,"Error: when inserting a Medium into DB");
            throw new RunTimeFailureException("Error: when inserting Medium into DB  - " + medium,ex);
        } finally{
            closeQuietly(st);
        }
        
    }
        
        
    private void closeQuietly(PreparedStatement st) {
        if(st != null){
            try{
                st.close();
            } catch(SQLException ex){
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }
    
        
        private Long getKey(ResultSet rs,Medium medium) throws SQLException,RunTimeFailureException{
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
    
    @Override
    public void deleteMedium(Medium med) throws IllegalArgumentException{
        if(med == null){
            throw new IllegalArgumentException("Error: med is null");
        }
        
        if(med.getId() == null){
            throw new IllegalArgumentException("Error: med is null");
        }  
               
                               
        PreparedStatement st = null;
        Connection conn = null;
        try{
            
            conn = datasource.getConnection();
            st = conn.prepareStatement("DELETE FROM MEDIUM WHERE id=?");
            st.setLong(1, med.getId());
            int deletedmeds = st.executeUpdate();
            if(deletedmeds != 1){
                throw new RunTimeFailureException("Error:  deleted can be only one medium" + med);
            }       
            
        } catch(SQLException ex){
            logger.log(Level.SEVERE,"Error: when deleting Medium - " + med);
            throw new RunTimeFailureException("Error: when deleting Medium - " + med,ex);
        } finally{
            Utils.closeQuietly(conn);
        }
    }
    
    
    @Override
    public Medium getMedium(Long id) throws IllegalArgumentException {
           
        if(id == null){
            throw new IllegalArgumentException("Id is null");
        } 
        
        if(id <= 0){
            throw new IllegalArgumentException("Id must be greather than zero.");
        }
        
        PreparedStatement st = null;
        Connection conn = null;
        try {
            conn = datasource.getConnection();
            st = conn.prepareStatement
                    ("SELECT id,name,author,genre,price,type FROM medium WHERE id=?");
            st.setLong(1,id);
            ResultSet rs = st.executeQuery();
            
            if (rs.next()) {
                Medium med = resultSetToMedium(rs);

                if (rs.next()) {
                    throw new RunTimeFailureException(
                            "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + med+ " and " + resultSetToMedium(rs));                    
                }            
                
                return med;
            } else {
                return null;
            }
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error: when retrieving medium with id " + id);
            throw new RunTimeFailureException(
                    "Error when retrieving medium with id " + id, ex);
        } finally {
            Utils.closeQuietly(conn);
        }
    }
    
    
    private Medium resultSetToMedium(ResultSet rs) throws SQLException {
        Medium medium = new Medium();
        medium.setId(rs.getLong("id"));
        medium.setName(rs.getString("name"));
        medium.setAuthor(rs.getString("author"));
        medium.setGenre(rs.getString("genre"));
        medium.setPrice(rs.getBigDecimal("price"));
        medium.setType(getEnum(rs.getString("type")));
        return medium;
        //tomas
    }
    
    private TypeOfMedium getEnum(String type) {
        if(type.equals("BOOK")) return TypeOfMedium.BOOK;
        if(type.equals("DVD")) return TypeOfMedium.CD;
        if(type.equals("CD")) return TypeOfMedium.DVD;
        return null;
        
    }
    
    public List<Medium> getAllMediums(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void updateMedium(Medium medium){
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
