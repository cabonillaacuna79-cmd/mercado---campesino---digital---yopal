import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Prototipo demostrativo de Mercado Campesino Digital de Yopal.
 * Java Swing, sin Maven ni MySQL. Los datos se conservan solo mientras la app está abierta.
 */
public class MercadoCampesinoYopal extends JFrame {
    private final Color verde = new Color(37, 105, 70);
    private final Color verdeClaro = new Color(231, 243, 233);
    private final Color crema = new Color(250, 247, 238);

    private final DefaultTableModel modeloProductos;
    private final JLabel estadoUsuario = new JLabel("Sesión: visitante");
    private final JLabel contadorSeleccion = new JLabel("Productos en mi selección: 0");
    private final List<String> seleccion = new ArrayList<>();
    private String nombreUsuario = null;
    private String tipoUsuario = "VISITANTE"; // VISITANTE, COMPRADOR, VENDEDOR

    public MercadoCampesinoYopal() {
        setTitle("Mercado Campesino Digital de Yopal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(crema);
        setLayout(new BorderLayout());

        add(crearEncabezado(), BorderLayout.NORTH);

        String[] columnas = {"Producto", "Productor / Vereda", "Presentación", "Precio (COP)"};
        Object[][] iniciales = {
            {"Plátano hartón", "Finca El Recuerdo · Morichal", "Racimo", "$18.000"},
            {"Yuca fresca", "Productora Ana · La Chaparrera", "Kilo", "$3.000"},
            {"Huevos campesinos", "Granja Los Pinos · Tilodirán", "Cubeta x 30", "$16.000"},
            {"Queso artesanal", "Lácteos El Llano · Yopal", "Libra", "$12.000"},
            {"Miel natural", "Apiario La Esperanza · Mata de Limón", "Frasco 500 g", "$22.000"},
            {"Tomate chonto", "Huerta Don Pedro · El Taladro", "Kilo", "$4.500"}
        };
        modeloProductos = new DefaultTableModel(iniciales, columnas) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        JPanel contenido = new JPanel(new BorderLayout(12, 14));
        contenido.setBackground(crema);
        contenido.setBorder(new EmptyBorder(18, 24, 18, 24));
        contenido.add(crearPanelCuenta(), BorderLayout.NORTH);

        JTable tabla = new JTable(modeloProductos);
        tabla.setRowHeight(32);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(verde);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionBackground(new Color(204, 229, 208));
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 215)));

        JPanel catalogo = new JPanel(new BorderLayout(0, 10));
        catalogo.setBackground(crema);
        JLabel titulo = new JLabel("Productos del campo");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 21));
        titulo.setForeground(verde);
        catalogo.add(titulo, BorderLayout.NORTH);
        catalogo.add(scroll, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        acciones.setBackground(crema);

        JButton comprar = new JButton("Agregar a mi selección");
        estiloBoton(comprar, verde, Color.WHITE);
        comprar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un producto del catálogo.");
                return;
            }
            String producto = modeloProductos.getValueAt(tabla.convertRowIndexToModel(fila), 0).toString();
            seleccion.add(producto);
            contadorSeleccion.setText("Productos en mi selección: " + seleccion.size());
            JOptionPane.showMessageDialog(this, producto + " se agregó a tu selección.");
        });

        JButton verSeleccion = new JButton("Ver mi selección");
        estiloBoton(verSeleccion, new Color(224, 238, 225), verde);
        verSeleccion.addActionListener(e -> {
            String mensaje = seleccion.isEmpty() ? "Tu selección está vacía."
                    : String.join("\n", seleccion);
            JOptionPane.showMessageDialog(this, mensaje, "Mi selección", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton registrarVendedor = new JButton("Registrarme como vendedor");
        estiloBoton(registrarVendedor, new Color(255, 223, 125), new Color(45, 65, 44));
        registrarVendedor.addActionListener(e -> registrarCuenta(true));

        JButton registrarUsuario = new JButton("Registrarme como usuario");
        estiloBoton(registrarUsuario, new Color(224, 238, 225), verde);
        registrarUsuario.addActionListener(e -> registrarCuenta(false));

        JButton publicar = new JButton("Registrar producto");
        estiloBoton(publicar, new Color(37, 105, 70), Color.WHITE);
        publicar.addActionListener(e -> registrarProducto());

        acciones.add(comprar);
        acciones.add(verSeleccion);
        acciones.add(registrarVendedor);
        acciones.add(registrarUsuario);
        acciones.add(publicar);

        JPanel pieAcciones = new JPanel(new BorderLayout());
        pieAcciones.setBackground(crema);
        pieAcciones.add(acciones, BorderLayout.CENTER);
        contadorSeleccion.setForeground(verde);
        contadorSeleccion.setFont(new Font("SansSerif", Font.BOLD, 13));
        pieAcciones.add(contadorSeleccion, BorderLayout.SOUTH);

        JPanel zonaCatalogo = new JPanel(new BorderLayout(0, 8));
        zonaCatalogo.setBackground(crema);
        zonaCatalogo.add(catalogo, BorderLayout.CENTER);
        zonaCatalogo.add(pieAcciones, BorderLayout.SOUTH);

        contenido.add(zonaCatalogo, BorderLayout.CENTER);
        add(contenido, BorderLayout.CENTER);

        JLabel pie = new JLabel("  Prototipo demostrativo · Apoyamos a los productores de Yopal y sus veredas  ");
        pie.setOpaque(true);
        pie.setBackground(new Color(35, 76, 54));
        pie.setForeground(Color.WHITE);
        pie.setBorder(new EmptyBorder(12, 14, 12, 14));
        add(pie, BorderLayout.SOUTH);

        actualizarEstado();
    }

    private JPanel crearEncabezado() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(verde);
        p.setBorder(new EmptyBorder(17, 25, 17, 25));
        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(verde);
        JLabel marca = new JLabel("MERCADO CAMPESINO");
        marca.setForeground(new Color(255, 223, 125));
        marca.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel titulo = new JLabel("Digital de Yopal");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 27));
        JLabel subtitulo = new JLabel("Del campo a tu mesa, apoyando lo nuestro.");
        subtitulo.setForeground(new Color(235, 245, 236));
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textos.add(marca);
        textos.add(Box.createVerticalStrut(3));
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(3));
        textos.add(subtitulo);
        p.add(textos, BorderLayout.CENTER);
        JLabel planta = new JLabel("  YOPAL  ");
        planta.setForeground(new Color(255, 223, 125));
        planta.setFont(new Font("SansSerif", Font.BOLD, 18));
        p.add(planta, BorderLayout.EAST);
        return p;
    }

    private JPanel crearPanelCuenta() {
        JPanel p = new JPanel(new BorderLayout(10, 4));
        p.setBackground(verdeClaro);
        p.setBorder(new EmptyBorder(12, 15, 12, 15));
        JLabel bienvenida = new JLabel("¡Compra local, fortalece el campo!");
        bienvenida.setForeground(verde);
        bienvenida.setFont(new Font("SansSerif", Font.BOLD, 18));
        estadoUsuario.setForeground(new Color(55, 72, 56));
        estadoUsuario.setFont(new Font("SansSerif", Font.BOLD, 13));
        p.add(bienvenida, BorderLayout.WEST);
        p.add(estadoUsuario, BorderLayout.EAST);
        return p;
    }

    private void registrarCuenta(boolean vendedor) {
        JTextField nombre = new JTextField(22);
        JTextField correo = new JTextField(22);
        JTextField telefono = new JTextField(22);
        JTextField ubicacion = new JTextField(22);

        JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
        formulario.add(new JLabel("Nombre completo:"));
        formulario.add(nombre);
        formulario.add(new JLabel("Correo electrónico:"));
        formulario.add(correo);
        formulario.add(new JLabel("Teléfono:"));
        formulario.add(telefono);
        formulario.add(new JLabel(vendedor ? "Finca / vereda:" : "Barrio / municipio:"));
        formulario.add(ubicacion);

        String titulo = vendedor ? "Registro de vendedor campesino" : "Registro de usuario comprador";
        int opcion = JOptionPane.showConfirmDialog(this, formulario, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) return;

        if (nombre.getText().trim().isEmpty() || correo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa por lo menos el nombre y el correo.");
            return;
        }

        nombreUsuario = nombre.getText().trim();
        tipoUsuario = vendedor ? "VENDEDOR" : "COMPRADOR";
        actualizarEstado();

        String mensaje = vendedor
                ? "¡Bienvenido, " + nombreUsuario + "!\nTu cuenta de vendedor quedó activa en este prototipo.\n"
                  + "Puedes comprar productos y también registrar tus propios productos."
                : "¡Bienvenido, " + nombreUsuario + "!\nTu cuenta de comprador quedó activa.\n"
                  + "Puedes explorar y seleccionar productos, pero no puedes publicar productos para vender.";
        JOptionPane.showMessageDialog(this, mensaje, "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void registrarProducto() {
        if (!"VENDEDOR".equals(tipoUsuario)) {
            String mensaje = "Para publicar productos debes registrarte como vendedor campesino.\n\n"
                    + "Los usuarios compradores pueden comprar, pero no publicar productos.";
            JOptionPane.showMessageDialog(this, mensaje, "Acceso restringido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField nombre = new JTextField(20);
        JTextField presentacion = new JTextField(20);
        JTextField precio = new JTextField(20);
        JTextField vereda = new JTextField(20);
        JTextArea descripcion = new JTextArea(3, 20);
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);

        JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
        formulario.add(new JLabel("Nombre del producto:"));
        formulario.add(nombre);
        formulario.add(new JLabel("Presentación (kg, unidad, etc.):"));
        formulario.add(presentacion);
        formulario.add(new JLabel("Precio en pesos (solo número):"));
        formulario.add(precio);
        formulario.add(new JLabel("Finca / vereda:"));
        formulario.add(vereda);
        formulario.add(new JLabel("Descripción:"));
        formulario.add(new JScrollPane(descripcion));

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Registrar producto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) return;

        String n = nombre.getText().trim();
        String p = presentacion.getText().trim();
        String v = vereda.getText().trim();
        String precioTexto = precio.getText().trim().replace(".", "").replace(",", "");
        if (n.isEmpty() || p.isEmpty() || v.isEmpty() || precioTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa el nombre, presentación, precio y vereda.");
            return;
        }
        try {
            long valor = Long.parseLong(precioTexto);
            if (valor <= 0) throw new NumberFormatException();
            modeloProductos.addRow(new Object[] {
                    n, nombreUsuario + " · " + v, p, "$" + String.format("%,d", valor).replace(',', '.')
            });
            JOptionPane.showMessageDialog(this, "Producto publicado en el catálogo de esta sesión.",
                    "Producto registrado", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número entero mayor que cero.");
        }
    }

    private void actualizarEstado() {
        if ("VENDEDOR".equals(tipoUsuario)) {
            estadoUsuario.setText("Sesión: " + nombreUsuario + " · VENDEDOR (puede comprar y publicar)");
        } else if ("COMPRADOR".equals(tipoUsuario)) {
            estadoUsuario.setText("Sesión: " + nombreUsuario + " · COMPRADOR (puede comprar)");
        } else {
            estadoUsuario.setText("Sesión: visitante");
        }
    }

    private void estiloBoton(JButton b, Color fondo, Color texto) {
        b.setBackground(fondo);
        b.setForeground(texto);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBorder(new EmptyBorder(10, 12, 10, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MercadoCampesinoYopal().setVisible(true));
    }
}
