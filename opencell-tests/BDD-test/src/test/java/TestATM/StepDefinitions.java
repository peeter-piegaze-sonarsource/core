package TestATM;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertEquals;

public class StepDefinitions {
    private ATM anATM;

    @Given("Given an ATM and I have {int} euros in my account")
    public void given_an_atm_and_i_have_euros_in_my_account(Integer acc) {
        anATM = new ATM();
        anATM.setAccount(acc);
    }
    @When("I ask for an amount of {int} euros")
    public void i_ask_for_an_amount_of_euros(Integer amt) {
        anATM.withDrawMoney(amt);
    }
    @Then("My account is {int} euros")
    public void my_account_is_euros(Integer acc) {
        assertEquals(anATM.getAccount(), acc.intValue());
    }
    @Then("The ATM returns {int} euros to me")
    public void the_atm_returns_euros_to_me(Integer amt) {
        assertEquals(anATM.getAmount(), amt.intValue());
    }

}
