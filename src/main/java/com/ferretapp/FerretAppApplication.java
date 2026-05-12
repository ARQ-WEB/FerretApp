package com.ferretapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//Este código es la clase principal de una aplicación Spring Boot que mediante la anotación @SpringBootApplication y el 
//método main inicia y levanta todo el contexto de la aplicación embebiendo un servidor web (como Tomcat) en el puerto por defecto
@SpringBootApplication
public class FerretAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(FerretAppApplication.class, args);
    }

}
