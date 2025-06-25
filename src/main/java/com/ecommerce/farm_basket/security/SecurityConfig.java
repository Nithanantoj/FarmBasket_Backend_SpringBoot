package com.ecommerce.farm_basket.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
     JwtRequestFilter jwtRequestFilter;

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        return  http.csrf().disable()
//                .authorizeHttpRequests((authorize) -> authorize
//                        .requestMatchers("/test").authenticated() // Require authentication for these endpoints
//                        .anyRequest().permitAll() // Allow all other requests
//                )
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login","/api/register").permitAll() // ✅ This will allow only the root "/" route publicly
                        .requestMatchers("/user/**", "/api/vegetables/get", "/api/order/**").hasAnyRole("customer", "farmer") // ✅ Role-based
                        .requestMatchers("/api/vegetables/farmer/**").hasRole("farmer")              // ✅ Role-based
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
