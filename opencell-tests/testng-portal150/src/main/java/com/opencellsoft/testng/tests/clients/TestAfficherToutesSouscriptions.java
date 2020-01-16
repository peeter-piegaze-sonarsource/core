package com.opencellsoft.testng.tests.clients;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.clients.AfficherToutesSouscriptionPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestAfficherToutesSouscriptions extends TestBase {
    @Test
    public void sousctiptions() throws InterruptedException {
           
        AfficherToutesSouscriptionPage afficherToutesSouscriptionPage = PageFactory.initElements(this.getDriver(),
            AfficherToutesSouscriptionPage.class);
        
        afficherToutesSouscriptionPage.afficherToutesSouscritpions(driver, data);
//To be used when the next GUI is OK  

        //testData(afficherToutesSouscriptionPage);
    }
    
    private void testData(AfficherToutesSouscriptionPage afficherToutesSouscriptionPage) {
        afficherToutesSouscriptionPage.getAfficherSouscriptionBtn().isSelected();
    }
}

