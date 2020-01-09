package com.opencellsoft.testng.pages.clients;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class ClientPage extends BasePage {
    
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/span/div/div[3]/button[4]/span[1]")
    private WebElement SuivantButton;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/span/div/div[3]/button[1]/span[1]")
    private WebElement precedentButton;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/span/div/div[2]/div/div")
    private WebElement paginationButton;
    @FindBy(xpath = "/html/body/div[2]/div[2]/ul/li[3]")
    private WebElement pagination25 ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div[1]/div[1]/div[1]/div/input")
    private WebElement searchButton ;
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[5]/td[2]/span")
    private WebElement selectClient ;
    
   
    
    public ClientPage(WebDriver driver) {
        super(driver);
    }
    
    
    
    public void clientDetailsTest(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
       
        forceClick(SuivantButton);
        forceClick(precedentButton);
        forceClick(paginationButton);
        forceClick(pagination25);
        forceClick(searchButton);
        searchButton.sendKeys("96561512225997892");

    }



    public WebElement getSuivantButton() {
        return SuivantButton;
    }



    public void setSuivantButton(WebElement suivantButton) {
        SuivantButton = suivantButton;
    }



    public WebElement getPrecedentButton() {
        return precedentButton;
    }



    public void setPrecedentButton(WebElement precedentButton) {
        this.precedentButton = precedentButton;
    }



    public WebElement getPaginationButton() {
        return paginationButton;
    }



    public void setPaginationButton(WebElement paginationButton) {
        this.paginationButton = paginationButton;
    }



    public WebElement getPagination25() {
        return pagination25;
    }



    public void setPagination25(WebElement pagination25) {
        this.pagination25 = pagination25;
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
    
   
  
    
  
   
}
