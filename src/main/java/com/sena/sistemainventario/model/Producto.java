package com.sena.sistemainventario.model;

import jakarta.persistence.*;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String categoria;

    @Column(length = 100)
    private String proveedor;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer cantidad;

    public Producto() {}

    public Producto(String codigo, String nombre, String categoria, String proveedor, Double precio, Integer cantidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.proveedor = proveedor;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}