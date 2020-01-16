package com.opencellsoft.testng.pages.clients;

import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;

public class AfficherToutesSouscriptionPage extends BasePage {
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[1]/div/div/div/div[2]/a[1]")
    private WebElement clientMenu;
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[1]/div[1]/div[1]/div/input")
    private WebElement client;
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr/td[2]/span")
    private WebElement compteID;
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div[2]/div/div[1]/div/div/div/div[3]/div/table/tbody/tr/td[1]/span")
    private WebElement compteClientID;
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[3]/div[3]/div/div[1]/div/div/div[2]/div[3]/div/table/tbody/tr/td/span")
    private WebElement compteFactID;
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[3]/div/div/div[1]/div/div/div[2]/div[2]/div/a/span[1]/div")
    private WebElement afficherSouscriptionBtn;
    
    public AfficherToutesSouscriptionPage(WebDriver driver) {
        super(driver);
    }
    
    public void afficherToutesSouscritpions(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(clientMenu);
        moveMouseAndClick(client);
        client.sendKeys("63021508253829808");
        moveMouseAndClick(compteID);
        moveMouseAndClick(compteClientID);
        moveMouseAndClick(compteFactID);
        moveMouseAndClick(afficherSouscriptionBtn);
    }
    
    /**
     * @return the clientMenu
     */
    public WebElement getClientMenu() {
        return clientMenu;
    }
    
    /**
     * @param clientMenu the clientMenu to set
     */
    public void setClientMenu(WebElement clientMenu) {
        this.clientMenu = clientMenu;
    }
    
    /**
     * @return the client
     */
    public WebElement getClient() {
        return client;
    }
    
    /**
     * @param client the client to set
     */
    public void setClient(WebElement client) {
        this.client = client;
    }
    
    /**
     * @return the compteID
     */
    public WebElement getCompteID() {
        return compteID;
    }
    
    /**
     * @param compteID the compteID to set
     */
    public void setCompteID(WebElement compteID) {
        this.compteID = compteID;
    }
    
    /**
     * @return the compteClientID
     */
    public WebElement getCompteClientID() {
        return compteClientID;
    }
    
    /**
     * @param compteClientID the compteClientID to set
     */
    public void setCompteClientID(WebElement compteClientID) {
        this.compteClientID = compteClientID;
    }
    
    /**
     * @return the compteFactID
     */
    public WebElement getCompteFactID() {
        return compteFactID;
    }
    
    /**
     * @param compteFactID the compteFactID to set
     */
    public void setCompteFactID(WebElement compteFactID) {
        this.compteFactID = compteFactID;
    }
    
    /**
     * @return the afficherSouscriptionBtn
     */
    public WebElement getAfficherSouscriptionBtn() {
        return afficherSouscriptionBtn;
    }
    
    /**
     * @param afficherSouscriptionBtn the afficherSouscriptionBtn to set
     */
    public void setAfficherSouscriptionBtn(WebElement afficherSouscriptionBtn) {
        this.afficherSouscriptionBtn = afficherSouscriptionBtn;
    }
    
}