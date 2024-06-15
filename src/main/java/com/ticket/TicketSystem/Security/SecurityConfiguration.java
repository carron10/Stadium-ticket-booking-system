package com.ticket.TicketSystem.Security;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final Environment env;

    public SecurityConfiguration(Environment env) {
        this.env = env;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (env.acceptsProfiles("test")) {
            http.csrf().disable();
        } 
        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/admin/**").authenticated()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().permitAll()
        )
                .formLogin(withDefaults())
                .logout(withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("lucky")
                .password("dzimbi")
                .roles("Admin")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}
