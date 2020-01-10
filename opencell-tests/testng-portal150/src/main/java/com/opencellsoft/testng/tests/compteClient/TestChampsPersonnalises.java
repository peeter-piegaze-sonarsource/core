package com.opencellsoft.testng.tests.compteClient;
import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.compteClient.ChampsPersonnalisesPage;

import com.opencellsoft.testng.tests.base.TestBase;

public class TestChampsPersonnalises extends  TestBase {
    /**
     * generate values.
     */
    
    /**
     * fill the constants.
     */
   
        
        
    public TestChampsPersonnalises() {

    }
    @Test
    public void champsPersonnalisesTabs() throws InterruptedException {
        
        /**
         * champs Personnalisés  details
         */
        ChampsPersonnalisesPage champsPersonnalisesdetails = PageFactory.initElements(this.getDriver(),
            ChampsPersonnalisesPage.class);
        
        
        champsPersonnalisesdetails.clientDetailsTest(driver, data);
        
    }
    
  
    
    
}
