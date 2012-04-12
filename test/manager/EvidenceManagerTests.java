package manager;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import manager1.*;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

//import static  

/**
 *
 * @author Dusan
 */
public class EvidenceManagerTests {
    
    private EvidenceManagerImpl manager;
    //private MediumManagerImpl mediumManager;
    //private StorageManagerImpl storageManager;
    private DataSource dataSource;

    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        //we will use in memory database
        ds.setUrl("jdbc:derby:memory:EvidenceManagerTests;create=true");        
        return ds;
    }

    private Storage s1, s2, s3, StorageWithNullId, StorageNotInDB;
    private Medium m1, m2, m3, m4, m5, MediumWithNullId, MediumNotInDB;
    private MediumManagerImpl mediumManager;
    private StorageManagerImpl storageManager;
    
    private void prepareTestData() {

        s1 = newStorage(100, "Storage 1");
        s2 = newStorage(1, "Storage 2");
        s3 = newStorage(300, "Storage 3");
        
        m1 = newMedium(s1.getId(),"Medium 1", "Author 1", "Genre 1", new BigDecimal(5), TypeOfMedium.BOOK);
        m2 = newMedium(s1.getId(),"Medium 2", "Author 2", "Genre 2", new BigDecimal(1), TypeOfMedium.DVD);
        m3 = newMedium(s2.getId(),"Medium 3", "Author 3", "Genre 3", new BigDecimal(2), TypeOfMedium.CD);
        m4 = newMedium(s2.getId(),"Medium 4", "Author 4", "Genre 4", new BigDecimal(3), TypeOfMedium.BOOK);
        m5 = newMedium(s3.getId(),"Medium 5", "Author 5", "Genre 5", new BigDecimal(4), TypeOfMedium.BOOK);
        
        
        mediumManager.createMedium(m1);
        mediumManager.createMedium(m2);
        mediumManager.createMedium(m3);
        mediumManager.createMedium(m4);
        mediumManager.createMedium(m5);
        
        storageManager.createStorage(s1);
        storageManager.createStorage(s2);
        storageManager.createStorage(s3);

        Storage storageWithNullId = newStorage(1,"Storage with null id");
        Storage storageNotInDB = newStorage(1,"Storage not in DB");
        storageNotInDB.setId(s3.getId() + 100);
        Medium mediumWithNullId = newMedium(s1.getId(),"Medium with null id", "", "", new BigDecimal(0),TypeOfMedium.CD);
        Medium mediumNotInDB = newMedium(s2.getId(),"Medium not in DB",  "", "", new BigDecimal(0),TypeOfMedium.CD);
        mediumNotInDB.setId(m5.getId() + 100);
        
    }     
       
    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        Utils.executeSqlScript(dataSource, EvidenceManagerTests.class.getResource("CreateTables.sql"));
        manager = new EvidenceManagerImpl();
        manager.setDataSource(dataSource);
        mediumManager = new MediumManagerImpl();
        mediumManager.setDataSource(dataSource);
        storageManager = new StorageManagerImpl();
        storageManager.setDataSource(dataSource);
        prepareTestData();
    }

    @After
    public void tearDown() throws SQLException {
        Utils.executeSqlScript(dataSource, EvidenceManagerTests.class.getResource("DropTables.sql"));
    }
    
    @Test
    public void findStorageOfMedium() throws RunTimeFailureException, IllegalEntityException {
        
        try{
            manager.findStorageOfMedium(m1);
        }catch(IllegalEntityException ex) {} // OK
        try{
            manager.findStorageOfMedium(m2);
        }catch(IllegalEntityException ex) {} // OK
        try{
            manager.findStorageOfMedium(m3);
        }catch(IllegalEntityException ex) {} // OK
        try{
            manager.findStorageOfMedium(m4);
        }catch(IllegalEntityException ex) {} // OK
        try{
            manager.findStorageOfMedium(m5);
        }catch(IllegalEntityException ex) {} // OK        
        
        manager.insertMediumIntoStorage(m1, s3);

        assertEquals(s3, manager.findStorageOfMedium(m1));
        assertStorageDeepEquals(s3, manager.findStorageOfMedium(m1));
        try{
            manager.findStorageOfMedium(m2);
        }catch(IllegalEntityException ex) {} // OK
        try{
            manager.findStorageOfMedium(m3);
        }catch(IllegalEntityException ex) {} // OK
        try{
            manager.findStorageOfMedium(m4);
        }catch(IllegalEntityException ex) {} // OK
        try{
            manager.findStorageOfMedium(m5);
        }catch(IllegalEntityException ex) {} // OK
        
        try {
            
            manager.findStorageOfMedium(null);
            fail();
        } catch (IllegalArgumentException ex) {} // OK
        
        try {
            manager.findStorageOfMedium(MediumWithNullId);
            fail();
        } catch (IllegalArgumentException ex) {} // OK
        
    }
    
    @Test
    public void getAllFreeStorages() throws RunTimeFailureException, IllegalEntityException {

        List<Storage> emptyStorages = Arrays.asList(s1,s2,s3);
        
        
        manager.insertMediumIntoStorage(m1, s3);
        manager.insertMediumIntoStorage(m3, s3);
        manager.insertMediumIntoStorage(m5, s1);

        emptyStorages = Arrays.asList(s2);
        assertStorageCollectionDeepEquals(emptyStorages, manager.getAllFreeStorages());
    }
    
    @Test
    public void getAllStorages() {

        List<Storage> storages = Arrays.asList(s1,s2,s3);
        assertStorageCollectionDeepEquals(storages, manager.getAllStorages());
            
    }
    
    @Test
    public void removeMediumFromStorage() throws RunTimeFailureException, IllegalEntityException {

        manager.insertMediumIntoStorage(m1, s3);
        manager.insertMediumIntoStorage(m3, s3);
        manager.insertMediumIntoStorage(m4, s3);
        manager.insertMediumIntoStorage(m5, s1);
                
        assertEquals(s3, manager.findStorageOfMedium(m1)); 
        try{
            manager.findStorageOfMedium(m2);
            fail();
        }catch(IllegalEntityException ex) {} // OK
        assertEquals(s3,manager.findStorageOfMedium(m3));
        assertEquals(s3, manager.findStorageOfMedium(m4));
        assertEquals(s1,manager.findStorageOfMedium(m5));

        manager.removeMediumFromStorage(m3, s3);
       
        assertEquals(s3, manager.findStorageOfMedium(m1));
        try{
            manager.findStorageOfMedium(m2);
            fail();
        }catch(IllegalEntityException ex) {} // OK
        try{
            manager.findStorageOfMedium(m3);
        } catch(IllegalEntityException ex) {} // OK
        assertEquals(s3, manager.findStorageOfMedium(m4));
        assertEquals(s1, manager.findStorageOfMedium(m5));
         
        
        try {
            manager.removeMediumFromStorage(m3, s1);            
            fail();
        } catch (IllegalEntityException ex) {} // OK

        try {
            manager.removeMediumFromStorage(m1, s1);
            fail();
        } catch (IllegalEntityException ex) {} // OK
        
        try {
            manager.removeMediumFromStorage(null, s2);
            fail();
        } catch (IllegalArgumentException ex) {} // OK

        try {
            manager.removeMediumFromStorage(MediumWithNullId, s2);
            fail();
        } catch (IllegalArgumentException ex) {} // OK

        try {
            manager.removeMediumFromStorage(MediumNotInDB, s2);
            fail();
        } catch (IllegalArgumentException ex) {} // OK

        try {
            manager.removeMediumFromStorage(m2, null);
            fail();
        } catch (IllegalArgumentException ex) {} // OK

        try {
            manager.removeMediumFromStorage(m2, StorageWithNullId);
            fail();
        } catch (IllegalArgumentException ex) {} // OK

        try {
            manager.removeMediumFromStorage(m2, StorageNotInDB);
            fail();
        } catch (IllegalArgumentException ex) {} // OK
    
        assertEquals(s3, manager.findStorageOfMedium(m1));
        try{
            manager.findStorageOfMedium(m2);
        }catch(IllegalEntityException ex) {} // OK
        try{
            manager.findStorageOfMedium(m3);
        } catch(IllegalEntityException ex) {} // OK
        assertEquals(s3, manager.findStorageOfMedium(m4));
        assertEquals(s1, manager.findStorageOfMedium(m5));

    }
    
    @Test
    public void insertMediumIntoStorage() throws RunTimeFailureException, IllegalEntityException {        
        
        manager.insertMediumIntoStorage(m1, s3);
        manager.insertMediumIntoStorage(m5, s1);
        manager.insertMediumIntoStorage(m3, s3);

                
        assertEquals(s3, manager.findStorageOfMedium(m1));
        assertStorageDeepEquals(s3, manager.findStorageOfMedium(m1));        
        assertEquals(s3, manager.findStorageOfMedium(m3));
        assertStorageDeepEquals(s3, manager.findStorageOfMedium(m3));        
        assertEquals(s1, manager.findStorageOfMedium(m5));
        assertStorageDeepEquals(s1, manager.findStorageOfMedium(m5));
    
        try{
            manager.findStorageOfMedium(m4);
            fail();            
        } catch(IllegalEntityException ex){} // OK 
        try {
            manager.insertMediumIntoStorage(m1, s3);
            fail();
        } catch (IllegalEntityException ex) {} // OK

        try {
            manager.insertMediumIntoStorage(m1, s2);
            fail();
        } catch (IllegalEntityException ex) {} // OK

        try {
            manager.insertMediumIntoStorage(null, s2);
            fail();
        } catch (IllegalArgumentException ex) {} // OK

        try {
            manager.insertMediumIntoStorage(MediumWithNullId, s2);
            fail();
        } catch (IllegalArgumentException ex) {} // OK

        try {
            manager.insertMediumIntoStorage(MediumNotInDB, s2);
            fail();
        } catch (IllegalArgumentException ex) {} // OK

        try {
            manager.insertMediumIntoStorage(m2, null);
            fail();
        } catch (IllegalArgumentException ex) {} // OK

        try {
            manager.insertMediumIntoStorage(m2, StorageWithNullId);
            fail();
        } catch (IllegalArgumentException ex) {} // OK

        try {
            manager.insertMediumIntoStorage(m2, StorageNotInDB);
            fail();
        } catch (IllegalArgumentException ex) {} // OK

        // Try to add medium to storage that is already full
        try {
            manager.insertMediumIntoStorage(m4, s2);
            manager.insertMediumIntoStorage(m2, s2);            
            fail();
        } catch (RunTimeFailureException ex) {} // OK

        // Check that previous tests didn't affect data in database
        
        assertEquals(s3, manager.findStorageOfMedium(m1));        
        assertEquals(s3, manager.findStorageOfMedium(m3));        
        assertEquals(s1, manager.findStorageOfMedium(m5)); 
        
        try{
            manager.findStorageOfMedium(m2);
            fail();
        }catch(IllegalEntityException ex) {} // OK
    }
    
    
    
    private static Medium newMedium(Long storageID,String name, String author,String gengre,
            BigDecimal price,TypeOfMedium type){
        
        Medium medium = new Medium();
        medium.setStorageID(storageID);
        medium.setName(name);
        medium.setAuthor(author);
        medium.setGenre(gengre);
        medium.setPrice(price);
        medium.setType(type);
        
        return medium;
    }
    
    private static Storage newStorage(int capacity, String address){
        Storage storage = new Storage();
        
        storage.setCapacity(capacity);
        storage.setAddress(address);
        
        return storage;
    }
    
    private void assertStorageDeepEquals(Storage expected, Storage actual){
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCapacity(), actual.getCapacity());
        assertEquals(expected.getAddress(), actual.getAddress());
    }

    private void assertMediumCollectionDeepEquals(List<Medium> expectedList, Storage actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Medium expected = expectedList.get(i);
            Medium actual = expectedList.get(i);
            assertDeepEquals(expected, actual);
        }
    }
    
    private void assertStorageCollectionDeepEquals(List<Storage> emptyStorage, List<Storage> allStorages) {
        
    }
    
    private static void assertDeepEquals(Medium expected, Medium actual){
        assertEquals(expected.getAuthor(), actual.getAuthor());
        assertEquals(expected.getGenre(), actual.getGenre());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertNotNull(expected.getType().compareTo(actual.getType()));
    }
    
}
    
  
