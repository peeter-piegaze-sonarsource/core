package com.opencell.test.bdd.billing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.account.GetBillingAccountResponseDto;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class BillingAccountStepDefinition implements En {

    public BillingAccountStepDefinition(BaseHook base) {
        Then("^The billing account is created$", () -> {

            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/account/billingAccount?billingAccountCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetBillingAccountResponseDto actualEntity = response.extract().body().as(GetBillingAccountResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getBillingAccount());
                    assertEquals(code, actualEntity.getBillingAccount().getCode());
                }
            });
        });
    }
}
