package se.liu.ida.tdp024.account.logic.test.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.impl.facade.AccountLogicFacadeImpl;
import se.liu.ida.tdp024.account.util.http.HTTPHelper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AccountLogicFacadeTest {

    @Mock private HTTPHelper http;
    @Mock private AccountEntityFacade accountRepo;

    private AccountLogicFacade logic;

    @BeforeEach
    public void init() {
        logic = new AccountLogicFacadeImpl(accountRepo, http);
    }

    // ------------------------------------------------------------
    // Kontoskapande tester
    // ------------------------------------------------------------

    /**  returnera "OK" när konto skapas korrekt. */
    @Test
    public void createAccountShouldReturnOK() {
        when(http.get(contains("/person/key"), eq("key"), eq("55"))).thenReturn("Test User");
        when(http.get(contains("/bank/name"), eq("name"), eq("HANDELSBANKEN")))
                .thenReturn("{\"key\":\"22\", \"name\":\"HANDELSBANKEN\"}");

        Account mockAcc = Mockito.mock(Account.class);
        when(accountRepo.createAccount("CHECK", "55", "22")).thenReturn(mockAcc);

        String result = logic.registerAccount("CHECK", "55", "HANDELSBANKEN");
        assertEquals("OK", result, "Kontoskapande borde lyckas när allt är giltigt.");
    }

    /**  ge "FAILED" om banken inte hittas. */
    @Test
    public void createAccountShouldFailIfBankMissing() {
        when(http.get(anyString(), eq("name"), anyString())).thenReturn("null");
        when(http.get(anyString(), eq("key"), anyString())).thenReturn("Test User");

        String result = logic.registerAccount("SAVINGS", "9", "FAKEBANK");
        assertEquals("FAILED", result);
    }

    /**  hantera undantag vid personuppslag utan att krascha. */
    @Test
    public void createAccountShouldHandleException() {
        when(http.get(anyString(), eq("key"), anyString()))
                .thenThrow(new RuntimeException("API not reachable"));

        String result = logic.registerAccount("SAVINGS", "77", "SWEDBANK");
        assertEquals("FAILED", result);
    }

    // ------------------------------------------------------------
    // Kredit och debet tester
    // ------------------------------------------------------------

    /** Kredit lyckas ska ge OK. */
    @Test
    public void creditShouldReturnOK() {
        Transaction t = Mockito.mock(Transaction.class);
        when(accountRepo.credit(2L, 200)).thenReturn(t);

        String result = logic.creditAccount(2L, 200);
        assertEquals("OK", result);
    }

    /** Kredit misslyckas ska ge FAILED. */
    @Test
    public void creditShouldReturnFailedWhenNull() {
        when(accountRepo.credit(anyLong(), anyInt())).thenReturn(null);

        String result = logic.creditAccount(2L, 0);
        assertEquals("FAILED", result);
    }

    @Test
    public void debitShouldReturnOK() {
        Transaction t = Mockito.mock(Transaction.class);
        when(accountRepo.debit(3L, 50)).thenReturn(t);

        String result = logic.debitAccount(3L, 50);
        assertEquals("OK", result);
    }

    /** Debet ska ge FAILED vid null. */
    @Test
    public void debitShouldReturnFailedWhenNoTransaction() {
        when(accountRepo.debit(anyLong(), anyInt())).thenReturn(null);

        String result = logic.debitAccount(3L, 999);
        assertEquals("FAILED", result);
    }

    /** Kredit och debet ska hantera kastade exceptions. */
    @Test
    public void creditAndDebitShouldHandleExceptionGracefully() {
        doThrow(new RuntimeException("DB connection lost"))
                .when(accountRepo).credit(anyLong(), anyInt());

        String creditResult = logic.creditAccount(1L, 100);
        assertEquals("FAILED", creditResult);

        doThrow(new RuntimeException("Transaction error"))
                .when(accountRepo).debit(anyLong(), anyInt());

        String debitResult = logic.debitAccount(1L, 25);
        assertEquals("FAILED", debitResult);
    }

    // ------------------------------------------------------------
    // Söka konton

    @Test
    public void findAccountsShouldReturnAccounts() {
        Account acc = Mockito.mock(Account.class);
        when(accountRepo.getAccounts("7")).thenReturn(List.of(acc));

        List<Account> found = logic.findAccounts("7");

        assertNotNull(found);
        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
    }

    /** Testar att findAccounts hanterar exceptions. */
    @Test
    public void findAccountsShouldHandleException() {
        when(accountRepo.getAccounts("8")).thenThrow(new RuntimeException("DB error"));

        List<Account> found = logic.findAccounts("8");
        assertTrue(found.isEmpty(), "Om datalagret kastar undantag ska tom lista returneras.");
    }
}
