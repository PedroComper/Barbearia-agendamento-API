package com.pedrocomper.barbearia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pedrocomper.barbearia.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);
}
