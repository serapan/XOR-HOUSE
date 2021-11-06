package gr.ntua.ece.softeng18b.controller.api;


import gr.ntua.ece.softeng18b.model.Product;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//import java.util.Optional;

public class JsonCreator {

    public static JSONObject productJson(Product product) {
        JSONObject obj =new JSONObject();
        obj.put("id", product.getId());
        obj.put("name", product.getName());
        obj.put("description", product.getDescription());
        obj.put("category", product.getCategory());
        obj.put("tags", product.getTags2());
        obj.put("withdrawn", product.getWithdrawn());
        JSONObject extraData = new JSONObject(product.getExtraData());
        extraData.put("rating", product.getRating());
        obj.put("extraData", extraData);
        return obj;
    }


    public static JSONObject productJson(List<Product> products) {
        JSONObject obj;
        JSONObject ret = new JSONObject();
        JSONArray arr = new JSONArray();
        for (int i = 0; i < products.size(); i++) {
            obj = productJson(products.get(i));
            arr.add(obj);
        }
        ret.put("products", arr);
        return ret;
    }

    public static Product productFromJson(JSONObject jsonProduct) {
        String description;
        String category;
        String tags;
        String name;
        if (!jsonProduct.containsKey("name") || !jsonProduct.containsKey("description") || !jsonProduct.containsKey("category") || !jsonProduct.containsKey("tags")) {
            return null;
        }
        try {
            name = jsonProduct.getAsString("name");
            description = jsonProduct.getAsString("description");
            category = jsonProduct.getAsString("category");
            tags = jsonProduct.getAsString("tags");
        }
        catch (Exception e) {
            return null;
        }
        tags = tags.substring(1, tags.length()-1);
        Boolean withdrawn;
        if (jsonProduct.containsKey("withdrawn")) {
            try {
                withdrawn = Boolean.parseBoolean(jsonProduct.getAsString("withdrawn"));
            }
            catch (Exception e){
                return null;
            }
        }
        else {
            withdrawn = false;
        }
        String extraData = "{}";
        Double rating = null;
        if (jsonProduct.containsKey("extraData")) {
            JSONObject extr = jsonFromString(jsonProduct.getAsString("extraData"));
            if (extr.containsKey("rating")) {
                rating = Double.parseDouble(extr.getAsString("rating"));
                extr.remove("rating");
                if (rating > 5 || rating < 0) {
                    return null;
                }
            }
            extraData = extr.toJSONString().replace("\"", "").replace("\\", "");
            System.out.println(extraData);
        }
        extraData = extraData.substring(1, extraData.length()-1);
        extraData = extraData.replace("=", ":");
        Product newProduct = new Product(name, rating, description, category, tags, withdrawn, extraData);
        return newProduct;
    }

    public static Product getAProductFromJson(JSONObject json) {
        JSONObject extr = jsonFromString(json.getAsString("extraData"));
        String extraData = extr.toJSONString().replace("\"", "").replace("\\", "");
        extraData = extraData.substring(1, extraData.length()-1);
        extraData = extraData.replace("=", ":");
        Product ret = new Product(json.getAsString("name"), 0., "", "", "", false, extraData);
        return ret;
    }


    public static boolean productIsPresent(ArrayList<Product> products, Product product) {
        HashMap<String, String> extraData = product.getExtraData();
        for (Product i: products) {
            boolean flag = true;
            HashMap<String, String> extraData2 = i.getExtraData();
            for (String key: extraData.keySet()) {
                if (extraData2.containsKey(key)) {
                    System.out.println(extraData2.get(key) + "      " + extraData.get(key));
                    if (!extraData2.get(key).equals(extraData.get(key))) {
                        flag = false;
                    }
                }
            }
            if (flag) {
                return true;
            }
        }
        return false;
    }

    public static Long productIsPresentId(ArrayList<Product> products, Product product) {
        HashMap<String, String> extraData = product.getExtraData();
        for (Product i: products) {
            boolean flag = true;
            HashMap<String, String> extraData2 = i.getExtraData();
            for (String key: extraData.keySet()) {
                if (extraData2.containsKey(key)) {
                    System.out.println(extraData2.get(key) + "      " + extraData.get(key));
                    if (!extraData2.get(key).equals(extraData.get(key))) {
                        flag = false;
                    }
                }
            }
            if (flag) {
                return i.getId();
            }
        }
        return null;
    }

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
    }
}