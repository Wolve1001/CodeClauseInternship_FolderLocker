import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.spec.KeySpec;
import java.util.Scanner;

public class FolderLocker {
    private static final String ENCRYPTION_EXTENSION = ".locked";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Folder Locker Application");
        System.out.print("Enter the folder path to lock/unlock: ");
        String folderPath = scanner.nextLine();

        System.out.print("Enter 'lock' or 'unlock': ");
        String action = scanner.nextLine();

        if (action.equalsIgnoreCase("lock")) {
            lockFolder(folderPath);
            System.out.println("Folder locked successfully.");
        } else if (action.equalsIgnoreCase("unlock")) {
            unlockFolder(folderPath);
            System.out.println("Folder unlocked successfully.");
        } else {
            System.err.println("Invalid action. Use 'lock' or 'unlock'.");
        }
    }

    private static void lockFolder(String folderPath) {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Invalid folder path.");
            return;
        }

        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    lockFile(file);
                }
            }
        }
    }

    private static void lockFile(File file) {
        try {
            FileInputStream inStream = new FileInputStream(file);
            byte[] fileData = new byte[(int) file.length()];
            inStream.read(fileData);
            inStream.close();

            // Generate a secret key from a password (you should use a more secure way to store/manage this)
            SecretKey secretKey = generateSecretKey("YourSecretPassword");

            // Create a cipher for encryption
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedData = cipher.doFinal(fileData);

            FileOutputStream outStream = new FileOutputStream(file.getPath() + ENCRYPTION_EXTENSION);
            outStream.write(encryptedData);
            outStream.close();

            file.delete(); // Delete the original unencrypted file
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void unlockFolder(String folderPath) {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Invalid folder path.");
            return;
        }

        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(ENCRYPTION_EXTENSION)) {
                    unlockFile(file);
                }
            }
        }
    }

    private static void unlockFile(File file) {
        try {
            FileInputStream inStream = new FileInputStream(file);
            FileOutputStream outStream = new FileOutputStream(file.getPath().replace(ENCRYPTION_EXTENSION, ""));

            // Generate a secret key from a password (you should use a more secure way to store/manage this)
            SecretKey secretKey = generateSecretKey("YourSecretPassword");

            // Create a cipher for decryption
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            CipherInputStream cipherInStream = new CipherInputStream(inStream, cipher);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cipherInStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

            cipherInStream.close();
            inStream.close();
            outStream.close();

            file.delete(); // Delete the encrypted file
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SecretKey generateSecretKey(String password) throws Exception {
        // Use a secure key derivation function (KDF) to generate a key from the password
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), new byte[16], 65536, 128); // Tweak these parameters
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }
}
