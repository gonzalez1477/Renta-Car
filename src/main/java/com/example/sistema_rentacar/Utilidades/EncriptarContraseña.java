package com.example.sistema_rentacar.Utilidades;


import org.mindrot.jbcrypt.BCrypt;

public class EncriptarContraseña {

    // Número de rondas de encriptación de 10-12
    private static final int BCRYPT_ROUNDS = 12;


    //Encripta una contraseña en texto plano
    public static String encryptPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }


    //Verifica si una contraseña en texto plano coincide con un hash
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // El hash no es válido
            System.err.println("Hash de contraseña inválido: " + e.getMessage());
            return false;
        }
    }


    //Verifica si un string es un hash BCrypt válido
    public static boolean isValidBCryptHash(String potentialHash) {
        if (potentialHash == null || potentialHash.isEmpty()) {
            return false;
        }

        // Los hashes BCrypt comienzan con $2a$, $2b$, o $2y$ y tienen 60 caracteres
        return potentialHash.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
    }
}
