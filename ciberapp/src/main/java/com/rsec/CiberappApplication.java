package com.rsec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Clase principal de la aplicación Spring Boot.
 * Esta clase contiene el punto de entrada de la aplicación donde se inicializa y arranca el contexto de Spring.
 *
 * @author marco vences
 */
@SpringBootApplication
public class CiberappApplication {

    /**
     * Método principal que ejecuta la aplicación Spring Boot.
     *
     * Este método invoca a SpringApplication.run para iniciar la aplicación y cargar el contexto de Spring.
     *
     * @param args Argumentos de la línea de comandos que pueden ser utilizados para configurar la aplicación.
     */
    public static void main(String[] args) {
        SpringApplication.run(CiberappApplication.class, args);
    }
}
