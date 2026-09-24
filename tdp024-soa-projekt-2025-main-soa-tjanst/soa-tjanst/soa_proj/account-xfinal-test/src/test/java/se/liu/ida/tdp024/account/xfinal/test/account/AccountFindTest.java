package se.liu.ida.tdp024.account.xfinal.test.account;

import org.junit.Assert;
import org.junit.Test;
import se.liu.ida.tdp024.account.util.http.HTTPHelper;
import se.liu.ida.tdp024.account.util.http.HTTPHelperImpl;
import se.liu.ida.tdp024.account.util.json.AccountJsonSerializer;
import se.liu.ida.tdp024.account.util.json.AccountJsonSerializerImpl;
import se.liu.ida.tdp024.account.xfinal.test.util.AccountDTO;
import se.liu.ida.tdp024.account.xfinal.test.util.FinalConstants;

public class AccountFindTest {
    
    private static final HTTPHelper httpHelper = new HTTPHelperImpl();
    private static final AccountJsonSerializer jsonSerializer = new AccountJsonSerializerImpl();
    
    
    
    @Test
    public void testFind() {
        {
            String person = "3";
            String bank = "SWEDBANK";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "3";
            String bank = "NORDEA";
            String accountType = "SAVINGS";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        {
            String person = "3";
            String bank = "JPMORGAN";
            String accountType = "CHECK";
            String response = httpHelper.get(FinalConstants.ENDPOINT + "account/create/", "person", person, "bank", bank, "accounttype", accountType);
            Assert.assertEquals("OK", response);
        }
        
        
        String json = httpHelper.get(FinalConstants.ENDPOINT + "account/find/person", "person", "3");
        AccountDTO[] accountDTos = jsonSerializer.fromJson(json, AccountDTO[].class);
        
        Assert.assertTrue(accountDTos.length > 2);
        
        
    }
    
    @Test
    public void testFindFailure() {
        
        
        String json = httpHelper.get(FinalConstants.ENDPOINT + "account/find/person", "person", "01219210");
        
        Assert.assertEquals("[]", json);
    }
}
