package ch.bbw.pr.tresorbackend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * PasswordEncryptionService
 * @autor Peter Rutschmann
 */
@Service
public class PasswordEncryptionService {
   private final String pepper = "jabadabadudulustigevogel";

   public String hashPassword(String password) {
      // BCrypt automatically handles salting internally
      String pepperPassword = password + pepper;
      return new BCryptPasswordEncoder().encode(pepperPassword);
   }

   public boolean checkPassword(String rawPassword, String encodedPassword) {
      String pepperPassword = rawPassword + pepper;
      return new BCryptPasswordEncoder().matches(pepperPassword, encodedPassword);
   }
}
