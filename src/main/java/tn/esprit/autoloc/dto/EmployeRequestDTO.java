package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.RoleEmploye;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeRequestDTO {

    @NotBlank(message = "Le nom de l'employé est obligatoire")
    @Size(max = 50, message = "Le nom de l'employé ne doit pas dépasser 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom de l'employé est obligatoire")
    @Size(max = 50, message = "Le prénom de l'employé ne doit pas dépasser 50 caractères")
    private String prenom;

    @NotNull(message = "Le rôle de l'employé est obligatoire")
    private RoleEmploye role;

    @NotNull(message = "L'agence de l'employé est obligatoire")
    private Long agenceId;
}
