package com.opencellsoft.testng.tests.clients;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.clients.ClientPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestClient extends  TestBase {
    /**
     * generate values.
     */
    public TestClient() {
        
    }
    @Test
    public void clientdetails() throws InterruptedException {
        
        /**
         * Client details
         */
        ClientPage paginationEtTriTest = PageFactory.initElements(this.getDriver(),
            ClientPage.class);
        
        
        paginationEtTriTest.clientDetailsTest(driver, data);
        
    }
    
    
    
}
