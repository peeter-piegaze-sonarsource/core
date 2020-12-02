package Response_code_update_seller;

import Tools.Constants;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Response_code_update_seller_stepDefs {

    private String server;
    private String id;
    private String status;

    @Given("Update seller on {string}")
    public void updateSellerOn(String arg0) {
        server = arg0;
    }

    @When("Field id filled by {string}")
    public void fieldIdFilledBy(String arg0) {
        id = arg0;
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
