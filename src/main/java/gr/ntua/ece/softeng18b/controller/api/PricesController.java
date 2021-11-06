package gr.ntua.ece.softeng18b.controller.api;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
//import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import gr.ntua.ece.softeng18b.model.*;
import gr.ntua.ece.softeng18b.repositories.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.RequestHeader;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@RestController
@RequestMapping("/observatory/api/prices")
public class PricesController {

    
    @Autowired
    private Info2Repository infoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    JdbcTemplate infoTemplate;

    @Autowired
    private TokenRepository tokenRepository;

    public static List<Date> getDatesBetween(Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);
        endCalendar.add(Calendar.DATE, 1);

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }


    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<Object> getAllProducts(@RequestParam(name="start", required=false) Integer start, @RequestParam(name="count", required=false) Integer count, @RequestParam(name="geoDist", required=false) Integer geoDist, @RequestParam(name="geoLng", required=false) Double geoLng, @RequestParam(name="geoLat", required=false) Double geoLat, @RequestParam(name="dateFrom", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") Date dateFrom, @RequestParam(name="dateTo", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") Date dateTo, @RequestParam(name="shops", required=false) List<String> shops, @RequestParam(name="products", required=false) List<String> products, @RequestParam(name="tags", required=false) List<String> tags, @RequestParam(name="sort", required=false) List<String> sort){
        ResponseEntity<Object> response = null;
        String dateFromStr = null;
        String dateToStr = null;
        int size = 4;
        if(start == null){
            start = 0;
        }
        if(count == null){
            count = 20;
        }
        if(geoDist!=null){
            size++;
            if(geoLng == null || geoLat == null){
                response = new ResponseEntity<>("geoDist and geoLng and geoLat must all have values or none of them can", HttpStatus.BAD_REQUEST);
                return response;
            }
        }
        if(geoLng!=null){
            size = size + 2;
            if(geoDist == null || geoLat == null){
                response = new ResponseEntity<>("geoDist and geoLng and geoLat must all have values or none of them can", HttpStatus.BAD_REQUEST);
                return response;
            }
        }
        if(geoLat!=null){
            size = size + 2;
            if(geoLng == null || geoDist == null){
                response = new ResponseEntity<>("geoDist and geoLng and geoLat must all have values or none of them can", HttpStatus.BAD_REQUEST);
                return response;
            }
        }
        if((dateFrom!=null && dateTo==null) || (dateTo!=null && dateFrom==null)){
            response = new ResponseEntity<>("dateFrom and dateTo must both have values or none of them can", HttpStatus.BAD_REQUEST);
            return response;
        }
        else{
            if(dateFrom==null && dateTo==null){
                LocalDate localDate = LocalDate.now();
                dateFromStr = localDate.toString();
                dateToStr = localDate.toString();
            }
            else{
                if(dateFrom.after(dateTo)){
                    response = new ResponseEntity<>("dateFrom must have smaller value than dateTo", HttpStatus.BAD_REQUEST);
                    return response;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dateFromStr = sdf.format(dateFrom);
                dateToStr = sdf.format(dateTo);
            }
        }
        if(sort == null){
            sort = new ArrayList<String>();
            sort.add(0,"price|ASC" );
        }
        for(String sortVal : sort){
            if(!sortVal.matches("price\\|ASC|price\\|DESC|dist\\|ASC|dist\\|DESC|date\\|ASC|date\\|DESC")){
                response = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                return response;
            }
        }
        if(!dateFromStr.matches("\\d{4}-\\d{2}-\\d{2}") || !dateToStr.matches("\\d{4}-\\d{2}-\\d{2}") || count<=0 || start < 0 ){
            response = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            return response;
        }
        if(products!=null){
            size = size + products.size();
        }
        if(shops!=null){
            size = size + shops.size();
        }
        if(tags!=null){
            size = size + 2*tags.size();
        }
        int i = -1;
        Object[] sqlArr = new Object[size];
        String sqlSelect = "SELECT price, product.sku, product.model, product.tags as p_tags, date, store.store_id, store.name, store.tags as s_tags, store.address " ;
        String sqlFrom = "FROM product, info2, store ";
        String sqlWhere = "WHERE ";
        String sqlOrder = "ORDER BY ";
        String sqlPage = "LIMIT " + "?" + " OFFSET " + "?";
        if(geoDist!=null && geoLat!=null && geoLng !=null){
            sqlArr[++i] = geoLat;
            sqlArr[++i] = geoLng;
            sqlArr[++i] = geoLat;
            sqlArr[++i] = geoLng;
            sqlArr[++i] = geoDist;
            sqlSelect = sqlSelect + ", distanceSphericalLaw(store.lat, store.lng, ?, ?) as dist ";
            sqlWhere = sqlWhere + "distanceSphericalLaw(store.lat, store.lng, ?, ?) < ? AND ";
        }
        sqlArr[++i] = dateFromStr;
        sqlArr[++i] = dateToStr;
        //sqlArr[++i] = dateFromStr;
        //sqlArr[++i] = dateToStr;
        //sqlArr[++i] = dateFromStr;
        //sqlArr[++i] = dateToStr;
        sqlWhere = sqlWhere + "product.sku = info2.sku AND store.store_id = info2.store_id AND ( date BETWEEN ? AND ? ) "; //(date_from BETWEEN ? AND ?) OR (date_to BETWEEN ? AND ?) OR (date_from <= ? AND date_to >= ?)
        if(products!=null){
            String temp = "(";
            for(String id : products){
                sqlArr[++i] = Long.parseLong(id);
                temp = temp + "?" + ", "; 
            }
            temp = temp.substring(0, temp.length()-2) + ")";
            sqlWhere = sqlWhere + "AND product.sku IN " + temp + " ";
        }
        if(shops!=null){
            String temp = "(";
            for(String id : shops){
                sqlArr[++i] = Long.parseLong(id);
                temp = temp + "?" + ", "; 
            }
            temp = temp.substring(0, temp.length()-2) + ")";
            sqlWhere = sqlWhere + "AND store.store_id IN " + temp + " ";
        }
        if(tags!=null){
            String temp1 = "product.tags LIKE ";
            String temp2 = "product.tags LIKE ";
            String temp3 = "store.tags LIKE ";
            String temp4 = "store.tags LIKE ";
            for(String tag : tags){
                sqlArr[++i] = "%" + tag + "%";
                sqlArr[++i] = "%" + tag + "%";
                temp1 = temp1 + "?" + " OR " + temp2 ;
                temp3 = temp3 + "?" + " OR " + temp4 ;
            }
            temp1 = temp1.substring(0, temp1.length()-22);
            temp3 = temp3.substring(0, temp3.length()-19);
            sqlWhere = sqlWhere + "AND ( (" + temp1 + ") OR (" + temp3 + ")) ";
        }
        HashMap<String, String> sortingRules = new HashMap<>();
        for(String way : sort){
            String temp[] = way.split("\\|");
            sortingRules.put(temp[0], temp[1]);
        }
        if(sortingRules.containsKey("price")){
            sqlOrder = sqlOrder + "price" + " " + sortingRules.get("price") + ", ";
        }
        if(sortingRules.containsKey("dist")){
            sqlOrder = sqlOrder + "dist" + " " + sortingRules.get("dist") + ", ";
        }
        if(sortingRules.containsKey("date")){
            sqlOrder = sqlOrder + "date" + " " + sortingRules.get("date") + ", ";
        }
        sqlArr[++i] = count;
        sqlArr[++i] = start;
        sqlOrder = sqlOrder.substring(0, sqlOrder.length()-2) + " ";
        String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder + sqlPage;
        List<Map<String, Object>> results = infoTemplate.queryForList(sql, sqlArr);
        if(results.size() == 0){
            response = new ResponseEntity<>("No Products Found With the Specified Criteria", HttpStatus.NOT_FOUND);
            return response;
        }
        JSONObject ret = new JSONObject();
        JSONArray arr = new JSONArray();
        for(Map<String, Object> result : results){
            JSONObject obj = new JSONObject();
            obj.put("price", result.get("price"));
            Date date = (Date) result.get("date");
            obj.put("date", date.toString());
            obj.put("productName", result.get("model"));
            obj.put("productId", result.get("sku"));
            String temp = (String) result.get("p_tags");
            String[] parts = temp.split(",");
            List<String> pTagsList = new ArrayList<>();
            for (String tempst: parts) {
                pTagsList.add(tempst.trim());
            }
            obj.put("productTags", pTagsList);
            obj.put("shopId", result.get("store_id"));
            obj.put("shopName", result.get("name"));
            temp = (String) result.get("s_tags");
            parts = temp.split(",");
            List<String> sTagsList = new ArrayList<>();
            for (String tempst: parts) {
                sTagsList.add(tempst.trim());
            }
            obj.put("shopTags", sTagsList);
            obj.put("shopAddress", result.get("address"));
            try{
                Double distance = (Double) result.get("dist");
                int newDistance = (int) Math.round(distance);
                Integer d = Integer.valueOf(newDistance);
                obj.put("shopDist", d);
            }
            catch (NullPointerException n){
                ;
            }
             arr.add(obj);
        }
        ret.put("prices", arr);
        ret.put("start", start);
        ret.put("count", count);
        ret.put("total", results.size());
        response = new ResponseEntity<>(ret, HttpStatus.OK);
        return response; 
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = "application/json")
    ResponseEntity<Object> insertProduct(@RequestBody MultiValueMap<String, String> paramMap, HttpServletRequest request, @RequestHeader("X-OBSERVATORY-AUTH") String token){
        List<Token> showToken = tokenRepository.findToken(token);
        if (showToken.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        JSONObject jsonInfo = UrlToJson.getJson(paramMap);
        ResponseEntity<Object> response = null;
        Principal p = request.getUserPrincipal();
        if(p == null){
            response = new ResponseEntity<>("No authorized user made this request", HttpStatus.UNAUTHORIZED);
            return response;
        }
        String username = p.getName();
        Optional<User> showUser = userRepository.findByUsername(username);
        User infoUser = showUser.get();
        if(!jsonInfo.containsKey("price") || !jsonInfo.containsKey("dateFrom") || !jsonInfo.containsKey("dateTo") || !jsonInfo.containsKey("productId") || !jsonInfo.containsKey("shopId")){
            response = new ResponseEntity<>("Missing Parameters", HttpStatus.BAD_REQUEST);
            return response;
        }
        String priceStr = null;
        String dateFromStr = null;
        String dateToStr = null;
        String productIdStr = null;
        String storeIdStr = null;
        try{
            priceStr = jsonInfo.getAsString("price");
            dateFromStr = jsonInfo.getAsString("dateFrom");
            dateToStr = jsonInfo.getAsString("dateTo");
            productIdStr = jsonInfo.getAsString("productId");
            storeIdStr = jsonInfo.getAsString("shopId");
        }
        catch (Exception e){
            response = new ResponseEntity<>("All Parameters Must Have Values", HttpStatus.BAD_REQUEST);
            return response;
        }
        Double price = Double.parseDouble(priceStr);
        Long productId = Long.valueOf(productIdStr);
        Long storeId = Long.valueOf(storeIdStr);
        if(price <= 0d || productId <= 0 || storeId <= 0 || !dateFromStr.matches("\\d{4}-\\d{2}-\\d{2}") || !dateToStr.matches("\\d{4}-\\d{2}-\\d{2}")){
            response = new ResponseEntity<>("Invalid Values", HttpStatus.BAD_REQUEST);
            return response;           
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dateFrom = null;
        Date dateTo = null;
        try{
           dateFrom = sdf.parse(dateFromStr);
        }
        catch (ParseException e){
            ;
        }
        try{
           dateTo = sdf.parse(dateToStr);
        }
        catch (ParseException e){
            ;
        }
        if(dateFrom.after(dateTo)){
            response = new ResponseEntity<>("dateFrom must have smaller value than dateTo", HttpStatus.BAD_REQUEST);
            return response;
        }
        Optional<Product> entryProduct = productRepository.findById(productId);
        Optional<Store> entryStore = storeRepository.findById(storeId);
        if(!entryStore.isPresent() && !entryProduct.isPresent()){
            response = new ResponseEntity<>("Product and Store Not Found", HttpStatus.NOT_FOUND);
            return response;
        }
        if(!entryStore.isPresent()){
            response = new ResponseEntity<>("Store Not Found", HttpStatus.NOT_FOUND);
            return response;
        }
        if(!entryProduct.isPresent()){
            response = new ResponseEntity<>("Product Not Found", HttpStatus.NOT_FOUND);
            return response;
        }
        LocalDate localDate = LocalDate.now();
        String today = localDate.toString();
        Date date = null;
        try{
            date = sdf.parse(today);
        }
        catch(ParseException e){
            ;
        }
        java.sql.Date sqlDateFrom = java.sql.Date.valueOf(dateFromStr);
        java.sql.Date sqlDateTo = java.sql.Date.valueOf(dateToStr);
        List<Date> interval = getDatesBetween(dateFrom, dateTo);
        List<Info2> templist;
        for (Date i: interval) {
            templist = infoRepository.getInfo2ByDate(i, entryProduct.get().getId(), entryStore.get().getId());
            if (!templist.isEmpty()) {
                return new ResponseEntity<>("price already in database", HttpStatus.BAD_REQUEST);
            }
        }
        Product infoProduct = entryProduct.get();
        Store infoStore = entryStore.get();
        Info2 currInfo;
        java.sql.Date sqlDate;
        JSONObject obj = null;
        JSONArray ret = new JSONArray();
        for (Date i: interval) {
            try {
                sqlDate = java.sql.Date.valueOf(i.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate().toString());
                currInfo = new Info2(price, infoProduct, infoUser, infoStore, sqlDate);
                infoRepository.save(currInfo);
                obj = new JSONObject();
                obj.put("price", price);
                obj.put("productName", infoProduct.getName());
                obj.put("productId", infoProduct.getId());
                obj.put("productTags", infoProduct.getTags2());
                obj.put("shopId", infoStore.getId());
                obj.put("shopName", infoStore.getName());
                obj.put("shopTags", infoStore.getTags2());
                obj.put("shopAddress", infoStore.getAddress());
                ret.add(obj);
            } catch (ConstraintViolationException e) {
                response = new ResponseEntity<>("There is a conflict with another product", HttpStatus.CONFLICT);
            }
        }
        JSONObject retj = new JSONObject();
        retj.put("prices", ret);
        retj.put("start", 0);
        retj.put("count", 0);
        retj.put("total", ret.size());
        return new ResponseEntity<>(retj, HttpStatus.OK);
    }
}
