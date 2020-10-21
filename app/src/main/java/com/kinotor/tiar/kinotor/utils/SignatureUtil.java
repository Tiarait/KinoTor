package com.kinotor.tiar.kinotor.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.kinotor.tiar.kinotor.BuildConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class SignatureUtil {

    public static boolean isGetHooked() {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = BuildConfig.APPLICATION_ID;
        packageInfo.signatures = new Signature[0];
        try {
            int length = ((Object[]) PackageInfo.class.getField("signatures").get(packageInfo)).length;
            return length != 0;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean validateAppSignature(Context context){
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : packageInfo.signatures) {

                String sha1 = getSHA1(signature.toByteArray());

                return BuildConfig.TRASH.equals(sha1);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    //computed the sha1 hash of the signature
    private static String getSHA1(byte[] sig) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA1", "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        digest.update(sig);
        byte[] hashtext = digest.digest();
        return bytesToHex(hashtext);
    }

    //util method to convert byte array to hex string
    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


}
