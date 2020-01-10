package com.opencellsoft.testng.tests.promotions;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import com.opencellsoft.testng.pages.promotions.PromotionsPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestPromotions extends TestBase {
    @Test
    public void promotions() throws InterruptedException {
        
        /**
         * Promotions details
         */
        PromotionsPage promotionsPage = PageFactory.initElements(this.getDriver(),
            PromotionsPage.class);
        
        promotionsPage.promotionsDetails(driver, data);
        testData(promotionsPage);
    }
    
    private void testData(PromotionsPage promotionsPage) {
        promotionsPage.getOffreEligible().isSelected();
    }
}
