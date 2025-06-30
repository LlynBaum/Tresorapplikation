package ch.bbw.pr.tresorbackend.controller;

import ch.bbw.pr.tresorbackend.model.Secret;
import ch.bbw.pr.tresorbackend.model.NewSecret;
import ch.bbw.pr.tresorbackend.model.EncryptCredentials;
import ch.bbw.pr.tresorbackend.model.User;
import ch.bbw.pr.tresorbackend.service.SecretService;
import ch.bbw.pr.tresorbackend.service.UserService;
import ch.bbw.pr.tresorbackend.util.EncryptUtil;
import ch.bbw.pr.tresorbackend.util.JwtUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SecretController
 * @author Peter Rutschmann
 */
@RestController
@AllArgsConstructor
@RequestMapping("api/secrets")
public class SecretController {

   private SecretService secretService;
   private UserService userService;
   private JwtUtil jwtUtil;

   // create secret REST API
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @PostMapping
   public ResponseEntity<String> createSecret2(@Valid @RequestBody NewSecret newSecret, BindingResult bindingResult, @CookieValue("jwt") String jwt) {
      //input validation
      if (bindingResult.hasErrors()) {
         List<String> errors = bindingResult.getFieldErrors().stream()
               .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
               .toList();
         System.out.println("SecretController.createSecret " + errors);

         JsonArray arr = new JsonArray();
         errors.forEach(arr::add);
         JsonObject obj = new JsonObject();
         obj.add("message", arr);
         String json = new Gson().toJson(obj);

         System.out.println("SecretController.createSecret, validation fails: " + json);
         return ResponseEntity.badRequest().body(json);
      }
      System.out.println("SecretController.createSecret, input validation passed");

      Long userId = jwtUtil.extractUserId(jwt);
      User user = userService.getUserById(userId);
      if (user == null) {
         JsonObject obj = new JsonObject();
         obj.addProperty("answer", "User not found");
         String json = new Gson().toJson(obj);
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(json);
      }

      // Use password from DB to encrypt
      Secret secret = new Secret(
            null,
            user.getId(),
            new EncryptUtil(user.getPassword()).encrypt(newSecret.getContent().toString())
      );

      //save secret in db
      secretService.createSecret(secret);
      System.out.println("SecretController.createSecret, secret saved in db");
      JsonObject obj = new JsonObject();
      obj.addProperty("answer", "Secret saved");
      String json = new Gson().toJson(obj);
      System.out.println("SecretController.createSecret " + json);
      return ResponseEntity.accepted().body(json);
   }

   // Build Get All Secrets REST API
   // http://localhost:8080/api/secrets
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @GetMapping
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<List<Secret>> getAllSecrets() {
      List<Secret> secrets = secretService.getAllSecrets();
      for (Secret secret : secrets) {
         User user = userService.getUserById(secret.getUserId());
         if (user != null) {
            try {
               secret.setContent(new EncryptUtil(user.getPassword()).decrypt(secret.getContent()));
            } catch (Exception e) {
               secret.setContent("not encryptable. Wrong password?");
            }
         } else {
            secret.setContent("user not found");
         }
      }
      return new ResponseEntity<>(secrets, HttpStatus.OK);
   }

