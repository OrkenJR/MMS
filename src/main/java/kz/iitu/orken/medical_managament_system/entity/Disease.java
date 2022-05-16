package kz.iitu.orken.medical_managament_system.entity;

import kz.iitu.orken.medical_managament_system.entity.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Getter
@Setter
public class Disease {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name of disease cannot be blank")
    @LastModifiedBy
    private String name;
    @ManyToMany(mappedBy = "diseases")
    private Set<Medicine> medicines;
}
