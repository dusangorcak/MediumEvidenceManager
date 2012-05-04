

/**
 *
 * @author Tomas
 */
public interface StorageManager {
    
    /**
     * Create new Storage into database. Id for the new storage is 
     * automatically generated into id attribute.
     * 
     * @param storage storage to be created.
     * @throws IllegalArgumentException when Storage is null, or storage has already 
     * assigned id (Storage is exist).
     * @throws  ServiceFailureException when db operation fails.
     */
    public void createStorage(Storage storage) throws IllegalArgumentException;   
    
    /**
     * Delete storage from database. 
     * 
     * @param storage storage to be deleted from database of storages.
     * @throws IllegalArgumentException when storage is null, or storage has 
     * null id (storage is not exist).
     * @throws  ServiceFailureException when db operation fails.
     */
    public void deleteStorage(Storage storage)throws IllegalArgumentException; 
    
    /**
     * Returns storage with given id.
     * 
     * @param id primary key of storage.
     * @return storage with given id or null if such storage has not id 
     * storage (is not exist).
     * @throws IllegalArgumentException when given id is null or id is <= 0.
     * @throws  ServiceFailureException when db operation fails.
     */
    public Storage getStorage(Long id)throws IllegalArgumentException; 
    
    /**
     * Updates storage in database of storages.
     * 
     * @param storage updated storage to be stored into database of storages.
     * @throws IllegalArgumentException when storage is null, or storage has 
     * null id (storage is not exist).
     * @throws  ServiceFailureException when db operation fails.
     */
    public void updateStorage(Storage storage)throws IllegalArgumentException; 
    
    
}
