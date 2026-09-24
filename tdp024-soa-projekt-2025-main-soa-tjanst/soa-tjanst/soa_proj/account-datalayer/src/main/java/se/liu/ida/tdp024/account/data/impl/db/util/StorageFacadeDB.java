package se.liu.ida.tdp024.account.data.impl.db.util;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;


@Component
@Primary
public class StorageFacadeDB implements StorageFacade {

    
    @Override
    public void cleardataStorage() {
        try {
            EMF.close();
        } catch (Exception e) {
            System.err.println("[StorageFacadeDB] Failed to clear storage: " + e.getMessage());
        }
    }
}
