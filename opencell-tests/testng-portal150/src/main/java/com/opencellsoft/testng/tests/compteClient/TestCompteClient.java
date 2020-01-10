package com.opencellsoft.testng.tests.compteClient;
import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.compteClient.CompteClientPage;

import com.opencellsoft.testng.tests.base.TestBase;

public class TestCompteClient extends  TestBase {
    /**
     * generate values.
     */
    
    /**
     * fill the constants.
     */
   
        
        
    public TestCompteClient() {
String test = "CL_" + System.currentTimeMillis();
        
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
    }
    @Test
    public void clientdetails() throws InterruptedException {
        
        /**
         * Client details
         */
        CompteClientPage compteclient = PageFactory.initElements(this.getDriver(),
            CompteClientPage.class);
        
        
        compteclient.clientDetailsTest(driver, data);
        
    }
    
    /**
     * Check updated  fields.
     * 
     * @param page compte client
     */
    private void testData(CompteClientPage page) {
        String code = page.getCompteClient().getAttribute(ATTRIBUTE_VALUE);
        String description = page.getPrenomCompteClient().getAttribute(ATTRIBUTE_VALUE);
        
        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));
        
    }
    
    
}
