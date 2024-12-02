package com.rsec.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsec.exceptions.TokenizationException;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;



/**
 * Servicio para la generación y descifrado de tokens cifrados.
 * Los tokens son generados utilizando el algoritmo AES en modo GCM con un tamaño de clave de 128 bits
 * y un vector de inicialización (IV) para garantizar la seguridad de los datos sensibles.
 *
 * @author marco vences
 */
@Service
public class TokenService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_SIZE = 12; // Tamaño del vector de inicialización en bytes
    private static final int TAG_SIZE = 128; // Tamaño del tag de autenticación en bits
    private static final byte[] SECRET_KEY = "1234567890123456".getBytes(); // Clave secreta para el cifrado

    /**
     * Genera un token cifrado basado en el username, email y name proporcionados.
     *
     * @param username Nombre de usuario.
     * @param email    Correo electrónico.
     * @param name     Nombre completo.
     * @return Token cifrado codificado en Base64.
     * @throws TokenizationException Si ocurre algún error durante la tokenización.
     */
    public static String tokenize(String username, String email, String name) throws TokenizationException {
        try {
            // Crear un JSON con los datos
            String json = String.format("{\"username\":\"%s\",\"email\":\"%s\",\"name\":\"%s\"}", username, email, name);

            // Generar el IV
            byte[] iv = generateIV();

            // Inicializar el cifrado
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(SECRET_KEY, ALGORITHM), parameterSpec);

            // Cifrar los datos
            byte[] encryptedData = cipher.doFinal(json.getBytes());

            // Combinar IV y datos cifrados
            byte[] combined = new byte[IV_SIZE + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, IV_SIZE);
            System.arraycopy(encryptedData, 0, combined, IV_SIZE, encryptedData.length);

            // Codificar en Base64 y devolver
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new TokenizationException("Error durante la tokenización", e);
        }
    }

    /**
     * Descifra un token cifrado para recuperar los datos originales.
     *
     * @param token Token cifrado codificado en Base64.
     * @return Mapa con las claves y valores recuperados del token (username, email, name).
     * @throws TokenizationException Si ocurre algún error durante la detokenización.
     */
    public static Map<String, String> detokenize(String token) throws TokenizationException {
        try {
            byte[] decodedToken = Base64.getDecoder().decode(token);

            // Validar longitud del token
            if (decodedToken.length < IV_SIZE) {
                throw new TokenizationException("El token es demasiado corto para contener un IV válido.", new Exception());
            }

            // Extraer IV y datos cifrados
            byte[] iv = new byte[IV_SIZE];
            byte[] encryptedBytes = new byte[decodedToken.length - IV_SIZE];
            System.arraycopy(decodedToken, 0, iv, 0, IV_SIZE);
            System.arraycopy(decodedToken, IV_SIZE, encryptedBytes, 0, encryptedBytes.length);

            // Inicializar el descifrado
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(SECRET_KEY, ALGORITHM), parameterSpec);

            // Descifrar los datos
            String json = new String(cipher.doFinal(encryptedBytes));

            // Analizar el JSON para obtener los datos originales
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            throw new TokenizationException("Error durante la detokenización", e);
        }
    }

    /**
     * Genera un vector de inicialización (IV) aleatorio.
     *
     * @return Un arreglo de bytes que representa el IV.
     */
    private static byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        new java.security.SecureRandom().nextBytes(iv);
        return iv;
    }
}
