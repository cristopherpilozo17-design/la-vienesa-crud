package ec.edu.vienesa;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

public class ProductoRepository {
    private final EntityManagerFactory emf;
    public ProductoRepository(EntityManagerFactory emf) { this.emf=emf; }
    public List<Producto> listar() {
        try (EntityManager em=emf.createEntityManager()) {
            return em.createQuery("select p from Producto p where p.activo=true order by p.id",Producto.class).getResultList();
        }
    }
    private void transaccion(Consumer<EntityManager> tarea) {
        try (EntityManager em=emf.createEntityManager()) {
            try { em.getTransaction().begin();tarea.accept(em);em.getTransaction().commit(); }
            catch (RuntimeException ex) { if(em.getTransaction().isActive())em.getTransaction().rollback();throw ex; }
        }
    }
    public Producto crear(String nombre,String presentacion,BigDecimal precio,int stock) {
        Producto p=new Producto(nombre,presentacion,precio,stock);
        transaccion(em->em.persist(p));return p;
    }
    public void actualizar(long id,String nombre,String presentacion,BigDecimal precio,int stock) {
        transaccion(em->{Producto p=em.find(Producto.class,id);
            if(p==null || !p.isActivo())throw new IllegalArgumentException("Producto activo no encontrado.");
            p.setNombre(nombre);p.setPresentacion(presentacion);p.setPrecio(precio);p.setStock(stock);
        });
    }
    public void eliminar(long id) {
        transaccion(em->{Producto p=em.find(Producto.class,id);
            if(p==null || !p.isActivo())throw new IllegalArgumentException("Producto activo no encontrado.");
            p.setActivo(false);
        });
    }
}
