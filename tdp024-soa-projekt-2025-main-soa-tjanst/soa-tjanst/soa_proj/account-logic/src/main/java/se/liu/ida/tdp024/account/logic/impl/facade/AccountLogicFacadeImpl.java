package se.liu.ida.tdp024.account.logic.impl.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.util.http.HTTPHelper;

import java.util.ArrayList;
import java.util.List;

/*
 *
 * Den här klassen anropas av REST-lagret (AccountController). skapar, hämtar, 
 * krediterar eller debiterar konton på ett säkert sätt.
 
 *   - AccountEntityFacade (datalagret) för att faktiskt spara/hämta data
 *   - HTTPHelper för att prata med externa tjänster (bank/person)
 *
 * @Service: gör klassen till en Spring-komponent (skapas automatiskt)
 
 */
@Service
@Primary // 
public class AccountLogicFacadeImpl implements AccountLogicFacade {


    private final AccountEntityFacade accountRepo; // datalagret (hanterar databasen)
    private final HTTPHelper http;                 // används för att göra HTTP-anrop till andra tjänster

    // Adresser till externa tjänster som används för verifiering
    private static final String PERSON_SERVICE = "http://python-person-service:8060";
    private static final String BANK_SERVICE = "http://go-gin-service:8070";

    // Konstruktor – Spring skickar in dessa automatiskt
    public AccountLogicFacadeImpl(AccountEntityFacade accountEntityFacade, HTTPHelper httpHelper) {
        // Spara referenser till datalagret och HTTP-hjälparen
        this.accountRepo = accountEntityFacade;
        this.http = httpHelper;
    }


    
    @Override
    public List<Account> findAccounts(String personKey) {
        try {
            // Ber logiklagret (AccountEntityFacade) hämta konton för given person
            List<Account> list = accountRepo.getAccounts(personKey);
            // Om listan finns, returnera den. Om inte, returnera tom lista.
            return list != null ? list : new ArrayList<>();
        } catch (Exception ex) {
            // Om något går fel, skriv ut fel och returnera tom lista
            System.err.println("[findAccounts] Exception: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * debitAccount – drar pengar från ett konto (tar bort pengar).
     * 
     * Kallas från REST-endpointen: /account-rest/account/debit
     */
    @Override
    public String debitAccount(Long accountId, Integer amount) {
        Transaction txn;
        try {
            // Försök debitera kontot via datalagret
            txn = accountRepo.debit(accountId, amount);
        } catch (Exception e) {
            // Om något går fel – logga och returnera "FAILED"
            System.err.println("[debitAccount] Error: " + e.getMessage());
            return "FAILED";
        }
        // Om transaktionen lyckades (txn != null), returnera "OK", annars "FAILED"
        return txn != null ? "OK" : "FAILED";
    }

    /**
     * creditAccount – lägger till pengar på ett konto.
     * 
     * Kallas från REST-endpointen: /account-rest/account/credit
     */
    @Override
    public String creditAccount(Long accountId, Integer amount) {
        try {
            // Försök kreditera kontot i datalagret
            Transaction result = accountRepo.credit(accountId, amount);
            // Returnera OK om det fungerade, annars FAILED
            return (result != null) ? "OK" : "FAILED";
        } catch (Exception e) {
            System.err.println("[creditAccount] Error: " + e.getMessage());
            return "FAILED";
        }
    }

    /**
     * registerAccount – skapar nytt konto efter att person och bank har verifierats.
     * 
     * Kallas från REST-endpointen: /account-rest/account/create
     */
    @Override
    public String registerAccount(String type, String person, String bank) {
        try {
            // Kolla att personen finns i person-tjänsten
            if (!personExists(person)) {
                System.err.println("[registerAccount] Ogiltig person: " + person);
                return "FAILED";
            }

            // Hämta bankens "key" (unik id) via bank-tjänsten
            String bankKey = retrieveBankKey(bank);
            if (bankKey == null) {
                System.err.println("[registerAccount] Okänd bank: " + bank);
                return "FAILED";
            }

            // Skapa konto i datalagret med de verifierade uppgifterna
            Account acc = accountRepo.createAccount(type, person, bankKey);

            //  Returnera "OK" om allt gick bra, annars "FAILED"
            return acc != null ? "OK" : "FAILED";

        } catch (Exception ex) {
            System.err.println("[registerAccount] Undantag: " + ex.getMessage());
            return "FAILED";
        }
    }

    
    private String retrieveBankKey(String bankName) {
        try {
            // Gör ett HTTP GET-anrop till banktjänsten:
            String json = http.get(BANK_SERVICE + "/bank/name", "name", bankName);

            // Om svaret är tomt eller null, betyder det att banken inte hittades
            if (json == null || json.isBlank() || "null".equalsIgnoreCase(json)) {
                return null;
            }

            // Om svaret innehåller data, omvandla JSON till ett objekt och hämta "key"
            JsonNode parsed = new ObjectMapper().readTree(json);
            return parsed.has("key") ? parsed.get("key").asText() : null;

        } catch (Exception e) {
            System.err.println("[retrieveBankKey] Fel: " + e.getMessage());
            return null;
        }
    }

    /**
     * personExists – kollar om en person finns i person-tjänsten.
     * 
     * REST → Logic → HTTPHelper → extern tjänst (Python-service)
     */
    private boolean personExists(String key) {
        try {
            // Gör HTTP GET-anrop till person-tjänsten:
            String response = http.get(PERSON_SERVICE + "/person/key", "key", key);
            if (response == null) return false;

            String clean = response.trim();
            return !(clean.isEmpty() || "null".equalsIgnoreCase(clean));

        } catch (Exception e) {
            System.err.println("[personExists] Exception: " + e.getMessage());
            return false;
        }
    }
}
