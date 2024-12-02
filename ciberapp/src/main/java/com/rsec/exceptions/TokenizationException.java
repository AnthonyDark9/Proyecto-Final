package com.rsec.exceptions;

/**
 * Excepción personalizada para errores relacionados con la tokenización.
 * Se utiliza para manejar errores específicos que ocurren durante el proceso
 * de tokenización o detokenización de datos.
 *
 * @author marco vences
 */
public class TokenizationException extends Exception {

    /**
     * Constructor para crear una instancia de `TokenizationException`.
     *
     * @param message Mensaje descriptivo del error ocurrido.
     * @param cause   La causa original de la excepción (opcional).
     */
    public TokenizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
