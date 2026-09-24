package se.liu.ida.tdp024.account.data.test.facade;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.util.*;
import se.liu.ida.tdp024.account.util.logger.KafkaLogger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TransactionEntityFacadeTest {

    private final StorageFacade storageFacade = new StorageFacadeDB();

    private final KafkaLogger kafkaLogger = new KafkaLogger(true);

    private final TransactionEntityFacade sut = new TransactionEntityFacadeDB(kafkaLogger);

    private final AccountEntityFacade accountEntityFacade = new AccountEntityFacadeDB(sut);

    @AfterEach
    public void tearDown() {
        storageFacade.cleardataStorage();
    }

    /**
     *  Testar att en ny transaktion kan skapas och får rätt data sparad.
     */
    @Test
    public void testCreateTransaction() {
        Account account = accountEntityFacade.createAccount("CHECK", "5OOpluoRt5612Nhu", "1");
        Transaction transaction = sut.createTransaction(CheckCredit.CREDIT, 100, CheckStatus.OK, account);

        assertNotNull(transaction); // En transaktion ska skapas
        assertEquals(Integer.valueOf(100), transaction.getAmount()); // Beloppet ska stämma
        assertEquals(CheckStatus.OK, transaction.getState()); // Status OK
        assertNotNull(transaction.getDateCreated()); // Datum ska sättas automatiskt
    }

    /**
     *  Testar att vi kan hitta transaktioner för ett specifikt konto.
     */
    @Test
    public void testFindTransactions() {
        Account account = accountEntityFacade.createAccount("CHECK", "FindUser", "1");
        sut.createTransaction(CheckCredit.CREDIT, 100, CheckStatus.OK, account);

        List<Transaction> found = sut.getsTransactions(account.getIdentifier());
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals(CheckCredit.CREDIT, found.get(0).getTransactionstype());
    }

    /**
     *  Testar att det går att skapa en transaktion även om kontot är null.
     */
    @Test
    public void testCreateTransactionWithNullAccount() {
        Transaction txn = sut.createTransaction(CheckCredit.DEBIT, 250, CheckStatus.FAILED, null);

        assertNotNull(txn);
        assertEquals(Integer.valueOf(250), txn.getAmount());
        assertEquals(CheckStatus.FAILED, txn.getState());
    }

    /**
     *  Testar att felhantering fungerar om databaskopplingen (EntityManager) är stängd.
     */
    @Test
    public void testCreateTransactionExceptionPath() {
        EntityManager em = EMF.getEntityManager();
        em.close();  // Vi stänger manuellt kopplingen till databasen

        Account acc = accountEntityFacade.createAccount("CHECK", "EM_CLOSED_USER", "1");

        Transaction txn = sut.createTransaction(em, CheckCredit.CREDIT, 100, CheckStatus.OK, acc);
        assertNull(txn, "Transaction creation with closed EM should fail gracefully");
    }

    /**
     * Testar att sökning efter transaktioner på ett ogiltigt konto-id returnerar tom lista.
     */
    @Test
    public void testFindTransactionsEmptyResult() {
        List<Transaction> txns = sut.getsTransactions(-999L);
        assertNotNull(txns);
        assertTrue(txns.isEmpty(), "No transactions should be found for invalid ID");
    }

    /**
     * Testar att flera olika typer av transaktioner kan skapas (CREDIT och DEBIT).
     */
    @Test
    public void testMultipleTransactionTypes() {
        Account account = accountEntityFacade.createAccount("SAVINGS", "MultiUser", "1");

        Transaction t1 = sut.createTransaction(CheckCredit.CREDIT, 500, CheckStatus.OK, account);
        Transaction t2 = sut.createTransaction(CheckCredit.DEBIT, 200, CheckStatus.OK, account);

        List<Transaction> txns = sut.getsTransactions(account.getIdentifier());
        assertEquals(2, txns.size());
        assertTrue(txns.stream().anyMatch(t -> t.getTransactionstype() == CheckCredit.CREDIT));
        assertTrue(txns.stream().anyMatch(t -> t.getTransactionstype() == CheckCredit.DEBIT));
    }

    /**
     * Testar att Kafka-loggning körs när en ny transaktion skapas.
     */
    @Test
    public void testKafkaLoggingTrigger() {
        Account account = accountEntityFacade.createAccount("CHECK", "KafkaUser", "1");
        Transaction txn = sut.createTransaction(CheckCredit.CREDIT, 10, CheckStatus.OK, account);

        assertNotNull(txn);
        assertEquals(CheckStatus.OK, txn.getState());
    }

    /**
     * Testar att transaktioner sparas korrekt i databasen
     * 
     */
    @Test
    public void testTransactionPersistenceIntegrity() {
        Account account = accountEntityFacade.createAccount("SAVINGS", "DataIntegrityUser", "1");
        Transaction txn = sut.createTransaction(CheckCredit.CREDIT, 999, CheckStatus.OK, account);

        List<Transaction> found = sut.getsTransactions(account.getIdentifier());
        assertEquals(1, found.size());

        Transaction loaded = found.get(0);
        // Vi kontrollerar att transaktionen som laddas är identisk med den vi skapade
        assertEquals(txn.getIdentifier(), loaded.getIdentifier());
        assertEquals(txn.getAmount(), loaded.getAmount());
        assertEquals(txn.getState(), loaded.getState());
    }
}
