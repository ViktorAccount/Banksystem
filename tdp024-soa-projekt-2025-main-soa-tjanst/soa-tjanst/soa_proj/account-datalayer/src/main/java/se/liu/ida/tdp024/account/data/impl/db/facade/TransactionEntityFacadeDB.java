package se.liu.ida.tdp024.account.data.impl.db.facade;


import jakarta.persistence.EntityManager;      
import jakarta.persistence.EntityTransaction;  
import jakarta.persistence.TypedQuery;         


import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Repository;              

import se.liu.ida.tdp024.account.data.api.entity.Account;      
import se.liu.ida.tdp024.account.data.api.entity.Transaction;  
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade; 
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionDB;       
import se.liu.ida.tdp024.account.data.impl.db.util.*;                      
import se.liu.ida.tdp024.account.util.logger.KafkaLogger;                 

import java.util.List;



@Repository // Markerar att denna klass hanterar databasoperationer i Spring
public class TransactionEntityFacadeDB implements TransactionEntityFacade {

    private final KafkaLogger kafkaLogger;

    // @Autowired = Spring Boot skapar automatiskt ett KafkaLogger-objekt
    // och skickar in det här i konstruktorn (dependency injection)
    @Autowired
    public TransactionEntityFacadeDB(KafkaLogger kafkaLogger) {
        this.kafkaLogger = kafkaLogger;
    }

   
    @Override
    public Transaction createTransaction(CheckCredit type, Integer amount, CheckStatus status, Account account) {

        // EntityManager =  kontakt med databasen (öppnar/stänger kommunikation)
        EntityManager em = EMF.getEntityManager();

        // EntityTransaction = själva databashändelsen (börjar, sparar, eller ångrar)
        EntityTransaction tx = em.getTransaction();

        try {
            // Börja skriva till databasen
            tx.begin();

            // Skapar en ny transaktion (TransactionDB är en JPA-entity som sparas i databasen)
            TransactionDB record = new TransactionDB(type, amount, status, account);

            // Sparar transaktionen i databasen (lägg till ny rad i tabellen)
            // DETTA represnterar en NY transacation record som ej fanns förut
            //FÖR att logga , lägga transaktioner ej updatdera
            em.persist(record);
            // 
// OBS: Vi använder 'persist()' här (inte 'merge()') eftersom
// varje transaktion är en NY post som ska läggas till i databasen.
// 'persist()' = INSERT för nya entiteter.
// 'merge()' används bara vid uppdatering av befintliga entiteter.
//
            

            // Godkänn ändringen (spara permanent i databasen)
            tx.commit();

            // Skicka loggmeddelande till Kafka så vi har spårbarhet
            logToKafka(record);

            // Returnerar den sparade transaktionen
            return record;

        } catch (Exception e) {
            // Exception = fel som kan uppstå, t.ex. databasfel eller nullvärde
            // Om något går fel under transaktionen:
            if (tx.isActive()) tx.rollback(); // rollback = ångra ändringen så inget sparas felaktigt
            System.err.println(" Error persisting transaction (new EM): " + e.getMessage());
            return null; // returnerar null för att visa att något gick fel
        } finally {
            // finally = körs alltid oavsett om det gick bra eller dåligt
            em.close();
        }
    }

    // ------------------------------------------------------------------------
    // Skapar en ny transaktion men använder redan aktiv databasanslutning
   
    @Override
    public Transaction createTransaction(EntityManager em, CheckCredit type, Integer amount, CheckStatus status, Account account) {

        // Skapar ny transaktionspost i minnet
        TransactionDB record = new TransactionDB(type, amount, status, account);

        try {
            // Sparar transaktionen direkt i databasen (ingen ny tx.begin() här)
            em.persist(record);
            // ------------------------------------------------------
// OBS: Vi använder 'persist()' här (inte 'merge()') eftersom
// varje transaktion är en NY post som ska läggas till i databasen.
// 'persist()' = INSERT för nya entiteter.
// 'merge()' används bara vid uppdatering av befintliga entiteter.
// 

            // Loggar till Kafka att transaktionen skapats
            logToKafka(record);

            // Returnerar transaktionen till anroparen (t.ex. AccountEntityFacadeDB)
            return record;
        } catch (Exception e) {
            // Fångar eventuella fel och skriver ut i terminalen
            System.err.println(" Error persisting transaction (shared EM): " + e.getMessage());
            return null;
        }
    }

    // ------------------------------------------------------------------------
    // Hämtar alla transaktioner för ett visst konto

    @Override
    public List<Transaction> getsTransactions(Long accountId) {
        // Skapar ny databasanslutning
        EntityManager em = EMF.getEntityManager();

        try {
            // TypedQuery = fråga till databasen 
            // Här hämtas alla TransactionDB där kontots id matchar det vi skickar in
            TypedQuery<Transaction> query = em.createQuery(
                "SELECT t FROM TransactionDB t WHERE t.account.id = :id", Transaction.class);

            query.setParameter("id", accountId);

            // Kör frågan och returnerar listan med transaktioner
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    //  Loggar transaktionen till Kafka (centralt loggsystem)
    
    private void logToKafka(Transaction tx) {

        // tx = förkortning för "transaction" (den transaktion vi loggar)
        if (tx == null) return; // Om inget att logga, avbryt

        try {
            // Skapar ett textmeddelande med info om transaktionen
            String msg = String.format(
                "Transaction recorded:\n\tType: %s\n\tAmount: %d\n\tStatus: %s",
                tx.getTransactionstype(),  // CREDIT eller DEBIT
                tx.getAmount(),            // beloppet
                tx.getState()              // OK eller FAILED
            );

            // Skickar meddelandet till Kafka (via KafkaLogger)
            kafkaLogger.sendDataMessage(msg);

        } catch (Exception e) {
            // Fångar eventuella fel vid loggning
            System.err.println(" Kafka logging failed: " + e.getMessage());
        }
    }
}


