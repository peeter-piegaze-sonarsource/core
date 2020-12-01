package Update_Seller;

import org.meveo.model.admin.Seller;
import io.cucumber.java.en.*;

import static org.junit.Assert.*;

public class Update_Seller_StepDefs {

    private Seller seller;
    private String id;
    private String code;
    private String description;
    private String tradingCurrency;
    private String status;

    @Given("Update seller")
    public void updateSeller() {
//        seller = new Seller();
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
        assertEquals( status, "200" );
    }
}
