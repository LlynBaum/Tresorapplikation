package ch.bbw.pr.tresorbackend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * PasswordEncryptionService
 * @author Peter Rutschmann
 */
@Service
public class PasswordEncryptionService {
   //todo ergänzen!

   public PasswordEncryptionService() {
      //todo anpassen!
   }

   public String hashPassword(String password) {
      return new BCryptPasswordEncoder().encode(password);
   }
}
