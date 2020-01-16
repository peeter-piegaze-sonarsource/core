package com.opencellsoft.testng.pages.souscriptions;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.opencellsoft.testng.pages.BasePage;

public class PointAccesPage extends BasePage {

    @FindBy(xpath = "/html/body/div/div/div/div/main/div[1]/div/div/div/div[2]/a[2]")
    private WebElement souscriptionMenu;
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[3]/div/table/tbody/tr[1]/td[2]/span")
    private WebElement souscriptionElement;
    
    @FindBy(xpath = "/html/body/div/div/div/div/main/div[2]/div/div[3]/div[1]/div/div[1]/div/div/div/div[2]/div/button/span[1]/div")
    private WebElement newointAccesBtn;
    
    @FindBy(xpath = "/html/body/div[2]/div[2]/div[3]/div[1]/div/div/input")
    private WebElement compteID;
    
    @FindBy(xpath = "/html/body/div[2]/div[2]/div[3]/div[2]/div/div/input")
    private WebElement dateDebutInput;
    
    @FindBy(xpath = "/html/body/div[3]/div[2]/div[1]/div[3]/div/div[3]/div[2]/button/span[1]")
    private WebElement dateDebut;
    
    @FindBy(xpath = "/html/body/div[3]/div[2]/div[2]/button[3]/span[1]")
    private WebElement dateDebutConfirm;
    
    @FindBy(xpath = "/html/body/div[2]/div[2]/div[3]/div[3]/div/div/input")
    private WebElement dateFinInput;
    
    @FindBy(xpath = "/html/body/div[3]/div[2]/div[1]/div[3]/div/div[4]/div[4]/button/span[1]")
    private WebElement dateFin;
    
    @FindBy(xpath = "/html/body/div[3]/div[2]/div[2]/button[3]/span[1]")
    private WebElement dateFinConfirm;
    
    @FindBy(xpath = "/html/body/div[2]/div[2]/div[4]/button[2]/span[1]")
    private WebElement confirmAccessPoint;

    public PointAccesPage(WebDriver driver) {
        super(driver);    }
    
    public void accessPointDetails(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        
        moveMouseAndClick(souscriptionMenu);
        moveMouseAndClick(souscriptionElement);
        moveMouseAndClick(newointAccesBtn);
        moveMouseAndClick(compteID);
        compteID.clear();
        compteID.sendKeys("96561512225997892");
        moveMouseAndClick(dateDebutInput);
        moveMouseAndClick(dateDebut);
        moveMouseAndClick(dateDebutConfirm);
        moveMouseAndClick(dateFinInput);
        moveMouseAndClick(dateFin);
        moveMouseAndClick(dateFinConfirm);}
    public void saveAccesPoint(WebDriver driver, Map<String, String> data)
            throws InterruptedException {
        moveMouseAndClick(confirmAccessPoint);
    }

    /**
     * @return the souscriptionMenu
     */
    public WebElement getSouscriptionMenu() {
        return souscriptionMenu;
    }

    /**
     * @param souscriptionMenu the souscriptionMenu to set
     */
    public void setSouscriptionMenu(WebElement souscriptionMenu) {
        this.souscriptionMenu = souscriptionMenu;
    }

    /**
     * @return the souscriptionElement
     */
    public WebElement getSouscriptionElement() {
        return souscriptionElement;
    }

    /**
     * @param souscriptionElement the souscriptionElement to set
     */
    public void setSouscriptionElement(WebElement souscriptionElement) {
        this.souscriptionElement = souscriptionElement;
    }

    /**
     * @return the newointAccesBtn
     */
    public WebElement getNewointAccesBtn() {
        return newointAccesBtn;
    }

    /**
     * @param newointAccesBtn the newointAccesBtn to set
     */
    public void setNewointAccesBtn(WebElement newointAccesBtn) {
        this.newointAccesBtn = newointAccesBtn;
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
     * @return the dateDebutInput
     */
    public WebElement getDateDebutInput() {
        return dateDebutInput;
    }

    /**
     * @param dateDebutInput the dateDebutInput to set
     */
    public void setDateDebutInput(WebElement dateDebutInput) {
        this.dateDebutInput = dateDebutInput;
    }

    /**
     * @return the dateDebut
     */
    public WebElement getDateDebut() {
        return dateDebut;
    }

    /**
     * @param dateDebut the dateDebut to set
     */
    public void setDateDebut(WebElement dateDebut) {
        this.dateDebut = dateDebut;
    }

    /**
     * @return the dateDebutConfirm
     */
    public WebElement getDateDebutConfirm() {
        return dateDebutConfirm;
    }

    /**
     * @param dateDebutConfirm the dateDebutConfirm to set
     */
    public void setDateDebutConfirm(WebElement dateDebutConfirm) {
        this.dateDebutConfirm = dateDebutConfirm;
    }

    /**
     * @return the dateFinInput
     */
    public WebElement getDateFinInput() {
        return dateFinInput;
    }

    /**
     * @param dateFinInput the dateFinInput to set
     */
    public void setDateFinInput(WebElement dateFinInput) {
        this.dateFinInput = dateFinInput;
    }

    /**
     * @return the dateFin
     */
    public WebElement getDateFin() {
        return dateFin;
    }

    /**
     * @param dateFin the dateFin to set
     */
    public void setDateFin(WebElement dateFin) {
        this.dateFin = dateFin;
    }

    /**
     * @return the dateFinConfirm
     */
    public WebElement getDateFinConfirm() {
        return dateFinConfirm;
    }

    /**
     * @param dateFinConfirm the dateFinConfirm to set
     */
    public void setDateFinConfirm(WebElement dateFinConfirm) {
        this.dateFinConfirm = dateFinConfirm;
    }

    /**
     * @return the confirmAccessPoint
     */
    public WebElement getConfirmAccessPoint() {
        return confirmAccessPoint;
    }

    /**
     * @param confirmAccessPoint the confirmAccessPoint to set
     */
    public void setConfirmAccessPoint(WebElement confirmAccessPoint) {
        this.confirmAccessPoint = confirmAccessPoint;
    }
}
