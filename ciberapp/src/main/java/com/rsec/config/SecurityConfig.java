package com.rsec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * Clase de configuración de seguridad para la aplicación.
 * Configura una cadena de filtros de seguridad que permite todas las solicitudes
 * y desactiva la protección CSRF para facilitar las pruebas.
 *
 * @author marco vences
 */
@Configuration
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad de la aplicación.
     *
     * Este método desactiva la protección CSRF y permite todas las solicitudes
     * sin necesidad de autenticación. Es útil para entornos de desarrollo o pruebas.
     *
     * @param http Objeto de configuración de seguridad HTTP proporcionado por Spring Security.
     * @return Una instancia de `SecurityFilterChain` configurada.
     * @throws Exception Si ocurre un error al construir la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // Deshabilita CSRF para permitir pruebas con herramientas como Postman.
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll() // Permitir todas las solicitudes sin autenticación.
                );
        return http.build();
    }
}