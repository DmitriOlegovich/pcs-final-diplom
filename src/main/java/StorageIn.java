
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageIn {
    protected static StorageIn storage;
    protected Map<String, List<PageEntry>> wordIndexing = new HashMap<>();

    private StorageIn() {
    }

    public static synchronized StorageIn getIndexedStorage() {
        if (storage == null) {
            storage = new StorageIn();
        }
        return storage;
    }

    public Map<String, List<PageEntry>> getStorage() {
        return wordIndexing;
    }
}