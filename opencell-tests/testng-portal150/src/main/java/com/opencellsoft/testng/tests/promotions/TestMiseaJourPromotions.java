package com.opencellsoft.testng.tests.promotions;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.opencellsoft.testng.pages.promotions.MiseaJourPromotionsPage;
import com.opencellsoft.testng.tests.base.TestBase;

public class TestMiseaJourPromotions  extends TestBase {
    @Test
    public void miseaJourpromotions() throws InterruptedException {
        
        /**
         * mise a jour Promotions details
         */
        MiseaJourPromotionsPage MiseaJourPromotionsPage = PageFactory.initElements(this.getDriver(),
            MiseaJourPromotionsPage.class);
        
        MiseaJourPromotionsPage.promotionsDetails(driver, data);
        testData(MiseaJourPromotionsPage);
        MiseaJourPromotionsPage.save(driver, data);
    }
    private void testData(MiseaJourPromotionsPage miseaJourPromotionsPage) {
        miseaJourPromotionsPage.getSaveBtn().isSelected();
    }
}
