package gr.ntua.ece.softeng18b.security;

import java.io.*;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.io.CharStreams;
import gr.ntua.ece.softeng18b.model.Token;
import gr.ntua.ece.softeng18b.repositories.TokenRepository;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private DataSource dataSource;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        try{
            UserCredentials creds;
            System.out.println(request.getContentType());
            if (!request.getContentType().equals("application/x-www-form-urlencoded;charset=UTF-8")) {
                creds = new ObjectMapper().readValue(request.getInputStream(), UserCredentials.class);
            }
            else {
                JSONObject obj = new JSONObject();
                obj.put("username", request.getParameter("username"));
                obj.put("password", request.getParameter("password"));
                creds = new ObjectMapper().readValue(obj.toJSONString(), UserCredentials.class);
            }
            try{
                Authentication result = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword()));
                return result;
            }
            catch (InternalAuthenticationServiceException e){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }   
            catch (AuthenticationException e){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
        ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(SecurityConstants.EXP_TIME, ChronoUnit.MILLIS);
        String token = Jwts.builder()
                        .setSubject(auth.getName())
                        .setExpiration(Date.from(expirationTime.toInstant()))
                        .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                        .compact();
        JSONObject json = new JSONObject();
        response.setContentType("application/json");
        json.put("token", SecurityConstants.TOKEN_PREFIX + token);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Token newtoken = new Token(token, timestamp);
        response.reset();
        PrintWriter out = response.getWriter();
        out.print(json);

        String s = null;
        String query = "python triplobypass.py " + token + " " + timestamp.toString();
        System.out.println(query);
        Process p = Runtime.getRuntime().exec(query);
        //Process p = Runtime.getRuntime().exec("ls");
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        // read the output from the command
        //System.out.println("Here is the standard output of the command:\n");
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        // read any errors from the attempted command
        //System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
        out.flush();
        out.close();
        return ;
    }

    private static class UserCredentials {
        private String username; 
        private String password;

        public String getUsername(){
            return this.username;
        }        
	    
        public String getPassword(){
            return this.password;
        }        

	}

}