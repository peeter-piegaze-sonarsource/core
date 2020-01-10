package com.opencellsoft.testng.pages.promotions;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;

public class MiseaJourPromotionsPage extends BasePage {

    public MiseaJourPromotionsPage(WebDriver driver) {
        super(driver);
    }
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[1]/div/div/div/div[2]/a[4]")
    private WebElement promotionsMenu;
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[1]/td[2]/span")
    private WebElement promotionElement;
    
    @FindBy(xpath="/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[3]/div[3]/div/input")
    private WebElement finValidite;
    
    @FindBy(xpath="/html/body/div[2]/div[2]/div[1]/div[3]/div/div[2]/div[6]/button/span[1]")
    private WebElement date;
    
    @FindBy(xpath="/html/body/div[2]/div[2]/div[2]/button[3]/span[1]")
    private WebElement confirmBtn;
    
    @FindBy(xpath="/html/body/div[2]/div[2]/ul/li[2]")
    private WebElement status;
    
    @FindBy(xpath="/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button[1]/span[1]")
    private WebElement saveBtn;
    
    @FindBy(xpath="/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[5]/div[2]/div/div/div")
    private WebElement statusList;
    public void promotionsDetails(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        
        moveMouseAndClick(promotionsMenu);
        moveMouseAndClick(promotionElement);
        moveMouseAndClick(finValidite);
        moveMouseAndClick(date);
        moveMouseAndClick(confirmBtn);
        moveMouseAndClick(statusList);
        moveMouseAndClick(status);}
    public void save(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(saveBtn);
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
     * @return the finValidite
     */
    public WebElement getFinValidite() {
        return finValidite;
    }
    /**
     * @param finValidite the finValidite to set
     */
    public void setFinValidite(WebElement finValidite) {
        this.finValidite = finValidite;
    }
    /**
     * @return the date
     */
    public WebElement getDate() {
        return date;
    }
    /**
     * @param date the date to set
     */
    public void setDate(WebElement date) {
        this.date = date;
    }
    /**
     * @return the confirmBtn
     */
    public WebElement getConfirmBtn() {
        return confirmBtn;
    }
    /**
     * @param confirmBtn the confirmBtn to set
     */
    public void setConfirmBtn(WebElement confirmBtn) {
        this.confirmBtn = confirmBtn;
    }
    /**
     * @return the status
     */
    public WebElement getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(WebElement status) {
        this.status = status;
    }
    /**
     * @return the saveBtn
     */
    public WebElement getSaveBtn() {
        return saveBtn;
    }
    /**
     * @param saveBtn the saveBtn to set
     */
    public void setSaveBtn(WebElement saveBtn) {
        this.saveBtn = saveBtn;
    }
    /**
     * @return the statusList
     */
    public WebElement getStatusList() {
        return statusList;
    }
    /**
     * @param statusList the statusList to set
     */
    public void setStatusList(WebElement statusList) {
        this.statusList = statusList;
    }
    
}
