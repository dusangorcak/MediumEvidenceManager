
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Dusan
 */
public class StorageManagerTest {
    
    private StorageManagerImpl storage;
    private DataSource dataSource;        
        
    @Before
    public void setUp() throws SQLException {        
        dataSource = Utils.prepareDataSource();
        storage = new StorageManagerImpl(dataSource);
        Connection conn = null;
        try{            
            conn = dataSource.getConnection();
            conn.prepareStatement("CREATE TABLE STORAGE ("
                    + "id bigint not null primary key generated always as identity,"
                    + "capacity int not null,"
                    + "address varchar(50) not null)").executeUpdate();
        }finally{
            Utils.closeQuietly(conn);
        }        
    }
    
    @After
    public void tearDown() throws SQLException {
        Connection conn = null;
        try{
            conn = dataSource.getConnection();                    
            conn.prepareStatement("DROP TABLE STORAGE").executeUpdate();
        }finally{
            Utils.closeQuietly(conn);
        }        
    }
        
    /**
     * Test of createStorage method, of class StorageManager.
     */
    @Test
    public void testCreateStorage() {
        Storage stor = newStorage(50,"Kounicova 2");
        storage.createStorage(stor);
        
        Long storageId = stor.getId();
        assertNotNull(storageId);
        Storage result = storage.getStorage(storageId);
        assertEquals(stor, result);
        assertNotSame(stor, result);
        assertDeepEquals(stor, result);
    }
    @Test
    public void testCreateStorageWithWrongAttributes(){
        try {
            storage.createStorage(null);
        } catch (IllegalArgumentException e) {
            //catched
        }
        
        Storage stor = newStorage(100,"Masarykova 1");
        stor.setId(1l);
        try {
            storage.createStorage(stor);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }        
        
        
        stor = newStorage(-1,"Masarykova 1"); 
        try {
            storage.createStorage(stor);
            fail();
        } catch (IllegalArgumentException ex) {
            //catched
        }
        
        stor = newStorage(0,"Masarykova 1"); 
        try {
            storage.createStorage(stor);
            fail();
        } catch (IllegalArgumentException ex) {
            //catched
        }
        
        stor = newStorage(-1,null); 
        try {
            storage.createStorage(stor);
            fail();
        } catch (IllegalArgumentException ex) {
            //catched
        }
        
               
        stor = newStorage(50,null); 
        try {
            storage.createStorage(stor);
            fail();
        } catch (IllegalArgumentException ex) {
            //catched
        }
    }
    
    /**
     * Test of deleteStorage method, of class StorageManager.
     */
    @Test
    public void testDeleteStorage() {
        
        Storage stor1 = newStorage(10000,"Klacelova 2");
        Storage stor2 = newStorage(20000,"Botanicka 36");
        
        storage.createStorage(stor1);
        storage.createStorage(stor2);
        
        assertNotNull(storage.getStorage(stor1.getId()));
        assertNotNull(storage.getStorage(stor2.getId()));

        storage.deleteStorage(stor1);
        
        assertNull(storage.getStorage(stor1.getId()));
        assertNotNull(storage.getStorage(stor2.getId()));
        
        storage.deleteStorage(stor2);
        assertNull(storage.getStorage(stor2.getId()));
        
     }
    
    
    @Test
    public void testDeleteStorageWithWrongAttributes() {
        Storage stor = newStorage(100,"Masarykova 1");
        storage.createStorage(stor);
        try {
            storage.deleteStorage(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //catched
        }
        
       try {
            stor.setId(null);
            storage.deleteStorage(stor);
            fail();
        } catch (IllegalArgumentException ex) {
            //catched
        }   
        
        try {
            stor.setId(1l);
            storage.deleteStorage(stor);
            fail();
        } catch (IllegalArgumentException ex) {
            //catched
        }
        
        
        try {
            stor.setAddress(null);
            storage.deleteStorage(stor);
            fail();
        } catch (IllegalArgumentException ex) {
            //catched
        }
        
    }

    /**
     * Test of getStorage method, of class StorageManager.
     */
    @Test
    public void testGetStorage() {
        //tested in method testeDeleteStorage()
    }

    /**
     * Test of updateStorage method, of class StorageManager.
     */
    @Test
    public void testUpdateStorage() {
        //vytvaram dva pre zistenie ci update jedneho neovplyvnuje druhy
        Storage stor1 = newStorage(100,"Klacelova 2");
        Storage stor2 = newStorage(2000,"Botanicka 36a");
        storage.createStorage(stor1);
        storage.createStorage(stor2);
        Long Id = stor1.getId();

        stor1 = storage.getStorage(Id);
        stor1.setCapacity(1);
        storage.updateStorage(stor1);        
        assertEquals(1, stor1.getCapacity());
        assertEquals("Klacelova 2", stor1.getAddress());
        
        stor1 = storage.getStorage(Id);
        stor1.setAddress("Masarykova 1");
        storage.updateStorage(stor1);        
        assertEquals(1, stor1.getCapacity());
        assertEquals("Masarykova 1", stor1.getAddress());

        // Check if updates didn't affected other records
        assertDeepEquals(stor2, storage.getStorage(stor2.getId()));
    }
    
    @Test
    public void updateStorageWithWrongAttributes() {

        Storage stor1 = newStorage(60000,"Botanicka 36a");
        storage.createStorage(stor1);
        Long Id = stor1.getId();
        
        try {
            storage.updateStorage(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //catched
        }
        
        try {
            stor1 = storage.getStorage(Id);
            stor1.setId(null);
            storage.updateStorage(stor1);        
            fail();
        } catch (IllegalArgumentException ex) {
            //catched
        }

        try {
            stor1 = storage.getStorage(Id);
            stor1.setId(Id - 1);
            storage.updateStorage(stor1);   
            fail();
        } catch (IllegalArgumentException ex) {
            //catched
        }
            

        try {
            stor1 = storage.getStorage(Id);
            stor1.setCapacity(-1);
            storage.updateStorage(stor1);        
            fail();
        } catch (IllegalArgumentException ex) {
            //catched
        }

        try {
            stor1 = storage.getStorage(Id);
            stor1.setAddress(null);
            storage.updateStorage(stor1);        
            fail();
        } catch (IllegalArgumentException ex) {
            //catched
        }
        
    }
    
    
    private static Storage newStorage(int capacity, String address){
        Storage stor = new Storage();
        stor.setCapacity(capacity);
        stor.setAddress(address);
        return stor;
    }
    
    private void assertDeepEquals(Storage expected, Storage actual){
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCapacity(), actual.getCapacity());
        assertEquals(expected.getAddress(), actual.getAddress());
    }

    
}

