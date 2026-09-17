package com.sena.sistemainventario.repository;

import com.sena.sistemainventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}