package se.liu.ida.tdp024.account.data.test.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import se.liu.ida.tdp024.account.data.impl.db.util.*;

import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

public class UtilityCoverageTest {

    @AfterEach
    public void tearDown() {
        EMF.close();
    }

    /** Testar att EMF kan skapa och stänga en EntityManager på ett säkert sätt. */
    @Test
    public void testEMFGetAndClose() {
        EntityManager em = EMF.getEntityManager();
        assertNotNull(em);
        assertTrue(EMF.isOpen(), "EMF should be open after getEntityManager()");
        em.close();
        EMF.close();
        assertFalse(EMF.isOpen(), "EMF should be closed after close()");
    }

    /** Testar att AccountType.isValid() korrekt validerar kontotyper. */
    @Test
    public void testAccountTypeValidation() {
        assertTrue(CheckValidationOfAccount.isValid("CHECK"));
        assertTrue(CheckValidationOfAccount.isValid("savings"));
        assertFalse(CheckValidationOfAccount.isValid("FAKE_TYPE"));
    }

    /** Testar att StatusType.isSuccessful() returnerar rätt värden. */
    @Test
    public void testStatusTypeHelpers() {
        assertTrue(CheckStatus.OK.isSuccessful());
        assertFalse(CheckStatus.FAILED.isSuccessful());
    }

    /** Testar att TransactionType.isCredit() returnerar korrekt beroende på typ. */
    @Test
    public void testTransactionTypeHelpers() {
        assertTrue(CheckCredit.CREDIT.isCredit());
        assertFalse(CheckCredit.DEBIT.isCredit());
    }

    /** Testar att StorageFacadeDB.emptyStorage() kan köras utan att kasta undantag. */
    @Test
    public void testStorageFacadeEmptyStorage() {
        StorageFacadeDB storageFacade = new StorageFacadeDB();
        assertDoesNotThrow(storageFacade::cleardataStorage);
    }
}
