package gr.ntua.ece.softeng18b.controller.api;


import gr.ntua.ece.softeng18b.model.Store;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;


import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
//import java.util.Optional;

public class StoreJsonCreator {


    public static JSONObject storeJson(Store store) {
        JSONObject obj =new JSONObject();
        obj.put("id", store.getId());
        obj.put("name", store.getName());
        obj.put("address", store.getAddress());
        obj.put("lng", store.getLng());
        obj.put("lat", store.getLat());
        obj.put("tags", store.getTags2());
        obj.put("withdrawn", store.getWithdrawn());
        return obj;
    }


    public static JSONObject storeJson(List<Store> stores) {
        JSONObject obj;
        JSONObject ret = new JSONObject();
        JSONArray arr = new JSONArray();
        for (int i = 0; i < stores.size(); i++) {
            obj = storeJson(stores.get(i));
            arr.add(obj);
        }
        ret.put("shops", arr);
        return ret;
    }

    public static Store storeFromJson(JSONObject jsonStore) {
        String tags;
        String name;
        Boolean withdrawn;
        Double lng;
        Double lat;
        String address;
        if (!jsonStore.containsKey("address") || !jsonStore.containsKey("name") || !jsonStore.containsKey("tags") || !jsonStore.containsKey("lng") || !jsonStore.containsKey("lat")) {
            return null;
        }
        try {
            name = jsonStore.getAsString("name");
            lng = Double.parseDouble(jsonStore.getAsString("lng"));
            lat = Double.parseDouble(jsonStore.getAsString("lat"));
            address = jsonStore.getAsString("address");
            tags = jsonStore.getAsString("tags");
            if (jsonStore.containsKey("withdrawn")) {
                withdrawn = Boolean.parseBoolean(jsonStore.getAsString("withdrawn"));
            }
            else {
                withdrawn = false;
            }
        }
        catch (Exception e) {
            return null;
        }
        tags = tags.substring(1, tags.length()-1);
        Store newStore = new Store(name, address, tags, withdrawn, lng, lat, "-1", "-1");
        return newStore;
    }


    public static boolean storeIsPresent(ArrayList<Store> stores, Store store) {
        for (Store i: stores) {
            if (i.getAddress().equals(store.getAddress()) && i.getName().equals(store.getName())) {
                return true;
            }
        }
        return false;
    }

    /*

    public static JSONObject jsonFromString(String ex) {
        ex = ex.replace("{", "").replace("}", "");
        ex = ex.replace("=", ":");
        String[] keyValuePairs = ex.split(",");
        JSONObject hm = new JSONObject();
        for(String pair : keyValuePairs){
            String[] keyValuePair = pair.split(":");
            try{
                hm.put(keyValuePair[0].trim(), keyValuePair[1].trim());
            }
            catch(IndexOutOfBoundsException e){
                //hm.put(keyValuePair[0].trim(), " ");
            }
        }
        return hm;
    }

    public static JSONObject combineExtraData(JSONObject current, JSONObject newe) {
        System.out.println("mesa");
        System.out.println(current);
        System.out.println(newe);
        for (String key: newe.keySet()) {
            current.put(key, newe.get(key));
        }
        System.out.println(current);
        return current;
    }*/
}