package com.rsec.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un usuario en el sistema.
 * Almacena información básica del usuario junto con datos cifrados y un token
 * que pueden ser utilizados para diversas operaciones de seguridad.
 *
 * @author marco vences
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    /**
     * Identificador único del usuario.
     * Generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de usuario.
     * Debe ser único y no puede ser nulo. Esta restricción se aplica a nivel de base de datos.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Nombre completo del usuario.
     * No puede ser nulo.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Correo electrónico del usuario.
     * Debe ser único y no puede ser nulo. Esta restricción se aplica a nivel de base de datos.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Datos sensibles cifrados asociados al usuario.
     * Se almacenan en formato de gran objeto (LOB) para manejar cadenas de gran tamaño.
     */
    @Lob
    private String cipherData;

    /**
     * Token generado para el usuario.
     * Almacena información en formato de gran objeto (LOB).
     */
    @Lob
    private String token;
}
