# seguridad-informatica-TP2

Instrucciones de uso:

Ubicar el archivo Main.jar en alguna carpeta. Por simplicidad, llevar a la misma carpeta el archivo a cifrar
Abrir una terminal en la ruta de dicha carpeta, ejecutar el código "java -jar Main.jar"
 - Para cifrar:
   Seleccionar la opción 1, escribir el nombre del archivo junto a su extensión (incluyendo la ruta si se encuentra en una dirección diferente a Main.jar).
   Escribir el nombre del archivo de salida, sin extensión, ya que se cifra el archivo a un formato .bin .
   Guardar la clave generada para descifrar el archivo.

 - Para descifrar:
   Volver a ejecutar el código "java -jar Main.jar" y seleccionar la opción 2.
   Ingresar el nombre del archivo cifrado junto a su extensión .bin .
   Ingresar el nombre del archivo de salida junto a su extensión original.
   Ingresar la clave generada al momento de cifrar.


Explicación del código:

Se importan las dependencias para el cifrado y descifrado, generación de claves, números aleatorios seguros para el IV, codificación y decodificación de datos en formato Base64 (para la clave), y lectura de la entrada del usuario desde la consola.
 - Constantes:
   
   ALGORITHM: Especifica el algoritmo criptográfico AES (Advanced Encryption Standard).
   TRANSFORMATION: Define la configuración completa del cifrado:
     AES: Algoritmo.
     CBC: Modo de operación (Cipher Block Chaining).
     PKCS5Padding: Relleno para asegurar que los datos tengan un tamaño múltiplo del tamaño de bloque (16 bytes para AES).
   KEY_SIZE: Tamaño de la clave en bits (256).
   IV_SIZE: Tamaño del vector de inicialización en bytes (16 bytes para AES en modo CBC).
 
 - Métodos:

   Método generateKey: Genera una clave AES-256 aleatoria.
   
   Método encryptFile: Cifra un archivo de entrada y guarda el resultado en un archivo de salida. Crea un objeto Cipher con la configuración AES/CBC/PKCS5Padding.
    Genera un IV aleatorio de 16 bytes usando SecureRandom (para que un mismo archivo cifrado dos veces resulte diferente).
    Configura el cifrado en modo ENCRYPT_MODE con la clave y el IV.
    Abre el archivo de entrada con FileInputStream y el de salida con FileOutputStream. En el cifrado mismo, comienza escribiendo el IV al inicio del archivo de salida (necesario para el descifrado), después se lee el archivo de entrada en bloques de 1024 bytes, cada bloque se va cifrando con cipher.update y guardando el resultado. Por último, finaliza el cifrado con cipher.doFinal para procesar los datos restantes y el padding.
   
   Método decryptFile: Descifra un archivo cifrado y guarda el contenido original en un archivo de salida. Crea de nuevo un objeto Cipher con la configuración AES/CBC/PKCS5Padding. Lee los primeros 16 bytes del archivo cifrado, que corresponden al IV (vector de inicialización) almacenado durante el cifrado. Después, configura el descifrado en modo DECRYPT_MODE con la clave proporcionada y el IV recuperado. Abre el archivo cifrado con FileInputStream y el de salida con FileOutputStream. En el descifrado mismo, se lee el archivo cifrado en bloques de 1024 bytes (ignorando los primeros 16 bytes del IV), cada bloque se descifra con cipher.update y se escribe el resultado en el archivo de salida. Por último, finaliza el descifrado con cipher.doFinal para eliminar el padding y procesar los datos restantes.
   
   Método main: Proporciona una interfaz de consola. Muestra un menú con las opciones "1. Cifrar archivo" y "2. Descifrar archivo".
   En ambos casos, mediante el objeto Scanner para leer la escritura en consola, primero se pide la ruta del archivo de entrada.
   Si se elige cifrar (opción 1), se pide el nombre del archivo de salida (sin extensión), se genera una clave AES-256, la muestra en Base64 para que el usuario la guarde, y llama a encryptFile para realizar el cifrado.
   Si se elige descifrar (opción 2), se pide el nombre del archivo de salida (con la extensión original), se pide la clave en Base64, la decodifica para crear un SecretKey, y llama a decryptFile para realizar el descifrado.
   Se captura cualquier error (como archivo no encontrado o clave inválida) y muestra un mensaje dado el caso. Para terminar, cierra el Scanner para liberar recursos.
