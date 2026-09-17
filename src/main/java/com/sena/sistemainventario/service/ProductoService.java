package com.sena.sistemainventario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sena.sistemainventario.model.Producto;
import com.sena.sistemainventario.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listarProductos() {
        return repository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Producto guardarProducto(Producto producto) {
        return repository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        Producto producto = repository.findById(id).orElseThrow();
        producto.setCodigo(productoActualizado.getCodigo());
        producto.setNombre(productoActualizado.getNombre());
        producto.setCategoria(productoActualizado.getCategoria());
        producto.setProveedor(productoActualizado.getProveedor());
        producto.setPrecio(productoActualizado.getPrecio());
        producto.setCantidad(productoActualizado.getCantidad());
        return repository.save(producto);
    }

    public void eliminarProducto(Long id) {
        repository.deleteById(id);
    }
}