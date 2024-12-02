package com.rsec.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 *
 * Clase que representa una respuesta estándar para manejar excepciones.
 * Se utiliza para devolver mensajes de error en un formato estructurado cuando
 * ocurren excepciones en la aplicación.
 *
 * @author marco vences
 */
@AllArgsConstructor // Genera un constructor con todos los atributos como parámetros.
@NoArgsConstructor  // Genera un constructor vacío.
@Data               // Genera automáticamente los métodos getters, setters, equals, hashCode y toString.
public class ResponseException {

    /**
     * Mensaje descriptivo del error ocurrido.
     */
    private String message;
}
