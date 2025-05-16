package ch.bbw.pr.tresorbackend.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

/**
 * LoginRequest
 * Represents the payload for login requests.
 */
@Value
public class LoginRequest {
   @NotEmpty(message="E-Mail is required.")
   private String email;

    @NotEmpty(message="Password is required.")
   private String password;
}
