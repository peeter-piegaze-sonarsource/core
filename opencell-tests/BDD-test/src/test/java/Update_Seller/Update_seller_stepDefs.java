package Update_Seller;

import Tools.Constants;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.mina.util.Base64;
import org.meveo.model.admin.Seller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class Update_seller_stepDefs {

    private Seller seller;
    private String server;
    private String id;
    private String code;
    private String description;
    private String tradingCurrency;
    private String status;

    @Given("Update seller on {string}")
    public void updateSellerOn(String arg0) {
        server = arg0;
        seller = new Seller();
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
    public void theStatusIs(String arg0) throws IOException {

        //-----------------------------------------------------------------------------------
        // This piece of code tests creates a new Http client with credentials
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials(Constants.USERNAME_OC_ADMIN, Constants.PASSWORD_OC_ADMIN);
        provider.setCredentials( AuthScope.ANY, credentials );

        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();

        HttpResponse response = client.execute( new HttpGet(Constants.SUFFIX_HTTPS + server +
                        Constants.PREFIX_API_V2 + id) );

        assertEquals( HttpStatus.SC_OK, response.getStatusLine().getStatusCode() );


//        //-----------------------------------------------------------------------------------
//        // This piece of code tests whether there exists an entity Seller in database which
//        // has the same id as parameter id. For this purpose, we need to create a HttpGet
//        // request
//        HttpPost requestPost = new HttpPost( Constants.SUFFIX_HTTPS + server +
//                Constants.PREFIX_API_V2 );
//        requestPost.setHeader( HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON );
//
//
//        char[] encoding = Base64Encoder.encode(
//                (Constants.USERNAME_OC_ADMIN + ":" + Constants.PASSWORD_OC_ADMIN).getBytes() );
//        requestPost.setHeader( HttpHeaders.AUTHORIZATION, "Basic " + encoding );
//
//
//        HttpResponse httpResponsePost = client.execute( requestPost );
//        status = arg0;
//
//for ( Header aHeader : requestPost.getAllHeaders() ) {
//    System.out.println("aHeader.toString() : " + aHeader.toString() );
//}
//System.out.println("requestPost : " + requestPost.toString() );
//
//System.out.println("requestPost.getRequestLine() : " + requestPost.getRequestLine());
//
//        assertEquals( status, String.valueOf( httpResponsePost.getStatusLine().getStatusCode() ) );
//
//String responseString = new BasicResponseHandler().handleResponse(httpResponsePost);
//
//System.out.println("responseString : " + responseString);


//        //-----------------------------------------------------------------------------------
//        // This piece of code tests whether there exists an entity Seller in database which
//        // has the same id as parameter id. For this, we need to create HttpPut request with a
//        // json payload containing pieces of information as code, description, etc.
//        HttpPut requestPut = new HttpPut( Constants.SUFFIX_HTTPS + server +
//                Constants.PREFIX_API_V2 + id );
//        requestPut.setHeader( HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON );
//        String encoding = Base64.getEncoder().encodeToString(
//                ( Constants.USERNAME_OC_ADMIN + ":" + Constants.PASSWORD_OC_ADMIN).getBytes() );
//        requestPut.setHeader( HttpHeaders.AUTHORIZATION, "Basic " + encoding);
//
//        String inputJson = "{\n" +
//                "  \"code\": \"Test_Method_Thang\",\n" +
//                "  \"description\": \"A new description of Thang\"\n" +
//                "}";
//        StringEntity stringEntity = new StringEntity(inputJson);
////        requestPut.setEntity(stringEntity);
////System.out.println( "inputJson " + inputJson );
//System.out.println( "Executing request " + requestPut.getRequestLine() );
//        HttpResponse httpResponsePut = client.execute( requestPut );
//
//        assertEquals( status, String.valueOf( httpResponsePut.getStatusLine().getStatusCode() ) );


        final String USER_AGENT = "PostmanRuntime/7.26.8";
        String url = "http://localhost:8080/JAXRSJsonCRUDExample/rest/countries";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Setting basic post request
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");
        String auth = Constants.USERNAME_OC_ADMIN + ":" + Constants.PASSWORD_OC_ADMIN;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
        String authHeaderValue = "Basic " + new String(encodedAuth);
    System.out.println("authHeaderValue : " + authHeaderValue);
        con.setRequestProperty("Authorization", authHeaderValue);

//        String postJsonData = "{"id":5,"countryName":"USA","population":8000}";

//        String postJsonData = "{\n" +
//                "  \"code\": \"Test_Method_Thang\",\n" +
//                "  \"description\": \"A new description of Thang\"\n" +
//                "}";

//        // Send post request
//        con.setDoOutput(true);
//        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(postJsonData);
//        wr.flush();
//        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer responseNew = new StringBuffer();

        while ((output = in.readLine()) != null) {
            responseNew.append(output);
        }
        in.close();

        //printing result from response
        System.out.println(responseNew.toString());

    }
}
