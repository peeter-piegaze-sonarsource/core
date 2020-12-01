package scenarios.Update_Seller;

import static org.junit.Assert.*;
import org.meveo.model.admin.Seller;

public class Update_Seller_StepDefs {

    private org.meveo.model.admin.Seller seller;
    private String id;
    private String code;
    private String description;
    private String tradingCurrency;
    private String status;

    @io.cucumber.java.en.Given("Update seller")
    public void updateSeller() {
        seller = new org.meveo.model.admin.Seller();
    }

    @io.cucumber.java.en.When("Field id filled by {string}")
    public void fieldIdFilledBy(String arg0) {
        id = arg0;
    }

    @io.cucumber.java.en.And("Field code filled by {string}")
    public void fieldCodeFilledBy(String arg0) {
        code = arg0;
    }

    @io.cucumber.java.en.And("Field description filled by {string}")
    public void fieldDescriptionFilledBy(String arg0) {
        description = arg0;
    }

    @io.cucumber.java.en.And("Field tradingCurrency filled by {string}")
    public void fieldTradingCurrencyFilledBy(String arg0) {
        tradingCurrency = arg0;
    }

    @io.cucumber.java.en.Then("The status is {string}")
    public void theStatusIs(String arg0) {
        status = arg0;
        assert( status == "200" );
    }
}
