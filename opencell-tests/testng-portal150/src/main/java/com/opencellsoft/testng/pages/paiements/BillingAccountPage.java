package com.opencellsoft.testng.pages.paiements;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class BillingAccountPage extends BasePage {
    
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[5]/td[2]/span")
    private WebElement selectClient ;
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div[2]/div/div[1]/div/div/div/div[3]/div/table/tbody/tr/td[1]/span")
    private WebElement selectCompteClient ;
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[3]/div[3]/div/div[1]/div/div/div[2]/div[2]/div/a/span[1]/div")
    private WebElement ajouterBAButton;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div/div[2]/div/input")
    private WebElement accountBA;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div/div[3]/div/input")
    private WebElement compteId;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div/div[4]/div/div/div")
    private WebElement titreList;
    @FindBy(xpath = "/html/body/div[2]/div[2]/ul/li[2]")
    private WebElement titreSelect ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[1]/div/div/div[6]/div/input")
    private WebElement nom ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[2]/span[1]/span/span")
    private WebElement adressTab ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[2]/div/div[1]/div[1]/div/input")
    private WebElement adress1 ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[2]/div/div[2]/div[1]/div/input")
    private WebElement  codePostal;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[2]/div/div[2]/div[2]/div/input")
    private WebElement  ville  ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[2]/div/div[2]/div[3]/div/div/div")
    private WebElement villeList ;
    
    @FindBy(xpath = "/html/body/div[2]/div[2]/ul/li[1]")
    private WebElement selectVille ;
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[2]/div/div[2]/div[3]/div/div/div")
    private WebElement listPays1 ;
    @FindBy(xpath = "/html/body/div[2]/div[2]/ul/li[1]")
    private WebElement selectPays2 ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[3]/span[1]/span/span")
    private WebElement diversTab ;
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div/div[1]/div/div/div")
    private WebElement cycledefacturationList ;
    @FindBy(xpath = "/html/body/div[2]/div[2]/ul/li[1]")
    private WebElement cycledefacturationSelect ;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div/div[2]/div/div/div")
    private WebElement  langueList;
    @FindBy(xpath = "/html/body/div[2]/div[2]/ul/li[2]")
    private WebElement  langueSelect;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div/div[3]/div/div/div")
    private WebElement  paysList;
    @FindBy(xpath = "/html/body/div[2]/div[2]/ul/li[1]")
    private WebElement  paysSelect;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span[3]/div/div/div[4]/div/div/div")
    private WebElement planderemiseList ;
    @FindBy(xpath = "/html/body/div[2]/div[2]/ul/li[1]")
    private WebElement  planderemiseSelect;
    @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button/span[1]")
    private WebElement  enregistrerButton;
      @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[1]/div/div/div/a[2]/span[1]/span/span")
    private WebElement adressTab2 ;
   
    
    public BillingAccountPage(WebDriver driver) {
        super(driver);
    }
    
    
    
    public void billingAccountTest(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        waitUntilElementDisplayed(selectClient, driver);
        forceClick(selectClient);
        waitUntilElementDisplayed(selectCompteClient, driver);
        forceClick(selectCompteClient);
        waitUntilElementDisplayed(ajouterBAButton, driver);
        ajouterBAButton.click();
        accountBA.click();
        accountBA.clear();
        accountBA.sendKeys((String) data.get(Constants.CODE));
        compteId.click();
        compteId.clear();
        compteId.sendKeys((String) data.get(Constants.DESCRIPTION));
        nom.click();
        nom.clear();
        nom.sendKeys((String) data.get(Constants.DESCRIPTION));
        
        forceClick(adressTab);
        
        adress1.click();
        adress1.clear();
        adress1.sendKeys((String) data.get(Constants.DESCRIPTION));
        forceClick(diversTab);
        waitUntilElementDisplayed(langueList, driver);
        forceClick(langueList);
        forceClick(langueSelect);
        forceClick(adressTab2);
       
        waitUntilElementDisplayed(ville, driver);
        ville.click();
        ville.clear();
        ville.sendKeys((String) data.get(Constants.DESCRIPTION));
        forceClick(diversTab);
        forceClick(paysList);
        forceClick(paysSelect);
        forceClick(adressTab);
        codePostal.click();
        codePostal.clear();
        codePostal.sendKeys("123") ;
        forceClick(diversTab);
        forceClick(planderemiseList);
        
        forceClick(planderemiseSelect);
        forceClick(adressTab);
         forceClick(villeList);
        forceClick(selectVille);
        forceClick(diversTab);
        forceClick(cycledefacturationList);
     
        forceClick(cycledefacturationSelect);
        
       

        forceClick(adressTab);
        forceClick(listPays1);
        forceClick(selectPays2);
        
        
         
        
        
        forceClick(enregistrerButton);
        
        
       
    }



    public WebElement getAccountBA() {
        return accountBA;
    }



    public void setAccountBA(WebElement accountBA) {
        this.accountBA = accountBA;
    }



    public WebElement getCompteId() {
        return compteId;
    }



    public void setCompteId(WebElement compteId) {
        this.compteId = compteId;
    }



    public WebElement getTitreList() {
        return titreList;
    }



    public void setTitreList(WebElement titreList) {
        this.titreList = titreList;
    }



    public WebElement getTitreSelect() {
        return titreSelect;
    }



    public void setTitreSelect(WebElement titreSelect) {
        this.titreSelect = titreSelect;
    }



    public WebElement getNom() {
        return nom;
    }



    public void setNom(WebElement nom) {
        this.nom = nom;
    }



    public WebElement getAdress1() {
        return adress1;
    }



    public void setAdress1(WebElement adress1) {
        this.adress1 = adress1;
    }



    public WebElement getCodePostal() {
        return codePostal;
    }



    public void setCodePostal(WebElement codePostal) {
        this.codePostal = codePostal;
    }



    public WebElement getVille() {
        return ville;
    }



    public void setVille(WebElement ville) {
        this.ville = ville;
    }



    public WebElement getVilleList() {
        return villeList;
    }



    public void setVilleList(WebElement villeList) {
        this.villeList = villeList;
    }



    public WebElement getSelectVille() {
        return selectVille;
    }



    public void setSelectVille(WebElement selectVille) {
        this.selectVille = selectVille;
    }



    public WebElement getCycledefacturationList() {
        return cycledefacturationList;
    }



    public void setCycledefacturationList(WebElement cycledefacturationList) {
        this.cycledefacturationList = cycledefacturationList;
    }



    public WebElement getCycledefacturationSelect() {
        return cycledefacturationSelect;
    }



    public void setCycledefacturationSelect(WebElement cycledefacturationSelect) {
        this.cycledefacturationSelect = cycledefacturationSelect;
    }



    public WebElement getLangueList() {
        return langueList;
    }



    public void setLangueList(WebElement langueList) {
        this.langueList = langueList;
    }



   

    public WebElement getPaysList() {
        return paysList;
    }



    public void setPaysList(WebElement paysList) {
        this.paysList = paysList;
    }



    public WebElement getPaysSelect() {
        return paysSelect;
    }



    public void setPaysSelect(WebElement paysSelect) {
        this.paysSelect = paysSelect;
    }



    public WebElement getPlanderemiseList() {
        return planderemiseList;
    }



    public void setPlanderemiseList(WebElement planderemiseList) {
        this.planderemiseList = planderemiseList;
    }



    public WebElement getPlanderemiseSelect() {
        return planderemiseSelect;
    }



    public void setPlanderemiseSelect(WebElement planderemiseSelect) {
        this.planderemiseSelect = planderemiseSelect;
    }



   
   
}
