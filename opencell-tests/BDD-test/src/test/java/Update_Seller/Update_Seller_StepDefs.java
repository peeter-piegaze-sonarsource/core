package Update_Seller;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.meveo.model.admin.Seller;
import org.meveo.service.admin.impl.SellerService;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

public class Update_Seller_StepDefs {

    @Inject
    private SellerService sellerService;

    private Seller seller;
    private String id;
    private String code;
    private String description;
    private String tradingCurrency;
    private String status;

    @Given("Update seller")
    public void updateSeller() {
        seller = sellerService.findByCode(postData.getCode());
    }

    @When("Field id filled by {string}")
    public void fieldIdFilledBy(String arg0) {
        id = arg0;
    }

    @And("Field code filled by {string}")
    public void fieldCodeFilledBy(String arg0) {
        code = arg0;
    }

    @And("Field description filled by {string}")
    public void fieldDescriptionFilledBy(String arg0) {
        description = arg0;
    }

    @And("Field tradingCurrency filled by {string}")
    public void fieldTradingCurrencyFilledBy(String arg0) {
        tradingCurrency = arg0;
    }

    @Then("The status is {string}")
    public void theStatusIs(String arg0) {
        status = arg0;
        assertEquals( status, "300" );
        assertEquals( status, seller.get );
    }
}
