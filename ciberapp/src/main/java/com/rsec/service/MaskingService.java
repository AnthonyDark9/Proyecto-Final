package com.rsec.service;

import org.springframework.stereotype.Service;

/**
 * Servicio para aplicar enmascaramiento de datos sensibles.
 * El enmascaramiento oculta parte del contenido de los datos, dejando visibles solo
 * algunos caracteres, lo que permite proteger información sensible al exponerla.
 *
 * @author marco vences
 */
@Service
public class MaskingService {

    /**
     * Enmascara un dato de usuario ocultando parte de su contenido.
     *
     * Si el dato es nulo o está vacío, se devuelve tal cual. Si el dato tiene menos o igual a
     * un carácter visible, no se realiza el enmascaramiento. En otros casos, se muestran los
     * primeros caracteres visibles y el resto se reemplaza con asteriscos.
     *
     * @param userData El dato del usuario que se desea enmascarar.
     * @return El dato enmascarado, o el original si no requiere enmascaramiento.
     */
    public String maskUserData(String userData) {
        if (userData == null || userData.isEmpty()) return userData; // Retorna tal cual si es nulo o vacío
        int visibleChars = 1; // Número de caracteres visibles al inicio del dato
        int totalLength = userData.length();
        if (totalLength <= visibleChars) return userData; // Si es corto, no se enmascara
        return userData.substring(0, visibleChars) + "*".repeat(totalLength - visibleChars);
    }
}
