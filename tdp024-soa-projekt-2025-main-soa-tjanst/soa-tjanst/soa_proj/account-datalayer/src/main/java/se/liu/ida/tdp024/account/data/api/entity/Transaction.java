/**
 
 * Detta interface beskriver hur en TRANSAKTION ska se ut i systemet.
 
 */

 package se.liu.ida.tdp024.account.data.api.entity;

 import se.liu.ida.tdp024.account.data.impl.db.util.CheckStatus;
 import se.liu.ida.tdp024.account.data.impl.db.util.CheckCredit;
 
 import java.io.Serializable;
 import java.util.Date;
 
 public interface Transaction extends Serializable {
 
     // -----------------------------------------------------------
    
     // -----------------------------------------------------------
     // Unikt id 
     Long getIdentifier();
 
 
     // --CheckCredit-----------------------------------------------
     CheckCredit getTransactionstype();
 
 
    // amount of money that was credited
     Integer getAmount();
 
 
     // Success eller NOT (Om transaction gick igenom)
     CheckStatus getState();
 
 
   
     Account getAccount();
 
 
     // -----------------------------------------------------------
        // när transaction skedde
     Date getDateCreated();
 }


/*
 * Krav 
 * entite 2 med account 
 * hanter datalagringn via jpa
 * loggar transaktion
 * 
 */