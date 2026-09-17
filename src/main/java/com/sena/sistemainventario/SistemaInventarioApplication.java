package com.sena.sistemainventario;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

import com.sena.sistemainventario.model.Producto;
import com.sena.sistemainventario.model.Usuario;
import com.sena.sistemainventario.repository.ProductoRepository;
import com.sena.sistemainventario.repository.UsuarioRepository;

@SpringBootApplication
public class SistemaInventarioApplication implements CommandLineRunner {

    @Autowired
    private ProductoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public static void main(String[] args) {
        SpringApplication.run(SistemaInventarioApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println();
        System.out.println("========================================");
        System.out.println("  SISTEMA DE INVENTARIO - VERIFICACION");
        System.out.println("========================================");
        System.out.println();

        if (usuarioRepository.count() == 0) {
            System.out.println("Creando usuario admin por defecto...");
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest("1234".getBytes());
            String hashed = java.util.Base64.getEncoder().encodeToString(hash);
            Usuario admin = new Usuario("admin", hashed, "Administrador", "admin@sena.com");
            admin.setRol("ADMIN");
            usuarioRepository.save(admin);
            System.out.println("Usuario admin creado (usuario: admin, contrasena: 1234)");
        }

        if (repository.count() == 0) {
            System.out.println("Base de datos vacia - cargando datos demo...");
            repository.save(new Producto("P001", "Teclado Mecanico", "Tecnologia", "Tecno SAS", 120000.0, 10));
            repository.save(new Producto("P002", "Mouse Inalambrico", "Tecnologia", "TechMax", 45000.0, 15));
            repository.save(new Producto("P003", "Monitor LED 24\"", "Tecnologia", "DisplayPro", 850000.0, 8));
            repository.save(new Producto("P004", "Impresora HP", "Tecnologia", "Hewlett Packard", 650000.0, 3));
            repository.save(new Producto("P005", "Memoria USB 64GB", "Accesorios", "DataStore", 45000.0, 20));
            repository.save(new Producto("P006", "Computador Acer", "Tecnologia", "Acer Colombia", 2500000.0, 2));
            repository.save(new Producto("P007", "Telefono Samsung", "Tecnologia", "Samsung", 1200000.0, 4));
            repository.save(new Producto("P008", "Audifonos Bluetooth", "Accesorios", "SoundMax", 130000.0, 0));
            repository.save(new Producto("P009", "Cuaderno Profesional", "Papeleria", "Papeleria Central", 8500.0, 50));
            repository.save(new Producto("P010", "Set de Boligrafos", "Papeleria", "Papeleria Central", 12000.0, 30));
            System.out.println("10 productos demo cargados correctamente.");
        }

        var productos = repository.findAll();

        System.out.println();
        System.out.println("Productos registrados: " + productos.size());

        double total = 0;
        int totalUnidades = 0;
        int agotados = 0;
        for (Producto p : productos) {
            total += p.getPrecio() * p.getCantidad();
            totalUnidades += p.getCantidad();
            if (p.getCantidad() == 0) agotados++;
        }

        System.out.printf("Valor total inventario: $%,.0f%n", total);
        System.out.println("Total unidades: " + totalUnidades);
        System.out.println("Productos agotados: " + agotados);
        System.out.println();
        System.out.println("Backend funcionando en: http://localhost:8080");
        System.out.println("API: http://localhost:8080/productos");
        System.out.println();
    }
}
