package com.opencellsoft.testng.pages.paiements;

import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class GestionCompteClientPage extends BasePage {
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[3]/div[2]/div/div[1]/div/div/div/div[2]/div/a/span[1]/div")
    private WebElement gestionCompteEtPaiements;
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[3]/div[2]/div/div[1]/div/div/div/div[2]/div/a/span[1]/div")
    private WebElement gererLeCompteEtLesPaiements;
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div[1]/div[1]/div/input")
    private WebElement categorieDoperation;
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div[1]/div[2]/div/input")
    private WebElement description ;
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div[2]/div[1]/div/input")
    private WebElement code ;
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div[2]/div[2]/div/input")
    private WebElement dateEffet ;
    @FindBy(xpath = "/html/body/div[2]/div[2]/div[1]/div[3]/div/div[2]/div[5]/button/span[1]")
    private WebElement date ;
    @FindBy(xpath = "/html/body/div[2]/div[2]/div[2]/button[3]/span[1]")
    private WebElement confirmDate ;
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[2]/span/div/div[2]/div[3]/div/input")
    private WebElement montant ;
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/form/div[3]/div/button/span[1]")
    private WebElement saveBtn ;
   
    
    public GestionCompteClientPage(WebDriver driver) {
        super(driver);
    }
    
    
    
    public void gestionCompteClientCreate(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
       
        moveMouseAndClick(gestionCompteEtPaiements);
        moveMouseAndClick(gererLeCompteEtLesPaiements);
        moveMouseAndClick(categorieDoperation);
        categorieDoperation.clear();
        categorieDoperation.sendKeys("CREDIT");
        moveMouseAndClick(description);
        description.clear();
        description.sendKeys("description");
        moveMouseAndClick(code);
        code.clear();
        code.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(dateEffet);
        moveMouseAndClick(date);
        moveMouseAndClick(confirmDate);
        moveMouseAndClick(montant);
        montant.clear();
        montant.sendKeys("1236");
        

    }
    public void gestionCompteClientSave(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(saveBtn);
    }

    /**
     * @return the gestionCompteEtPaiements
     */
    public WebElement getGestionCompteEtPaiements() {
        return gestionCompteEtPaiements;
    }



    /**
     * @param gestionCompteEtPaiements the gestionCompteEtPaiements to set
     */
    public void setGestionCompteEtPaiements(WebElement gestionCompteEtPaiements) {
        this.gestionCompteEtPaiements = gestionCompteEtPaiements;
    }



    /**
     * @return the gererLeCompteEtLesPaiements
     */
    public WebElement getGererLeCompteEtLesPaiements() {
        return gererLeCompteEtLesPaiements;
    }



    /**
     * @param gererLeCompteEtLesPaiements the gererLeCompteEtLesPaiements to set
     */
    public void setGererLeCompteEtLesPaiements(WebElement gererLeCompteEtLesPaiements) {
        this.gererLeCompteEtLesPaiements = gererLeCompteEtLesPaiements;
    }



    /**
     * @return the categorieDoperation
     */
    public WebElement getCategorieDoperation() {
        return categorieDoperation;
    }



    /**
     * @param categorieDoperation the categorieDoperation to set
     */
    public void setCategorieDoperation(WebElement categorieDoperation) {
        this.categorieDoperation = categorieDoperation;
    }



    /**
     * @return the description
     */
    public WebElement getDescription() {
        return description;
    }



    /**
     * @param description the description to set
     */
    public void setDescription(WebElement description) {
        this.description = description;
    }



    /**
     * @return the code
     */
    public WebElement getCode() {
        return code;
    }



    /**
     * @param code the code to set
     */
    public void setCode(WebElement code) {
        this.code = code;
    }



    /**
     * @return the dateEffet
     */
    public WebElement getDateEffet() {
        return dateEffet;
    }



    /**
     * @param dateEffet the dateEffet to set
     */
    public void setDateEffet(WebElement dateEffet) {
        this.dateEffet = dateEffet;
    }



    /**
     * @return the montant
     */
    public WebElement getMontant() {
        return montant;
    }



    /**
     * @param montant the montant to set
     */
    public void setMontant(WebElement montant) {
        this.montant = montant;
    }



    /**
     * @return the saveBtn
     */
    public WebElement getSaveBtn() {
        return saveBtn;
    }



    /**
     * @param saveBtn the saveBtn to set
     */
    public void setSaveBtn(WebElement saveBtn) {
        this.saveBtn = saveBtn;
    }
   
}
