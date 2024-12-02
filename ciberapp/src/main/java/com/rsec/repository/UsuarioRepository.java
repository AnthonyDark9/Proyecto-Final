package com.rsec.repository;

import com.rsec.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad `Usuario`.
 * Extiende la interfaz `JpaRepository` para proporcionar métodos estándar de persistencia
 * como guardar, eliminar, actualizar y buscar usuarios en la base de datos.
 *
 * @author marco vences
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Los métodos básicos de persistencia son proporcionados automáticamente por JpaRepository.
}
