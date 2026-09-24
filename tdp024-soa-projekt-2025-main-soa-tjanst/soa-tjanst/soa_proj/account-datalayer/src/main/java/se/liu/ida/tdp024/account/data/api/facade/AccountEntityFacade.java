/*
 * -------------------------------------------------------------
 * Detta interface definierar vilka funktioner (metoder)
 * som datalagret erbjuder till logiklagret.
 *

 */

 package se.liu.ida.tdp024.account.data.api.facade;

 import se.liu.ida.tdp024.account.data.api.entity.Account;
 import se.liu.ida.tdp024.account.data.api.entity.Transaction;
 
 import java.util.List;
 
 public interface AccountEntityFacade {
 
     // ----------------------------------------------------------
     // Skapar ett nytt konto för en person på en viss bank.
     //
     // Parametrar:
     //  - accountType: vilken typ av konto (CHECK eller SAVINGS)
     // Personnyckel från person api, banknyckel från bank api
    
     Account createAccount(String accountType, String personKey, String bankKey);
 
 
     // ----------------------------------------------------------
     // Krediterar ett konto (lägger till pengar).
     // Skapar en Transaction-post även om operationen misslyckas.
     //
     
     Transaction credit(Long accountId, Integer amount);
 
 
     // ----------------------------------------------------------
     // Debiterar ett konto (tar ut pengar).
     // Skapar en Transaction-post även om operationen misslyckas.
     //
     // Parametrar:
     //  - accountId: ID för kontot som ska debiteras
    
     Transaction debit(Long accountId, Integer amount);
 
 
     // ----------------------------------------------------------
     // Hämtar alla konton som tillhör en viss person.
     //
     // Parametrar:
     //  - personKey: nyckeln som identifierar personen (från Person-API:t)
     //
     // Returnerar:
     //  - En lista med alla konton kopplade till den personen.
     //
     
     List<Account> getAccounts(String personKey);
 }
