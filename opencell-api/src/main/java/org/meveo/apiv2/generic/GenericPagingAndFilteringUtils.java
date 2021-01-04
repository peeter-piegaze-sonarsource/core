package org.meveo.apiv2.generic;

import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Utils class for working with GenericPagingAndFiltering.
 *
 * @author Thang Nguyen
 */
public class GenericPagingAndFilteringUtils {

    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";
    private static final String SORT = "sort";
    private static final String INTERVAL = "interval";
    private static final char DESCENDING_SIGN = '-';
    private static final char INTERVAL_DELIMITER = ','; // use as delimiter inside of an interval [id=2,5;description=2,]
    private static final char FIELD_INTERVAL_DELIMITER = '=';
    private static final char INTERVALS_DELIMITER = ';'; // use as delimiter between different intervals [id=2,5;description=2,]
    private static final char INTERVAL_OPEN_HOOK = '[';
    private static final char INTERVAL_CLOSE_HOOK = ']';
    private static final String ASCENDING_ORDER = "ASCENDING";
    private static final String DESCENDING_ORDER = "DESCENDING";
    private static final String SPACE_DELIMITER = " ";
    private static final String MULTI_SORTING_DELIMITER = ",";
    private static final String FROM_RANGE = "fromRange";
    private static final String TO_RANGE = "toRange";

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
                String allSortFieldsAndOrders = queryParams.get(aKey).get(0);
                String[] allSortFieldsSplit = allSortFieldsAndOrders.split(MULTI_SORTING_DELIMITER);
                StringBuilder sortOrders = new StringBuilder();
                StringBuilder sortFields = new StringBuilder();

                for ( int i = 0; i < allSortFieldsSplit.length - 1; i++ ) {
                    if ( allSortFieldsSplit[i].charAt(0) == DESCENDING_SIGN ) {
                        sortOrders.append( DESCENDING_ORDER + MULTI_SORTING_DELIMITER );

                        // Remove the sign '-' in case of DESCENDING
                        sortFields.append( allSortFieldsSplit[i].substring(1) + MULTI_SORTING_DELIMITER );
                    }
                    else {
                        sortOrders.append( ASCENDING_ORDER + MULTI_SORTING_DELIMITER );
                        sortFields.append( allSortFieldsSplit[i] + MULTI_SORTING_DELIMITER );
                    }
                }

                if ( allSortFieldsSplit[allSortFieldsSplit.length - 1].charAt(0) == DESCENDING_SIGN ) {
                    sortOrders.append( DESCENDING_ORDER );

                    // Remove the sign '-' in case of DESCENDING
                    sortFields.append( allSortFieldsSplit[allSortFieldsSplit.length - 1].substring(1) );
                }
                else {
                    sortOrders.append( ASCENDING_ORDER );
                    sortFields.append( allSortFieldsSplit[allSortFieldsSplit.length - 1] );
                }

                builder.sortOrder( sortOrders.toString() );
                builder.sortBy( sortFields.toString() );
            }
            else if ( aKey.equals( INTERVAL ) ) {
                List<String> aList = queryParams.get(aKey);

                String intervalString = aList.get(0);

                if ( intervalString.charAt(0) == INTERVAL_OPEN_HOOK &&
                        intervalString.charAt( intervalString.length() - 1 ) == INTERVAL_CLOSE_HOOK ) {
                    intervalString = intervalString.substring(1, intervalString.length() - 1);
                    String[] arrIntervals = intervalString.split( String.valueOf(INTERVALS_DELIMITER) );
                    Map<String, Object> genericFilters = new HashMap<>();

                    for ( String anIntervalWithField : arrIntervals ) {
                        String[] fieldAndItsInterval = anIntervalWithField.split( String.valueOf(FIELD_INTERVAL_DELIMITER) );
                        String anInterval = fieldAndItsInterval[1];
                        String aField = fieldAndItsInterval[0];
                        if ( StringUtils.countMatches( anInterval, String.valueOf( INTERVAL_DELIMITER ) ) == 1 ) {
                            if ( anInterval.charAt(anInterval.length() - 1) == INTERVAL_DELIMITER ) {
                                String leftBoundedValue = anInterval.substring( 0, anInterval.length() - 1 );
                                genericFilters.put( FROM_RANGE + SPACE_DELIMITER + aField, leftBoundedValue );
                            }
                            else if ( anInterval.charAt(0) == INTERVAL_DELIMITER ) {
                                String rightBoundedValue = anInterval.substring( 1 );
                                genericFilters.put( TO_RANGE + SPACE_DELIMITER + aField, rightBoundedValue );
                            }
                            else {
                                String[] bothValues = anInterval.split( String.valueOf(INTERVAL_DELIMITER) );
                                genericFilters.put( FROM_RANGE + SPACE_DELIMITER + aField, bothValues[0] );
                                genericFilters.put( TO_RANGE + SPACE_DELIMITER + aField, bothValues[1] );
                            }
                        }
                        else {
                            System.out.println("NOT A GOOD FORMAT OF INTERVAL, SHOULD ADD AN EXCEPTION HERE");
                        }
                    }

                    builder.filters( genericFilters );
                }
                else {
                    System.out.println("NOT A GOOD FORMAT OF INTERVAL, SHOULD ADD AN EXCEPTION HERE");
                }





//                // Process interval (fromRange, toRange, etc.)
//                Map<String, Object> genericFilters = new HashMap<>();
//                List<String> aList = queryParams.get(aKey);
//
//                for ( String aValue : aList ) {
//                    if ( StringUtils.countMatches( aValue, String.valueOf( INTERVAL_DELIMITER ) ) == 1 ) {
//                        if ( aValue.charAt(aValue.length() - 1) == INTERVAL_DELIMITER ) {
//                            String leftBoundedValue = aValue.substring( 0, aValue.length() - 1 );
//                            genericFilters.put( FROM_RANGE + SPACE_DELIMITER + aKey, leftBoundedValue );
//                        }
//                        else if ( aValue.charAt(0) == INTERVAL_DELIMITER ) {
//                            String rightBoundedValue = aValue.substring( 1 );
//                            genericFilters.put( TO_RANGE + SPACE_DELIMITER + aKey, rightBoundedValue );
//                        }
//                        else {
//                            String[] bothValues = aValue.split( String.valueOf(INTERVAL_DELIMITER) );
//                            genericFilters.put( FROM_RANGE + SPACE_DELIMITER + aKey, bothValues[0] );
//                            genericFilters.put( TO_RANGE + SPACE_DELIMITER + aKey, bothValues[1] );
//                        }
//                    }
//                    else {
//                        System.out.println("NOT A GOOD FORMAT OF INTERVAL, SHOULD ADD AN EXCEPTION HERE");
//                    }
//                }
//
//                builder.filters( genericFilters );
            }
        }

        return builder.build();
    }

}
