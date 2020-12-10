package BDDexperimentation.Update_entity;

import BDDexperimentation.Utils.Constants;
import BDDexperimentation.Utils.KeyCloakAuthenticationHook;
import BDDexperimentation.Utils.Payload;
import BDDexperimentation.Utils.RestApiUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import org.apache.http.HttpStatus;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class Update_entity_stepDefs {

    // These fields are required in the generation process of the request UPDATE
    private String entity;
    private String id;
    private String payload;
    private String jsonPath;
    private String url;
    private int status;
    private ExtractableResponse aResponse;

    @Given("Update {string} with {string}")
    public void updateWith(String arg0, String arg1) throws ParseException, IOException {
        entity = arg0;
        id = arg1;

        //--------------------------------------------------------------------
        // This piece of code tests creates a new instance of Keycloak token
        KeyCloakAuthenticationHook single_instance = KeyCloakAuthenticationHook.getInstance();
        single_instance.setProperties();
        single_instance.authenticateAsAdmin();

        url = Constants.PREFIX_PUT_API_V2 + entity + Constants.SEPARATOR_SLASH + id;

        aResponse = RestApiUtils.post( url, Constants.EMPTY_PAYLOAD_TO_VERIFY_EXISTENCE ).extract();

        // A request POST tests existence of entity based on id
        assertEquals( aResponse.statusCode(), HttpStatus.SC_OK );
    }

    @When("Fields filled by {string}")
    public void fieldsFilledBy(String arg0) throws ParseException {
        // Read payload from jsonFile
        jsonPath = arg0;
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream =
                     Files.lines( Paths.get(jsonPath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        payload = contentBuilder.toString();
    }

    @When("All fields tested")
    public void allFieldsTested() throws ParseException {
        // Create the new payload from the response of the request POST
        // This payload is created when user needs to test all fields ("All fields tested")
        payload = Payload.generatePayload( aResponse.asString() );
    }

    @Then("The status is {int}")
    public void theStatusIs(Integer arg0) throws ParseException {
        status = arg0;

        // This line is used to update the entity and to execute the assertion
        RestApiUtils.put( url, payload ).assertThat().statusCode( status );
        // This piece of code is used to verify if the request has updated the entity
        String aResult = RestApiUtils.post( url, Constants.EMPTY_PAYLOAD_TO_VERIFY_EXISTENCE )
                .extract().asString();
        Payload.comparePayloadToResult( payload, aResult, entity );
    }
}
