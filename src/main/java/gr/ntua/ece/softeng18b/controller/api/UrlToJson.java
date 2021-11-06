package gr.ntua.ece.softeng18b.controller.api;

import net.minidev.json.JSONObject;
import org.springframework.util.MultiValueMap;

public class UrlToJson {

    public static JSONObject getJson(MultiValueMap<String, String> paramMap) {
        JSONObject obj = new JSONObject();
        String temp;
        for (String i: paramMap.keySet()) {
            temp = paramMap.get(i).toString();
            obj.put(i, temp.substring(1,temp.length()-1));
        }
        return obj;
    }
}
