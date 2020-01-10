package com.opencellsoft.testng.tests.compteClient;
import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.Constants;
import com.opencellsoft.testng.pages.compteClient.CreateCompteClientPage;

import com.opencellsoft.testng.tests.base.TestBase;

public class TestCreateCompteClient extends  TestBase {
    /**
     * generate values.
     */
    
    /**
     * fill the constants.
     */
   
        
        
    public TestCreateCompteClient() {
String test = "CL_" + System.currentTimeMillis();
        
        data.put(Constants.CODE, test);
        data.put(Constants.DESCRIPTION, test);
    }
    @Test
    public void clientdetails() throws InterruptedException {
        
        /**
         * Client details
         */
        CreateCompteClientPage createCompteclient = PageFactory.initElements(this.getDriver(),
            CreateCompteClientPage.class);
        
        
        createCompteclient.clientDetailsTest(driver, data);
        
    }
    
    /**
     * Check updated  fields.
     * 
     * @param page compte client
     */
    private void testData(CreateCompteClientPage page) {
        String code = page.getCompteClient().getAttribute(ATTRIBUTE_VALUE);
        String description = page.getIdCompteClient().getAttribute(ATTRIBUTE_VALUE);
        
        assertEquals(code, data.get(Constants.CODE));
        assertEquals(description, data.get(Constants.DESCRIPTION));
        
    }
    
    
}
