package com.core.hostal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import com.core.hostal.security.HostalAccessDeniedHandler;
import com.core.hostal.security.HostalAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            HostalAuthenticationProvider authenticationProvider,
            AuthenticationSuccessHandler successHandler,
            AuthenticationFailureHandler failureHandler,
            HostalAccessDeniedHandler accessDeniedHandler,
            SecurityContextRepository securityContextRepository) throws Exception {
        http
            .securityContext(context -> context.securityContextRepository(securityContextRepository))
            .authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**").permitAll()
                .requestMatchers("/", "/inicio", "/habitaciones", "/contacto").permitAll()
                .requestMatchers("/login", "/register").permitAll()
                .requestMatchers("/recuperar-contrasena", "/restablecer-contrasena").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/mis-reservas/**").hasRole("CLIENTE")
                .requestMatchers("/reservar").hasRole("CLIENTE")
                .requestMatchers(HttpMethod.GET, "/api/disponibilidad").hasRole("CLIENTE")
                .requestMatchers(HttpMethod.GET, "/api/tipos-cambio").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/reservas").hasRole("CLIENTE")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/inicio")
                .permitAll())
            .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }
}
