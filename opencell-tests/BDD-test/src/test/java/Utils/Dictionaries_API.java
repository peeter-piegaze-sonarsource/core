package Utils;

import java.util.HashMap;
import java.util.Map;

// These dictionaries need to be enriched to treat all possible entities
// of Opencell
public class Dictionaries_API {

    public static final Map<String, String> DESCRIPTION_DICT =
            new HashMap<String, String>() {{
                put("tradingCurrency", "prDescription");
            }};


    public static final Map<String, String> CODE_DICT =
            new HashMap<String, String>() {{
                put("tradingCurrency", "currencyCode");
            }};


}
