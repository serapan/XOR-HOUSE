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
@RequestMapping("/observatory/api/category")
public class CategoryController {
    @Autowired
    private ProductRepository productRepository;

   /*
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private StoreRepository storeRepository;
    */

    @RequestMapping(value = "/{category}", method = RequestMethod.GET)
    ResponseEntity<Object> getProducts(@PathVariable("category") String category, @RequestParam(name="start", required=false) Integer start, @RequestParam(name="count", required=false) Integer count, @RequestParam(name="status", required=false) String status) {
        if (!category.matches("laptop|phone|tv|tablet|gaming")) {
            return new ResponseEntity<>("There are no products in this category", HttpStatus.BAD_REQUEST);
        }
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
                products = productRepository.findALLProductsWithPrices(category, start, count);
            } else {
                products = productRepository.findProductsWithPrices(category, start, count, status.equals("WITHDRAWN"));
            }
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Product pr;
        Long id;
        String name;
        String extraData;
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
            pr = new Product(name, rating, description, category, tags, withdrawn, extraData);
            pr.setId(id);
            temp = JsonCreator.productJson(pr);
            temp.put("price", (Double) i.get("Minprice"));
            ret.add(temp);
        }
        obj.put("start", start);
        obj.put("count", count);
        obj.put("total", products.size());
        obj.put("products", ret);
        ResponseEntity<Object> response = new ResponseEntity<>(obj, HttpStatus.OK);
        return response;
    }

    /*
    laptop: rating,cpu,storage,graphics,brand,ram
    phone: rating,screen size/technology, cpu, ram/rom, brand
    tablet: rating,screen size/technology, cpu, connectivity, ram/rom, brand
    gaming: rating, storage, output type, brand
    tv: rating, type, size, resolution, frequency, smart, brand
     */

    @RequestMapping(value = "/extradata/{category}", method = RequestMethod.GET)
    ResponseEntity<Object> getExtraData(@PathVariable("category") String category) {
        HashMap<String, HashSet<String>> extra = new HashMap<>();
        HashMap<String, String[]> config = new HashMap<>();
        config.put("laptop", new String[]{"cpu","storage","graphics","brand","ram"});
        config.put("phone", new String[]{"screen size/technology", "cpu", "ram/rom", "brand"});
        config.put("tablet", new String[]{"screen size/technology", "cpu", "connectivity", "ram/rom", "brand"});
        config.put("gaming", new String[]{"storage", "output type", "brand"});
        config.put("tv", new String[]{"type", "size", "resolution", "frequency", "smart", "brand"});
        for (String i: config.get(category.toLowerCase())) {
            extra.put(i, new HashSet<>());
        }
        JSONObject obj;
        ArrayList<Product> products = (ArrayList<Product>) productRepository.findByCategory(category.toLowerCase());
        if (products.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        for (Product i: products) {
            obj = i.getJsonExtraData();
            for (String j: config.get(category.toLowerCase())) {
                extra.get(j).add(obj.getAsString(j));
            }
        }
        obj = new JSONObject();
        for (String i: config.get(category.toLowerCase())) {
            obj.put(i, extra.get(i).toArray());
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }
}