
import java.math.BigDecimal;



public class Medium {
    private Long id;    
    private String name;
    private String author;
    private String genre;
    private BigDecimal price;
    private TypeOfMedium type;
    
    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public TypeOfMedium getType() {
        return type;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setId(Long id) {
        if(this.id == null) this.id = id;
        else throw new IllegalArgumentException(" Error: Id set multiple times");
    
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setType(TypeOfMedium type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Medium other = (Medium) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Medium{" + "id=" + id + ", name=" + name + ", author=" + author + ", genre=" + genre + ", price=" + price + ", type=" + type + '}';
    }
    
    
}
