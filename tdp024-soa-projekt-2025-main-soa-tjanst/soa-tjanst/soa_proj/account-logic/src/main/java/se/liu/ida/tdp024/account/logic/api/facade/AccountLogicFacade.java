package se.liu.ida.tdp024.account.logic.api.facade;

import java.util.List;
import se.liu.ida.tdp024.account.data.api.entity.Account;

// Detta är ett skelett (interface) som definierar vilka metoder som måste finnas i logiklagret.
// Om en klass inte implementerar dessa metoder med samma argument blir koden ogiltig och fungerar inte.

public interface AccountLogicFacade {

    // --- Skapar nytt konto baserat på kontotyp, person och bank ---
    String registerAccount(
            String accountType,
            String personKey,
            String bankName
    );

    // --- Hämtar alla konton som tillhör en viss person ---
    List<Account> findAccounts(
            String personKey
    );

    // --- Kreditera ett konto med ett visst belopp ---
    String creditAccount(
            Long accountId,
            Integer amount
    );

    // --- Debitera ett konto med ett visst belopp ---
    String debitAccount(
            Long accountId,
            Integer amount
    );
}
