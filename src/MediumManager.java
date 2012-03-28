

import java.util.List;

/**
 *
 * @author Tomas
 */
public interface MediumManager {
    
    /**
     * Stores new medium. Id for the new medium is automatically
     * generated and assigned into id attribute.
     * 
     * @param medium medium to be created.
     * @throws IllegalArgumentException when medium is null, or medium has already 
     * assigned id.    
     */
    void createMedium(Medium medium);
    
     /**
     * Deletes medium from database. 
     * 
     * @param medium medium to be deleted from db.
     * @throws IllegalArgumentException when medium is null, or medium has null id.     
     */
    void deleteMedium(Medium medium);
    
    
    /**
     * Returns medium with given id.
     * 
     * @param id primary key of medium.
     * @return medium with given id or null if such medium does not exist.
     * @throws IllegalArgumentException when given id is null.    
     */
    Medium getMedium(Long id);
    
    
    /**
     * Returns list of all mediums.
     * 
     * @return list of all mediums.     
     */
    List<Medium> getAllMediums();
    
    /**
     * Updates medium in database.
     * 
     * @param madium updated medium to be stored into database.
     * @throws IllegalArgumentException when medium is null, or medium has null id.
     */
    void updateMedium(Medium medium);
}
