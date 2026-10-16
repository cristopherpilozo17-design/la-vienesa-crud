package ec.edu.vienesa;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Map;
import java.nio.file.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CrudTest {
    @Test void confirmaCrudDesdePantallaConBaseEnMemoria() throws Exception {
        Map<String,Object> props=Map.of("jakarta.persistence.jdbc.driver","org.h2.Driver",
            "jakarta.persistence.jdbc.url","jdbc:h2:mem:vienesa;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
            "jakarta.persistence.jdbc.user","sa","jakarta.persistence.jdbc.password","",
            "hibernate.hbm2ddl.auto","create-drop");
        try(EntityManagerFactory emf=Persistence.createEntityManagerFactory("vienesaPU",props)) {
            ProductoRepository repo=new ProductoRepository(emf);
            SwingUtilities.invokeAndWait(()->{
                try {
                    ProductoPanel p=new ProductoPanel(repo,"H2 EN MEMORIA - PRUEBA");p.setSize(1020,650);layout(p);
                    p.nombre.setText("Salchicha Frankfurt");p.presentacion.setText("450 g");p.precio.setText("3.50");p.stock.setText("100");
                    p.crear.doClick();assertEquals(1,repo.listar().size());captura(p,"01_crear.png");
                    p.consultar.doClick();assertEquals(1,p.modelo.getRowCount());captura(p,"02_consultar.png");
                    p.tabla.setRowSelectionInterval(0,0);p.precio.setText("3.75");p.stock.setText("90");p.actualizar.doClick();
                    assertEquals(new BigDecimal("3.75"),repo.listar().get(0).getPrecio());assertEquals(90,repo.listar().get(0).getStock());captura(p,"03_actualizar.png");
                    long id=repo.listar().get(0).getId();p.tabla.setRowSelectionInterval(0,0);p.confirmar.setSelected(true);p.eliminar.doClick();
                    assertTrue(repo.listar().isEmpty());
                    try(EntityManager em=emf.createEntityManager()){assertFalse(em.find(Producto.class,id).isActivo());}
                    captura(p,"04_desactivar.png");
                    p.nombre.setText("Producto invalido");p.presentacion.setText("450 g");p.precio.setText("-1");p.stock.setText("1");p.crear.doClick();assertTrue(repo.listar().isEmpty());
                } catch(Exception e){throw new RuntimeException(e);}
            });
        }
    }
    private static void layout(java.awt.Container c){c.doLayout();for(java.awt.Component x:c.getComponents())if(x instanceof java.awt.Container)layout((java.awt.Container)x);}
    private static void captura(ProductoPanel p,String nombre) throws Exception {
        String destino=System.getProperty("evidencias.dir");if(destino==null)return;
        layout(p);
        java.awt.Container vp=p.tabla.getParent();
        p.tabla.setSize(vp.getWidth(),Math.max(vp.getHeight(),p.tabla.getPreferredSize().height));
        p.tabla.doLayout();
        javax.swing.JScrollPane sp=(javax.swing.JScrollPane)javax.swing.SwingUtilities.getAncestorOfClass(javax.swing.JScrollPane.class,p.tabla);
        sp.setColumnHeaderView(p.tabla.getTableHeader());
        layout(sp);
        Path carpeta=Path.of(destino);Files.createDirectories(carpeta);
        BufferedImage imagen=new BufferedImage(p.getWidth(),p.getHeight(),BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g=imagen.createGraphics();p.printAll(g);g.dispose();ImageIO.write(imagen,"png",carpeta.resolve(nombre).toFile());
    }
}
