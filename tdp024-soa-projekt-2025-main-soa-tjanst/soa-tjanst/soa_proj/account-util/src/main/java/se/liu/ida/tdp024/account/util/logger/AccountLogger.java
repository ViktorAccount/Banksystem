package se.liu.ida.tdp024.account.util.logger;

public interface AccountLogger {
//log mesages

    enum TodoLoggerLevel {
        DEBUG, INFO, NOTIFY, WARNING, ERROR, CRITICAL, ALERT, EMERGENCY
    }

    void log(Throwable throwable);

    //LOgging 
    void log(TodoLoggerLevel level, String shortMessage, String longMessage);
}
