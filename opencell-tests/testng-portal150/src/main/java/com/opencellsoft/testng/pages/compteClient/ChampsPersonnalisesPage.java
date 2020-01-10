package com.opencellsoft.testng.pages.compteClient;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class ChampsPersonnalisesPage extends BasePage {
    
   
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[3]/div[1]/div/div/div[1]/div[2]/div[2]/div/button[2]/span[1]/span")
    private WebElement authentificationButton ;
   
    
    public ChampsPersonnalisesPage(WebDriver driver) {
        super(driver);
    }
    
    
    
    public void clientDetailsTest(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
       
        
               forceClick(authentificationButton);
        
    }



    public WebElement getAuthentificationButton() {
        return authentificationButton;
    }



    public void setAuthentificationButton(WebElement authentificationButton) {
        this.authentificationButton = authentificationButton;
    }
   

  
  
   
}
