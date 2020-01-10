package com.opencellsoft.testng.tests.souscriptions;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.souscriptions.SouscriptionsPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestSouscriptions extends TestBase {
    @Test
    public void souscriptions() throws InterruptedException {
        
        /**
         * souscription details
         */
        SouscriptionsPage souscriptionsPage = PageFactory.initElements(this.getDriver(),
            SouscriptionsPage.class);
        
        souscriptionsPage.souscriptionsDetails(driver, data);
        testData(souscriptionsPage);
    }
    
    private void testData(SouscriptionsPage souscriptionsPage) {
        souscriptionsPage.getSouscripteurTab().isSelected();
    }
}
