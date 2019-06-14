package org.meveo.api.account;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomerApiTest {
    @InjectMocks
    private CustomerApi sut;
    
    @Mock
    private CustomerBrandService customerBrandService;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Test
    public void given_null_brand_code_when_find_brand_bycode_then_should_throw_exception() {
        //Expected
        expectedException.expect(EntityDoesNotExistsException.class);
        expectedException.expectMessage("CustomerBrand with code=null does not exists");
        //Given
        String brandCode = null;
        //When
        sut.findCustomerBrand(brandCode);
    }
    
    @Test
    public void given_correct_brand_code_but_record_not_exist_when_find_brand_bycode_then_should_throw_exception() {
        //Expected
        expectedException.expect(EntityDoesNotExistsException.class);
        expectedException.expectMessage("CustomerBrand with code=AS-400 does not exists.");
        //Given
        String brandCode = "AS-400";
        //When
        when(customerBrandService.findByCode(anyString())).thenReturn(null);
        sut.findCustomerBrand(brandCode);
    }
}