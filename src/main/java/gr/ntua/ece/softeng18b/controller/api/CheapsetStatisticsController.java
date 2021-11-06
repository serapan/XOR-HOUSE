package gr.ntua.ece.softeng18b.controller.api;

import gr.ntua.ece.softeng18b.model.Role;
import gr.ntua.ece.softeng18b.model.User;
import gr.ntua.ece.softeng18b.repositories.InfoRepository;
import gr.ntua.ece.softeng18b.repositories.ProductRepository;
import gr.ntua.ece.softeng18b.repositories.StoreRepository;
import gr.ntua.ece.softeng18b.repositories.UserRepository;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/observatory/api/cheapsetstatistics")
public class CheapsetStatisticsController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private InfoRepository infoRepository;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<Object> getStatistics(){
        JSONObject ret = new JSONObject();
        ret.put("Products", productRepository.howMany());
        ret.put("Stores", storeRepository.howMany());
        ret.put("Users", userRepository.howMany());
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @RequestMapping(value = "/pie", method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<Object> getStoreStatistics(HttpServletRequest request) {
        Principal p = request.getUserPrincipal();
        if(p == null){
            return new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
        }
        String username = p.getName();
        Optional<User> showUser = userRepository.findByUsername(username);
        if (!showUser.isPresent()) {
            return new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
        }
        User usr = showUser.get();
        Role role = usr.getRole();
        String rolestr = role.getRole();
        if (rolestr.equals("subscribed")) {
            return new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
        }
        JSONArray ret = new JSONArray();
        JSONObject obj;
        List<Map<String, Object>> s = infoRepository.findPercentagesPerStore();
        for (Map<String, Object> i: s) {
            obj = new JSONObject();
            obj.put("label", i.get("name"));
            obj.put("value", i.get("percentage").toString());
            ret.add(obj);
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @RequestMapping(value = "/productnum/{storeId}", method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<Object> getStoreNumStatistics(@PathVariable("storeId") Long storeId, HttpServletRequest request) {
        Principal p = request.getUserPrincipal();
        if(p == null){
            return new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
        }
        String username = p.getName();
        Optional<User> showUser = userRepository.findByUsername(username);
        if (!showUser.isPresent()) {
            return new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
        }
        User usr = showUser.get();
        Role role = usr.getRole();
        String rolestr = role.getRole();
        if (rolestr.equals("subscribed")) {
            return new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
        }
        JSONObject obj = new JSONObject();
        List<Map<String, Object>> s = infoRepository.findNumProductsOfStore(storeId);
        obj.put("count", s.get(0).get("counter"));
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/productnumcat/{storeId}", method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<Object> getStoreNumStatisticsPerCategory(@PathVariable("storeId") Long storeId, HttpServletRequest request) {
        Principal p = request.getUserPrincipal();
        if(p == null){
            return new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
        }
        String username = p.getName();
        Optional<User> showUser = userRepository.findByUsername(username);
        if (!showUser.isPresent()) {
            return new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
        }
        User usr = showUser.get();
        Role role = usr.getRole();
        String rolestr = role.getRole();
        if (rolestr.equals("subscribed")) {
            return new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
        }
        JSONObject obj;
        JSONArray ret = new JSONArray();
        List<Map<String, Object>> s = infoRepository.findNumProductsOfStorePerCategory(storeId);
        for (Map<String, Object> i: s) {
            obj = new JSONObject();
            obj.put("label", i.get("category"));
            obj.put("value", i.get("counter").toString());
            ret.add(obj);
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @RequestMapping(value = "/goodstores", method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<Object> getStoreNumStatistics(HttpServletRequest request) {
        Principal p = request.getUserPrincipal();
        if(p == null){
            return new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
        }
        String username = p.getName();
        Optional<User> showUser = userRepository.findByUsername(username);
        if (!showUser.isPresent()) {
            return new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
        }
        User usr = showUser.get();
        Role role = usr.getRole();
        String rolestr = role.getRole();
        if (rolestr.equals("subscribed")) {
            return new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
        }
        JSONObject obj;
        JSONArray ret = new JSONArray();
        List<Map<String, Object>> s = infoRepository.findThreeFirstStores2();
        int a = 1;
        for (Map<String, Object> i: s) {
            obj = new JSONObject();
            obj.put("label", i.get("name") + " (" + i.get("address") + ")");
            //obj.put("store_id", i.get("store_id"));
            obj.put("value", i.get("counter").toString());
            //obj.put("order", Integer.toString(a));
            a++;
            ret.add(obj);
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }
}
