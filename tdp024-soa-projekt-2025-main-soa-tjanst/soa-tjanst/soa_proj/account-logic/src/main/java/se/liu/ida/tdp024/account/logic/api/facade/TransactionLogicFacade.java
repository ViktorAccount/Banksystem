package se.liu.ida.tdp024.account.logic.api.facade;

import java.util.List;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;

// TransactionLogicFacade – logiklagrets skelett för transaktioner
// De metoder logiklagret MÅSTE ha för att hantera transaktioner.
//
public interface TransactionLogicFacade {


    // Returnerar en lista med Transaction-objekt.
    List<Transaction> findTransactions(Long accountId);
}
