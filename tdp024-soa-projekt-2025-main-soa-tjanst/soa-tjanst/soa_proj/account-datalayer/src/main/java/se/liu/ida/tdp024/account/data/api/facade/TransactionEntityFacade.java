/*
 * ------------------------------------------------------------
 * Detta interface definierar hur datalagret ska hantera
 * transaktioner (både insättningar och uttag).
 *
 * Det används av logiklagret för att:
 *   - skapa nya transaktioner (credit/debit)
 *   - hämta transaktionshistorik för ett konto
 *
 * Liknelse:
 * Tänk dig att varje gång någon rör pengar på ett konto,
 * så skrivs ett kvitto ut. Denna facade beskriver HUR dessa
 * kvitton skapas och sparas i arkivet (databasen).
 * ------------------------------------------------------------
 */

 package se.liu.ida.tdp024.account.data.api.facade;

 import se.liu.ida.tdp024.account.data.api.entity.Account;
 import se.liu.ida.tdp024.account.data.api.entity.Transaction;
 import se.liu.ida.tdp024.account.data.impl.db.util.CheckStatus;
 import se.liu.ida.tdp024.account.data.impl.db.util.CheckCredit;
 
 import jakarta.persistence.EntityManager;
 import java.util.List;
 
 public interface TransactionEntityFacade {
 
     // ------------------------------------------------------------
     // Skapar en ny transaktion i databasen.
   
     Transaction createTransaction(CheckCredit type, Integer amount, CheckStatus status, Account account);
 
 
     // ------------------------------------------------------------
     // En överlagrad version av createTransaction() som tar emot
    
     // ------------------------------------------------------------
     Transaction createTransaction(EntityManager em, CheckCredit type, Integer amount, CheckStatus status, Account account);
 
 
     // ------------------------------------------------------------
     
    
     List<Transaction> getsTransactions(Long accountId);
 }
 


