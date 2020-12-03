package Update_entity;

import Utils.Constants;
import Utils.KeyCloakAuthenticationHook;
import Utils.RestApiUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.http.HttpStatus;

public class Update_entity_stepDefs {

    // These fields are required in the generation process of the request UPDATE
    private String entity;
    private String id;
    private String env;
    private String payload;
    private String status;

    // These might not be required in the generation process of the request UPDATE
    private String code;
    private String description;

    @Given("Update {string} with {string} on {string}")
    public void updateWithOn(String arg0, String arg1, String arg2) {
        entity = arg0;
        id = arg1;
        env = arg2;

        //--------------------------------------------------------------------
        // This piece of code tests creates a new instance of Keycloak token
        KeyCloakAuthenticationHook.getInstance();

        // A request POST tests existence of entity with id
        String url = env + Constants.PREFIX_API_V2 + entity +
                Constants.SEPARATOR_SLASH + id;
        RestApiUtils.post( url, Constants.EMPTY_PAYLOAD_TO_VERIFY_EXISTENCE ).
                assertThat().statusCode( HttpStatus.SC_OK );
    }

    @When("Fields filled by {string}, {string}")
    public void fieldsFilledBy(String arg0, String arg1) {
        code = arg0;
        description = arg1;
    }

    @Then("The status is {string}")
    public void theStatusIs(String arg0) {
        status = arg0;

        String url = env + Constants.PREFIX_API_V2 + entity +
                Constants.SEPARATOR_SLASH + id;
        payload = "{\"code\":\"" + code + "\",\"description\":\"" + description + "\"}";

        // This line is to update the entity and to execute the assertion
        RestApiUtils.put( url, payload ).assertThat().statusCode( Integer.valueOf( status ) );

    }
}
