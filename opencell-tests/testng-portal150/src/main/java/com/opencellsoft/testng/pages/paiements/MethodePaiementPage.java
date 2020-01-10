package com.opencellsoft.testng.pages.paiements;

import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;

public class MethodePaiementPage extends BasePage {
    public MethodePaiementPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[3]/div[4]/div/div[1]/div/div/div[1]/div/button/span[1]/div")
    private WebElement newPM;
    
    @FindBy(xpath = "/html/body/div[2]/div[2]/div[3]/div[1]/div/div/div/div")
    private WebElement methodeDePaiementList;
    
    @FindBy(xpath = "/html/body/div[3]/div[2]/ul/li[3]")
    private WebElement paymentMethodVirement;
    
    @FindBy(xpath = "/html/body/div[2]/div[2]/div[3]/div[2]/div/div/input")
    private WebElement alias;
    
    @FindBy(xpath = "/html/body/div[2]/div[2]/div[4]/button[2]/span[1]")
    private WebElement confirmBtn;
    ////////////////////////// client page ///////////
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[1]/div/div/div/div[2]/a[1]")
    private WebElement compteClientList;
    @FindBy(xpath ="/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[1]/td[2]/span")
    private WebElement compteClient;
    @FindBy(xpath ="/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div[2]/div/div[1]/div/div/div/div[3]/div/table/tbody/tr[2]/td[1]/span")
    private WebElement compte;

    ////////////////////////////////////
    
    public void clientDetails(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        ////////// Client Page /////////////
        moveMouseAndClick(compteClientList);
        moveMouseAndClick(compteClient);
        moveMouseAndClick(compte);
    }
    
    public void paiementMethodDetails(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(newPM);
        moveMouseAndClick(methodeDePaiementList);
        moveMouseAndClick(paymentMethodVirement);
        moveMouseAndClick(alias);
        alias.sendKeys("96561512225997892");
    }
    
    public void paiementMethodSave(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(confirmBtn);
        
    }

    /**
     * @return the newPM
     */
    public WebElement getNewPM() {
        return newPM;
    }

    /**
     * @param newPM the newPM to set
     */
    public void setNewPM(WebElement newPM) {
        this.newPM = newPM;
    }

    /**
     * @return the methodeDePaiementList
     */
    public WebElement getMethodeDePaiementList() {
        return methodeDePaiementList;
    }

    /**
     * @param methodeDePaiementList the methodeDePaiementList to set
     */
    public void setMethodeDePaiementList(WebElement methodeDePaiementList) {
        this.methodeDePaiementList = methodeDePaiementList;
    }

    /**
     * @return the paymentMethodVirement
     */
    public WebElement getPaymentMethodVirement() {
        return paymentMethodVirement;
    }

    /**
     * @param paymentMethodVirement the paymentMethodVirement to set
     */
    public void setPaymentMethodVirement(WebElement paymentMethodVirement) {
        this.paymentMethodVirement = paymentMethodVirement;
    }

    /**
     * @return the alias
     */
    public WebElement getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(WebElement alias) {
        this.alias = alias;
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

}