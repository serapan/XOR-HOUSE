package gr.ntua.ece.softeng18b.controller.api;

import gr.ntua.ece.softeng18b.model.Product;
//import gr.ntua.ece.softeng18b.model.Store;
import gr.ntua.ece.softeng18b.repositories.ProductRepository;
//import gr.ntua.ece.softeng18b.repositories.StoreRepository;
//import gr.ntua.ece.softeng18b.repositories.UserRepository;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.*;

@RestController
@RequestMapping("/observatory/api/display")
public class displayController {
    @Autowired
    private ProductRepository productRepository;

   /*
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private StoreRepository storeRepository;
    */

    @RequestMapping(value = "/products/{productId}", method = RequestMethod.GET)
    ResponseEntity<Object> getPricesOfProduct(@PathVariable("productId") Integer productId) {
        List<Map<String, Object>> prices = productRepository.findPricesForProduct(productId);
        JSONArray ret = new JSONArray();
        JSONObject obj;
        for (Map<String, Object> i: prices) {
            obj = new JSONObject();
            obj.put("storeId", i.get("store_id"));
            obj.put("name", i.get("name"));
            obj.put("price", i.get("pr"));
            obj.put("lng", i.get("lng"));
            obj.put("lat", i.get("lat"));
            ret.add(obj);
        }
        obj = new JSONObject();
        obj.put("prices", ret);
        ResponseEntity<Object> response = new ResponseEntity<>(obj, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value = "/stores/{storeId}", method = RequestMethod.GET)
    ResponseEntity<Object> getProductsPerStore(@PathVariable("storeId") Integer storeId, @RequestParam(name="start", required=false) Integer start, @RequestParam(name="count", required=false) Integer count, @RequestParam(name="status", required=false) String status) {
        List<Map<String, Object>> products;
        if(start == null){
            start = 0;
        }
        if(count == null){
            count = 20;
        }
        if(status == null || status.isEmpty()){
            status = "ACTIVE";
        }
        if(!status.matches("ALL|ACTIVE|WITHDRAWN") || count<=0 || start < 0){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        try {
            if (status.equals("ALL")) {
                products = productRepository.findAllProductsPerStore(storeId, start, count);
            } else {
                products = productRepository.findProductsPerStore(storeId, start, count, status.equals("WITHDRAWN"));
            }
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Product pr;
        Long id;
        String name;
        String extraData;
        String category;
        String tags;
        Double rating;
        String description;
        Boolean withdrawn;
        JSONArray ret = new JSONArray();
        JSONObject obj = new JSONObject();
        JSONObject temp;
        for (Map<String, Object> i: products) {
            id =  ((BigInteger)i.get("sku")).longValue();
            name = (String) i.get("model");
            extraData = (String) i.get("specs");
            tags = (String) i.get("tags");
            rating = (Double) i.get("rating");
            description = (String) i.get("description");
            withdrawn = (Boolean) i.get("withdrawn");
            category = (String)i.get("category");
            pr = new Product(name, rating, description, category, tags, withdrawn, extraData);
            pr.setId(id);
            temp = JsonCreator.productJson(pr);
            temp.put("price", (Double) i.get("price"));
            ret.add(temp);
        }
        obj.put("start", start);
        obj.put("count", count);
        obj.put("total", products.size());
        obj.put("products", ret);
        ResponseEntity<Object> response = new ResponseEntity<>(obj, HttpStatus.OK);
        return response;
    }
}
