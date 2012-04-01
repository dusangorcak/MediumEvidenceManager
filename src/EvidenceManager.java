
import java.util.List;


/**
 *
 * @author Tomas
 */
public interface EvidenceManager {
 
    long findStorageOfMedium(Medium medium);
    
    List<Medium> getMediumsFromStorage(Storage storage);
    
    void putMediumIntoStorage(Medium medium, Storage storage);
    
    void removeMediumFromStorage(Medium medium); 
    
    List<Storage> getAllFreeStorages();
    
    List<Storage> getAllStorages();
}
