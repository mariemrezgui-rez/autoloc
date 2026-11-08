package tn.esprit.autoloc.dto;

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
public class AgenceRequestDTO {

    @NotBlank(message = "Le nom de l'agence est obligatoire")
    @Size(max = 100, message = "Le nom de l'agence ne doit pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "La ville de l'agence est obligatoire")
    @Size(max = 50, message = "La ville de l'agence ne doit pas dépasser 50 caractères")
    private String ville;

    @NotBlank(message = "L'adresse de l'agence est obligatoire")
    @Size(max = 255, message = "L'adresse de l'agence ne doit pas dépasser 255 caractères")
    private String adresse;

    @NotBlank(message = "Le téléphone de l'agence est obligatoire")
    @Pattern(regexp = "^[0-9+ .-]{8,20}$", message = "Le téléphone de l'agence est invalide")
    private String telephone;
}
