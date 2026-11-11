package com.example.proyecto.util;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class SeguridadUtil {

    private static final int LONGITUD_SAL = 16;
    private static final int ITERACIONES = 10000;
    private static final int LONGITUD_CLAVE = 256;

    private SeguridadUtil() {
    }

    public static String generarHash(String password) {
        byte[] sal = new byte[LONGITUD_SAL];
        new SecureRandom().nextBytes(sal);
        byte[] hash = derivar(password, sal);
        return Base64.encodeToString(sal, Base64.NO_WRAP) + ":" +
                Base64.encodeToString(hash, Base64.NO_WRAP);
    }

    public static boolean verificar(String password, String almacenado) {
        String[] partes = almacenado.split(":");
        if (partes.length != 2) {
            return false;
        }
        byte[] sal = Base64.decode(partes[0], Base64.NO_WRAP);
        byte[] esperado = Base64.decode(partes[1], Base64.NO_WRAP);
        byte[] calculado = derivar(password, sal);
        return MessageDigest.isEqual(esperado, calculado);
    }

    private static byte[] derivar(String password, byte[] sal) {
        try {
            PBEKeySpec especificacion = new PBEKeySpec(password.toCharArray(), sal, ITERACIONES, LONGITUD_CLAVE);
            SecretKeyFactory fabrica = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return fabrica.generateSecret(especificacion).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No se pudo generar el hash de la contraseña", e);
        }
    }
}