package org.meveo.apiv2.generic;

import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

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
    private static final String IN_LIST = "inList";

    private static final char DESCENDING_SIGN = '-';
    private static final char COMMA_DELIMITER = ','; // use as delimiter inside of an interval [id=2,5;description=2,]
    private static final char EQUAL_DELIMITER = '=';
    private static final char SEMI_COLON_DELIMITER = ';'; // use as delimiter between different intervals [id=2,5;description=2,]
    private static final char OPEN_HOOK = '[';
    private static final char CLOSE_HOOK = ']';
    private static final String COMMA_ENCODE = "%2C";
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
        Map<String, Object> genericFilters = new HashMap<>();

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

                if ( intervalString.charAt(0) == OPEN_HOOK &&
                        intervalString.charAt( intervalString.length() - 1 ) == CLOSE_HOOK ) {
                    intervalString = intervalString.substring(1, intervalString.length() - 1);
                    String[] arrIntervals = intervalString.split( String.valueOf(SEMI_COLON_DELIMITER) );

                    for ( String anIntervalWithField : arrIntervals ) {
                        String[] fieldAndItsInterval = anIntervalWithField.split( String.valueOf(EQUAL_DELIMITER) );
                        String anInterval = fieldAndItsInterval[1];
                        String aField = fieldAndItsInterval[0];
                        if ( StringUtils.countMatches( anInterval, String.valueOf( COMMA_DELIMITER ) ) == 1 ) {
                            if ( anInterval.charAt(anInterval.length() - 1) == COMMA_DELIMITER ) {
                                String leftBoundedValue = anInterval.substring( 0, anInterval.length() - 1 );
                                genericFilters.put( FROM_RANGE + SPACE_DELIMITER + aField, leftBoundedValue );
                            }
                            else if ( anInterval.charAt(0) == COMMA_DELIMITER ) {
                                String rightBoundedValue = anInterval.substring( 1 );
                                genericFilters.put( TO_RANGE + SPACE_DELIMITER + aField, rightBoundedValue );
                            }
                            else {
                                String[] bothValues = anInterval.split( String.valueOf(COMMA_DELIMITER) );
                                genericFilters.put( FROM_RANGE + SPACE_DELIMITER + aField, bothValues[0] );
                                genericFilters.put( TO_RANGE + SPACE_DELIMITER + aField, bothValues[1] );
                            }
                        }
                        else {
                            System.out.println("NOT A GOOD FORMAT OF INTERVAL, SHOULD ADD AN EXCEPTION HERE");
                        }
                    }
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
            else if ( aKey.equals( IN_LIST ) ) {
                List<String> aList = queryParams.get(aKey);

                String inListString = aList.get(0);

                if ( inListString.charAt(0) == OPEN_HOOK &&
                        inListString.charAt( inListString.length() - 1 ) == CLOSE_HOOK ) {
                    inListString = inListString.substring(1, inListString.length() - 1);
                    String[] arrInList = inListString.split( String.valueOf(SEMI_COLON_DELIMITER) );
                    for ( String anInlistWithField : arrInList ) {
                        String[] fieldAndItsInList = anInlistWithField.split( String.valueOf(EQUAL_DELIMITER) );
                        String aField = fieldAndItsInList[0];
                        String anInList = fieldAndItsInList[1].substring( 1, fieldAndItsInList[1].length() - 1 );
                        String[] elementsInList = anInList.split( String.valueOf(COMMA_DELIMITER) );
                        for ( String anElement : elementsInList ) {
                            if ( anElement.contains( COMMA_ENCODE ) ) {
System.out.println( "anElement DAY NE : " + anElement );
                                anElement.replaceAll( COMMA_ENCODE, String.valueOf(COMMA_DELIMITER) );
                            }
                        }
                        List inList = Arrays.asList(elementsInList);
                        genericFilters.put( IN_LIST + SPACE_DELIMITER + aField, inList );
                    }
                }
                else {
                    System.out.println("NOT A GOOD FORMAT OF INLIST, SHOULD ADD AN EXCEPTION HERE");
                }
            }
            builder.filters( genericFilters );
        }

        return builder.build();
    }

}
