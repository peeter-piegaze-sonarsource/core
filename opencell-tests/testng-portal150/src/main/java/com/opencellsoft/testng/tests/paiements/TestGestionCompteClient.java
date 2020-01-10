package com.opencellsoft.testng.tests.paiements;

import static org.testng.Assert.assertEquals;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.paiements.GestionCompteClientPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestGestionCompteClient extends TestBase {
    /**
     * generate values.
     */
    public TestGestionCompteClient() {
            String test = "P" + System.currentTimeMillis();
            data.put(Constants.CODE, test);
        
    }
    
    @Test
    public void gestionCompteClient() throws InterruptedException {
        
        /**
         * Client details
         */
        GestionCompteClientPage gestionCompteClientPage = PageFactory.initElements(this.getDriver(),
            GestionCompteClientPage.class);
        
        gestionCompteClientPage.gestionCompteClientCreate(driver, data);
        testData(gestionCompteClientPage);
        gestionCompteClientPage.gestionCompteClientSave(driver, data);
        
    }
    
    private void testData(GestionCompteClientPage gestionCompteClientPage) {
        String code = gestionCompteClientPage.getCode().getAttribute(ATTRIBUTE_VALUE);
        assertEquals(code, data.get(Constants.CODE));
    }
    
}
