package Utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.reflect.Field;
import java.util.*;

/** Class contains all useful methods to construct, compare, update payload
 * @author Thang Nguyen
 * @version 1.0
 */
public class Payload {

    static JSONParser parser = new JSONParser();

    /**
     * Construct payload
     * @author Thang Nguyen
     * @version 1.0
     * @since   2020-12-06
     * @param object
     * @return
     * @throws IllegalAccessException
     */
    public static String constructPayload( Object object ) throws IllegalAccessException {
        String payload = "{\"";
        String entityName = "entity";
        String code = "code";
        String description = "description";

        for ( Field aField : object.getClass().getDeclaredFields() ) {

            if ( aField.getName().equals( entityName ) ) {
                aField.setAccessible(true);
                entityName = aField.get( object ).toString();
            }

            if ( aField.getName().equals( code ) ) {
                if ( Dictionaries_API.CODE_DICT.containsKey( entityName ) )
                    code = Dictionaries_API.CODE_DICT.get( entityName );

                aField.setAccessible(true);
                payload += code + "\":\"" + aField.get( object ).toString() + "\",\"";
            }

            if ( aField.getName().equals( description ) ) {
                if ( Dictionaries_API.DESCRIPTION_DICT.containsKey( entityName ) )
                    description = Dictionaries_API.DESCRIPTION_DICT.get( entityName );

                aField.setAccessible(true);
                payload += description + "\":\"" + aField.get( object ).toString();
            }
        }

        payload += "\"}";

        return payload;
    }

    /**
     * Compare two payloads: payload that we want to update and result retrieved from
     * database after executing the UPDATE request
     * @author Thang Nguyen
     * @version 1.0
     * @since   2020-12-06
     * @param payload : payload that we want to update on the entity
     * @param result : result retrieved from database
     * @return boolean value : true if the entity has been successfully updated, false otherwise
     * @throws ParseException
     */
    public static boolean comparePayloadToResult( String payload, String result ) throws ParseException {
        JSONObject jsonPayload = (JSONObject) parser.parse( payload );
        JSONObject jsonResponse = (JSONObject) parser.parse(
                ( (JSONObject) parser.parse( result ) ).get("data").toString() );

        for ( Iterator iterator = jsonPayload.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();

            if ( ! jsonPayload.get(key).toString()
                    .equals( jsonResponse.get(key).toString() ) ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Generate a new payload from the response of the request POST used to get a particular
     * entity (based on id). This new payload is used for updating the entity
     * @author  Thang Nguyen
     * @version 1.0
     * @since   2020-12-07
     * @param payload : the initial payload
     * @param env : the environment on that all requests are executed
     * @return the method returns the new payload whose fields have been updated
     * @throws ParseException
     */
    public static String generatePayload( String payload, String env ) throws ParseException {
        JSONObject jsonResponse = (JSONObject) parser.parse(
                ( (JSONObject) parser.parse( payload ) ).get("data").toString() );

        // Remove fields that we cannot update
        jsonResponse.remove( "id" );
        jsonResponse.remove( "auditable" );
        jsonResponse.remove( "accountType" ); // Why this field cannot be updated?

        for ( Iterator iterator = jsonResponse.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            Object element = jsonResponse.get( key );

            if ( element instanceof JSONObject ) {
                Set aSetObj = ( (JSONObject)element ).keySet();
                String url = env + Constants.PREFIX_POST_ALL_API_V2 + key;
                String getAllPostResponse = RestApiUtils.post( url, Constants.EMPTY_PAYLOAD_TO_VERIFY_EXISTENCE )
                        .extract().asString();
                JSONArray jsonAllPostResponse = (JSONArray) parser.parse(
                        ( (JSONObject) parser.parse( getAllPostResponse ) ).get("data").toString() );

                for ( Object anObj : aSetObj ) {
                    Random ran = new Random();
                    Object arrResp[] = new Object[jsonAllPostResponse.size()];
                    for ( int i = 0; i < jsonAllPostResponse.size(); i++ ) {
                        JSONObject jsonObj = (JSONObject) jsonAllPostResponse.get(i);
                        arrResp[i] = jsonObj.get( anObj );
                    }
                    ((JSONObject) element).put( anObj , arrResp[ ran.nextInt(arrResp.length) ] );
                }
                jsonResponse.put( key, element );
            }
            else if ( element.toString().equals( "false" ) )
                jsonResponse.put( key, "true" );
            else if ( element.toString().equals( "true" ) )
                jsonResponse.put( key, "false" );
            else
                jsonResponse.put( key, element.toString() + "_updated" );
        }

        return jsonResponse.toString();
    }

    /**
     * Update the payload when users want to modify several fields
     * @author  Thang Nguyen
     * @version 1.0
     * @since   2020-12-07
     * @param payload : the initial payload before being updated
     * @param updatedFields : a map that contains all fields which will be updated
     * @return the payload after being updated
     * @throws ParseException
     */
    public static String updatePayload( String payload, Map<String, String> updatedFields ) throws ParseException {
        JSONObject jsonPayload = (JSONObject) parser.parse( payload );

        for ( Map.Entry<String, String> entry : updatedFields.entrySet() ) {
            jsonPayload.put( entry.getKey(), entry.getValue() );
        }

        return jsonPayload.toString();
    }

}
