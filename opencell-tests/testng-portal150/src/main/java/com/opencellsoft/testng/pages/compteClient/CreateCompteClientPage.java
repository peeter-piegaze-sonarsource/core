package com.opencellsoft.testng.pages.compteClient;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class CreateCompteClientPage extends BasePage {
    
   
    
    
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div[2]/div/div[1]/div/div/div/div[2]/div/a/span[1]/div")
    private WebElement createButton ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[1]/div[1]/div/input")
    private WebElement compteClient ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div[1]/div[2]/div/input")
    private WebElement idCompteClient ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[3]/span[1]/span/span")
    private WebElement diversTab ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div/div[1]/div/div/div")
    private WebElement deviseList ;
    @FindBy(xpath = "/html/body/div[2]/div[2]/ul/li[1]")
    private WebElement selectDevise ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div/div[2]/div/div/div")
    private WebElement langueList ;
    @FindBy(xpath = "/html/body/div[2]/div[2]/ul/li[2]")
    private WebElement langueSelect ; 
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div/div[3]/div/div/div")
    private WebElement categorieCreditList ;
    @FindBy(xpath = "/html/body/div[2]/div[2]/ul/li[5]")
    private WebElement selectCategorieCredit  ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button/span[1]")
    private WebElement  enregistrerButton;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[5]/td[2]/span")
    private WebElement selectClient ;
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div[2]/div/div[1]/div/div/div/div[3]/div/table/tbody/tr/td[1]/span")
    private WebElement selectCompteClient ;
    
    
    public CreateCompteClientPage(WebDriver driver) {
        super(driver);
    }
    
    
    
    public void clientDetailsTest(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
       
        
       // forceClick(searchButton);
       // searchButton.sendKeys("96561512225997892");
        forceClick(selectClient);
       // forceClick(selectCompteClient);
        forceClick(createButton);
        compteClient.click();
        compteClient.clear();
        compteClient.sendKeys((String) data.get(Constants.CODE));
        idCompteClient.click();
        idCompteClient.clear();
        idCompteClient.sendKeys((String) data.get(Constants.DESCRIPTION));
       
        forceClick(diversTab);
        forceClick(deviseList);
        forceClick(selectDevise);
        forceClick(langueList);
        forceClick(langueSelect);
        forceClick(categorieCreditList);
      
        selectCategorieCredit.click();
        forceClick(enregistrerButton);
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




    public WebElement getCompteClient() {
        return compteClient;
    }



    public void setCompteClient(WebElement compteClient) {
        this.compteClient = compteClient;
    }



    public WebElement getIdCompteClient() {
        return idCompteClient;
    }



    public void setIdCompteClient(WebElement idCompteClient) {
        this.idCompteClient = idCompteClient;
    }



   
   
  
    
  
   
}
