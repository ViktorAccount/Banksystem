package se.liu.ida.tdp024.account.logic.test.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.logic.api.facade.TransactionLogicFacade;
import se.liu.ida.tdp024.account.logic.impl.facade.TransactionLogicFacadeImpl;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class TransactionLogicFacadeTest {

    private TransactionEntityFacade mockRepo;
    private TransactionLogicFacade transactionLogic;

    @BeforeEach
    public void setup() {
        mockRepo = Mockito.mock(TransactionEntityFacade.class);
        transactionLogic = new TransactionLogicFacadeImpl(mockRepo);
    }

   

    /** Ska returnera lista med transaktioner om datalagret fungerar. */
    @Test
    @DisplayName("Returnerar transaktioner vid giltigt konto-ID")
    public void shouldReturnTransactionListWhenDataLayerOK() {
        Transaction mockTx = Mockito.mock(Transaction.class);
        when(mockRepo.getsTransactions(10L)).thenReturn(List.of(mockTx));

        List<Transaction> results = transactionLogic.findTransactions(10L);

        assertNotNull(results, "Transaktionslistan ska inte vara null");
        assertEquals(1, results.size(), "Borde finnas exakt en transaktion");
    }

    /** Testar att ingen NullPointerException kastas om listan är tom. */
    @Test
    @DisplayName("Hanterar tom lista utan fel")
    public void shouldHandleEmptyListSafely() {
        when(mockRepo.getsTransactions(2L)).thenReturn(Collections.emptyList());

        List<Transaction> result = transactionLogic.findTransactions(2L);

        assertTrue(result.isEmpty(), "Borde returnera tom lista vid inga träffar");
    }

   
    /** Testar att fel i datalagret loggas och resulterar i tom lista. */
    @Test
    @DisplayName("Returnerar tom lista vid undantag i datalagret")
    public void shouldReturnEmptyListWhenRepositoryThrows() {
        when(mockRepo.getsTransactions(anyLong())).thenThrow(new RuntimeException("Database failure"));

        List<Transaction> result = transactionLogic.findTransactions(999L);

        assertNotNull(result);
        assertEquals(0, result.size(), "Vid undantag ska tom lista returneras");
    }

    /** Testar att ogiltigt konto-ID (negativt eller noll) ger tom lista direkt. */
    @Test
    @DisplayName("Ignorerar ogiltigt konto-ID")
    public void shouldReturnEmptyListWhenAccountIdInvalid() {
        List<Transaction> negative = transactionLogic.findTransactions(-1L);
        List<Transaction> zero = transactionLogic.findTransactions(0L);

        assertTrue(negative.isEmpty(), "Negativt konto-ID ska ignoreras");
        assertTrue(zero.isEmpty(), "Konto-ID 0 ska ignoreras");
    }

    /** Testar att null-ID hanteras korrekt utan exception. */
    @Test
    @DisplayName("Hanterar null som konto-ID utan att krascha")
    public void shouldHandleNullAccountIdGracefully() {
        List<Transaction> result = transactionLogic.findTransactions(null);
        assertTrue(result.isEmpty());
    }
}
