package kz.iitu.orken.medical_managament_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString(exclude = {"diseases"})
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name of medicine cannot be blank")
    @CreatedBy
    private String name;

    private Long price;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "diseases_medicine",
            joinColumns = @JoinColumn(
                    name = "medicine_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "disease_id", referencedColumnName = "id"))
    private Set<Disease> diseases;

    public Medicine(String name, Long price) {
        this.name = name;
        this.price = price;
    }
}
