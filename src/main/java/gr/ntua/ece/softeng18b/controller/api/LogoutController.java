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
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

//import gr.ntua.ece.softeng18b.repositories.InfoRepository;
import gr.ntua.ece.softeng18b.repositories.ProductRepository;
import net.minidev.json.JSONObject;


@RestController
@RequestMapping("/observatory/api/logout")
public class LogoutController {

    @Autowired
    private TokenRepository tokenRepository;


    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    ResponseEntity<Object> logout(@RequestHeader(value="X-OBSERVATORY-AUTH") String token) {
        tokenRepository.deleteToken(token);
        JSONObject obj = new JSONObject();
        obj.put("message", "OK");
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }
}