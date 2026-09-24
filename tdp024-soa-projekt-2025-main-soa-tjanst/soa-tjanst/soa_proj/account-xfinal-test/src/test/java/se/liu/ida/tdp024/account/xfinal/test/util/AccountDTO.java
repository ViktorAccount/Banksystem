package se.liu.ida.tdp024.account.xfinal.test.util;

import java.util.List;

public class AccountDTO {

    private long id;
    private String personKey;
    private String bankKey;
    private long holdings;
    private String accountType;
    private List<Object> transactions;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 71 * hash + (this.personKey != null ? this.personKey.hashCode() : 0);
        hash = 71 * hash + (this.bankKey != null ? this.bankKey.hashCode() : 0);
        hash = 71 * hash + (int) (this.holdings ^ (this.holdings >>> 32));
        hash = 71 * hash + (this.accountType != null ? this.accountType.hashCode() : 0);
        hash = 71 * hash + (this.transactions != null ? this.transactions.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AccountDTO other = (AccountDTO) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.personKey == null) ? (other.personKey != null) : !this.personKey.equals(other.personKey)) {
            return false;
        }
        if ((this.bankKey == null) ? (other.bankKey != null) : !this.bankKey.equals(other.bankKey)) {
            return false;
        }
        if (this.holdings != other.holdings) {
            return false;
        }
        if ((this.accountType == null) ? (other.accountType != null) : !this.accountType.equals(other.accountType)) {
            return false;
        }
        if (this.transactions != other.transactions && (this.transactions == null || !this.transactions.equals(other.transactions))) {
            return false;
        }
        return true;
    }
    
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPersonKey() {
        return personKey;
    }

    public void setPersonKey(String personKey) {
        this.personKey = personKey;
    }

    public String getBankKey() {
        return bankKey;
    }

    public void setBankKey(String bankKey) {
        this.bankKey = bankKey;
    }

    public long getHoldings() {
        return holdings;
    }

    public void setHoldings(long holdings) {
        this.holdings = holdings;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public List<Object> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Object> transactions) {
        this.transactions = transactions;
    }
}
