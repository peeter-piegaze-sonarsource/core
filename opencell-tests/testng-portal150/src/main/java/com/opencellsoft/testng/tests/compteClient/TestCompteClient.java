package com.opencellsoft.testng.tests.compteClient;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.compteClient.CompteClientPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestCompteClient extends  TestBase {
    /**
     * generate values.
     */
    public TestCompteClient() {
        
    }
    @Test
    public void clientClientdetails() throws InterruptedException {
        
        /**
         * Client details
         */
        CompteClientPage compteclient = PageFactory.initElements(this.getDriver(),
            CompteClientPage.class);
        
        
        compteclient.clientDetailsTest(driver, data);
        
    }
    
    
    
}
