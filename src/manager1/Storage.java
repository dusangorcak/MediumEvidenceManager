package manager1;



/**
 * This class represent Storage (of some mediums). Storage has id(Long), 
 * capacity must be greater than zero and address(String) which can not be null. In one storage could be 
 * one or more mediums (CDs, Books etc.) 
 *
 * 
* @author Tomas
 */
public class Storage {
    
    private Long id;
    private String name;
    private int capacity;
    private String address;

        
    public Long getId() {
        return id;
    }

     public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
     

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setId(Long id) {
        if(this.id == null) this.id = id;
        else throw new IllegalArgumentException(" Error: Id set multiple times");
    }
    
    public String getAddress() {
        return address;
    }

    public int getCapacity() {
        return capacity;
    }

        
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Storage other = (Storage) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Storage{" + "id=" + id + ", capacity=" + capacity + ", address=" + address + '}';
    }
    
    
}