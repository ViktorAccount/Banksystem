package se.liu.ida.tdp024.account.data.impl.db.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/*
 * --------------------------------------------------------------
hantera JPA:s
 * "EntityManagerFactory" och "EntityManager".
 
 */

public final class EMF {

    // ----------------------------------------------------------
    // instans av fabriken som skapar EntityManagers.
   
    // ----------------------------------------------------------
    private static EntityManagerFactory factory;

   
    
    // ----------------------------------------------------------
    private EMF() {
    }

    /*
     * ----------------------------------------------------------
     * Hämtar en ny EntityManager (en databasanslutning).
     *
     * Om fabriken inte finns ännu (eller har stängts),
    
    -----------------------------------------------
     */
    public static EntityManager getEntityManager() {
        synchronized (EMF.class) {
            if (factory == null || !factory.isOpen()) {
                factory = Persistence.createEntityManagerFactory("account-dataPU");
            }
        }
        return factory.createEntityManager();
    }

    // ----------------------------------------------------------
    // Stänger fabriken om den är öppen.
    //
    
    public static void close() {
        if (factory != null && factory.isOpen()) {
            System.out.println("[EMF] Closing factory. Derby home: " + System.getProperty("derby.system.home"));
            factory.close();
        }
    }

    // ----------------------------------------------------------
    // Kontrollerar om fabriken är öppen.
    
    public static boolean isOpen() {
        return factory != null && factory.isOpen();
    }
}




