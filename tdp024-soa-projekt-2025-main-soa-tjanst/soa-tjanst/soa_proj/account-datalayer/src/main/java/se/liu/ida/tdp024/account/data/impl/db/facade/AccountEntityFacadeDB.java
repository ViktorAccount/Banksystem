package se.liu.ida.tdp024.account.data.impl.db.facade;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountDatabase;
import se.liu.ida.tdp024.account.data.impl.db.util.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**

 * Klassens uppgift:
 *  Skapa nya konton (createAccount)
 * Kreditera och debitera konton (credit/debit)
 * Logga alla transaktioner via TransactionEntityFacade
 
 */
@Repository  // Markerar att denna klass hanterar databasoperationer i Spring
public class AccountEntityFacadeDB implements AccountEntityFacade {


    // Används för att skapa transaktioner (t.ex. kredit/debit-loggar)
    private final TransactionEntityFacade transactionFacade;

    @Autowired
    public AccountEntityFacadeDB(TransactionEntityFacade transactionFacade) {
        this.transactionFacade = transactionFacade;
    }

    // -----------------------------------
    // Skapa nytt konto i databasen
    // -----------------------------------
    @Override
    public Account createAccount(String accountType, String personKey, String bankKey) {

        // EntityManager = vår "databaskoppling"
        EntityManager em = EMF.getEntityManager();
        // Transaction = vår "pågående databasoperation" (typ ett kvitto vi fyller i)
        EntityTransaction tx = em.getTransaction();

        try {
            // Skapa ett nytt konto-objekt som följer ritningen (AccountDatabase)
            AccountDatabase newAccount = new AccountDatabase(
                CheckValidationOfAccount.valueOf(accountType), // Kontotyp (SAVINGS / CHECK)
                personKey, // Personnyckel (från person-api)
                bankKey    // Banknyckel (från bank-api)
            );

            // Starta databastransaktion (nu börjar vi skriva i databasen)
            tx.begin();
            //Skapar ett HELT nytt konto i databas, därför används persist, och ej update merge eftersom vi skapar
            em.persist(newAccount);  // Spara kontot till databasen
            tx.commit();             // Godkänn ändringen (spara permanent)

            // Returnera det skapade kontot
            return newAccount;

        } catch (Exception e) {
            // Om något går fel, rulla tillbaka ändringen (inget sparas)
            if (tx.isActive()) tx.rollback();
            System.err.println("Account creation failed for type: " + accountType + " | " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    
    @Override
    public Transaction credit(Long accountId, Integer amount) {
        return executeTransaction(accountId, amount, CheckCredit.CREDIT);
    }

   
    @Override
    public Transaction debit(Long accountId, Integer amount) {
        // Skickar till samma hjälpfunktion men med DEBIT istället
        return executeTransaction(accountId, amount, CheckCredit.DEBIT);
    }

    // -----------------------------------
    // Hämta alla konton som tillhör en viss person
    // -----------------------------------
    @Override
    public List<Account> getAccounts(String personKey) {
        EntityManager em = EMF.getEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                "SELECT a FROM AccountDatabase a WHERE a.personKey = :key", Account.class);
            query.setParameter("key", personKey);
            return query.getResultList(); // Returnerar listan med konton
        } finally {
            em.close(); // Stänger databaskopplingen
        }
    }



    // KOMPLETERING 
    //KOMPLETERING
    //1. // INGEN LÅSNING PÅ DENNA NIVÅ EFTERSOM DETTA GÖRS I DATABASNIVÅ KOKMPLETERINGö
    //2. // KOMPLETRERINGEN HÄR SKA MERGE ANVÄNDAS ISTÄLLLET FÖR ATT UPDATERA.

    //FIXAT

    // -----------------------------------
    private Transaction executeTransaction(Long accountId, Integer amount, CheckCredit txType) {

         // INGEN LÅSNING PÅ DENNA NIVÅ EFTERSOM DETTA GÖRS I DATABASNIVÅ KOKMPLETERING
        //FIXAD KOMPLETERING TAGIT BORT LÅSNINGEN 

        try (EntityManager em = EMF.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            Transaction transactionResult = null;

            try {
                tx.begin(); // Startar transaktionen (vi börjar skriva till databasen)

                // Hämtar kontot med skrivlås från databasen
                AccountDatabase account = em.find(AccountDatabase.class, accountId, LockModeType.PESSIMISTIC_WRITE);

                // Om kontot inte finns, avbryt
                if (account == null) {
                    System.err.println(" No account found for ID: " + accountId);
                    tx.rollback();
                    return null;
                }

                // Förbereder status (om transaktionen lyckas eller inte)
                CheckStatus outcome = CheckStatus.FAILED;

                // Om kredit → lägg till pengar
                if (txType == CheckCredit.CREDIT) {
                    account.setHoldings(account.getHoldings() + amount);
                    outcome = CheckStatus.OK;

                // Om debit → ta bort pengar (men bara om det finns tillräckligt)
                } else if (txType == CheckCredit.DEBIT && account.getHoldings() >= amount) {
                    account.setHoldings(account.getHoldings() - amount);
                    outcome = CheckStatus.OK;
                }

                // Uppdatera kontot i databasen
                em.merge(account); // KOMPLETRERING HÄR SKA MERGE ANVÄNDAS ISTÄLLLET FÖR ATT UPDATERA
                //FIXAT ändrat till merge istället för persist

                // Skapa en ny transaktionspost i databasen (via TransactionEntityFacade)
                transactionResult = transactionFacade.createTransaction(
                    em, txType, amount, outcome, account
                );

                // Spara ändringen (commit = gör allt permanent)
                tx.commit();

            } catch (Exception e) {
                // Om något går fel, avbryt hela operationen
                if (tx.isActive()) tx.rollback();
                System.err.println("Transaction failed (" + txType + "): " + e.getMessage());
                e.printStackTrace();
            }

            // Returnerar den skapade transaktionen
            return transactionResult;

        } 
    }
}
