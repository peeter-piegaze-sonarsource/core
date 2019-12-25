package com.opencellsoft.testng.pages.offers;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * offer.
 * 
 * @author AIT BRAHIM Maria
 *
 */
public class Offers extends BasePage {
  public Offers(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

/**
   * button create.
   */
  @FindBy(xpath = "/html/body/div[1]/div/div/div/main/div[2]/div/div[2]/div[2]/div/div/div[2]/div/a/span[1]/span")
  private WebElement btnCreate;
  /**
   * new code label.
   */
  @FindBy(id = "moduleForm:code_txt")
  private WebElement codeOfferModels;
  /**
   * new description label.
   */
  @FindBy(id = "moduleForm:description")
  private WebElement descriptionOfferModel;
  /**
   * offer code.
   */
  @FindBy(id = "moduleForm:offerSelectId_selectLink")
  private WebElement offerCode;

  /**
   * constructor.
   * 
   * @param driver WebDriver
   */
  public OfferModels(final WebDriver driver) {
    super(driver);
  }

  /**
   * Opening offer model menu.
   * 
   * @param driver WebDriver
   */
  public void gotoListPage(WebDriver driver) {
    WebElement offerMenu = driver.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div[1]/div/div/div/div[2]/a[2]"));
    moveMouseAndClick(offerMenu);
    
  }

  /**
   * entering data.
   * 
   * @param driver WebDriver
   * @param data code, description, code, entity
 * @throws InterruptedException 
   */
  public void fillData(WebDriver driver, Map<String, String> data) throws InterruptedException {
      waitUntilElementDisplayed(btnCreate, driver);
      btnCreate.click();
      waitUntilElementDisplayed(codeOfferModels, driver);
    codeOfferModels.click();
    codeOfferModels.clear();
    codeOfferModels.sendKeys((String) data.get(Constants.CODE));
    waitUntilElementDisplayed(descriptionOfferModel, driver);
    descriptionOfferModel.click();
    descriptionOfferModel.clear();
    descriptionOfferModel.sendKeys((String) data.get(Constants.DESCRIPTION));
    waitUntilElementDisplayed(offerCode, driver);
    offerCode.click();
    driver.findElement(By.cssSelector("tr.ui-datatable-even:nth-child(1) > td:nth-child(1)")).click();
    WebElement moduleIandA = driver.findElement(By.id("moduleForm:script_selectLink"));
    waitUntilElementDisplayed(moduleIandA, driver);
    moveMouseAndClick(moduleIandA);
    WebElement element = driver.findElement(By.xpath("/html/body/div[10]/div[2]/form/div[2]/div[2]/table/tbody/tr[1]/td[1]"));
    waitUntilElementDisplayed(element, driver);
    moveMouseAndClick(element);
    WebElement btnSave = driver.findElements(By.className("ui-button-text-icon-left")).get(0);
    waitUntilElementDisplayed(btnSave, driver);
    moveMouseAndClick(btnSave);
  }



  /**
   * code setter.
   * 
   * @param codeOfferModels setter
   */
  public void setcodeOfferModels(WebElement codeOfferModels) {
    this.codeOfferModels = codeOfferModels;
  }

  /**
   * code getter.
   * 
   * @return code
   */
  public WebElement getcodeOfferModels() {
    return this.codeOfferModels;
  }

  /**
   * description setter.
   * 
   * @param descriptionOfferModel setter
   */
  public void setdescriptionOfferModel(WebElement descriptionOfferModel) {
    this.descriptionOfferModel = descriptionOfferModel;
  }

  /**
   * description getter.
   * 
   * @return description
   */
  public WebElement getdescriptionOfferModel() {
    return this.descriptionOfferModel;
  }

}
