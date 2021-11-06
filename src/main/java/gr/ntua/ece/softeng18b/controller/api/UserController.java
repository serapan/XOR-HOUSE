package gr.ntua.ece.softeng18b.controller.api;


import gr.ntua.ece.softeng18b.model.Role;
import gr.ntua.ece.softeng18b.model.User;
import gr.ntua.ece.softeng18b.repositories.RoleRepository;
import gr.ntua.ece.softeng18b.repositories.UserRepository;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/observatory/api/user")
public class UserController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @RequestMapping(value = "/signup", method = RequestMethod.POST, produces = "application/json")
    ResponseEntity<Object> signup(@RequestBody JSONObject userInfo) {
        Optional<User> showUser = userRepository.findByUsername(userInfo.getAsString("uname"));
        JSONObject obj = new JSONObject();
        if (showUser.isPresent()) {
            obj.put("message","Username already exists");
            return new ResponseEntity<>(obj, HttpStatus.BAD_REQUEST);
        }
        showUser = userRepository.findByEmail(userInfo.getAsString("email"));
        if (showUser.isPresent()) {
            obj.put("message","Email already exists");
            return new ResponseEntity<>(obj, HttpStatus.BAD_REQUEST);
        }
        String firstName = userInfo.getAsString("fname");
        String lastName = userInfo.getAsString("lname");
        String username = userInfo.getAsString("uname");
        String password = userInfo.getAsString("password");
        String salt = BCrypt.gensalt();
        password = (String) BCrypt.hashpw(password, salt);
        String email = userInfo.getAsString("email");
        Optional<Role> showRole = roleRepository.getRole(userInfo.getAsString("role"));
        Role role = showRole.get();
        User user = new User(firstName, lastName, username, password, email, role);
        userRepository.save(user);
        obj.put("message","ok");
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }
}