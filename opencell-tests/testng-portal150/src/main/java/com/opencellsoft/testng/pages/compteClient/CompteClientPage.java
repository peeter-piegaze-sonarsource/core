package com.opencellsoft.testng.pages.compteClient;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class CompteClientPage extends BasePage {
    
   
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[1]/div[1]/div[1]/div/input")
    private WebElement searchButton ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[5]/td[2]/span")
    private WebElement selectClient ;
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div[2]/div/div[1]/div/div/div/div[3]/div/table/tbody/tr/td[1]/span")
    private WebElement selectCompteClient ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[2]/div[3]/div/input")
    private WebElement nomCompteClient ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[1]/div[1]/div/input")
    private WebElement compteClient ;   
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[2]/div[2]/div/input")
    private WebElement prenomCompteClient ;
    
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button[1]/span[1]")
    private WebElement saveButton ;
    
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div[2]/div/div[1]/div/div/div/div[2]/div/a/span[1]/div")
    private WebElement createButton ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[1]/div[2]/div/input")
    private WebElement idCompteClient ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[3]/span[1]/span/span")
    private WebElement diversTab ;
    public CompteClientPage(WebDriver driver) {
        super(driver);
    }
    
    
    
    public void clientDetailsTest(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
       
        
       // forceClick(searchButton);
       // searchButton.sendKeys("96561512225997892");
        forceClick(selectClient);
        forceClick(selectCompteClient);
        compteClient.click();
        compteClient.clear();
        compteClient.sendKeys((String) data.get(Constants.CODE));
        prenomCompteClient.click();
        prenomCompteClient.clear();
        prenomCompteClient.sendKeys((String) data.get(Constants.DESCRIPTION));
        nomCompteClient.click();
        nomCompteClient.clear();
        nomCompteClient.sendKeys((String) data.get(Constants.DESCRIPTION));
        forceClick(saveButton);
        forceClick(selectCompteClient);
    }
   

    public WebElement getSearchButton() {
        return searchButton;
    }



    public void setSearchButton(WebElement searchButton) {
        this.searchButton = searchButton;
    }



    public WebElement getSelectClient() {
        return selectClient;
    }



    public void setSelectClient(WebElement selectClient) {
        this.selectClient = selectClient;
    }



    public WebElement getSelectCompteClient() {
        return selectCompteClient;
    }



    public void setSelectCompteClient(WebElement selectCompteClient) {
        this.selectCompteClient = selectCompteClient;
    }



    public WebElement getNomCompteClient() {
        return nomCompteClient;
    }



    public void setNomCompteClient(WebElement nomCompteClient) {
        this.nomCompteClient = nomCompteClient;
    }



    public WebElement getCompteClient() {
        return compteClient;
    }



    public void setCompteClient(WebElement compteClient) {
        this.compteClient = compteClient;
    }



    public WebElement getPrenomCompteClient() {
        return prenomCompteClient;
    }



    public void setPrenomCompteClient(WebElement prenomCompteClient) {
        this.prenomCompteClient = prenomCompteClient;
    }
    
   
  
    
  
   
}
