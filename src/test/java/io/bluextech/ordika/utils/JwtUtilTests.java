//package io.bluextech.ordika.utils;
///* Created by limxuanhui on 25/8/24 */
//
//import io.jsonwebtoken.Jwt;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Deserializer;
//import io.jsonwebtoken.jackson.io.JacksonDeserializer;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class JwtUtilTests {
//
//    private Long ACCESS_TOKEN_VALID_DURATION_MS;
//    private Long REFRESH_TOKEN_VALID_DURATION_MS;
//    private String JWT_SECRET;
//    private JwtUtil jwtUtil;
//    private Deserializer<Map<String, ?>> deserializer;
//
//    @BeforeEach
//    void setUp() {
//        this.ACCESS_TOKEN_VALID_DURATION_MS = 3600000L;
//        this.REFRESH_TOKEN_VALID_DURATION_MS = 86400000L;
//        this.JWT_SECRET = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b";
//        this.jwtUtil = new JwtUtil(JWT_SECRET);
//        this.deserializer = new JacksonDeserializer<>();
//    }
//
//    @Test
//    void testIfDecodeAndEncodeAreReversible_Test() {
//////        Jwts.SIG.RS256.keyPair().build();
////        System.out.println("SECRET: " + SECRET);
////        var x = Decoders.BASE64.decode(SECRET);
////        System.out.println("X: " + x);
////        var y = Encoders.BASE64.encode(x);
////        System.out.println("Y: " + y);
//////        var z = Arrays.toString(Decoders.BASE64.decode(y));
//////        System.out.println("Z: " + z);
////        assertEquals(y, SECRET);
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    void createToken_AccessToken_Test() {
//        String subject = "ordika-user-id";
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("role", "ROLE_ORDIKA_USER");
//        claims.put("tokenType", "access");
//        String token = jwtUtil.createToken(subject, claims, ACCESS_TOKEN_VALID_DURATION_MS);
//        System.out.println(token);
//        assertTrue(jwtUtil.isTokenValid(token, subject));
//
//        Jwt<?, ?> parsedToken = Jwts.parser().json(deserializer)
//                .verifyWith(jwtUtil.getSecretKey())
//                .build()
//                .parse(token);
//
//        Map<String, Object> parsedHeader = parsedToken.getHeader();
//        assertEquals(parsedHeader.get("typ"), "access");
//
//        Map<String, Object> parsedPayload = (Map<String, Object>) parsedToken.getPayload();
//        assertEquals(parsedPayload.get("role"), "ROLE_ORDIKA_USER");
//        System.out.println(parsedPayload.get("role"));
//
//        long expirationMs = ((long)(parsedPayload.get("exp")) - (long)(parsedPayload.get("iat"))) * 1000;
//        System.out.println(expirationMs);
//        assertTrue(expirationMs >= ACCESS_TOKEN_VALID_DURATION_MS);
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    void createToken_RefreshToken_Test() {
//        String subject = "ordika-user-id";
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("role", "ROLE_ORDIKA_USER");
//        claims.put("tokenType", "refresh");
//        String token = jwtUtil.createToken(subject, claims, REFRESH_TOKEN_VALID_DURATION_MS);
//        System.out.println(token);
//        assertTrue(jwtUtil.isTokenValid(token, subject));
//
//        Jwt<?, ?> parsedToken = Jwts.parser().json(deserializer)
//                .verifyWith(jwtUtil.getSecretKey())
//                .build()
//                .parse(token);
//
//        Map<String, Object> parsedHeader = parsedToken.getHeader();
//        assertEquals(parsedHeader.get("typ"), "refresh");
//
//        Map<String, Object> parsedPayload = (Map<String, Object>) parsedToken.getPayload();
//        assertEquals(parsedPayload.get("role"), "ROLE_ORDIKA_USER");
//        System.out.println(parsedPayload.get("role"));
//
//        long expirationMs = ((long)(parsedPayload.get("exp")) - (long)(parsedPayload.get("iat"))) * 1000;
//        System.out.println(expirationMs);
//        assertTrue(expirationMs >= REFRESH_TOKEN_VALID_DURATION_MS);
//    }
//}
