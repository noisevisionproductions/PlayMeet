package com.noisevisionproductions.playmeet.utilities;

import android.content.Context;
import android.util.Log;

import com.noisevisionproductions.playmeet.userManagement.UserModel;

public class UserModelDecryptor {
    public static UserModel decryptUserModel(Context context, UserModel encryptedUserModel) throws Exception {
        UserModel decryptedUserModel = new UserModel();
        AESDataEncryption encryption = new AESDataEncryption(context);
        decryptedUserModel.setName(encryptedUserModel.getName() != null ? encryption.decrypt(encryptedUserModel.getName()) : null);
        decryptedUserModel.setAge(encryptedUserModel.getAge() != null ? encryption.decrypt(encryptedUserModel.getAge()) : null);
        decryptedUserModel.setLocation(encryptedUserModel.getLocation() != null ? encryption.decrypt(encryptedUserModel.getLocation()) : null);
        decryptedUserModel.setGender(encryptedUserModel.getGender() != null ? encryption.decrypt(encryptedUserModel.getGender()) : null);
        decryptedUserModel.setAboutMe(encryptedUserModel.getAboutMe() != null ? encryption.decrypt(encryptedUserModel.getAboutMe()) : null);

        return decryptedUserModel;
    }

    public static void getDercyptionError(String message) {
        Log.e("Decryption Error", message);
    }
}
