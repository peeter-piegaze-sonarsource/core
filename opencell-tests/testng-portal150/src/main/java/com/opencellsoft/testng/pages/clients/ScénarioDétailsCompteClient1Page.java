package com.opencellsoft.testng.pages.clients;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class ScénarioDétailsCompteClient1Page extends BasePage{

    public ScénarioDétailsCompteClient1Page(WebDriver driver) {
        super(driver);
    }
   
    
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/span/div/div[3]/button[4]/span[1]")
    private WebElement SuivantButton;
    
    public void  fillCustomerAndSave( WebDriver driver,Map<String, String> data) throws InterruptedException {
       
    }
}
