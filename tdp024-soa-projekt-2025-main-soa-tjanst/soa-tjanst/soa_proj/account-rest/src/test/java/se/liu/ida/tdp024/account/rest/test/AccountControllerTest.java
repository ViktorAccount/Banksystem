package se.liu.ida.tdp024.account.rest.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountDatabase;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionDB;
import se.liu.ida.tdp024.account.data.impl.db.util.*;
import se.liu.ida.tdp024.account.logic.api.facade.*;
import se.liu.ida.tdp024.account.rest.AccountController;
import se.liu.ida.tdp024.account.util.logger.KafkaLogger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AccountControllerTest {

    private final AccountLogicFacade accountLogic = mock(AccountLogicFacade.class);
    private final TransactionLogicFacade transactionLogic = mock(TransactionLogicFacade.class);
    private final KafkaLogger kafkaLogger = mock(KafkaLogger.class);

    private final AccountController controller =
            new AccountController(transactionLogic, accountLogic, kafkaLogger);

  
    @AfterEach
    public void cleanUp() {
        Mockito.reset(accountLogic, transactionLogic, kafkaLogger);
    }

    // -----------------------------------------------------------------
    // TEST 1 – Skapa konto
    
    @Test
    public void shouldCreateAccountSuccessfully() {
        when(accountLogic.registerAccount("SAVINGS", "U001", "NORDEA"))
                .thenReturn("OK");

        String response = controller.createAccount("SAVINGS", "U001", "NORDEA");
        assertEquals("OK", response);
    }

    /**
     * Testar att skapandet misslyckas om logiken returnerar "FAILED".
     */
    @Test
    public void shouldFailToCreateInvalidAccount() {
        when(accountLogic.registerAccount("INVALID", "U404", "FAKEBANK"))
                .thenReturn("FAILED");

        String response = controller.createAccount("INVALID", "U404", "FAKEBANK");
        assertEquals("FAILED", response);
    }

    // -----------------------------------------------------------------
    // TEST 2 – Hämta konton
    // -----------------------------------------------------------------
    /**
     * Testar att findAccounts returnerar korrekt lista med konton.
     */
    @Test
    public void shouldReturnAccountsForPerson() {
        Account acc1 = new AccountDatabase(CheckValidationOfAccount.CHECK, "U100", "1");
        Account acc2 = new AccountDatabase(CheckValidationOfAccount.SAVINGS, "U100", "2");

        when(accountLogic.findAccounts("U100")).thenReturn(List.of(acc1, acc2));

        List<Account> result = controller.findAccounts("U100");
        assertEquals(2, result.size());
        assertEquals("CHECK", result.get(0).getTypeOfAccount());
        assertEquals("U100", result.get(0).getAccountHolderKey());
    }

    // -----------------------------------------------------------------
    // TEST 3 – Kreditera konto
    // -----------------------------------------------------------------
    /**
     * Testar att creditAccount returnerar "OK".
     */
    @Test
    public void shouldCreditAccountReturnOK() {
        when(accountLogic.creditAccount(5L, 300)).thenReturn("OK");

        String res = controller.creditAccount(5L, 300);
        assertEquals("OK", res);
    }

    // -----------------------------------------------------------------
    // TEST 4 – Debitera konto
    // -----------------------------------------------------------------
    /**
     * Testar att debitAccount fungerar korrekt.
     */
    @Test
    public void shouldDebitAccountReturnOK() {
        when(accountLogic.debitAccount(10L, 50)).thenReturn("OK");

        String res = controller.debitAccount(10L, 50);
        assertEquals("OK", res);
    }

    /**
     * Testar att debitAccount returnerar "FAILED" vid misslyckad debitering.
     */
    @Test
    public void shouldReturnFailedWhenDebitFails() {
        when(accountLogic.debitAccount(10L, 9999)).thenReturn("FAILED");

        String res = controller.debitAccount(10L, 9999);
        assertEquals("FAILED", res);
    }

       // -----------------------------------------------------------------
    // TEST 5 – Hämta transaktioner
    // -----------------------------------------------------------------
    /**
     * Testar att findTransactions returnerar korrekt lista med data.
     */
    @Test
    public void shouldReturnTransactionList() {
        Account acc = new AccountDatabase(CheckValidationOfAccount.CHECK, "U900", "4");
        Transaction t1 = new TransactionDB(CheckCredit.CREDIT, 100, CheckStatus.OK, acc);
        Transaction t2 = new TransactionDB(CheckCredit.DEBIT, 60, CheckStatus.OK, acc);

        when(transactionLogic.findTransactions(4L)).thenReturn(List.of(t1, t2));

        List<Transaction> result = controller.findTransactions(4L);

        // Kontroll att listan inte är tom
        assertFalse(result.isEmpty());

        // Kontroll av antal och värden
        assertEquals(2, result.size());
        assertEquals(CheckCredit.CREDIT, result.get(0).getTransactionstype());  
        assertEquals(100, result.get(0).getAmount());
        assertEquals(CheckStatus.OK, result.get(0).getState());          
    }

}
