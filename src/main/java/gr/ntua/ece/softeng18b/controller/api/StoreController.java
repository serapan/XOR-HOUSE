package gr.ntua.ece.softeng18b.controller.api;


import java.security.Principal;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import gr.ntua.ece.softeng18b.model.Role;
//import com.google.api.client.json.Json;
import gr.ntua.ece.softeng18b.model.Store;
import gr.ntua.ece.softeng18b.model.Token;
import gr.ntua.ece.softeng18b.model.User;
import gr.ntua.ece.softeng18b.repositories.StoreRepository;
import gr.ntua.ece.softeng18b.repositories.TokenRepository;
import gr.ntua.ece.softeng18b.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

//import gr.ntua.ece.softeng18b.model.Product;
//import gr.ntua.ece.softeng18b.repositories.ProductRepository;
import net.minidev.json.JSONObject;


@RestController
@RequestMapping("/observatory/api/shops")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    JdbcTemplate infoTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;
    

    /*
    public StoreRepository getStoreRepository(){
        return this.storeRepository;
    }

    public void setStoreRepository(ProductRepository StoreRepository){
        this.storeRepository = storeRepository;
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
        List<Store> stores;
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
                stores = storeRepository.findAllStores(start, count, sort);
            } else {
                stores = storeRepository.findStores(start, count, sort, status.equals("WITHDRAWN"));
            }
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        JSONObject obj = StoreJsonCreator.storeJson(stores);
        obj.put("start", start);
        obj.put("count", count);
        obj.put("total", stores.size());
        ResponseEntity<Object> response = new ResponseEntity<>(obj, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value = "/{storeId}", method = RequestMethod.GET)
    ResponseEntity<Object> getStore(@PathVariable("storeId") Long storeId) {
        ResponseEntity<Object> response = null;
        if(storeId == null || storeId <= 0){
            response = new ResponseEntity<>("Invalid Product Id", HttpStatus.BAD_REQUEST);
        }
        Optional<Store> showStore = storeRepository.findById(storeId);
        if (showStore.isPresent()) {
            response = new ResponseEntity<>(StoreJsonCreator.storeJson(showStore.get()), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("Product Not Found", HttpStatus.NOT_FOUND);
        }
        return response;
    }


    @RequestMapping(method = RequestMethod.POST,  consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = "application/json")
    ResponseEntity<Object> getNewProduct(@RequestBody MultiValueMap<String, String> paramMap, @RequestHeader("X-OBSERVATORY-AUTH") String token) {
        List<Token> showToken = tokenRepository.findToken(token);
        if (showToken.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        JSONObject jsonStore = UrlToJson.getJson(paramMap);
        jsonStore.put("tags", "["+jsonStore.getAsString("tags")+"]");
        Store newStore = StoreJsonCreator.storeFromJson(jsonStore);
        if (newStore == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        if (StoreJsonCreator.storeIsPresent((ArrayList<Store>) storeRepository.findByName(newStore.getName()), newStore)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Store createdStore = storeRepository.save(newStore);
        ResponseEntity<Object> response = new ResponseEntity<>(StoreJsonCreator.storeJson(createdStore), HttpStatus.OK);
        return response;
    }


    @RequestMapping(value = "/{storeId}", method = RequestMethod.PUT, consumes = "application/json")
    ResponseEntity<Object> updateStore(@PathVariable("storeId") Long storeId, @RequestBody JSONObject jsonStore, @RequestHeader("X-OBSERVATORY-AUTH") String token) {
        List<Token> showToken = tokenRepository.findToken(token);
        if (showToken.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        ResponseEntity<Object> response = null;
        if(storeId == null || storeId <= 0){
            response = new ResponseEntity<>("Invalid Product Id", HttpStatus.BAD_REQUEST);
        }
        Optional<Store> showStore = storeRepository.findById(storeId);
        if (!showStore.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        //Store existing = showStore.get();
        Store updatedStore = StoreJsonCreator.storeFromJson(jsonStore);
        if (updatedStore == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        updatedStore.setId(storeId);
        Store createdStore = storeRepository.save(updatedStore);
        response = new ResponseEntity<>(StoreJsonCreator.storeJson(createdStore), HttpStatus.CREATED);
        return response;
    }


    @RequestMapping(value = "/{storeId}", method = RequestMethod.DELETE)
    ResponseEntity<Object> deleteStore(@PathVariable("storeId") Long storeId, HttpServletRequest request, @RequestHeader("X-OBSERVATORY-AUTH") String token) {
        List<Token> showToken = tokenRepository.findToken(token);
        if (showToken.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        ResponseEntity<Object> response = null;
        if(storeId == null || storeId <= 0){
            response = new ResponseEntity<>("Invalid Product Id", HttpStatus.BAD_REQUEST);
        }
        Optional<Store> showStore = storeRepository.findById(storeId);
        if (!showStore.isPresent()) {
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
            Store tempproduct = showStore.get();
            tempproduct.setWithdrawn(true);
            storeRepository.save(tempproduct);
            JSONObject retj = new JSONObject();
            retj.put("message", "ok");
            response = new ResponseEntity<>(retj, HttpStatus.OK);
            return response; 
        }
        try {
            Object[] sqlObj = new Object[1];
            sqlObj[0] = storeId;
            String sql1 = "DELETE FROM info WHERE store_id = ?";
            String sql2 = "DELETE FROM store WHERE store_id = ?";
            infoTemplate.update(sql1, sqlObj);
            infoTemplate.update(sql2, sqlObj);
            //storeRepository.removeById(storeId);
            JSONObject retj = new JSONObject();
            retj.put("message", "ok");
            response = new ResponseEntity<>(retj, HttpStatus.OK);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            response = new ResponseEntity<>("Shop Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response = new ResponseEntity<>("An error has occured", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @RequestMapping(value = "/{storeId}", method = RequestMethod.PATCH, consumes = "application/json", produces = "application/json")
    ResponseEntity<Object> patchStore(@RequestBody JSONObject jsonStore, @PathVariable("storeId") Long storeId, @RequestHeader("X-OBSERVATORY-AUTH") String token) {
        List<Token> showToken = tokenRepository.findToken(token);
        if (showToken.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        ResponseEntity<Object> response = null;
        if(storeId == null || storeId <= 0){
            response = new ResponseEntity<>("Invalid Product Id", HttpStatus.BAD_REQUEST);
        }
        Optional<Store> showStore = storeRepository.findById(storeId);
        if (!showStore.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        if (jsonStore.keySet().size() != 1) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Store existing = showStore.get();
        if (!jsonStore.keySet().contains("name")) {
            jsonStore.put("name", existing.getName());
        }
        if (!jsonStore.keySet().contains("tags")) {
            jsonStore.put("tags", existing.getTags());
        }
        if (!jsonStore.keySet().contains("lng")) {
            jsonStore.put("lng", existing.getLng());
        }
        if (!jsonStore.keySet().contains("lat")) {
            jsonStore.put("lat", existing.getLat());
        }
        if (!jsonStore.keySet().contains("address")) {
            jsonStore.put("address", existing.getAddress());
        }
        if (!jsonStore.keySet().contains("withdrawn")) {
            jsonStore.put("withdrawn", existing.getWithdrawn());
        }


        Store updatedStore = StoreJsonCreator.storeFromJson(jsonStore);
        updatedStore.setId(existing.getId());
        Store createdStore = storeRepository.save(updatedStore);
        response = new ResponseEntity<>(StoreJsonCreator.storeJson(createdStore), HttpStatus.OK);
        return response;
    }
}