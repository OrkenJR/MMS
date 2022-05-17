package kz.iitu.orken.medical_managament_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"medicines"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Disease {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name of disease cannot be blank")
    @LastModifiedBy
    private String name;

    @ManyToMany(mappedBy = "diseases", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Medicine> medicines;

    public Disease(String name) {
        this.name = name;
    }
}
