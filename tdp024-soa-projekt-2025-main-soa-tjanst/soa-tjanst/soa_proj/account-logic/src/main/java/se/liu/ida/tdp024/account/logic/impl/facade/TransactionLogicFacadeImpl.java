package se.liu.ida.tdp024.account.logic.impl.facade;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import se.liu.ida.tdp024.account.data.api.entity.Transaction; 
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade; 
import se.liu.ida.tdp024.account.logic.api.facade.TransactionLogicFacade; 

import java.util.ArrayList;
import java.util.List;

/**
 * TransactionLogicFacadeImpl
 * 
 *  klassen ansvarar  hämta transaktioner köp, insättningar, uttag
 * 
 */
@Service   
@Primary   
public class TransactionLogicFacadeImpl implements TransactionLogicFacade {

    private final TransactionEntityFacade txRepo;

    
    public TransactionLogicFacadeImpl(TransactionEntityFacade transactionEntityFacade) {
        this.txRepo = transactionEntityFacade;  
    }

   
    @Override
    public List<Transaction> findTransactions(Long accountId) {

        // 1. Kontrollera att vi fått ett giltigt kontonummer 
        if (accountId == null || accountId <= 0) {
            System.err.println("[TransactionLogic] Ogiltigt accountId: " + accountId);
            // Returnerar en tom lista om kontonumret är felaktigt
            return new ArrayList<>();
        }

        try {
            List<Transaction> transactions = txRepo.getsTransactions(accountId);

            //  2. Om inga transaktioner hittades, returnera en tom lista istället för null
            if (transactions == null) {
                System.err.println("[TransactionLogic] Tom lista hämtad för konto: " + accountId);
                return new ArrayList<>();
            }

            // 3. Om allt gick bra — skicka tillbaka listan med transaktioner till REST-lagret
            return transactions;

        } catch (Exception ex) {
            // 4.  Om något går fel 
            logError("Fel vid hämtning av transaktioner för konto " + accountId, ex);
            return new ArrayList<>();
        }
    }

    /**
   
     */
    private void logError(String message, Exception e) {
        // Skriver ut ett felmeddelande i konsolen
        System.err.println("[TransactionLogic] " + message);

        // Om det finns detaljer om felet, skriv ut dem också
        if (e != null && e.getMessage() != null) {
            System.err.println("  -> Orsak: " + e.getMessage());
        }
    }
}
