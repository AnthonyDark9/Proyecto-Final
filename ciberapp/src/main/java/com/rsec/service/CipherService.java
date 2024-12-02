package com.rsec.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.Security;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para el cifrado y descifrado de datos sensibles utilizando el algoritmo AES en modo GCM.
 * Utiliza el proveedor de seguridad BouncyCastle para proporcionar soporte avanzado de cifrado.
 *
 * @author marco vences
 */
@Service
public class CipherService {

    /**
     * Clave secreta utilizada para las operaciones de cifrado y descifrado.
     */
    private final SecretKey secretKey;

    // Inicialización del proveedor de seguridad de BouncyCastle
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Constructor que genera una clave secreta para AES con un tamaño de 256 bits.
     *
     * @throws Exception Si ocurre algún error durante la generación de la clave.
     */
    public CipherService() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "BC");
        keyGenerator.init(256); // Tamaño de la clave de 256 bits
        this.secretKey = keyGenerator.generateKey();
    }

    /**
     * Cifra los datos proporcionados (username, email y name) en formato JSON utilizando AES/GCM/NoPadding.
     *
     * @param username Nombre de usuario a cifrar.
     * @param email    Correo electrónico a cifrar.
     * @param name     Nombre del usuario a cifrar.
     * @return Cadena cifrada codificada en Base64.
     * @throws Exception Si ocurre algún error durante el proceso de cifrado.
     */
    public String encrypt(String username, String email, String name) throws Exception {
        // Crear un mapa con los campos a cifrar
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("username", username);
        dataMap.put("email", email);
        dataMap.put("name", name);

        // Convertir el mapa en un JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(dataMap);

        // Generar un IV aleatorio (12 bytes recomendado para GCM)
        byte[] iv = new byte[12];
        new java.security.SecureRandom().nextBytes(iv);

        // Inicializar el cifrado AES/GCM
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv); // 128 bits de tag size
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

        // Cifrar los datos
        byte[] encryptedData = cipher.doFinal(jsonData.getBytes());

        // Combinar el IV con los datos cifrados y codificar en Base64
        byte[] combined = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

        // Devolver el dato cifrado en Base64
        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * Descifra los datos cifrados proporcionados en formato Base64 utilizando AES/GCM/NoPadding.
     *
     * @param encryptedData Cadena cifrada en Base64 que contiene el IV y los datos cifrados.
     * @return Cadena de texto descifrada.
     * @throws Exception Si ocurre algún error durante el proceso de descifrado.
     */
    public String decrypt(String encryptedData) throws Exception {
        // Decodificar el texto cifrado de Base64
        byte[] combined = Base64.getDecoder().decode(encryptedData);

        // Extraer el IV de los primeros 12 bytes
        byte[] iv = new byte[12];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        // Extraer los datos cifrados
        byte[] actualEncryptedData = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, actualEncryptedData, 0, actualEncryptedData.length);

        // Inicializar el cifrado AES/GCM
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv); // 128 bits de tag size
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        // Descifrar los datos
        byte[] decryptedData = cipher.doFinal(actualEncryptedData);

        return new String(decryptedData);
    }
}