   // Build Get Secrets for Current User REST API
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @GetMapping("/user")
   public ResponseEntity<List<Secret>> getSecretsForCurrentUser(@CookieValue("jwt") String jwt) {
      Long userId = jwtUtil.extractUserId(jwt);
      User user = userService.getUserById(userId);
      if (user == null) {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
      List<Secret> secrets = secretService.getSecretsByUserId(user.getId());
      for (Secret secret : secrets) {
         try {
            secret.setContent(new EncryptUtil(user.getPassword()).decrypt(secret.getContent()));
         } catch (Exception e) {
            secret.setContent("not encryptable. Wrong password?");
         }
      }
      return ResponseEntity.ok(secrets);
   }

   // Build Get Secrets by userId REST API
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @PostMapping("/byuserid")
   public ResponseEntity<List<Secret>> getSecretsByUserId(@RequestBody EncryptCredentials credentials) {
      System.out.println("SecretController.getSecretsByUserId " + credentials);

      List<Secret> secrets = secretService.getSecretsByUserId(credentials.getUserId());
      if (secrets.isEmpty()) {
         System.out.println("SecretController.getSecretsByUserId secret isEmpty");
         return ResponseEntity.notFound().build();
      }
      //Decrypt content
      for(Secret secret: secrets) {
         try {
            secret.setContent(new EncryptUtil(credentials.getEncryptPassword()).decrypt(secret.getContent()));
         } catch (EncryptionOperationNotPossibleException e) {
            System.out.println("SecretController.getSecretsByUserId " + e + " " + secret);
            secret.setContent("not encryptable. Wrong password?");
         }
      }

      System.out.println("SecretController.getSecretsByUserId " + secrets);
      return ResponseEntity.ok(secrets);
   }

   // Build Get Secrets by email REST API
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @PostMapping("/byemail")
   public ResponseEntity<List<Secret>> getSecretsByEmail(@RequestBody EncryptCredentials credentials) {
      System.out.println("SecretController.getSecretsByEmail " + credentials);

      User user = userService.findByEmail(credentials.getEmail());

      List<Secret> secrets = secretService.getSecretsByUserId(user.getId());
      if (secrets.isEmpty()) {
         System.out.println("SecretController.getSecretsByEmail secret isEmpty");
         return ResponseEntity.notFound().build();
      }
      //Decrypt content
      for(Secret secret: secrets) {
         try {
            secret.setContent(new EncryptUtil(credentials.getEncryptPassword()).decrypt(secret.getContent()));
         } catch (EncryptionOperationNotPossibleException e) {
            System.out.println("SecretController.getSecretsByEmail " + e + " " + secret);
            secret.setContent("not encryptable. Wrong password?");
         }
      }

      System.out.println("SecretController.getSecretsByEmail " + secrets);
      return ResponseEntity.ok(secrets);
   }

   // Build Update Secrete REST API
   // http://localhost:8080/api/secrets/1
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @PutMapping("{id}")
   public ResponseEntity<String> updateSecret(
         @PathVariable("id") Long secretId,
         @Valid @RequestBody NewSecret newSecret,
         @CookieValue("jwt") String jwt,
         BindingResult bindingResult) {
      //input validation
      if (bindingResult.hasErrors()) {
         List<String> errors = bindingResult.getFieldErrors().stream()
               .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
               .toList();
         System.out.println("SecretController.createSecret " + errors);

         JsonArray arr = new JsonArray();
         errors.forEach(arr::add);
         JsonObject obj = new JsonObject();
         obj.add("message", arr);
         String json = new Gson().toJson(obj);

         System.out.println("SecretController.updateSecret, validation fails: " + json);
         return ResponseEntity.badRequest().body(json);
      }

      Long userId = jwtUtil.extractUserId(jwt);
      User user = userService.getUserById(userId);

      //get Secret with id
      Secret dbSecrete = secretService.getSecretById(secretId);
      if(dbSecrete == null){
         System.out.println("SecretController.updateSecret, secret not found in db");
         JsonObject obj = new JsonObject();
         obj.addProperty("answer", "Secret not found in db");
         String json = new Gson().toJson(obj);
         System.out.println("SecretController.updateSecret failed:" + json);
         return ResponseEntity.badRequest().body(json);
      }

      //check if Secret in db has not same userid
      if(dbSecrete.getUserId() != user.getId()){
         System.out.println("SecretController.updateSecret, not same user id");
         JsonObject obj = new JsonObject();
         obj.addProperty("answer", "Secret has not same user id");
         String json = new Gson().toJson(obj);
         System.out.println("SecretController.updateSecret failed:" + json);
         return ResponseEntity.badRequest().body(json);
      }

      //check if Secret can be decrypted with password
      try {
         new EncryptUtil(user.getPassword()).decrypt(dbSecrete.getContent());
      } catch (EncryptionOperationNotPossibleException e) {
         System.out.println("SecretController.updateSecret, invalid password");
         JsonObject obj = new JsonObject();
         obj.addProperty("answer", "Password not correct.");
         String json = new Gson().toJson(obj);
         System.out.println("SecretController.updateSecret failed:" + json);
         return ResponseEntity.badRequest().body(json);
      }
      //modify Secret in db.
      Secret secret = new Secret(
            secretId,
            user.getId(),
            new EncryptUtil(user.getPassword()).encrypt(newSecret.getContent().toString())
      );
      Secret updatedSecret = secretService.updateSecret(secret);
      //save secret in db
      secretService.createSecret(secret);
      System.out.println("SecretController.updateSecret, secret updated in db");
      JsonObject obj = new JsonObject();
      obj.addProperty("answer", "Secret updated");
      String json = new Gson().toJson(obj);
      System.out.println("SecretController.updateSecret " + json);
      return ResponseEntity.accepted().body(json);
   }

   // Build Delete Secret REST API
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @DeleteMapping("{id}")
   public ResponseEntity<String> deleteSecret(@PathVariable("id") Long secretId) {
      //todo: Some kind of brute force delete, perhaps test first userid and encryptpassword
      secretService.deleteSecret(secretId);
      System.out.println("SecretController.deleteSecret succesfully: " + secretId);
      return new ResponseEntity<>("Secret successfully deleted!", HttpStatus.OK);
   }
}
