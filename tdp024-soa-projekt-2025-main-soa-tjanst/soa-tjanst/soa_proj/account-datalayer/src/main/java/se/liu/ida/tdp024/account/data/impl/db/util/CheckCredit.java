package se.liu.ida.tdp024.account.data.impl.db.util;

// transaktion typs, returner true om det är credit
public enum CheckCredit {
    CREDIT,
    DEBIT;

    public boolean isCredit() {
        return this == CREDIT;
    }
}
