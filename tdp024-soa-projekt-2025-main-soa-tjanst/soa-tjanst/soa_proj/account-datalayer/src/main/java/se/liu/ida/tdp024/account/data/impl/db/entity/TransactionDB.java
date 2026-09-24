package se.liu.ida.tdp024.account.data.impl.db.entity;

import jakarta.persistence.*;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.impl.db.util.CheckStatus;
import se.liu.ida.tdp024.account.data.impl.db.util.CheckCredit;

import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/*

 */
@Entity
public class TransactionDB implements Transaction {

    // ----------------------------------------------------------
    // @Id + @GeneratedValue = varje transaktion får ett unikt ID.
    // JPA skapar det automatiskt när posten sparas.
    //
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // ----------------------------------------------------------
    // Vilken typ av transaktion det är:
    // CREDIT (pengar in) eller DEBIT (pengar ut)

   //LÄGG TILL @Enumerated(EnumType.STRING)
    private CheckCredit type;
    private Integer amount;

    // ----------------------------------------------------------
    // När transaktionen skapades.
   
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    // ----------------------------------------------------------
    // Status för transaktionen – OK eller FAILED.
    
    private CheckStatus status;

    // ----------------------------------------------------------
    // Relation till kontot som transaktionen tillhör.
    // @ManyToOne = många transaktioner kan höra till ett konto.
  
    @ManyToOne(targetEntity = AccountDatabase.class)
    private Account account;

    // ----------------------------------------------------------
    
    protected TransactionDB() {
    }

    // ----------------------------------------------------------
    // Konstruktor som används när en ny transaktion skapas.
    // Logiklagret använder den här när någon gör kredit/debit.
    //
  
    public TransactionDB(CheckCredit type, Integer amount, CheckStatus status, Account account) {
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.account = account;
    }

    // ----------------------------------------------------------
    // @PrePersist = JPA kör denna metod precis innan objektet

    @PrePersist
    protected void setTimestampBeforePersist() {
        this.created = Date.from(Instant.now());
    }

   
    @Override
    public Long getIdentifier() {
        return id; // Transaktionens ID
    }

    @Override
    @com.fasterxml.jackson.annotation.JsonProperty("type")
    public CheckCredit getTransactionstype() {
        return type; // Typ: CREDIT eller DEBIT
    }

    @Override
    public Integer getAmount() {
        return amount; // Hur mycket pengar som hanterades
    }

    @Override
    public CheckStatus getState() {
        return status; // Status: OK eller FAILED
    }

    @Override
    public Account getAccount() {
        return account; // Kontot transaktionen hör till
    }

    @Override
    public Date getDateCreated() {
        return created; // När transaktionen skapades
    }

    // ----------------------------------------------------------
    // toString() används för loggning och felsökning.
    // Skriver ut transaktionsinfo som text.
    
    @Override
    public String toString() {
        return "TransactionDB{id=" + id +
                ", type=" + type +
                ", amount=" + amount +
                ", status=" + status +
                ", created=" + created +
                ", accountId=" + (account != null ? account.getIdentifier() : null) + '}';
    }
}
