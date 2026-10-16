package ec.edu.vienesa;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(nullable = false, length = 40)
    private String presentacion;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private boolean activo = true;
    public boolean isActivo() { return activo; }
    public void setActivo(boolean valor) { activo = valor; }

    public Producto() { }
    public Producto(String nombre, String presentacion, BigDecimal precio, int stock) {
        this.nombre = nombre;
        this.presentacion = presentacion;
        setPrecio(precio);
        setStock(stock);
    }
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getPresentacion() { return presentacion; }
    public BigDecimal getPrecio() { return precio; }
    public Integer getStock() { return stock; }
    public void setNombre(String valor) { nombre = valor; }
    public void setPresentacion(String valor) { presentacion = valor; }
    public void setPrecio(BigDecimal valor) {
        if (valor == null || valor.signum() < 0 || valor.scale() > 2)
            throw new IllegalArgumentException("Precio: importe no negativo con hasta dos decimales.");
        precio = valor;
    }
    public void setStock(int valor) {
        if (valor < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
        stock = valor;
    }
    @Override public String toString() {
        return "Producto{id=" + id + ", nombre='" + nombre + "', presentacion='"
            + presentacion + "', precio=" + precio + ", stock=" + stock + "}";
    }
}
