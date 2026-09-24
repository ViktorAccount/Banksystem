package se.liu.ida.tdp024.account.data.impl.db.util;

/**
 * Vilka typer av konton som kan skapas i systemet
 * Antingen Check eller Savings

 */
public enum CheckValidationOfAccount {
    //lönte konto och sparkonto
    CHECK, 
    SAVINGS;

    // kontroler om strängen är en gilig kontotype 
    // value, typsträngen som ska testas
    // true om värdet matchar gilit konto type
    // giligliga typer via rest
    public static boolean isValid(String value) {
        for (CheckValidationOfAccount t : values()) {
            if (t.name().equalsIgnoreCase(value)) return true;
        }
        return false;
    }
}
