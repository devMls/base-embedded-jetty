/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 *
 * @author mlarr
 */
public class JWT {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static boolean verify(String text, String signature, String tokenJWT) {
        try {
            Algorithm algorithmHS = Algorithm.HMAC256(tokenJWT);
            JWTVerifier verifier = com.auth0.jwt.JWT.require(algorithmHS).build();

            DecodedJWT verify = verifier.verify(signature);

            Claim claim = verify.getClaim("dato");

            if (claim.asString().equals(text)) {
                return true;
            } else {
                return false;
            }

        } catch (JWTVerificationException exception) {
            return false;
        }

    }

    public static boolean verify(String signature, String password) {
        try {
            Algorithm algorithmHS = Algorithm.HMAC256(password);

            JWTVerifier verifier = com.auth0.jwt.JWT.require(algorithmHS).build();

            DecodedJWT verify = verifier.verify(signature);

            return true;

        } catch (JWTVerificationException exception) {
            return false;
        }

    }

    public static String createUserToken(Object id, String password) {
        Algorithm algorithmHS = Algorithm.HMAC256(password);

        JWTCreator.Builder builder = com.auth0.jwt.JWT.create();

        return builder.withClaim("user", String.valueOf(id)).sign(algorithmHS);
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}
