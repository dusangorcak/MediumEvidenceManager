
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
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

    MediumManagerImpl(DataSource dataSource) {
        this.datasource = dataSource;
    }   
    
    
    
    @Override
    public void createMedium(Medium medium) throws IllegalArgumentException {
        if(medium == null){
            throw new IllegalArgumentException("medium is null");
        }        
        if(medium.getId() != null){
            throw new IllegalArgumentException("medium has allready set id");
        }        
        if(medium.getAuthor() == null || medium.getAuthor().trim().isEmpty()){
            throw new IllegalArgumentException("Author has to be set.");
        }        
        if(medium.getName() == null || medium.getName().trim().isEmpty()){
            throw new IllegalArgumentException("Name has to be set.");
        }
        
        if(medium.getGenre() == null || medium.getGenre().trim().isEmpty()){
            throw new IllegalArgumentException("Genre has to be set.");
        }
        
        if(medium.getPrice() == null){
            throw new IllegalArgumentException("Price has to be set.");
        }
        
        if(medium.getPrice().signum() == -1 || medium.getPrice().signum() == 0){
            throw new IllegalArgumentException("Price must be greather than zero.");
        }
        
        if( !medium.getType().equals(TypeOfMedium.BOOK) &&
            !medium.getType().equals(TypeOfMedium.CD) &&
            !medium.getType().equals(TypeOfMedium.DVD)){
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
            medium.setId(Utils.getKey(rs,medium));
        } catch(SQLException ex){
            logger.log(Level.SEVERE,"Error: when inserting a Medium into DB");
            throw new RunTimeFailureException("Error: when inserting Medium into DB  - " + medium,ex);
        } finally{
            Utils.closeQuietly(conn,st);
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
                     
        if(med.getAuthor() == null || med.getAuthor().trim().isEmpty()){
            throw new IllegalArgumentException("Author has to be set.");
        }        
        if(med.getName() == null || med.getName().trim().isEmpty()){
            throw new IllegalArgumentException("Name has to be set.");
        }
        
        if(med.getGenre() == null || med.getGenre().trim().isEmpty()){
            throw new IllegalArgumentException("Genre has to be set.");
        }
        
        if(med.getPrice() == null){
            throw new IllegalArgumentException("Price has to be set.");
        }
        
        if(med.getPrice().signum() == -1 || med.getPrice().signum() == 0){
            throw new IllegalArgumentException("Price must be greather than zero.");
        }
        
        if( !med.getType().equals(TypeOfMedium.BOOK) &&
            !med.getType().equals(TypeOfMedium.CD) &&
            !med.getType().equals(TypeOfMedium.DVD)){
            throw new IllegalArgumentException("Wrong type of medium.");
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
            Utils.closeQuietly(conn,st);
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
                Medium medium = Utils.resultSetToMedium(rs);

                if (rs.next()) {
                    throw new RunTimeFailureException(
                            "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + medium+ " and " + Utils.resultSetToMedium(rs));                    
                }            
                
                return medium;
            } else {
                return null;
            }
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error: when retrieving medium with id " + id);
            throw new RunTimeFailureException(
                    "Error when retrieving medium with id " + id, ex);
        } finally {
            Utils.closeQuietly(conn,st);
        }
    }     
    
    @Override
    public List<Medium> getAllMediums() throws IllegalArgumentException {
        PreparedStatement st = null;
        Connection conn = null;
        try {
            conn = datasource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id,name,author,genre,price,type FROM medium");
            ResultSet rs = st.executeQuery();
            
            List<Medium> result = new ArrayList<Medium>();
            while (rs.next()) {
                result.add(Utils.resultSetToMedium(rs));
            }
            return result;
            
        } catch(SQLException ex){
            logger.log(Level.SEVERE, "Error: when retrieving");
            throw new RunTimeFailureException("Error: retrieving medium - ",ex);
        } finally{
            Utils.closeQuietly(conn);
        }
    }
    
    @Override
    public void updateMedium(Medium medium) throws IllegalArgumentException {
        if(medium == null){
            throw new IllegalArgumentException("Error: medium is null");
        }
        
        if(medium.getId() == null){
            throw new IllegalArgumentException("Error: medium is null");
        }  
                     
        if(medium.getAuthor() == null || medium.getAuthor().trim().isEmpty()){
            throw new IllegalArgumentException("Author has to be set.");
        }        
        if(medium.getName() == null || medium.getName().trim().isEmpty()){
            throw new IllegalArgumentException("Name has to be set.");
        }
        
        if(medium.getGenre() == null || medium.getGenre().trim().isEmpty()){
            throw new IllegalArgumentException("Genre has to be set.");
        }
        
        if(medium.getPrice() == null){
            throw new IllegalArgumentException("Price has to be set.");
        }
        
        if(medium.getPrice().signum() == -1 || medium.getPrice().signum() == 0){
            throw new IllegalArgumentException("Price must be greather than zero.");
        }
        
        if( !medium.getType().equals(TypeOfMedium.BOOK) &&
            !medium.getType().equals(TypeOfMedium.CD) &&
            !medium.getType().equals(TypeOfMedium.DVD)){
            throw new IllegalArgumentException("Wrong type of mediumium.");
        }
              
               
        PreparedStatement st = null;
        Connection conn = null;
        try {
            conn = datasource.getConnection();
            st = conn.prepareStatement
                    ("UPDATE medium SET name=?, author=?, genre=?, price=?, type=?"
                            + " WHERE id=?");
            st.setLong(6,medium.getId());
            st.setString(1, medium.getName());
            st.setString(2, medium.getAuthor());
            st.setString(3,medium.getGenre());
            st.setBigDecimal(4, medium.getPrice());
            st.setString(5, medium.getType().toString());
            int updated = st.executeUpdate();
            if(updated > 5){
                throw new RunTimeFailureException("Error: updated may be max 5 attributes" + medium);
            }
            
        
        } catch(SQLException ex){
            logger.log(Level.SEVERE, "Error: when deleting medium -{0}", medium);
            throw new RunTimeFailureException("Error: deleting medium - " + medium,ex);
        } finally{
            Utils.closeQuietly(conn,st);
        }
    }
}
