# Informe Técnico: seguridad-informatica-TP2

## Introducción

Este proyecto implementa una herramienta de cifrado y descifrado de archivos utilizando el algoritmo AES-256 en modo CBC con padding PKCS5. El objetivo es proteger la confidencialidad de archivos mediante una clave secreta generada aleatoriamente para cada operación de cifrado.

## Funcionamiento del Código

### 1. Algoritmo y Configuración
- **Algoritmo:** AES (Advanced Encryption Standard)
- **Modo:** CBC (Cipher Block Chaining)
- **Padding:** PKCS5Padding
- **Tamaño de clave:** 256 bits
- **Tamaño del IV:** 16 bytes

### 2. Componentes Principales

#### a) Generación de Clave
- Se utiliza `KeyGenerator` para crear una clave AES-256 aleatoria.
- La clave se muestra al usuario en formato Base64 para facilitar su almacenamiento y reutilización.

#### b) Cifrado de Archivos
- Se genera un IV (vector de inicialización) aleatorio para cada cifrado.
- El IV se almacena al inicio del archivo cifrado.
- El archivo se lee en bloques y se cifra usando la clave y el IV.
- El resultado se guarda en un archivo con extensión `.enc` o `.bin`.

#### c) Descifrado de Archivos
- Se recupera el IV desde el inicio del archivo cifrado.
- El usuario debe ingresar la clave Base64 utilizada en el cifrado.
- El archivo se descifra y se guarda con su extensión original.

#### d) Interfaz Gráfica (GUI)
- Permite seleccionar archivos, cifrar y descifrar mediante botones.
- Muestra la clave generada y permite copiarla al portapapeles.
- Informa al usuario sobre el estado de las operaciones.

## Estructura de Archivos
- `FileEncryption.java`: Lógica de cifrado y descifrado.
- `FileEncryptionGUI.java`: Interfaz gráfica de usuario.
- `README.md`: Este informe y anexo de uso.

## Seguridad
- Cada archivo cifrado utiliza un IV único, garantizando que el mismo archivo cifrado dos veces produzca resultados diferentes.
- La clave nunca se almacena en disco, solo se muestra al usuario.
- El usuario es responsable de guardar la clave Base64 para poder descifrar el archivo posteriormente.

---

## Anexo: Instrucciones de Ejecución

### Compilación y Creación del JAR

1. Asegúrese de tener Java 8 o superior instalado.
2. Abra una terminal y navegue al directorio del proyecto.
3. Compile el proyecto con el siguiente comando:
   ```bash
   javac -d out src/FileEncryption.java src/FileEncryptionGUI.java
   jar cfe FileEncryptionGUI.jar FileEncryptionGUI -C out .
   ```

### Ejecución con Interfaz Gráfica (GUI)
1. Ejecute el .Jar `FileEncryptionGUI`. o con el comando 
    ```bash
    java -jar FileEncryptionGUI.jar
    ```
2En la ventana:
   - Pulse "Explorar..." para seleccionar el archivo.
   - Pulse "Encriptar" para cifrar. Se mostrará la clave Base64 (cópiela y guárdela).
   - Para descifrar, seleccione el archivo cifrado, ingrese la clave Base64 y pulse "Desencriptar".
   - Elija el nombre y ubicación del archivo de salida.

### Requisitos
- Java 8 o superior.

---

## Créditos
Trabajo Práctico 2 - Seguridad Informática - UNQ 2025

Autores: Matias Dominguez y Matias Acosta
