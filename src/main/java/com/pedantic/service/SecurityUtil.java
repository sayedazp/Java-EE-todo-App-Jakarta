package com.pedantic.service;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.util.ByteSource;

import javax.crypto.spec.SecretKeySpec;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

//Change 1


@RequestScoped
public class SecurityUtil {


    @Inject
    private QueryService queryService;
    




    public SecretKey generateKey(String keyString) {
        keyString = keyString + "la;skdlsakdlkas;dk;saldk;lsadklksadjashdkashdkjashdashjdhaskjd";
        byte[] keyBytes = keyString.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
                
//        return new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "DES"); 
    }

    public boolean authenticateUser(String email, String password) {
        return queryService.authenticateUser(email, password);

    }

    public Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }



    public boolean passwordsMatch(String dbStoredHashedPassword, String saltText, String clearTextPassword) {
        ByteSource salt = ByteSource.Util.bytes(Hex.decode(saltText));
        String hashedPassword = hashAndSaltPassword(clearTextPassword, salt);
        System.err.println("salt" + salt.toString());
        System.out.println("com.pedantic.service.SecurityUtil.passwordsMatch()");
        System.out.println("hashedpassword" + hashedPassword);
        return hashedPassword.equals(dbStoredHashedPassword);
    }

    public Map<String, String> hashPassword(String clearTextPassword) {
        ByteSource salt = getSalt();


        Map<String, String> credMap = new HashMap<>();
        credMap.put("hashedPassword", hashAndSaltPassword(clearTextPassword, salt));
        credMap.put("salt", salt.toHex());
        System.out.println("hashedPassword  " + credMap.get("hashedPassword"));
        System.out.println("salt  " + credMap.get("salt"));
        return credMap;


    }

    private String hashAndSaltPassword(String clearTextPassword, ByteSource salt) {
        return new Sha512Hash(clearTextPassword, salt, 2000000).toHex();
    }

    private ByteSource getSalt() {
        return new SecureRandomNumberGenerator().nextBytes();
    }

}
