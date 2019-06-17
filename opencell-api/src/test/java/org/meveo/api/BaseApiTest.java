package org.meveo.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BaseApiTest {
    @InjectMocks
    private CalendarApi calendarApi;
    
    @Test
    public void should_create_new_paging_and_filtering_when_param_is_null() {
        //Given
        PagingAndFiltering pagingAndFiltering = null;
        //When
        PagingAndFiltering result = calendarApi.initIfNull(pagingAndFiltering);
        //Then
        assertThat(result).isNotNull();
    }
    
    @Test
    public void should_return_the_param_if_it_s_not_null() {
        //Given
        PagingAndFiltering pagingAndFiltering = new PagingAndFiltering();
        pagingAndFiltering.setSortBy("flirtikit");
        //When
        PagingAndFiltering result = calendarApi.initIfNull(pagingAndFiltering);
        //Then
        assertThat(result).isNotNull();
        assertThat(result.getSortBy()).isEqualTo(pagingAndFiltering.getSortBy());
    }
    
    @Test
    public void should_return_the_default_sort_by_when_param_sort_by_is_null() {
        //Given
        PagingAndFiltering pagingAndFiltering = new PagingAndFiltering();
        //When
        String sortBy = calendarApi.getDefaultSortBy(pagingAndFiltering, "flirtikit");
        //Then
        assertThat(sortBy).isEqualTo("flirtikit");
    }
    
    @Test
    public void should_return_the_default_sort_by_when_param_sort_by_is_empty() {
        //Given
        PagingAndFiltering pagingAndFiltering = new PagingAndFiltering();
        pagingAndFiltering.setSortBy("");
        //When
        String sortBy = calendarApi.getDefaultSortBy(pagingAndFiltering, "flirtikit");
        //Then
        assertThat(sortBy).isEqualTo("flirtikit");
    }
    @Test
    public void should_return_the_param_sort_by_when_param_sort_by_is_not_blank() {
        //Given
        PagingAndFiltering pagingAndFiltering = new PagingAndFiltering();
        pagingAndFiltering.setSortBy("bidlidez");
        //When
        String sortBy = calendarApi.getDefaultSortBy(pagingAndFiltering, "flirtikit");
        //Then
        assertThat(sortBy).isEqualTo("bidlidez");
    }
    
    
}