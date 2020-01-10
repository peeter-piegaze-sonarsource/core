package com.opencellsoft.testng.pages.promotions;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;

public class PromotionsPage extends BasePage {
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[1]/div/div/div/div[2]/a[4]")
    private WebElement promotionsMenu;
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[1]/td[2]/span")
    private WebElement promotionElement;
    
    @FindBy(xpath="/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[2]/span[1]/span")
    private WebElement offreEligible;
    public PromotionsPage(WebDriver driver) {
        super(driver);
    }
    public void promotionsDetails(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        
        moveMouseAndClick(promotionsMenu);
        moveMouseAndClick(promotionElement);
        moveMouseAndClick(offreEligible);
    }
    /**
     * @return the promotionsMenu
     */
    public WebElement getPromotionsMenu() {
        return promotionsMenu;
    }
    /**
     * @param promotionsMenu the promotionsMenu to set
     */
    public void setPromotionsMenu(WebElement promotionsMenu) {
        this.promotionsMenu = promotionsMenu;
    }
    /**
     * @return the promotionElement
     */
    public WebElement getPromotionElement() {
        return promotionElement;
    }
    /**
     * @param promotionElement the promotionElement to set
     */
    public void setPromotionElement(WebElement promotionElement) {
        this.promotionElement = promotionElement;
    }
    /**
     * @return the offreEligible
     */
    public WebElement getOffreEligible() {
        return offreEligible;
    }
    /**
     * @param offreEligible the offreEligible to set
     */
    public void setOffreEligible(WebElement offreEligible) {
        this.offreEligible = offreEligible;
    }
    
}
