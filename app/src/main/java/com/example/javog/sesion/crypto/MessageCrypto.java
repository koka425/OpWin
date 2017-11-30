package com.example.javog.sesion.crypto;

/**
 * Created by Jose Pablo on 11/30/2017.
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageCrypto {

    public static final String HASH_MD5 = "MD5";
    public static final String HASH_SHA = "SHA-1";
    public static final String HASH_SHA256 = "SHA-256";

    private MessageDigest md;

    public String GenerateHash(String data, String hashMode){
        try {
            md = MessageDigest.getInstance(hashMode);
            md.update(data.getBytes());
            byte[] mdHash = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte bytes: mdHash) {
                sb.append(String.format("%02x", bytes & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException nse){
            nse.printStackTrace();
        }
        return null;
    }

}
