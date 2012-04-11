package manager1;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author Tomas
 */
public class EvidenceManagerImpl implements EvidenceManager{
    
    private static final Logger logger = Logger.getLogger(
            EvidenceManagerImpl.class.getName());

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }    

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }
    
    @Override
    public Storage findStorageOfMedium(Medium medium) throws RunTimeFailureException, IllegalEntityException{
        checkDataSource();
        if(medium == null) 
            throw new IllegalArgumentException("medium is null");
        
        if(medium.getId() == null)
            throw new IllegalEntityException("id is null " + medium);
        
        Connection conn = null;
        PreparedStatement st = null;
        
        try {
             conn = dataSource.getConnection();
             st = conn.prepareStatement(
                    "SELECT storage.id, col, row, capacity, note " +
                    "FROM storage JOIN ON medium.id = storage.storageId " +
                    "WHERE medium.id = ?");
             st.setLong(1, medium.getId());
             ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Storage result = Utils.resultSetToStorage(rs);                
                if (rs.next()) {
                    throw new RunTimeFailureException(
                            "Internal integrity error: more graves with the same id found!");
                }
                return result;
            } else {
                return null;
            }
            
        } catch(SQLException ex){
            logger.log(Level.SEVERE, "Error: when retrieving");
            throw new RunTimeFailureException("Error: retrieving medium - ",ex);
        } finally{
            Utils.closeQuietly(conn);
        }
        
        
    }
    
    @Override
    public void insertMediumIntoStorage(Medium medium, Storage storage) throws RunTimeFailureException, IllegalEntityException{
        checkDataSource();
        if(storage == null) 
            throw new IllegalArgumentException("storage is null");
        
        if(storage.getId() == null)
            throw new IllegalEntityException("storage.id is null");
        
        if(storage.getCapacity() == storage.getActualCapacity())
            throw new IllegalEntityException("storage is full");
        
        if(medium == null) 
            throw new IllegalArgumentException("medium is null");
            
         if(medium.getId() == null)
             throw new IllegalEntityException("medium.id is null");
        
        
         Connection conn = null;
         PreparedStatement st = null;
         
         try {             
             conn = dataSource.getConnection();
             st = conn.prepareStatement("UPDATE Medium SET storageId = ? WHERE id = ? AND storageId IS NULL");
             st.setLong(1, storage.getId());
             st.setLong(2, medium.getId());
             int count = st.executeUpdate();
             
             if(count != 1){
                throw new IllegalEntityException("Error: when inserting a Medium into Storage :" + medium + " " + storage );
             }
             
             st = conn.prepareStatement("UPDATE Storage SET actualCapacity = ? WHERE id = ?");
             st.setInt(1, storage.getActualCapacity() + 1);
             st.setLong(2, storage.getId());
             count = st.executeUpdate();
           
             if(count != 1){
                throw new IllegalEntityException("Error: when inserting a Medium into Storage :" + medium + " " + storage ); 
             }
             medium.setStorageID(storage.getId());
             storage.setActualCapacity(storage.getActualCapacity() +1);
         }catch(SQLException ex){
            String msg = "Error: when inserting medium into storage" + medium + " " + storage;
            medium.setStorageID(null);
            logger.log(Level.SEVERE, msg, storage);
            throw new RunTimeFailureException(msg, ex);     
                 
         } finally{
             Utils.closeQuietly(conn, st);
             Utils.doRollbackQuietly(conn);
         }
     }
             
    @Override
    public void removeMediumFromStorage(Medium medium, Storage storage) throws IllegalEntityException{
        checkDataSource();
        if(storage == null) 
            throw new IllegalArgumentException("storage is null");
        
        if(storage.getId() == null)
            throw new IllegalEntityException("storage.id is null");
        
        if(medium == null) 
            throw new IllegalArgumentException("medium is null");
            
         if(medium.getId() == null)
             throw new IllegalEntityException("medium.id is null");
         
         if(medium.getStorageID() == null)
             throw new IllegalEntityException("medium is not in the storage");
        
         Connection conn = null;
         PreparedStatement st = null;
         try{
             conn = dataSource.getConnection();
             st = conn.prepareStatement("UPDATE Medium SET storageId = null WHERE id = ? AND storageId = ?");
             st.setLong(1, medium.getId());
             st.setLong(2, storage.getId());
             int count = st.executeUpdate();
             if(count != 1){
                 throw new IllegalEntityException("Error: when removing medium from storage" + medium + " " + storage);
             }
             medium.setStorageID(null);
         } catch(SQLException ex){
            String msg = "Error: when removing medium from  storage" + medium + " " + storage;
            logger.log(Level.SEVERE, msg, storage);
            throw new RunTimeFailureException(msg, ex);     
                 
         } finally{
             Utils.closeQuietly(conn, st);
             Utils.doRollbackQuietly(conn);
         }
    }
    
    @Override
    public List<Storage> getAllFreeStorages() throws RunTimeFailureException{
        
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT storage.id, col, row, capacity, note " +
                    "FROM storage LEFT JOIN medium ON storage.id = medium.graveId " +
                    "GROUP BY storage.id, col, row, capacity, note " +
                    "HAVING COUNT(medium.id) < capacity");
            
        
        ResultSet rs = st.executeQuery();
            
            List<Storage> result = new ArrayList<Storage>();
            while (rs.next()) {
                result.add(Utils.resultSetToStorage(rs));
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
    public List<Storage> getAllStorages() throws RunTimeFailureException{
        
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT storage.id, col, row, capacity, note " +
                    "FROM storage LEFT JOIN medium ON storage.id = medium.graveId " +
                    "GROUP BY storage.id, col, row, capacity, note ");
                    
        ResultSet rs = st.executeQuery();
            
            List<Storage> result = new ArrayList<Storage>();
            while (rs.next()) {
                result.add(Utils.resultSetToStorage(rs));
            }
            return result;
            
        } catch(SQLException ex){
            logger.log(Level.SEVERE, "Error: when retrieving");
            throw new RunTimeFailureException("Error: retrieving medium - ",ex);
        } finally{
            Utils.closeQuietly(conn);
        }
        
    }
}
