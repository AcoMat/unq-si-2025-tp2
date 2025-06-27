import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.io.File;

public class FileEncryptionGUI extends JFrame {

    private final JTextField filePathField;
    private final JTextField keyField;
    private final JTextArea messageArea;
    private final JButton encryptButton;
    private final JButton decryptButton;
    private final JButton copyKeyButton;

    private File selectedFile;

    public FileEncryptionGUI() {
        super("Encriptador de Archivos v1.0");

        // --- Configuración de la ventana principal ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null); // Centrar en la pantalla
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Márgenes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Componentes de la GUI ---

        // Fila 1: Selección de Archivo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        add(new JLabel("Archivo:"), gbc);

        filePathField = new JTextField(30);
        filePathField.setEditable(false);
        gbc.gridx = 1;
        gbc.weightx = 1;
        add(filePathField, gbc);

        JButton browseButton = new JButton("Explorar...");
        gbc.gridx = 2;
        gbc.weightx = 0;
        add(browseButton, gbc);

        // Fila 2: Clave de Encriptación
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Clave Base64:"), gbc);

        keyField = new JTextField(30);
        gbc.gridx = 1;
        gbc.weightx = 1;
        add(keyField, gbc);

        copyKeyButton = new JButton("Copiar");
        copyKeyButton.setEnabled(false);
        gbc.gridx = 2;
        gbc.weightx = 0;
        add(copyKeyButton, gbc);

        // Fila 3: Botones de Acción
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        encryptButton = new JButton("Encriptar");
        decryptButton = new JButton("Desencriptar");
        encryptButton.setEnabled(false);
        decryptButton.setEnabled(false);
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3; // Ocupa las 3 columnas
        add(buttonPanel, gbc);

        // Fila 4: Área de Mensajes
        messageArea = new JTextArea(8, 40);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1; // Permite que el área de texto crezca verticalmente
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        // --- Lógica de los Eventos (Action Listeners) ---

        browseButton.addActionListener(e -> selectFile());
        encryptButton.addActionListener(e -> encryptSelectedFile());
        decryptButton.addActionListener(e -> decryptSelectedFile());
        copyKeyButton.addActionListener(e -> copyKeyToClipboard());
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());

            // Habilitar siempre el botón de encriptar
            encryptButton.setEnabled(true);

            // Habilitar el botón de desencriptar solo si el archivo tiene extensión .enc
            String fileName = selectedFile.getName().toLowerCase();
            if (fileName.endsWith(".enc")) {
                decryptButton.setEnabled(true);
                log("Archivo seleccionado: " + selectedFile.getName() + " (archivo encriptado)");
            } else {
                decryptButton.setEnabled(false);
                log("Archivo seleccionado: " + selectedFile.getName() + " (para encriptar)");
                log("Nota: Para desencriptar, seleccione un archivo con extensión .enc");
            }
        }
    }

    private void encryptSelectedFile() {
        if (selectedFile == null) {
            log("Error: Por favor, selecciona un archivo primero.");
            return;
        }

        try {
            SecretKey key = FileEncryption.generateKey();
            String outputFilePath = selectedFile.getAbsolutePath() + ".enc";

            log("Encriptando archivo...");
            FileEncryption.encryptFile(selectedFile.getAbsolutePath(), outputFilePath, key);

            String keyString = FileEncryption.keyToString(key);
            keyField.setText(keyString);
            copyKeyButton.setEnabled(true);

            log("¡Éxito! Archivo encriptado y guardado como " + outputFilePath);
            log("Clave generada. ¡GUÁRDALA EN UN LUGAR SEGURO!");

        } catch (Exception ex) {
            log("Error durante la encriptación: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void decryptSelectedFile() {
        if (selectedFile == null) {
            log("Error: Por favor, selecciona un archivo encriptado (.enc) primero.");
            return;
        }

        String keyString = keyField.getText();
        if (keyString == null || keyString.trim().isEmpty()) {
            log("Error: Por favor, introduce la clave Base64 para desencriptar.");
            return;
        }

        // Validar la clave Base64 antes de mostrar el diálogo
        try {
            SecretKey key = FileEncryption.stringToKey(keyString.trim());
            // Si llegamos aquí, la clave es válida
            log("Clave Base64 válida. Procediendo con la desencriptación...");
        } catch (Exception ex) {
            log("Error: La clave Base64 introducida no es válida.");
            log("Asegúrate de que la clave esté completa y sea correcta.");
            return; // No continuar si la clave no es válida
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar archivo desencriptado como...");
        // Sugerir nombre de archivo sin la extensión .enc
        String originalName = selectedFile.getName();
        if (originalName.toLowerCase().endsWith(".enc")) {
            originalName = originalName.substring(0, originalName.length() - 4);
        }
        fileChooser.setSelectedFile(new File(originalName));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            try {
                log("Desencriptando archivo...");
                SecretKey key = FileEncryption.stringToKey(keyString.trim());
                FileEncryption.decryptFile(selectedFile.getAbsolutePath(), outputFile.getAbsolutePath(), key);
                log("¡Éxito! Archivo desencriptado y guardado como " + outputFile.getName());
            } catch (Exception ex) {
                log("Error durante la desencriptación: " + ex.getMessage());
                log("Asegúrate de que la clave y el archivo son correctos.");
                ex.printStackTrace();
            }
        }
    }

    private void copyKeyToClipboard() {
        String keyText = keyField.getText();
        if (keyText != null && !keyText.isEmpty()) {
            StringSelection stringSelection = new StringSelection(keyText);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            log("Clave copiada al portapapeles.");
        }
    }

    private void log(String message) {
        messageArea.append(message + "\n");
    }

    public static void main(String[] args) {
        // Asegura que la GUI se cree y se muestre en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Opcional: Establecer un Look and Feel más moderno si está disponible
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new FileEncryptionGUI().setVisible(true);
        });
    }
}
