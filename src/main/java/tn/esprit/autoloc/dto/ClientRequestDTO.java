package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestDTO {

    @NotBlank(message = "Le nom du client est obligatoire")
    @Size(max = 50, message = "Le nom du client ne doit pas dépasser 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom du client est obligatoire")
    @Size(max = 50, message = "Le prénom du client ne doit pas dépasser 50 caractères")
    private String prenom;

    @NotBlank(message = "L'e-mail du client est obligatoire")
    @Email(message = "L'e-mail du client est invalide")
    @Size(max = 100, message = "L'e-mail du client ne doit pas dépasser 100 caractères")
    private String email;

    @NotBlank(message = "Le téléphone du client est obligatoire")
    @Pattern(regexp = "^[0-9+ .-]{8,20}$", message = "Le téléphone du client est invalide")
    private String telephone;

    @NotBlank(message = "Le numéro de permis du client est obligatoire")
    @Pattern(regexp = "^[0-9A-Za-z-]{5,20}$", message = "Le numéro de permis du client est invalide")
    private String numPermis;

    @NotBlank(message = "La date d'inscription du client est obligatoire")
    @Size(max = 20, message = "La date d'inscription ne doit pas dépasser 20 caractères")
    private String dateInscription;
}
