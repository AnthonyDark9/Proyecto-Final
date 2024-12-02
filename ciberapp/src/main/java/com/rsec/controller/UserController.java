package com.rsec.controller;

import com.rsec.exceptions.ResponseException;
import com.rsec.exceptions.TokenizationException;
import com.rsec.model.Usuario;
import com.rsec.repository.UsuarioRepository;
import com.rsec.service.CipherService;
import com.rsec.service.MaskingService;
import com.rsec.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Controlador REST para gestionar operaciones relacionadas con los usuarios.
 * Proporciona endpoints para crear, obtener, cifrar, descifrar, y gestionar tokens de usuarios.
 * @author marco vences
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UsuarioRepository usuarioRepository;
    private final CipherService cipherService;
    private final MaskingService maskingService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class); // Configuración del logger

    @Autowired
    private TokenService tokenService;

    /**
     * Constructor del controlador.
     *
     * @param userRepository Repositorio para gestionar operaciones con la base de datos de usuarios.
     * @param cipherService Servicio para cifrar y descifrar datos sensibles.
     * @param maskingService Servicio para aplicar enmascaramiento a datos sensibles.
     */
    public UserController(UsuarioRepository userRepository, CipherService cipherService, MaskingService maskingService) {
        this.usuarioRepository = userRepository;
        this.cipherService = cipherService;
        this.maskingService = maskingService;
    }

    /**
     * Crea un nuevo usuario con datos cifrados, enmascarados y un token generado.
     *
     * @param user Objeto `Usuario` que contiene los datos del usuario a crear.
     * @return El usuario creado y guardado en la base de datos.
     * @throws Exception Si ocurre un error durante el proceso de cifrado o enmascaramiento.
     */
    @PostMapping
    public Usuario createUser(@RequestBody Usuario user) throws Exception {
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("El username y el email no pueden estar vacíos.");
        }

        // Cifrar los datos sensibles
        user.setCipherData(cipherService.encrypt(user.getUsername(), user.getEmail(), user.getName()));

        // Generar un token
        String token = TokenService.tokenize(user.getUsername(), user.getEmail(), user.getName());
        user.setToken(token);

        // Aplicar enmascaramiento
        user.setUsername(maskingService.maskUserData(user.getUsername()));
        user.setEmail(maskingService.maskUserData(user.getEmail()));
        user.setName(maskingService.maskUserData(user.getName()));

        // Guardar el usuario en la base de datos
        return usuarioRepository.save(user);
    }

    /**
     * Obtiene todos los usuarios de la base de datos.
     *
     * @return Lista de todos los usuarios.
     */
    @GetMapping("/obtenerUsuarios")
    public List<Usuario> getAllUsers() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id Identificador único del usuario.
     * @return El usuario encontrado o un mensaje de error si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        Optional<Usuario> user = usuarioRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            ResponseException errorResponse = new ResponseException("Usuario con ID " + id + " no encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Desencripta datos cifrados proporcionados en el cuerpo de la solicitud.
     *
     * @param payload Mapa que contiene los datos cifrados bajo la clave `cipherData`.
     * @return Los datos descifrados o un mensaje de error si falla el proceso.
     */
    @PostMapping("/decrypt")
    public ResponseEntity<String> decryptData(@RequestBody Map<String, String> payload) {
        String cipherData = payload.get("cipherData");
        if (cipherData == null || cipherData.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: El campo 'cipherData' no puede estar vacío.");
        }
        try {
            String decryptedJson = cipherService.decrypt(cipherData);
            return ResponseEntity.ok(decryptedJson);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al descifrar los datos: " + e.getMessage());
        }
    }

    /**
     * Detokeniza un token proporcionado y devuelve los datos originales.
     *
     * @param requestBody Mapa que contiene el token bajo la clave `token`.
     * @return Los datos originales del token o un mensaje de error si falla el proceso.
     */
    @PostMapping("/detokenize")
    public ResponseEntity<Map<String, String>> detokenize(@RequestBody Map<String, String> requestBody) {
        try {
            String token = requestBody.get("token");
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El campo 'token' no puede estar vacío."));
            }
            Map<String, String> result = tokenService.detokenize(token);
            return ResponseEntity.ok(result);
        } catch (TokenizationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
