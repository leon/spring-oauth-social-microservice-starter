package se.radley.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private ObjectMapper objectMapper = new ObjectMapper();
    private Signer signer;
    private SignatureVerifier verifier;

    @Autowired
    public JwtService(@Value("${jwt.key}") String key) {
        this.signer = new MacSigner(key);
        this.verifier = new MacSigner(key);
    }

    public String encode(Object token) {
        String content;
        try {
            content = objectMapper.writeValueAsString(token);
        }
        catch (Exception e) {
            throw new IllegalStateException("Cannot encode access token to JSON", e);
        }
        return JwtHelper.encode(content, signer).getEncoded();
    }

    public UserJwtToken decode(String token) {
        try {
            Jwt jwt = JwtHelper.decodeAndVerify(token, verifier);
            String content = jwt.getClaims();
            return objectMapper.readValue(content, UserJwtToken.class);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Cannot decode access token from JSON", e);
        }
    }
}

