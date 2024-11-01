package pk.guimx;

import java.util.HashMap;
import java.util.UUID;

public class Cache {
    private HashMap<String,UUID> cachedUUIDs;
    public Cache(){
         cachedUUIDs = new HashMap<>();
    }

    public HashMap<String,UUID> getCachedUUIDs(){
        return cachedUUIDs;
    }
}