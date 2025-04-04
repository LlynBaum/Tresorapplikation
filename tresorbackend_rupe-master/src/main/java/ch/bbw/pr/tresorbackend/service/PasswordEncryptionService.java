package ch.bbw.pr.tresorbackend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;

/**
 * PasswordEncryptionService
 * @autor Peter Rutschmann
 */
@Service
public class PasswordEncryptionService {
   private final String pepper = "staticPepperValue";
   private final SecureRandom secureRandom = new SecureRandom();

   public String hashPassword(String password) {
      String salt = generateSalt();
      String saltedPassword = salt + password + pepper;
      return new BCryptPasswordEncoder().encode(saltedPassword);
   }

   private String generateSalt() {
      byte[] saltBytes = new byte[16];
      secureRandom.nextBytes(saltBytes);
      return new String(saltBytes);
   }
}
