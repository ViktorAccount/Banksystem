package se.liu.ida.tdp024.account.rest;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.api.facade.TransactionLogicFacade;
import se.liu.ida.tdp024.account.util.logger.KafkaLogger;

import java.util.List;


@RestController
@RequestMapping("/account-rest/account")  // Basadress för alla endpoints
public class AccountController {

    // Beroenden till logiklagret och Kafka-loggning
    private final TransactionLogicFacade transactionFacade;
    private final AccountLogicFacade accountFacade;
    private final KafkaLogger kafka;

    // Spring skapar dessa objekt automatiskt (dependency injection)
    @Autowired
    public AccountController(TransactionLogicFacade transactionFacade,
                             AccountLogicFacade accountFacade,
                             KafkaLogger kafka) {
        this.transactionFacade = transactionFacade;
        this.accountFacade = accountFacade;
        this.kafka = kafka;
    }

    // ------------------------------------------------------------
    // 1. Skapa konto
    
    @GetMapping({"/create", "/create/"})
    public String createAccount(
            @RequestParam(defaultValue = "") String accounttype,
            @RequestParam(defaultValue = "") String person,
            @RequestParam(defaultValue = "") String bank) {

        logKafka("create", "Type: " + accounttype + ", Bank: " + bank);
        return accountFacade.registerAccount(accounttype, person, bank);
    }

    // ------------------------------------------------------------
    // 2. Hämta konton för en person
    
    @GetMapping("/find/person")
    public List<Account> findAccounts(@RequestParam(defaultValue = "") String person) {
        logKafka("find/person", "Find accounts for person=" + person);
        return accountFacade.findAccounts(person);
    }

    // ------------------------------------------------------------
    // 3. Kreditera konto
    
    @GetMapping("/credit")
    public String creditAccount(
            @RequestParam(defaultValue = "") Long id,
            @RequestParam(defaultValue = "") Integer amount) {

        logKafka("credit", "Credit account id=" + id + ", amount=" + amount);
        return accountFacade.creditAccount(id, amount);
    }

    // ------------------------------------------------------------
    // 4. Debitera konto
    
    @GetMapping("/debit")
    public String debitAccount(
            @RequestParam(defaultValue = "") Long id,
            @RequestParam(defaultValue = "") Integer amount) {

        logKafka("debit", "Debit account id=" + id + ", amount=" + amount);
        return accountFacade.debitAccount(id, amount);
    }

    // ------------------------------------------------------------
    // 5. Visa transaktioner för ett konto
    
    @GetMapping("/transactions")
    public List<Transaction> findTransactions(@RequestParam(defaultValue = "") Long id) {
        logKafka("transactions", "Fetch transactions for account id=" + id);
        return transactionFacade.findTransactions(id);
    }

    // ------------------------------------------------------------
    //  för Kafka-loggning
   
    private void logKafka(String path, String info) {
        kafka.sendRestMessage(
                "Method: GET\n" +
                "\tEndpoint: /account-rest/account/" + path + "\n" +
                "\t" + info
        );
    }
}
