package se.liu.ida.tdp024.account.data.impl.db.entity;

import jakarta.persistence.*;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.impl.db.util.CheckValidationOfAccount;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ---------------------------------------------------------------
 * Detta är JPA-implementationen av interfacet "Account".
 *
 * Klassen visar hur ett konto faktiskt lagras i databasen.
 *

 */
@Entity
public class AccountDatabase implements Account {

    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    
    private String personKey;
    private String bankKey;

   
    private Integer holdings = 0;

    
    @Enumerated(EnumType.STRING)
    private CheckValidationOfAccount accountType;

   
    protected AccountDatabase() {
    }

    
    public AccountDatabase(CheckValidationOfAccount type, String personKey, String bankKey) {
        this.accountType = type;
        this.personKey = personKey;
        this.bankKey = bankKey;
    }

    // -----------------------------------------------------------
    // Nedan är "getters" och "setters" – metoder som hämtar eller
    // uppdaterar vardenvärden 
    // -----------------------------------------------------------

    @Override
    @JsonProperty("id")
    public Long getIdentifier() {
        return id; // Returnerar kontots unika ID
    }

    @Override
    @JsonProperty("personKey")
    public String getAccountHolderKey() {
        return personKey; // Returnerar vem kontot tillhör (personens nyckel)
    }

    @Override
    @JsonProperty("accountType")
    public String getTypeOfAccount() {
        // Om accountType inte är null, returnera dess textnamn
        return accountType != null ? accountType.name() : null;
    }

    @Override
    @JsonProperty("bankKey")
    public String getBankKey() {
        return bankKey; // Returnerar vilken bank kontot tillhör
    }

    @Override
    @JsonProperty("holdings")
    public Integer getHoldings() {
        return holdings; // Returnerar nuvarande saldo
    }

    @Override
    public void setHoldings(Integer newHoldings) {
        this.holdings = newHoldings; // Uppdaterar saldot (vid kredit/debit)
    }

    // -----------------------------------------------------------
    // toString() används för loggning och felsökning.
   
    @Override
    public String toString() {
        return "AccountDB{id=" + id +
                ", type=" + accountType +
                ", personKey='" + personKey + '\'' +
                ", bankKey='" + bankKey + '\'' +
                ", holdings=" + holdings + '}';
    }
}
