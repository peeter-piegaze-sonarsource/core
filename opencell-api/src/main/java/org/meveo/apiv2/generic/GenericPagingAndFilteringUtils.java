package org.meveo.apiv2.generic;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Iterator;

/**
 * Utils class for working with GenericPagingAndFiltering.
 *
 * @author Thang Nguyen
 */
public class GenericPagingAndFilteringUtils {

    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";
    private static final String SORT = "sort";
    private static final char DESCENDING_SIGN = '-';
    private static final String ASCENDING_ORDER = "ASCENDING";
    private static final String DESCENDING_ORDER = "DESCENDING";
    private static final String MULTI_SORTING_DELIMITER = ",";

    /**
     * Is used to create an instance of immutable class ImmutableGenericPagingAndFiltering
     *
     * @param queryParams a multivaluedMap containing all query params (limit, offset, etc.)
     * @return an instance of immutable class ImmutableGenericPagingAndFiltering
     */
    public static GenericPagingAndFiltering
    constructImmutableGenericPagingAndFiltering(MultivaluedMap<String, String> queryParams) {
        ImmutableGenericPagingAndFiltering.Builder builder = ImmutableGenericPagingAndFiltering.builder();
        Iterator<String> itQueryParams = queryParams.keySet().iterator();

        while (itQueryParams.hasNext()){
            String aKey = itQueryParams.next();

            if ( aKey.equals( LIMIT ) )
                builder.limit( Long.parseLong( queryParams.get(aKey).get(0) ) );
            else if ( aKey.equals( OFFSET ) )
                builder.offset( Long.parseLong( queryParams.get(aKey).get(0) ) );
            else if ( aKey.equals( SORT ) ) {
                String allSortFields = queryParams.get(aKey).get(0);

                // process sortOrder
                if ( allSortFields.charAt(0) == DESCENDING_SIGN ) {
                    builder.sortOrder( DESCENDING_ORDER );

                    // Remove the sign '-' in case of DESCENDING
                    allSortFields = allSortFields.substring(1);
                }

                // process sortBy
                String[] allSortFieldsSplit = allSortFields.split(MULTI_SORTING_DELIMITER);
                for ( int i = 0; i < allSortFieldsSplit.length; i++ ) {
                    System.out.println( "allSortFieldsSplit[i] : " + allSortFieldsSplit[i] );
                    builder.sortBy( allSortFieldsSplit[i] );
                }

//                builder.sortBy();
//System.out.println( "First element : " + queryParams.get( 0 ).toString() );
//System.out.println( "Second element : " + queryParams.get( 1 ).toString() );
//System.out.println( "Third element : " + queryParams.get( 2 ).toString() );

            }
        }

        return builder.build();
    }

}
