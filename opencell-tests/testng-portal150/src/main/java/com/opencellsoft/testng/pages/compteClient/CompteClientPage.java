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
   
    
    public CompteClientPage(WebDriver driver) {
        super(driver);
    }
    
    
    
    public void clientDetailsTest(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
       
        
        forceClick(searchButton);
        searchButton.sendKeys("96561512225997892");
        forceClick(selectClient);
        forceClick(selectCompteClient);
    }
    
   
  
    
  
   
}
