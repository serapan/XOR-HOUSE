package gr.ntua.ece.softeng18b.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import gr.ntua.ece.softeng18b.services.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService){
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException{
        String header = request.getHeader(SecurityConstants.HEADER_STRING);
        if(header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)){
            chain.doFilter(request, response);
			return;
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuth = getAuthenticationToken(request);
        if(usernamePasswordAuth != null){
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuth);
            chain.doFilter(request, response);
        }
        else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("The token is expired and not valid anymore.");
        }
        
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(HttpServletRequest request)
            throws ExpiredJwtException {
        String token = request.getHeader(SecurityConstants.HEADER_STRING);
        if(token == null){
            return null;
        }
        try{
            String username = Jwts.parser()
                            .setSigningKey(SecurityConstants.SECRET)
                            .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                            .getBody()
                            .getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return username!=null ? new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities()) : null;
        }
        catch (ExpiredJwtException e){
            return null;
        }
    }
}