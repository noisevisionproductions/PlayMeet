package com.noisevisionproductions.playmeet.utilities;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

public class AESDataEncryption {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    public static String encrypt(String valueToEnc) {
        try {
            Key key = getSecretKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecureRandom secureRandom = new SecureRandom();

            byte[] iv = new byte[cipher.getBlockSize()];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
            byte[] encValue = cipher.doFinal(valueToEnc.getBytes());
            byte[] finalCipherText = new byte[iv.length + encValue.length];
            System.arraycopy(iv, 0, finalCipherText, 0, iv.length);
            System.arraycopy(encValue, 0, finalCipherText, iv.length, encValue.length);
            return Base64.getEncoder().encodeToString(finalCipherText);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encryptedValue) {
        try {
            Key key = getSecretKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] encValue = Base64.getDecoder().decode(encryptedValue);
            byte[] iv = new byte[cipher.getBlockSize()];
            System.arraycopy(encValue, 0, iv, 0, iv.length);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            byte[] cipherText = new byte[encValue.length - iv.length];
            System.arraycopy(encValue, iv.length, cipherText, 0, cipherText.length);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            byte[] decryptedValue = cipher.doFinal(cipherText);
            return new String(decryptedValue);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Key getSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder("myKeyAlias",
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setRandomizedEncryptionRequired(true)
                    .build());
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create a secret key", e);
        }
      /*  // Replace this with your own method of securely generating/storing keys
        byte[] keyValue = new byte[]{'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};
        return new SecretKeySpec(keyValue, "AES");*/
    }
}
