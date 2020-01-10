package com.opencellsoft.testng.pages.souscriptions;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;

public class SouscriptionsPage extends BasePage {
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[1]/div/div/div/div[2]/a[2]")
    private WebElement souscriptionMenu;
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[1]/td[2]/span")
    private WebElement souscriptionElement;
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[2]/span[1]/span/span")
    private WebElement souscripteurTab;
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[3]/div[2]/div/div[1]/div/div/div[3]/div[2]/div/table/tbody/tr/td[2]/span")
    private WebElement serviceInstance;
    
    @FindBy(xpath = "/html/body/div[2]/div[2]/div[3]/div[1]/button[1]/span[1]")
    private WebElement annulerBtn;
    
    public SouscriptionsPage(WebDriver driver) {
        super(driver);
    }
    
    public void souscriptionsDetails(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        
        moveMouseAndClick(souscriptionMenu);
        moveMouseAndClick(souscriptionElement);
        moveMouseAndClick(souscripteurTab);
        moveMouseAndClick(serviceInstance);
        moveMouseAndClick(annulerBtn);
    }
    
    /**
     * @return the souscriptionMenu
     */
    public WebElement getSouscriptionMenu() {
        return souscriptionMenu;
    }
    
    /**
     * @param souscriptionMenu the souscriptionMenu to set
     */
    public void setSouscriptionMenu(WebElement souscriptionMenu) {
        this.souscriptionMenu = souscriptionMenu;
    }
    
    /**
     * @return the souscriptionElement
     */
    public WebElement getSouscriptionElement() {
        return souscriptionElement;
    }
    
    /**
     * @param souscriptionElement the souscriptionElement to set
     */
    public void setSouscriptionElement(WebElement souscriptionElement) {
        this.souscriptionElement = souscriptionElement;
    }
    
    /**
     * @return the souscripteurTab
     */
    public WebElement getSouscripteurTab() {
        return souscripteurTab;
    }
    
    /**
     * @param souscripteurTab the souscripteurTab to set
     */
    public void setSouscripteurTab(WebElement souscripteurTab) {
        this.souscripteurTab = souscripteurTab;
    }
    
    /**
     * @return the serviceInstance
     */
    public WebElement getServiceInstance() {
        return serviceInstance;
    }
    
    /**
     * @param serviceInstance the serviceInstance to set
     */
    public void setServiceInstance(WebElement serviceInstance) {
        this.serviceInstance = serviceInstance;
    }
    
    /**
     * @return the annulerBtn
     */
    public WebElement getAnnulerBtn() {
        return annulerBtn;
    }
    
    /**
     * @param annulerBtn the annulerBtn to set
     */
    public void setAnnulerBtn(WebElement annulerBtn) {
        this.annulerBtn = annulerBtn;
    }
}
