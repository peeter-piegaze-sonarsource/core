package Update_Seller;

import Tools.Constants;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.meveo.api.account.SellerApi;
import org.meveo.model.admin.Seller;

import javax.inject.Inject;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Update_seller_stepDefs {

    private Seller seller;
    private String server;
    private String id;
    private String code;
    private String description;
    private String tradingCurrency;
    private String status;

    @Inject
    private SellerApi sellerApi;

    @Given("Update seller on {string}")
    public void updateSellerOn(String arg0) {
        server = arg0;
        seller = new Seller();
    }

    @When("Field id filled by {string}")
    public void fieldIdFilledBy(String arg0) {
        id = arg0;
//System.out.println("id : " + id);
//        sellerApi.
//        seller = sellerService.findByCode(id);
//        assertNotNull( seller );
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
    public void theStatusIs(String arg0) throws IOException {
        HttpUriRequest request = new HttpGet( Constants.SUFFIX_HTTPS + server +
                Constants.PREFIX_API_V2 + id );
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
        status = arg0;

        assertEquals( status, String.valueOf( httpResponse.getStatusLine().getStatusCode() ) );
    }
}
