package com.opencellsoft.testng.tests.paiements;
import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.paiements.BillingAccountPage;

import com.opencellsoft.testng.tests.base.TestBase;

public class TestBillingAccount extends  TestBase {
    /**
     * generate values.
     */
    
    /**
     * fill the constants.
     */
   
        
        
    public TestBillingAccount() {
String test = "CL_" + System.currentTimeMillis();
        
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
    }
    @Test
    public void billingAccountpage() throws InterruptedException {
        
        /**
         * Client details
         */
        BillingAccountPage billingAccountpage = PageFactory.initElements(this.getDriver(),
            BillingAccountPage.class);
        
        
        billingAccountpage.billingAccountTest(driver, data);
        
    }
    
    /**
     * Check updated  fields.
     * 
     * @param page compte client
     */
    private void testData(BillingAccountPage page) {
        String code = page.getAccountBA().getAttribute(ATTRIBUTE_VALUE);
        String description = page.getCompteId().getAttribute(ATTRIBUTE_VALUE);
        
        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));
        
    }
    
    
}
