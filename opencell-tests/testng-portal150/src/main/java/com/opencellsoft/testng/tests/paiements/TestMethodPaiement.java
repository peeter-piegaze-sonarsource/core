package com.opencellsoft.testng.tests.paiements;

import static org.testng.Assert.assertEquals;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.paiements.MethodePaiementPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestMethodPaiement extends TestBase {
    /**
     * generate values.
     */
    public TestMethodPaiement() {
            String test = "P" + System.currentTimeMillis();
            data.put(Constants.CODE, test);
        
    }
    
    @Test
    public void methodePaiement() throws InterruptedException {
        
        /**
         * Client details
         */
        MethodePaiementPage methodePaiementPage = PageFactory.initElements(this.getDriver(),
            MethodePaiementPage.class);
        
        methodePaiementPage.clientDetails(driver, data);
        methodePaiementPage.paiementMethodDetails(driver, data);
        testData(methodePaiementPage);
        methodePaiementPage.paiementMethodSave(driver, data);
        
    }
    
    private void testData(MethodePaiementPage methodePaiementPage) {
        String alias = methodePaiementPage.getAlias().getAttribute(ATTRIBUTE_VALUE);
        assertEquals(alias, "96561512225997892");
    }
    
}
