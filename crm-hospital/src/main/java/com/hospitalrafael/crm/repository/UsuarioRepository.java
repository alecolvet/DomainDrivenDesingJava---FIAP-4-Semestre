package com.hospitalrafael.crm.repository;

import com.hospitalrafael.crm.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByDoc(String doc);
    boolean existsByEmail(String email);
    boolean existsByDoc(String doc);
}
