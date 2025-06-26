import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;

public class FileEncryption {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 16;

    /**
     * Genera una nueva clave secreta AES de 256 bits.
     * @return La SecretKey generada.
     * @throws Exception si el algoritmo no está disponible.
     */
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE);
        return keyGen.generateKey();
    }

    /**
     * Convierte una SecretKey a un string Base64 para fácil almacenamiento y transporte.
     * @param secretKey La clave a convertir.
     * @return El string de la clave en formato Base64.
     */
    public static String keyToString(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * Convierte un string Base64 de vuelta a una SecretKey.
     * @param keyStr El string de la clave en formato Base64.
     * @return La SecretKey reconstruida.
     */
    public static SecretKey stringToKey(String keyStr) {
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }


    /**
     * Encripta un archivo.
     * @param inputFile Path del archivo de entrada.
     * @param outputFile Path del archivo de salida.
     * @param key La clave secreta para la encriptación.
     * @throws Exception si ocurre un error durante la encriptación.
     */
    public static void encryptFile(String inputFile, String outputFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom(); // Usamos SecureRandom estándar
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            // Escribe el IV al principio del archivo de salida
            outputStream.write(iv);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, bytesRead);
                if (output != null) {
                    outputStream.write(output);
                }
            }
            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null) {
                outputStream.write(outputBytes);
            }
        }
    }

    /**
     * Desencripta un archivo.
     * @param inputFile Path del archivo encriptado.
     * @param outputFile Path para guardar el archivo desencriptado.
     * @param key La clave secreta usada para encriptar.
     * @throws Exception si ocurre un error durante la desencriptación.
     */
    public static void decryptFile(String inputFile, String outputFile, SecretKey key) throws Exception {
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            // Lee el IV del principio del archivo encriptado
            byte[] iv = new byte[IV_SIZE];
            if (inputStream.read(iv) != IV_SIZE) {
                throw new IllegalArgumentException("Archivo cifrado inválido: IV no encontrado o incompleto.");
            }
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, bytesRead);
                if (output != null) {
                    outputStream.write(output);
                }
            }
            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null) {
                outputStream.write(outputBytes);
            }
        }
    }
}