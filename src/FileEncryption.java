import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;

public class FileEncryption {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 16;

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE);
        return keyGen.generateKey();
    }

    public static void encryptFile(String inputFile, String outputFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = SecureRandom.getInstanceStrong();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        outputFile += ".bin";

        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(iv);

            byte[] buffer = new byte[1024];
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

    public static void decryptFile(String inputFile, String outputFile, SecretKey key) throws Exception {
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] iv = new byte[IV_SIZE];
            if (inputStream.read(iv) != IV_SIZE) {
                throw new IllegalArgumentException("Archivo cifrado inválido: IV no encontrado.");
            }
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            byte[] buffer = new byte[1024];
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Programa de Cifrado/Descifrado AES-256 ===");
        System.out.println("1. Cifrar archivo");
        System.out.println("2. Descifrar archivo");
        System.out.print("Seleccione 1 o 2: ");
        String choice = scanner.nextLine();

        System.out.print("Ruta del archivo de entrada: ");
        String inputFile = scanner.nextLine();


        try {
            if (choice.equals("1")) {
                System.out.print("Nombre del archivo de salida (sin extensión): ");
                String outputFile = scanner.nextLine();
                SecretKey key = generateKey();
                System.out.println("Clave generada (guardar para descifrar): " + Base64.getEncoder().encodeToString(key.getEncoded()));
                encryptFile(inputFile, outputFile, key);
                System.out.println("Archivo cifrado exitosamente: " + outputFile);
            } else if (choice.equals("2")) {
                System.out.print("Nombre del archivo de salida (con extensión original): ");
                String outputFile = scanner.nextLine();
                System.out.print("Ingrese la clave en Base64: ");
                String keyBase64 = scanner.nextLine();
                keyBase64 = keyBase64.trim();
                byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
                SecretKey key = new SecretKeySpec(keyBytes, ALGORITHM);
                decryptFile(inputFile, outputFile, key);
                System.out.println("Archivo descifrado exitosamente: " + outputFile);
            } else {
                System.out.println("Opción inválida. Use '1' para cifrar o '2' para descifrar.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}