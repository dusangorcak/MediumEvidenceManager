package manager;


import manager1.MediumManagerImpl;
import manager1.TypeOfMedium;
import manager1.Utils;
import manager1.EvidenceManager;
import manager1.Medium;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import manager1.*;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Tomas Marton
 */

public class MediumManagerTest {
    
    private MediumManagerImpl manager;
    private StorageManagerImpl storageManager;
    private Storage storage;
    private DataSource dataSource;
    
    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        //we will use in memory database
        ds.setUrl("jdbc:derby:memory:MediumManagerTest;create=true");       
        return ds;
    }
    
    @Before
    public void setUp() throws SQLException{
        dataSource = prepareDataSource();        
        Utils.executeSqlScript(dataSource, EvidenceManagerTests.class.getResource("CreateTables.sql"));
        manager = new MediumManagerImpl();
        manager.setDataSource(dataSource);
        
    }
    
    @After
    public void tearDown() throws SQLException {
        Utils.executeSqlScript(dataSource, EvidenceManagerTests.class.getResource("DropTables.sql"));      
    }
    
    @Test
    public void createMedium(){
        BigDecimal price = new BigDecimal("200.00");
        Medium medium = newMedium("Java","XY","Programming",price,TypeOfMedium.DVD);        
        manager.createMedium(medium);
        
        Long mediumId = medium.getId();
        assertNotNull(mediumId);
        Medium test = manager.getMedium(mediumId);
        assertEquals(medium, test);
        assertNotSame(medium, test);
        assertDeepEquals(medium, test);
    }
    
    @Test
    public void getMedium(){
        BigDecimal price = new BigDecimal("200.00");
        Medium medium = newMedium("Java","XY","Programming",price,TypeOfMedium.DVD);
        manager.createMedium(medium);
        
        Long mediumId = medium.getId();
        Medium test = manager.getMedium(mediumId);
        assertEquals(medium,test);
        assertDeepEquals(medium, test);
    }
    
    @Test
    public void getAllMediums(){
        
        assertTrue(manager.getAllMediums().isEmpty());
        
        BigDecimal price1 = new BigDecimal("200.00");
        BigDecimal price2 = new BigDecimal("100.50");
        Medium medium1 = newMedium("Java","XY","Programming",price1,TypeOfMedium.DVD);
        Medium medium2 = newMedium("Harry Potter","Rowling","Fiction",price2,TypeOfMedium.CD);
        
        manager.createMedium(medium2);
        manager.createMedium(medium1);
        
        List<Medium> list1 = Arrays.asList(medium1,medium2);
        List<Medium> list2 = manager.getAllMediums();
        
        Collections.sort(list1, mediumComparator);
        Collections.sort(list2, mediumComparator);
        
        assertEquals(list1,list2);
        assertDeepEquals(list1, list2);
        
    }
    
    @Test
    public void createMediumWithWrongParams(){
        
        try {
            manager.createMedium(null);
            fail();
        } catch (IllegalArgumentException ex) {
           //OK
        }
        
        BigDecimal price = new BigDecimal("200.00");        
        Medium medium = newMedium("Java","XY","Programming",price,TypeOfMedium.DVD);
        medium.setId(2l);
        try {
            manager.createMedium(medium);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
               
        medium = newMedium("","XY","Programming",price,TypeOfMedium.DVD);
        try {
            manager.createMedium(medium);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        medium = newMedium("Java","","Programming",price,TypeOfMedium.DVD);
        try {
            manager.createMedium(medium);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        medium = newMedium("Java","XY","",price,TypeOfMedium.DVD);
        try {
            manager.createMedium(medium);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        price = new BigDecimal("-1");
        medium = newMedium("Java","XY","Programming",price,TypeOfMedium.DVD);
        try {
            manager.createMedium(medium);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        price = new BigDecimal("0");
        medium = newMedium("Java","XY","Programming",price,TypeOfMedium.DVD);
        try {
            manager.createMedium(medium);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
    }
    
    @Test
    public void updateMedium(){
      BigDecimal price1 = new BigDecimal("200.00");
      BigDecimal price2 = new BigDecimal("100.50");
      Medium medium1 = newMedium("Java","XY","Programming",price1,TypeOfMedium.DVD);
      Medium medium2 = newMedium("Harry Potter","Rowling","Fiction",price2,TypeOfMedium.CD);
      manager.createMedium(medium2);
      manager.createMedium(medium1);
      Long mediumId = medium1.getId();      
      
      medium1 = manager.getMedium(mediumId);
      
      medium1.setAuthor("XX");
      manager.updateMedium(medium1);
      assertEquals("Java", medium1.getName());
      assertEquals("XX", medium1.getAuthor());
      assertEquals("Programming", medium1.getGenre());
      assertEquals(0, price1.compareTo(medium1.getPrice()));
      assertEquals(TypeOfMedium.DVD,medium1.getType());
      
      medium1 = manager.getMedium(mediumId);
      medium1.setGenre("Nonfiction");
      manager.updateMedium(medium1);
      assertEquals("Java", medium1.getName());
      assertEquals("XX", medium1.getAuthor());
      assertEquals("Nonfiction", medium1.getGenre());
      assertEquals(0, price1.compareTo(medium1.getPrice()));
      assertEquals(TypeOfMedium.DVD,medium1.getType());
              
      medium1 = manager.getMedium(mediumId);
      medium1.setName("C++");
      manager.updateMedium(medium1);
      assertEquals("C++", medium1.getName());
      assertEquals("XX", medium1.getAuthor());
      assertEquals("Nonfiction", medium1.getGenre());
      assertEquals(0, price1.compareTo(medium1.getPrice()));
      assertEquals(TypeOfMedium.DVD,medium1.getType());
      
      medium1 = manager.getMedium(mediumId);
      medium1.setPrice(price2);
      manager.updateMedium(medium1);
      assertEquals("C++", medium1.getName());
      assertEquals("XX", medium1.getAuthor());
      assertEquals("Nonfiction", medium1.getGenre());
      assertEquals(0, price2.compareTo(medium1.getPrice()));
      assertEquals(TypeOfMedium.DVD,medium1.getType());
      
      medium1 = manager.getMedium(mediumId);
      medium1.setType(TypeOfMedium.CD);
      manager.updateMedium(medium1);
      assertEquals("C++", medium1.getName());
      assertEquals("XX", medium1.getAuthor());
      assertEquals("Nonfiction", medium1.getGenre());
      assertEquals(0, price2.compareTo(medium1.getPrice()));
      assertEquals(TypeOfMedium.CD,medium1.getType());
      
      assertDeepEquals(medium2, manager.getMedium(medium2.getId()));
    }
    
    @Test
    public void updateMediumWithWrongParams(){
      BigDecimal price = new BigDecimal("200.00");
      Medium medium = newMedium("Java","XY","Programming",price,TypeOfMedium.DVD);
      manager.createMedium(medium);
      Long mediumId = medium.getId();
      
      try {
            manager.updateMedium(null);
            fail();
      } catch (IllegalArgumentException ex) {
            //OK
      }
      
      try {
        medium = manager.getMedium(mediumId);
        medium.setId(null);
        manager.updateMedium(medium);        
        fail();
      } catch (IllegalArgumentException ex) {
        //OK
      }
      
      try {
        medium = manager.getMedium(mediumId);
        medium.setId(mediumId + 1);
        manager.updateMedium(medium);        
        fail();
      } catch (IllegalArgumentException ex) {
        //OK
      }
      
      try {
        medium = manager.getMedium(mediumId);
        medium.setAuthor(null);
        manager.updateMedium(medium);        
        fail();
      } catch (IllegalArgumentException ex) {
        //OK
      }
      
      try {
        medium = manager.getMedium(mediumId);
        medium.setGenre(null);
        manager.updateMedium(medium);        
        fail();
      } catch (IllegalArgumentException ex) {
        //OK
      }
      
      try {
        medium = manager.getMedium(mediumId);
        medium.setName(null);
        manager.updateMedium(medium);        
        fail();
      } catch (IllegalArgumentException ex) {
        //OK
      }
      
      try {
        medium = manager.getMedium(mediumId);
        BigDecimal pr = new BigDecimal("-1.00");
        medium.setPrice(pr);
        manager.updateMedium(medium);        
        fail();
      } catch (IllegalArgumentException ex) {
        //OK
      }
      
      try {
        medium = manager.getMedium(mediumId);
        BigDecimal pr = new BigDecimal("0.00");
        medium.setPrice(pr);
        manager.updateMedium(medium);        
        fail();
      } catch (IllegalArgumentException ex) {
        //OK
      }
        
    }
    
    @Test
    public void deleteMedium(){
      BigDecimal price1 = new BigDecimal("200.00");
      BigDecimal price2 = new BigDecimal("100.50");
      Medium medium1 = newMedium("Java","XY","Programming",price1,TypeOfMedium.DVD);
      Medium medium2 = newMedium("Harry Potter","Rowling","Fiction",price2,TypeOfMedium.CD);
      manager.createMedium(medium2);
      manager.createMedium(medium1);
      
      assertNotNull(manager.getMedium(medium1.getId()));
      assertNotNull(manager.getMedium(medium2.getId()));
      
      manager.deleteMedium(medium1);
      
      assertNull(manager.getMedium(medium1.getId()));
      assertNotNull(manager.getMedium(medium2.getId()));
    }
    
    @Test
    public void deleteMediumWithWrongParams(){
      BigDecimal price = new BigDecimal("100.50");
      Medium medium = newMedium("Java","XY","Programming",price,TypeOfMedium.DVD);
      manager.createMedium(medium);
      
      try {
        manager.deleteMedium(null);
        fail();
      } catch (IllegalArgumentException ex) {
       //OK
      }
      
      try {
        medium.setId(null);
        manager.deleteMedium(medium);
        fail();
        } catch (IllegalArgumentException ex) {
            //OK
      }
      
      try {
        medium.setId(1l);
        manager.deleteMedium(medium); 
        fail();
      } catch (IllegalArgumentException ex) {
            //OK
      }
    }
    
    private static Medium newMedium(String name, String author,String gengre,
            BigDecimal price,TypeOfMedium type){
        
        Medium medium = new Medium();
        medium.setName(name);
        medium.setAuthor(author);
        medium.setGenre(gengre);
        medium.setPrice(price);
        medium.setType(type);
        
        return medium;
    }
    
    
    
    private static void assertDeepEquals(Medium expected, Medium actual){
        assertEquals(expected.getAuthor(), actual.getAuthor());
        assertEquals(expected.getGenre(), actual.getGenre());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertNotNull(expected.getType().compareTo(actual.getType()));
    }
    
    private void assertDeepEquals(List<Medium> expectedList, List<Medium> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Medium expected = expectedList.get(i);
            Medium actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }
    
    private static Comparator<Medium> mediumComparator = new Comparator<Medium>() {
    
        @Override
        public int compare(Medium m1, Medium m2){
            return Long.valueOf(m1.getId()).compareTo(Long.valueOf(m2.getId()));
        }
    };

   
    
}
