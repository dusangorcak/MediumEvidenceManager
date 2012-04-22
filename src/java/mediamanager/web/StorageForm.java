
package mediamanager.web;

import javax.servlet.http.HttpServletRequest;
import manager1.Storage;

/**
 *
 * @author Tomas
 */
public class StorageForm {
    
    private Long id;
    private String capacity;
    private String address;
    
    public static StorageForm extractFromRequest(HttpServletRequest request) {
        StorageForm storageForm = new StorageForm();
        String idString = request.getParameter("id");
        storageForm.setId(idString == null ? null : Long.parseLong(idString));        
        storageForm.setCapacity(request.getParameter("capacity"));
        storageForm.setAddress(request.getParameter("address"));
        return storageForm;
    }
    
    public Storage validateAndToStorage(StringBuilder errors){
        Storage storage = new Storage();
        storage.setAddress(address);
        storage.setCapacity(stringToInt(capacity, errors));
        
        if(errors.length() > 0){
            return null;
        }        
        return storage;
    }
    
    private static int stringToInt(String value, StringBuilder errors){
        int result;
        if(value == null || value.trim().isEmpty()){
            errors.append("Field 'capacity' is not filled <br/>");
            return 0;
        }
        
        try{
            result = Integer.parseInt(value);
        }catch(NumberFormatException ex){
            errors.append("Field 'capacity' is not a number: ").append(value).append(". <br/>");
            return 0;
        }
        if(result <= 0){
            errors.append("Field 'capacity' has illegal value: ").append(result).append(". <br/>");
            return 0;
        }
        
        return result;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    
}
