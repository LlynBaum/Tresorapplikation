package ch.bbw.pr.tresorbackend.util;

import org.jasypt.util.text.AES256TextEncryptor;

/**
 * EncryptUtil
 * Used to encrypt content.
 * Not implemented yet.
 * @author Peter Rutschmann
 */
public class EncryptUtil {

   private final AES256TextEncryptor textEncryptor;

   public EncryptUtil(String secretKey) {
      textEncryptor = new AES256TextEncryptor();
      textEncryptor.setPassword(secretKey);
   }

   public String encrypt(String data) {
      return textEncryptor.encrypt(data);
   }

   public String decrypt(String data) {
      return textEncryptor.decrypt(data);
   }
}
