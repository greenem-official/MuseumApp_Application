package org.daylight.museumapp.components.common.storage;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private static final Path KEY_FILE = Path.of(System.getProperty("user.home"), ".app-secret.key");

    private static SecretKey secretKey;

    // Получение или создание ключа
    private static SecretKey getKey() throws Exception {
        if (secretKey != null) return secretKey;

        if (Files.exists(KEY_FILE)) {
            byte[] keyBytes = Base64.getDecoder().decode(Files.readAllBytes(KEY_FILE));
            secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, ALGORITHM);
        } else {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256);
            secretKey = keyGen.generateKey();
            Files.write(KEY_FILE, Base64.getEncoder().encode(secretKey.getEncoded()));
        }

        return secretKey;
    }

    public static byte[] encrypt(byte[] data) throws Exception {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getKey(), new GCMParameterSpec(TAG_LENGTH, iv));

        byte[] encrypted = cipher.doFinal(data);

        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

        return result;
    }

    public static byte[] decrypt(byte[] encryptedData) throws Exception {
        byte[] iv = new byte[IV_LENGTH];
        byte[] ciphertext = new byte[encryptedData.length - IV_LENGTH];

        System.arraycopy(encryptedData, 0, iv, 0, IV_LENGTH);
        System.arraycopy(encryptedData, IV_LENGTH, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, getKey(), new GCMParameterSpec(TAG_LENGTH, iv));

        return cipher.doFinal(ciphertext);
    }
}
