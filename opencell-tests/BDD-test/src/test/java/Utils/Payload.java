package Utils;

import java.lang.reflect.Field;

public class Payload {

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


}
