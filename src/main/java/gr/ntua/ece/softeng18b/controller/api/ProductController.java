package gr.ntua.ece.softeng18b.controller.api;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

//import com.google.api.client.json.Json;
import gr.ntua.ece.softeng18b.model.*;
import gr.ntua.ece.softeng18b.repositories.StoreRepository;
import gr.ntua.ece.softeng18b.repositories.TokenRepository;
import gr.ntua.ece.softeng18b.repositories.UserRepository;

//import gr.ntua.ece.softeng18b.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

//import gr.ntua.ece.softeng18b.repositories.InfoRepository;
import gr.ntua.ece.softeng18b.repositories.ProductRepository;
import net.minidev.json.JSONObject;


@RestController
@RequestMapping("/observatory/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    JdbcTemplate infoTemplate;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TokenRepository tokenRepository;

    /*
    public ProductRepository getProductRepository(){
        return this.productRepository;
    }

    public void setProductRepository(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public UserRepository getUserRepository(){
        return this.userRepository;
    }

    public void setUserRepository(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<Object> getAllProducts(@RequestParam(name="start", required=false) Integer start, @RequestParam(name="count", required=false) Integer count, @RequestParam(name="status", required=false) String status, @RequestParam(name="sort", required=false) String sort) {
        List<Product> products;
        if(start == null){
            start = 0;
        }
        if(count == null){
            count = 20;
        }
        if(status == null || status.isEmpty()){
            status = "ACTIVE";
        }
        if(sort == null || sort.isEmpty()){
            sort = "id|DESC";
        }
        if(!status.matches("ALL|ACTIVE|WITHDRAWN") || !sort.matches("id\\|DESC|id\\|ASC|name\\|ASC|name\\|DESC") || count<=0 || start < 0){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        try {
            if (status.equals("ALL")) {
                products = productRepository.findAllProducts(start, count, sort);
            } else {
                products = productRepository.findProducts(start, count, sort, status.equals("WITHDRAWN"));
            }
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        JSONObject obj = JsonCreator.productJson(products);
        obj.put("start", start);
        obj.put("count", count);
        obj.put("total", products.size());
        ResponseEntity<Object> response = new ResponseEntity<>(obj, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    ResponseEntity<Object> getProduct(@PathVariable("productId") Long productId, @RequestParam(name = "displayShops", required = false) Boolean displayShops) {
        ResponseEntity<Object> response = null;
        if(productId == null || productId <= 0){
            response = new ResponseEntity<>("Invalid Product Id", HttpStatus.BAD_REQUEST);
        }
        else{
            Optional<Product> showProduct = productRepository.findById(productId);
            //String ret = JsonCreator.productJson(showProduct);
            if (showProduct.isPresent()) {
                JSONObject ret = JsonCreator.productJson(showProduct.get());
                if (displayShops != null) {
                    ArrayList<Store> shops = (ArrayList<Store>) storeRepository.findStoresByProductId(productId);
                    ret.put("shops", StoreJsonCreator.storeJson(shops));
                }
                response = new ResponseEntity<>(ret, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>("Product Not Found", HttpStatus.NOT_FOUND);
            }
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = "application/json")
    ResponseEntity<Object> getNewProduct(@RequestBody MultiValueMap<String, String> paramMap, @RequestHeader("X-OBSERVATORY-AUTH") String token) {
        List<Token> showToken = tokenRepository.findToken(token);
        System.out.println(token + "    gamo gamo");
        if (showToken.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        JSONObject jsonProduct = UrlToJson.getJson(paramMap);
        jsonProduct.put("tags", "["+jsonProduct.getAsString("tags")+"]");
        Product newProduct = JsonCreator.productFromJson(jsonProduct);
        if (newProduct == null) {
            return new ResponseEntity<>("Invalid Product", HttpStatus.BAD_REQUEST);
        }
        //System.out.println(((ArrayList<Product>) productRepository.findByName(newProduct.getName())).size());
        if (JsonCreator.productIsPresent((ArrayList<Product>) productRepository.findByName(newProduct.getName()), newProduct)) {
            return new ResponseEntity<>("Product Already Exists", HttpStatus.BAD_REQUEST);
        }
        Product createdProduct = productRepository.save(newProduct);
        ResponseEntity<Object> response = new ResponseEntity<>(JsonCreator.productJson(createdProduct), HttpStatus.OK);
        return response;
    }


    @RequestMapping(value = "/{productId}", method = RequestMethod.PUT, consumes = "application/json")
    ResponseEntity<Object> updateProduct(@PathVariable("productId") Long productId, @RequestBody JSONObject jsonProduct, @RequestHeader("X-OBSERVATORY-AUTH") String token) {
        List<Token> showToken = tokenRepository.findToken(token);
        if (showToken.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        if(productId <= 0 || productId == null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Optional<Product> showProduct = productRepository.findById(productId);
        if (!showProduct.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        //Product existing = showProduct.get();
        Product updatedProduct = JsonCreator.productFromJson(jsonProduct);
        if (updatedProduct == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        updatedProduct.setId(productId);
        Product createdProduct = productRepository.save(updatedProduct);
        return new ResponseEntity<>(JsonCreator.productJson(createdProduct), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.DELETE)
    ResponseEntity<Object> deleteProduct(@PathVariable("productId") Long productId, HttpServletRequest request, @RequestHeader("X-OBSERVATORY-AUTH") String token) {
        List<Token> showToken = tokenRepository.findToken(token);
        if (showToken.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        ResponseEntity<Object> response = null;
        if(productId <= 0 || productId == null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Optional<Product> showProduct = productRepository.findById(productId);
        if (!showProduct.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        Principal p = request.getUserPrincipal();
        if(p == null){
            response = new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
            return response;
        }
        String username = p.getName();
        Optional<User> showUser = userRepository.findByUsername(username);
        if (!showUser.isPresent()) {
            response = new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
            return response;
        }
        User usr = showUser.get();
        Role role = usr.getRole();
        String rolestr = role.getRole();
        if (!rolestr.equals("admin")) {
            Product tempproduct = showProduct.get();
            tempproduct.setWithdrawn(true);
            productRepository.save(tempproduct);
            JSONObject retj = new JSONObject();
            retj.put("message", "ok");
            response = new ResponseEntity<>(retj, HttpStatus.OK);
            return response; 
        }

        /*try {
            infoRepository.deleteInfoBySku(productId);
        }
        catch (Exception e){
            ;
        }*/
        try {
            Object[] sqlObj = new Object[1];
            sqlObj[0] = productId;
            String sql1 = "DELETE FROM info WHERE sku = ?";
            String sql2 = "DELETE FROM product WHERE sku = ?";
            infoTemplate.update(sql1, sqlObj);
            infoTemplate.update(sql2, sqlObj);
            //productRepository.removeById(productId);
            JSONObject retj = new JSONObject();
            retj.put("message", "ok");
            response = new ResponseEntity<>(retj, HttpStatus.OK);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            response = new ResponseEntity<>("Product Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response = new ResponseEntity<>("An error has occured", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.PATCH, consumes = "application/json", produces = "application/json")
    ResponseEntity<Object> patchProduct(@RequestBody JSONObject jsonProduct, @PathVariable("productId") Long productId, @RequestHeader("X-OBSERVATORY-AUTH") String token) {
        List<Token> showToken = tokenRepository.findToken(token);
        if (showToken.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        if(productId <= 0 || productId == null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Optional<Product> showProduct = productRepository.findById(productId);
        if (!showProduct.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        if (jsonProduct.keySet().size() != 1) {
            return new ResponseEntity<>("Only one product field can be patched", HttpStatus.BAD_REQUEST);
        }
        Product existing = showProduct.get();
        if (!jsonProduct.keySet().contains("description")) {
            jsonProduct.put("description", existing.getDescription());
        }
        if (!jsonProduct.keySet().contains("category")) {
            jsonProduct.put("category", existing.getCategory());
        }
        if (!jsonProduct.keySet().contains("tags")) {
            jsonProduct.put("tags", existing.getTags());
        }
        if (!jsonProduct.keySet().contains("name")) {
            jsonProduct.put("name", existing.getName());
        }
        Double rating = null;
        if (!jsonProduct.keySet().contains("extraData")) {
            jsonProduct.put("extraData", existing.getJsonExtraData());
        }
        else {
            JSONObject newe = JsonCreator.jsonFromString(jsonProduct.getAsString("extraData"));
            if (newe.containsKey("rating")) {
                try {
                    rating = Double.parseDouble(newe.getAsString("rating"));
                    if (rating > 5 || rating < 0) {
                        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                    }
                }
                catch (Exception e) {
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
                newe.remove("rating");
            }
            jsonProduct.put("extraData", JsonCreator.combineExtraData(existing.getJsonExtraData(), newe));
        }
        Product updatedProduct = JsonCreator.productFromJson(jsonProduct);
        if (updatedProduct == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        updatedProduct.setId(productId);
        if (rating == null) {
            updatedProduct.setRating(existing.getRating());
        }
        else {
            if (existing.getRating() != null) {
                updatedProduct.setRating((existing.getRating() * existing.getRated() + rating) / (existing.getRated() + 1));
                updatedProduct.setRated(existing.getRated() + 1);
            }
            else {
                updatedProduct.setRating(rating);
                updatedProduct.setRated(1);
            }
        }
        Product createdProduct = productRepository.save(updatedProduct);
        return new ResponseEntity<>(JsonCreator.productJson(createdProduct), HttpStatus.OK); 
    }
}