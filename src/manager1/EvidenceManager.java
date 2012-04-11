package manager1;


import java.util.List;


/**
 ** This service allows to manipulate with associations between mediums and 
 * storages.
 * 
 * @author Tomas
 */
public interface EvidenceManager {
 
    Storage findStorageOfMedium(Medium medium) throws RunTimeFailureException, IllegalEntityException;
    
    public void removeMediumFromStorage(Medium medium, Storage storage) throws IllegalEntityException;
    //List<Medium> getMediumsFromStorage(Storage storage);
    
    void insertMediumIntoStorage(Medium medium, Storage storage) throws RunTimeFailureException, IllegalEntityException;
    
    List<Storage> getAllFreeStorages() throws RunTimeFailureException; 
    
    List<Storage> getAllStorages() throws RunTimeFailureException;
}
