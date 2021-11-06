package gr.ntua.ece.softeng18b;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import static org.hamcrest.Matchers.containsString;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gr.ntua.ece.softeng18b.controller.api.ProductController;
import gr.ntua.ece.softeng18b.model.Product;
import gr.ntua.ece.softeng18b.repositories.ProductRepository;
import net.minidev.json.JSONObject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.isNull;

import java.util.List;
import java.util.Optional;

import org.hamcrest.collection.IsArrayContainingInOrder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductTests {
    ProductController controller = null;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Before
    public void beforeEach() {
        controller = new ProductController();
    }

    @After
    public void afterEach() {
        controller = null;
    }

    @Test
    public void testFindById() {
        Optional<Product> product = productRepository.findById(1L);
        assertThat(product.get()).isNotNull();
    }
    
    @Test
    public void testFindByName() {
        List<Product> products = productRepository.findByName("name1");
        assertThat(products).isNotEmpty().isNotNull();
    }

    @Test
    public void testFindByCategory() {
        List<Product> products = productRepository.findByCategory("category");
        assertThat(products).isNotEmpty().isNotNull();
    }

    @Test
    public void testFindAllProducts() {
        List<Product> products = productRepository.findAllProducts(0, 20, "id|DESC");
        assertThat(products).isNotEmpty().isNotNull();
    }

    @Test
    public void testFindProducts() {
        List<Product> products = productRepository.findProducts(0, 20, "id|DESC", false);
        assertThat(products).isNotEmpty().isNotNull().doesNotHaveDuplicates();
    }

    @Test
    public void testSaveProduct() {
        Product p1 = new Product("namename", 0.0, "description1", "category", "tag1", false, "");
        productRepository.save(p1);

        assertThat(p1.getId()).isEqualTo(productRepository.findByName("namename").get(0).getId());
    }

    @Test
    public void testUpdateProduct() {
        Product p1 = productRepository.findByName("namename").get(0);
        p1.setName("newName");
        productRepository.save(p1);
        assertEquals(p1.getName(), productRepository.findByName("newName").get(0).getName());
    }

    @Test
    public void testDeleteProduct() {
        Product p1 = productRepository.findByName("newName").get(0);
        productRepository.delete(p1);
        assertThat(productRepository.findByName("newName")).isEmpty();
    }

    @Test
    public void testControllerIsValid() {
        assertNotNull(controller);
    }

    @Test
    public void testGetProducts() throws Exception{
        mockMvc.perform(get("https://localhost:8765/observatory/api/products"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("name1")))
        .andExpect(content().string(containsString("total")))
        .andExpect(content().string(containsString("start")))
        .andExpect(content().string(containsString("count")))
        .andExpect(content().string(containsString("products")))
        .andExpect(content().string(containsString("extraData")))
        .andExpect(content().string(containsString("rating")))
        .andExpect(content().string(containsString("description")))
        .andExpect(content().string(containsString("id")))
        .andExpect(content().string(containsString("tags")))
        .andExpect(content().string(containsString("name")))
        .andExpect(content().string(containsString("withdrawn")))
        .andExpect(content().string(containsString("category")));
    }

    @Test
    public void testGetProduct() throws Exception{
        mockMvc.perform(get("https://localhost:8765/observatory/api/products/1"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("extraData")))
        .andExpect(content().string(containsString("rating")))
        .andExpect(content().string(containsString("description")))
        .andExpect(content().string(containsString("id")))
        .andExpect(content().string(containsString("tags")))
        .andExpect(content().string(containsString("name")))
        .andExpect(content().string(containsString("withdrawn")))
        .andExpect(content().string(containsString("category")));
    }

    @Test
    public void testPostProductInvalidProduct() throws Exception{
        String creds = "{\"username\" : \"pig_benis\",\"password\" : \"12345\"}"; 
        System.out.println(creds);
        MvcResult result =  mockMvc.perform(post("https://localhost:8765/observatory/api/login").contentType(MediaType.APPLICATION_JSON).content(creds))
        .andExpect(status().isOk())
        .andReturn();
        String res = result.getResponse().getContentAsString();
        String token = res.substring(10, res.length()-2);
        String s = "description=description4&category=category4&tags=tag4&withdrawn=false&extraData=\"\"";
        mockMvc.perform(post("https://localhost:8765/observatory/api/products").contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).header("X-OBSERVATORY-AUTH", token).content(s))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid Product")));
    }

    @Test
    public void testPostProductAlreadyExists() throws Exception{
        String creds = "{\"username\" : \"pig_benis\",\"password\" : \"12345\"}"; 
        System.out.println(creds);
        MvcResult result =  mockMvc.perform(post("https://localhost:8765/observatory/api/login").contentType(MediaType.APPLICATION_JSON).content(creds))
        .andExpect(status().isOk())
        .andReturn();
        String res = result.getResponse().getContentAsString();
        String token = res.substring(10, res.length()-2);
        String s = "name=name1&description=description1&category=category1&tags=tag1&withdrawn=false&extraData=";
        mockMvc.perform(post("https://localhost:8765/observatory/api/products").contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).header("X-OBSERVATORY-AUTH", token).content(s))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Product Already Exists")));
    }

    @Test
    public void testPostProductNewProduct() throws Exception{
        String creds = "{\"username\" : \"pig_benis\",\"password\" : \"12345\"}"; 
        System.out.println(creds);
        MvcResult result =  mockMvc.perform(post("https://localhost:8765/observatory/api/login").contentType(MediaType.APPLICATION_JSON).content(creds))
        .andExpect(status().isOk())
        .andReturn();
        String res = result.getResponse().getContentAsString();
        String token = res.substring(10, res.length()-2);
        String s = "name=name4&description=description4&category=category4&tags=tag4&withdrawn=false&extraData=\"\"";
        mockMvc.perform(post("https://localhost:8765/observatory/api/products").contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).header("X-OBSERVATORY-AUTH", token).content(s))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("description")))
        .andExpect(content().string(containsString("tags")))
        .andExpect(content().string(containsString("name")))
        .andExpect(content().string(containsString("category")));
    }

    /*@Test
    public void testPutProduct() throws Exception{
        String creds = "{\"username\" : \"pig_benis\",\"password\" : \"12345\"}"; 
        System.out.println(creds);
        MvcResult result =  mockMvc.perform(post("https://localhost:8765/observatory/api/login").contentType(MediaType.APPLICATION_JSON).content(creds))
        .andExpect(status().isOk())
        .andReturn();
        String res = result.getResponse().getContentAsString();
        String token = res.substring(10, res.length()-2);
        String s = "{ \"name\":\"yolo\", \"description\": \"yololo\", \"category\" : \"yolololo\', \"tags\" : \"yololololo\"}";
        mockMvc.perform(put("https://localhost:8765/observatory/api/products/{productId}", 6L).contentType("application/json").header("X-OBSERVATORY-AUTH", token).content(s))
        .andExpect(status().isCreated())
        .andExpect(content().string(containsString("description")))
        .andExpect(content().string(containsString("tags")))
        .andExpect(content().string(containsString("name")))
        .andExpect(content().string(containsString("category")));
    }*/

    @Test
    public void testPatchProduct() throws Exception{
        String creds = "{\"username\" : \"pig_benis\",\"password\" : \"12345\"}"; 
        System.out.println(creds);
        MvcResult result =  mockMvc.perform(post("https://localhost:8765/observatory/api/login").contentType(MediaType.APPLICATION_JSON).content(creds))
        .andExpect(status().isOk())
        .andReturn();
        String res = result.getResponse().getContentAsString();
        String token = res.substring(10, res.length()-2);
        String s = "{ \"description\" : \"description2eee\"}";
        mockMvc.perform(patch("https://localhost:8765/observatory/api/products/{productId}", 2L).contentType("application/json").header("X-OBSERVATORY-AUTH", token).content(s))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("description")))
        .andExpect(content().string(containsString("tags")))
        .andExpect(content().string(containsString("name")))
        .andExpect(content().string(containsString("category")));
    }

    @Test
    public void testPatchProductMultipleFields() throws Exception{
        String creds = "{\"username\" : \"pig_benis\",\"password\" : \"12345\"}"; 
        System.out.println(creds);
        MvcResult result =  mockMvc.perform(post("https://localhost:8765/observatory/api/login").contentType(MediaType.APPLICATION_JSON).content(creds))
        .andExpect(status().isOk())
        .andReturn();
        String res = result.getResponse().getContentAsString();
        String token = res.substring(10, res.length()-2);
        String s = "{ \"description\" : \"description2eee\", \"name\" : \"hate and war\"}";
        mockMvc.perform(patch("https://localhost:8765/observatory/api/products/{productId}", 2L).contentType("application/json").header("X-OBSERVATORY-AUTH", token).content(s))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Only one product field can be patched")));
    }

    @Test
    public void testDeleteProductAdmin() throws Exception{
        String creds = "{\"username\" : \"pig_benis\",\"password\" : \"12345\"}"; 
        System.out.println(creds);
        MvcResult result =  mockMvc.perform(post("https://localhost:8765/observatory/api/login").contentType(MediaType.APPLICATION_JSON).content(creds))
        .andExpect(status().isOk())
        .andReturn();
        String res = result.getResponse().getContentAsString();
        String token = res.substring(10, res.length()-2);
        mockMvc.perform(delete("https://localhost:8765/observatory/api/products/{productId}", 3L).header("X-OBSERVATORY-AUTH", token))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("ok")))
        .andExpect(content().string(containsString("message")));
    }

    @Test
    public void testDeleteProductSubscribed() throws Exception{
        String creds = "{\"username\" : \"hugo_balls\",\"password\" : \"12345\"}"; 
        System.out.println(creds);
        MvcResult result =  mockMvc.perform(post("https://localhost:8765/observatory/api/login").contentType(MediaType.APPLICATION_JSON).content(creds))
        .andExpect(status().isOk())
        .andReturn();
        String res = result.getResponse().getContentAsString();
        String token = res.substring(10, res.length()-2);
        mockMvc.perform(delete("https://localhost:8765/observatory/api/products/{productId}", 2L).header("X-OBSERVATORY-AUTH", token))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("ok")))
        .andExpect(content().string(containsString("message")));
    }

    @Test
    public void testDeleteProductGuest() throws Exception{
        mockMvc.perform(delete("https://localhost:8765/observatory/api/products/{productId}", 1L))
        .andExpect(status().isForbidden());
    }
}
