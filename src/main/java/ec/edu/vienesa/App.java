package ec.edu.vienesa;
import jakarta.persistence.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Map;

public class App {
    static String variable(String nombre,String defecto){String valor=System.getenv(nombre);return valor==null||valor.isBlank()?defecto:valor;}
    public static void main(String[] args) throws Exception {
        String clave=System.getenv("DB_PASSWORD");
        if(clave==null||clave.isBlank()) {
            final String[] respuesta = new String[1];
            SwingUtilities.invokeAndWait(() -> {
                JPasswordField campo = new JPasswordField(24);
                Object[] mensaje = {"Escribe la contrasena de PostgreSQL del usuario "
                    + variable("DB_USER", "postgres") + ":", campo};
                int opcion = JOptionPane.showConfirmDialog(null, mensaje,
                    "La Vienesa - Conexion a PostgreSQL",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if(opcion == JOptionPane.OK_OPTION) {
                    char[] caracteres = campo.getPassword();
                    respuesta[0] = new String(caracteres);
                    java.util.Arrays.fill(caracteres, '\0');
                    campo.setText("");
                }
            });
            clave = respuesta[0];
            if(clave==null || clave.isBlank()) return;
        }
        EntityManagerFactory emf=Persistence.createEntityManagerFactory("vienesaPU",Map.of(
            "jakarta.persistence.jdbc.url",variable("DB_URL","jdbc:postgresql://localhost:5432/vienesa_academica"),
            "jakarta.persistence.jdbc.user",variable("DB_USER","postgres"),"jakarta.persistence.jdbc.password",clave));
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        SwingUtilities.invokeLater(()->{
            JFrame ventana=new JFrame("La Vienesa | Gestion academica de productos");
            ventana.setContentPane(new ProductoPanel(new ProductoRepository(emf),"PostgreSQL"));
            ventana.setSize(1080,700);ventana.setLocationRelativeTo(null);ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ventana.addWindowListener(new WindowAdapter(){@Override public void windowClosed(WindowEvent e){emf.close();}});
            ventana.setVisible(true);
        });
    }
}
