package com.sena.sistemainventario.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sena.sistemainventario.model.Producto;
import com.sena.sistemainventario.service.ProductoService;

@RestController
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping("/productos")
    public List<Producto> listarProductos() {
        return service.listarProductos();
    }

    @GetMapping("/productos/{id}")
    public Producto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).orElseThrow();
    }

    @PostMapping("/productos")
    public Producto registrarProducto(@RequestBody Producto producto) {
        return service.guardarProducto(producto);
    }

    @PutMapping("/productos/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        return service.actualizarProducto(id, producto);
    }

    @DeleteMapping("/productos/{id}")
    public void eliminarProducto(@PathVariable Long id) {
        service.eliminarProducto(id);
    }
}