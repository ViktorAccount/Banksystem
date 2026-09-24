package se.liu.ida.tdp024.account.data.impl.db.util;

/*
 * Markera om om kresdi/debit har misslyckats
 * eller ok
 
 */
public enum CheckStatus {
    OK,
    FAILED;

    // kolla kontroller om status är ok
    public boolean isSuccessful() {
        return this == OK;
    }
}
