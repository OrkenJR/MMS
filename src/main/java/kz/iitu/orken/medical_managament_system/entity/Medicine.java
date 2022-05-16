package kz.iitu.orken.medical_managament_system.entity;

import kz.iitu.orken.medical_managament_system.entity.user.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Getter
@Setter
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name of medicine cannot be blank")
    @CreatedBy
    private String name;

    private Long price;
    @ManyToMany
    @JoinTable(
            name = "diseases_medicine",
            joinColumns = @JoinColumn(
                    name = "medicine_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "disease_id", referencedColumnName = "id"))
    private Set<Disease> diseases;
}
