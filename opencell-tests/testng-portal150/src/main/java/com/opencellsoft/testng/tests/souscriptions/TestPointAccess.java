package com.opencellsoft.testng.tests.souscriptions;
import static org.testng.Assert.assertEquals;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.souscriptions.PointAccesPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestPointAccess extends TestBase {
    @Test
    public void pointAccess() throws InterruptedException {
           
        PointAccesPage pointAccesPage = PageFactory.initElements(this.getDriver(),
            PointAccesPage.class);
        
        pointAccesPage.accessPointDetails(driver, data);
        testData(pointAccesPage);
        pointAccesPage.saveAccesPoint(driver, data);
    }
    
    private void testData(PointAccesPage pointAccesPage) {
        String compteID = pointAccesPage.getCompteID().getAttribute(ATTRIBUTE_VALUE);

        assertEquals(compteID, "96561512225997892");
    }
}
