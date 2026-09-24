package se.liu.ida.tdp024.account.xfinal.test.account;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import se.liu.ida.tdp024.account.util.http.HTTPHelper;
import se.liu.ida.tdp024.account.util.http.HTTPHelperImpl;
import se.liu.ida.tdp024.account.xfinal.test.util.FinalConstants;

public class AccountCreateTest {

    private static final HTTPHelper httpHelper = new HTTPHelperImpl();

    @Test
    public void createSuccess() {

        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "1";
            String bank = "NORDEA";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "1";
            String bank = "NORDEA";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "1";
            String bank = "SWEDBANK";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "4";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "4";
            String bank = "JPMORGAN";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "4";
            String bank = "NORDNET";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }

    }

    @Test
    public void createSuccessAllCombos() {

        List<String> personIds = new ArrayList<String>();
        List<String> bankNames = new ArrayList<String>();
        List<String> accountTypes = new ArrayList<String>();

        personIds.add("1");
        personIds.add("2");
        personIds.add("3");
        personIds.add("4");
        personIds.add("5");

        bankNames.add("SWEDBANK");
        bankNames.add("IKANOBANKEN");
        bankNames.add("JPMORGAN");
        bankNames.add("NORDEA");
        bankNames.add("CITIBANK");
        bankNames.add("HANDELSBANKEN");
        bankNames.add("SBAB");
        bankNames.add("HSBC");
        bankNames.add("NORDNET");

        accountTypes.add("CHECK");
        accountTypes.add("SAVINGS");

        for (String personId : personIds) {
            for (String bankName : bankNames) {
                for (String accountType : accountTypes) {
                    String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", personId, "bank", bankName, "accounttype", accountType);
                    Assert.assertEquals("OK", response);
                }
            }
        }

    }

    @Test
    public void createFailure() {

        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "CREDITCARD";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "01219210";
            String bank = "SWEDBANK";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "3";
            String bank = "LEHMAN";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "3";
            String bank = "";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "";
            String bank = "SWEDBANK";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "";
            String bank = "SWEDBANK";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "";
            String bank = "";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "";
            String bank = "";
            String accountType = "";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "";
            String bank = "";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "";
            String bank = "";
            String accountType = "CREDITCARD";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVING"; //It should be SAVINGS
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "bank", bank, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "bank", bank);
            Assert.assertEquals("FAILED", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "accounttype", accountType);
            Assert.assertEquals("FAILED", response);
        }

        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/");
            Assert.assertEquals("FAILED", response);
        }


        /* Wrong endpoint (i.e. incorrect request) */
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("", response);
        }
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("", response);
        }



    }
}
