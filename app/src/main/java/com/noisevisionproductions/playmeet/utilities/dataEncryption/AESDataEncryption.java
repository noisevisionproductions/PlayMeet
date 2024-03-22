package com.noisevisionproductions.playmeet.utilities.dataEncryption;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.noisevisionproductions.playmeet.R;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESDataEncryption {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private byte[] key;

    public AESDataEncryption(Context context) {
        Security.addProvider(new BouncyCastleProvider());
        loadKeyFromProperties(context);
    }

    private void loadKeyFromProperties(Context context) {
        Properties prop = new Properties();
        try (InputStream input = context.getResources().openRawResource(R.raw.config)) {
            prop.load(input);
            String keyString = prop.getProperty("key");
            this.key = keyString.getBytes(StandardCharsets.UTF_8);
        } catch (Resources.NotFoundException e) {
            System.err.println("file config.properties not found in res/raw");
            Log.e("Error While encrypting", "Error While encrypting " + e.getMessage());
        } catch (IOException e) {
            Log.e("Error While encrypting", "Error While encrypting " + e.getMessage());
            System.err.println("error while loading file config.properties");
        }
    }

    public String encrypt(String valueToEnc) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(Arrays.copyOf(this.key, 16), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOf(this.key, cipher.getBlockSize()));
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] cipherText = cipher.doFinal(valueToEnc.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public String decrypt(String encryptedValue) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(Arrays.copyOf(this.key, 16), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOf(this.key, cipher.getBlockSize()));
        byte[] encValue = Base64.getDecoder().decode(encryptedValue);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decryptedValue = cipher.doFinal(encValue);
        return new String(decryptedValue, StandardCharsets.UTF_8);
    }
}