package ec.edu.vienesa;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/** Una sola pantalla. Las operaciones ORM se concentran en ProductoRepository. */
public class ProductoPanel extends JPanel {
    private final ProductoRepository repo;
    final JTextField nombre=new JTextField(), presentacion=new JTextField(), precio=new JTextField(), stock=new JTextField();
    final JButton crear=new JButton("Registrar"), consultar=new JButton("Consultar"), actualizar=new JButton("Actualizar"), eliminar=new JButton("Desactivar"), nuevo=new JButton("Limpiar");
    final JCheckBox confirmar=new JCheckBox("Confirmo desactivar el producto seleccionado");
    final JLabel estado=new JLabel("Listo para gestionar productos.");
    final DefaultTableModel modelo=new DefaultTableModel(new Object[]{"ID","Producto","Presentacion","Precio USD","Stock"},0) {
        @Override public boolean isCellEditable(int row,int col){return false;}
    };
    final JTable tabla=new JTable(modelo);
    private Long id;
    private List<Producto> productos=List.of();
    private final Color oscuro=new Color(45,52,48), verde=new Color(32,98,74), fondo=new Color(246,245,240);
    public ProductoPanel(ProductoRepository repo,String entorno) {
        this.repo=repo;
        setLayout(new BorderLayout(0,20));setBorder(new EmptyBorder(26,30,24,30));setBackground(fondo);
        JPanel cabecera=new JPanel(new BorderLayout());cabecera.setOpaque(false);
        JLabel marca=new JLabel("LA VIENESA  /  PRODUCTOS");marca.setFont(new Font("SansSerif",Font.BOLD,25));marca.setForeground(verde);
        JLabel subtitulo=new JLabel("Caso academico | CRUD con Java, Hibernate y "+entorno);subtitulo.setForeground(oscuro);
        cabecera.add(marca,BorderLayout.NORTH);cabecera.add(subtitulo,BorderLayout.SOUTH);add(cabecera,BorderLayout.NORTH);
        JPanel contenido=new JPanel(new BorderLayout(0,18));contenido.setOpaque(false);
        JPanel formulario=new JPanel(new GridLayout(2,4,14,7));formulario.setOpaque(false);
        for(String t:new String[]{"Nombre del producto","Presentacion","Precio (USD)","Stock (unidades)"})formulario.add(new JLabel(t));
        for(JTextField t:new JTextField[]{nombre,presentacion,precio,stock}){t.setFont(new Font("SansSerif",Font.PLAIN,16));t.setPreferredSize(new Dimension(170,38));formulario.add(t);}
        JPanel superior=new JPanel(new BorderLayout(0,12));superior.setOpaque(false);superior.add(formulario,BorderLayout.NORTH);
        JPanel botones=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));botones.setOpaque(false);
        for(JButton b:new JButton[]{crear,consultar,actualizar,eliminar,nuevo}){b.setFont(new Font("SansSerif",Font.BOLD,13));b.setPreferredSize(new Dimension(135,38));botones.add(b);}
        crear.setBackground(verde);crear.setForeground(Color.WHITE);superior.add(botones,BorderLayout.CENTER);
        confirmar.setOpaque(false);superior.add(confirmar,BorderLayout.SOUTH);contenido.add(superior,BorderLayout.NORTH);
        tabla.setRowHeight(35);tabla.setFont(new Font("SansSerif",Font.PLAIN,14));tabla.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,13));tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);tabla.getColumnModel().getColumn(1).setPreferredWidth(300);
        contenido.add(new JScrollPane(tabla),BorderLayout.CENTER);add(contenido,BorderLayout.CENTER);
        JPanel pie=new JPanel(new GridLayout(2,1,0,9));pie.setOpaque(false);estado.setFont(new Font("SansSerif",Font.BOLD,13));estado.setForeground(verde);
        pie.add(estado);pie.add(new JLabel("Datos de demostracion. Desactivar conserva el historial (activo = false)."));add(pie,BorderLayout.SOUTH);
        tabla.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())seleccionar();});
        crear.addActionListener(e->operar(()->{validar();Producto p=repo.crear(nombre.getText().trim(),presentacion.getText().trim(),importe(),unidades());cargar();mensaje("CREAR: producto registrado con ID "+p.getId()+".");}));
        consultar.addActionListener(e->operar(()->{cargar();mensaje("LEER: consulta completada. Productos activos: "+modelo.getRowCount()+".");}));
        actualizar.addActionListener(e->operar(()->{long clave=seleccion();validar();repo.actualizar(clave,nombre.getText().trim(),presentacion.getText().trim(),importe(),unidades());cargar();mensaje("ACTUALIZAR: cambios guardados para ID "+clave+".");}));
        eliminar.addActionListener(e->operar(()->{long clave=seleccion();if(!confirmar.isSelected())throw new IllegalArgumentException("Marca la confirmacion antes de desactivar.");repo.eliminar(clave);cargar();limpiar();mensaje("BORRAR: ID "+clave+" desactivado. Historial conservado.");}));
        nuevo.addActionListener(e->{limpiar();mensaje("Formulario limpio. Puedes registrar un nuevo producto.");});
        operar(()->cargar());
    }
    private void seleccionar(){int fila=tabla.getSelectedRow();if(fila<0)return;Producto p=productos.get(fila);id=p.getId();nombre.setText(p.getNombre());presentacion.setText(p.getPresentacion());precio.setText(p.getPrecio().toPlainString());stock.setText(p.getStock().toString());confirmar.setSelected(false);}
    private long seleccion(){if(id==null)throw new IllegalArgumentException("Selecciona primero una fila de la tabla.");return id;}
    private BigDecimal importe(){try{return new BigDecimal(precio.getText().trim().replace(',','.'));}catch(NumberFormatException ex){throw new IllegalArgumentException("Escribe un precio valido, por ejemplo 3.50.");}}
    private int unidades(){try{return Integer.parseInt(stock.getText().trim());}catch(NumberFormatException ex){throw new IllegalArgumentException("El stock debe ser un numero entero.");}}
    private void validar(){if(nombre.getText().isBlank()||nombre.getText().trim().length()>100)throw new IllegalArgumentException("Nombre obligatorio: maximo 100 caracteres.");if(presentacion.getText().isBlank()||presentacion.getText().trim().length()>40)throw new IllegalArgumentException("Presentacion obligatoria: maximo 40 caracteres.");new Producto(nombre.getText(),presentacion.getText(),importe(),unidades());}
    private void cargar(){List<Producto> nuevos=repo.listar();tabla.clearSelection();productos=nuevos;modelo.setRowCount(0);for(Producto p:productos)modelo.addRow(new Object[]{p.getId(),p.getNombre(),p.getPresentacion(),p.getPrecio(),p.getStock()});id=null;}
    private void limpiar(){id=null;tabla.clearSelection();nombre.setText("");presentacion.setText("");precio.setText("");stock.setText("");confirmar.setSelected(false);}
    private void mensaje(String s){estado.setForeground(verde);estado.setText(s);}
    private void operar(Runnable tarea){try{tarea.run();}catch(RuntimeException ex){estado.setForeground(new Color(160,35,35));estado.setText("ERROR: "+(ex instanceof IllegalArgumentException?ex.getMessage():"No se pudo completar la operacion; revisa la consola y la conexion."));ex.printStackTrace();}}
}
