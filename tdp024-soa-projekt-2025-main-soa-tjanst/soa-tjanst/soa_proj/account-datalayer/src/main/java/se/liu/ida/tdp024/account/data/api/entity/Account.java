package se.liu.ida.tdp024.account.data.api.entity;

import java.io.Serializable;

// ---------------------------------------------------------


public interface Account extends Serializable {

    // Varje konto måste ha ett unikt ID 
   
    Long getIdentifier();


    // -----------------------------------------------------
    // Hämtar personens nyckel (ID) som kontot tillhör.
    // Den här nyckeln kommer från Person-tjänsten (inte från databasen direkt).
    // hämtas direkt från person api
   
    String getAccountHolderKey();


    // -----------------------------------------------------
    // Returnerar vilken typ av konto det är.
    // Kan vara CHECK (lönekonto) eller SAVINGS (sparkonto).
   
    String getTypeOfAccount();


    // -----------------------------------------------------
    // Hämtar bankens nyckel (ID).
    // Denna nyckel hämtas från Bank-api tjänsten.
   
    String getBankKey();


    // -----------------------------------------------------
    // Hämtar det nuvarande saldot (hur mycket pengar som finns på kontot).
    
    Integer getHoldings();


    //  uptader konto balance
    void setHoldings(Integer holdings);
}
