
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author Tomas
 */
public class EvidenceManagerImpl implements {
    
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
    
    
    public long findStorageOfMedium(Medium medium) throws IllegalEntityException{
        if(medium == null) 
            throw new IllegalArgumentException("medium is null");
        
        if(medium.getId() == null)
            throw new IllegalEntityException("id is null " + medium);
        
    }
    
    public List<Medium> getMediumsFromStorage(Storage storage){
        checkDataSource();
        if(storage == null)
    }
    
    public void putMediumIntoStorage(Medium medium, Storage storage) throws RunTimeFailureException, IllegalEntityException{
        checkDataSource();
        if(storage == null) 
            throw new IllegalArgumentException("storage is null");
        
        if(storage.getId() == null)
            throw new IllegalEntityException("storage.id is null");
        
        if(medium == null) 
            throw new IllegalArgumentException("medium is null");
            
         if(medium.getId() == null)
             throw new IllegalEntityException("medium.id is null");
        
        
         Connection conn = null;
         PreparedStatement st = null;
         
         try {
             conn = dataSource.getConnection();
             st = conn.prepareStatement("UPDATE Medium SET storageId = ? WHERE id = ? AND graveId IS NULL");
             st.setLong(1, storage.getCapacity());
             st.setLong(2, medium.getId());
             int count = st.executeUpdate();
             
             if(count != 1){
                throw new IllegalEntityException("Error: when inserting a Medium into Storage :" + medium + " " + storage );
             }
         }catch(SQLException ex){
            String msg = "Error: when inserting medium into storage" + medium + " " + storage;
            logger.log(Level.SEVERE, msg, storage);
            throw new RunTimeFailureException(msg, ex);     
                 
         } finally{
             Utils.closeQuietly(conn, st);
         }
     }
             
    
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
         } catch(SQLException ex){
            String msg = "Error: when removing medium from  storage" + medium + " " + storage;
            logger.log(Level.SEVERE, msg, storage);
            throw new RunTimeFailureException(msg, ex);     
                 
         } finally{
             Utils.closeQuietly(conn, st);
         }
    }
    
    public List<Storage> getAllFreeStorages(){
        
    }
    
    public List<Storage> getAllStorages(){
        
    }
}
