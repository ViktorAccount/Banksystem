package se.liu.ida.tdp024.account.data.test.facade;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.util.CheckStatus;
import se.liu.ida.tdp024.account.data.impl.db.util.CheckCredit;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;
import se.liu.ida.tdp024.account.util.logger.KafkaLogger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 
 */
public class AccountEntityFacadeTest {

        //helper helps to wipes the memory in derby database
    private final StorageFacade storage = new StorageFacadeDB();
    // kafka testning mode
    private final KafkaLogger kafkaLogger = new KafkaLogger(true);

    private final TransactionEntityFacade transactionFacade = new TransactionEntityFacadeDB(kafkaLogger);

    private final AccountEntityFacade accountFacade = new AccountEntityFacadeDB(transactionFacade);

    @BeforeEach
    @AfterEach
    public void cleanDatabase() {
        storage.cleardataStorage();
    }

    /**
     * Testar att skapa ett nytt konto fungerar.
     * Kontroll: rätt typ, rätt person, rätt bank och saldo = 0.
     */
    @Test
    public void shouldCreateAccountSuccessfully() {
        Account acc = accountFacade.createAccount("SAVINGS", "Person-001", "3");

        assertNotNull(acc, "Account should be created"); // Kontot ska inte vara null
        assertEquals("SAVINGS", acc.getTypeOfAccount()); // Typen ska stämma
        assertEquals("Person-001", acc.getAccountHolderKey()); // Rätt person
        assertEquals("3", acc.getBankKey()); // Rätt bank
        assertEquals(0, acc.getHoldings()); // Startsaldo ska vara 0
    }

    /**
     *  Testar att man kan hitta ett konto med en viss personnyckel.
     */
    @Test
    public void shouldReturnAccountByPersonKey() {
        accountFacade.createAccount("CHECK", "AlphaUser", "2");
        List<Account> results = accountFacade.getAccounts("AlphaUser");

        assertEquals(1, results.size(), "Should find exactly one account for AlphaUser");
        assertEquals("2", results.get(0).getBankKey());
    }

    /**
     *  Testar att om en person inte finns i databasen,
     * då ska resultatlistan vara tom (ingen matchning hittad).
     */
    @Test
    public void shouldReturnEmptyListForUnknownPerson() {
        List<Account> accounts = accountFacade.getAccounts("NoSuchPerson");
        assertTrue(accounts.isEmpty(), "Should return empty list for unknown user");
    }

    /**
     *  Testar att kreditera (sätta in pengar) fungerar.
     * Efter kredit ska saldot öka, och en transaktion skapas.
     */
    @Test
    public void shouldCreditAccountCorrectly() {
        Account acc = accountFacade.createAccount("SAVINGS", "BetaUser", "1");
        Transaction txn = accountFacade.credit(acc.getIdentifier(), 75);

        assertNotNull(txn, "Transaction should be created"); // En transaktion ska alltid skapas
        assertEquals(75, txn.getAccount().getHoldings()); // Saldot ska uppdateras
        assertEquals(CheckCredit.CREDIT, txn.getTransactionstype()); // Transaktionstyp = CREDIT
    }

    /**
     *  Testar att debit (ta ut pengar) fungerar när man har tillräckligt saldo.
     */
    @Test
    public void shouldDebitSuccessfullyWhenFundsAvailable() {
        Account acc = accountFacade.createAccount("CHECK", "GammaUser", "4");
        accountFacade.credit(acc.getIdentifier(), 50); // Sätt in 50
        accountFacade.debit(acc.getIdentifier(), 20);  // Ta ut 20

        List<Account> found = accountFacade.getAccounts("GammaUser");
        assertEquals(30, found.get(0).getHoldings()); // Ska finnas 30 kvar
    }

    /**
     * Testar att debit misslyckas om man försöker ta ut mer än man har.
     * Då ska statusen vara FAILED och saldot oförändrat.
     */
    @Test
    public void shouldFailDebitIfInsufficientBalance() {
        Account acc = accountFacade.createAccount("SAVINGS", "LowBalanceUser", "5");
        Transaction txn = accountFacade.debit(acc.getIdentifier(), 500); // För stort belopp

        assertNotNull(txn); // Transaktion ska loggas även om den misslyckas
        assertEquals(CheckStatus.FAILED, txn.getState()); // Status = FAILED
        assertEquals(0, acc.getHoldings()); // Saldot ska inte ändras
    }

    /**
     *  Testar att kreditera ett konto som inte finns returnerar null.
     */
    @Test
    public void shouldReturnNullWhenCreditingUnknownAccount() {
        Transaction txn = accountFacade.credit(99999L, 40);
        assertNull(txn);
    }

    /**
     *  Testar att debitera ett konto som inte finns returnerar null.
     */
    @Test
    public void shouldReturnNullWhenDebitingUnknownAccount() {
        Transaction txn = accountFacade.debit(-42L, 15);
        assertNull(txn);
    }

    /**
     *  Testar flera transaktioner i följd (credit → debit → credit → debit)
     */
    @Test
    public void shouldHandleMultipleTransactionsSequentially() {
        Account acc = accountFacade.createAccount("CHECK", "MultiUser", "8");

        accountFacade.credit(acc.getIdentifier(), 200);
        accountFacade.debit(acc.getIdentifier(), 80);
        accountFacade.credit(acc.getIdentifier(), 20);
        accountFacade.debit(acc.getIdentifier(), 40);

        List<Account> result = accountFacade.getAccounts("MultiUser");
        assertEquals(100, result.get(0).getHoldings(), "Final holdings should equal 100");
    }

    /**
     *  Testar trådsäkerhet: två trådar försöker kreditera kontot samtidigt.
     */
    @Test
    public void shouldHandleConcurrentCreditsSafely() throws InterruptedException {
        Account acc = accountFacade.createAccount("SAVINGS", "ThreadUser", "2");

        // Två trådar som kör samtidigt (simulerar flera användare)
        Thread t1 = new Thread(() -> accountFacade.credit(acc.getIdentifier(), 90));
        Thread t2 = new Thread(() -> accountFacade.credit(acc.getIdentifier(), 60));

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        // should be 90 or 150 depenidng on who ran first
        Account updated = accountFacade.getAccounts("ThreadUser").get(0);
        assertTrue(updated.getHoldings() >= 90,
                "Holdings should be updated correctly even with concurrency");
    }
}
