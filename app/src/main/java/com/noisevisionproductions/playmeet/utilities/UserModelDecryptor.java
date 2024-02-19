package com.noisevisionproductions.playmeet.utilities;

import android.util.Log;

import com.noisevisionproductions.playmeet.userManagement.UserModel;

public class UserModelDecryptor {
    public static UserModel decryptUserModel(UserModel encryptedUserModel) throws Exception {
        UserModel decryptedUserModel = new UserModel();
        decryptedUserModel.setName(encryptedUserModel.getName() != null ? AESDataEncryption.decrypt(encryptedUserModel.getName()) : null);
        decryptedUserModel.setAge(encryptedUserModel.getAge() != null ? AESDataEncryption.decrypt(encryptedUserModel.getAge()) : null);
        decryptedUserModel.setLocation(encryptedUserModel.getLocation() != null ? AESDataEncryption.decrypt(encryptedUserModel.getLocation()) : null);
        decryptedUserModel.setGender(encryptedUserModel.getGender() != null ? AESDataEncryption.decrypt(encryptedUserModel.getGender()) : null);
        decryptedUserModel.setAboutMe(encryptedUserModel.getAboutMe() != null ? AESDataEncryption.decrypt(encryptedUserModel.getAboutMe()) : null);

        return decryptedUserModel;
    }

    public static void getDercyptionError(String message) {
        Log.e("Decryption Error", message);
    }
}
