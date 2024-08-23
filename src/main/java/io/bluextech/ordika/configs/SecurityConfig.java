package io.bluextech.ordika.configs;
/* Created by limxuanhui on 12/8/24 */

import io.bluextech.ordika.utils.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.oauth2ResourceServer(
//                j -> j.jwt(k -> k.jwkSetUri("http://localhost:8081/oauth2/jwks"))
//        ).authorizeHttpRequests(r -> r.anyRequest().authenticated());
//        return http
//                .build();
//        http.formLogin(Customizer.withDefaults());
//        http.httpBasic(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(request ->
                request.requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated());

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterAt(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        var uds = new InMemoryUserDetailsManager();
        UserDetails u1 = User.withUsername("Joseph").password(passwordEncoder().encode("password123")).build();
        uds.createUser(u1);
        return uds;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
////        return (web) -> web.ignoring().requestMatchers("/").requestMatchers(RequestMatcher.MatchResult.)
//        return null;
//    }
}
