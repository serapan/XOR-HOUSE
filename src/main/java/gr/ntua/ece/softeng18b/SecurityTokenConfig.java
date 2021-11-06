package gr.ntua.ece.softeng18b;

import gr.ntua.ece.softeng18b.security.JwtAuthenticationFilter;
import gr.ntua.ece.softeng18b.security.JwtAuthorizationFilter;
import gr.ntua.ece.softeng18b.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;


@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter{
    
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityTokenConfig(UserDetailsServiceImpl userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(authenticationManager());
        jwtAuthFilter.setFilterProcessesUrl("/observatory/api/login");
        http.authorizeRequests()
            .antMatchers(HttpMethod.POST, "/observatory/api/login").permitAll()
            .antMatchers(HttpMethod.GET,"/observatory/api/products").permitAll()
            .antMatchers(HttpMethod.GET,"/observatory/api/products/*").permitAll()
            .antMatchers(HttpMethod.POST, "/observatory/api/products").hasAnyAuthority("subscribed", "admin", "merchant")
            .antMatchers(HttpMethod.PUT, "/observatory/api/products/*").hasAnyAuthority("subscribed", "admin", "merchant")
            .antMatchers(HttpMethod.PATCH, "/observatory/api/products/*").hasAnyAuthority("subscribed", "admin", "merchant")
            .antMatchers(HttpMethod.DELETE, "/observatory/api/products/*").hasAnyAuthority("subscribed", "admin", "merchant")
            .antMatchers(HttpMethod.GET,"/observatory/api/shops").permitAll()
            .antMatchers(HttpMethod.GET,"/observatory/api/shops/*").permitAll()
            .antMatchers(HttpMethod.POST, "/observatory/api/shops").hasAnyAuthority("subscribed", "admin", "merchant")
            .antMatchers(HttpMethod.PUT, "/observatory/api/shops/*").hasAnyAuthority("subscribed", "admin", "merchant")
            .antMatchers(HttpMethod.PATCH, "/observatory/api/shops/*").hasAnyAuthority("subscribed", "admin", "merchant")
            .antMatchers(HttpMethod.DELETE, "/observatory/api/shops/*").hasAnyAuthority("subscribed", "admin", "merchant")
            .antMatchers(HttpMethod.GET,"/observatory/api/prices").permitAll()
            .antMatchers(HttpMethod.POST, "/observatory/api/prices").hasAnyAuthority("subscribed", "admin", "merchant")
                .and() 
            .addFilter(jwtAuthFilter)
            .addFilter(new JwtAuthorizationFilter(authenticationManager(), userDetailsService))
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}