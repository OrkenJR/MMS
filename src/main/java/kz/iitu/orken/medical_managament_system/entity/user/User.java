package kz.iitu.orken.medical_managament_system.entity.user;

import com.fasterxml.jackson.annotation.*;
import kz.iitu.orken.medical_managament_system.entity.Treatment;
import lombok.*;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@ToString(exclude = {"roles", "treatmentsAsPatient", "treatmentsAsDoctor"})
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "First name should not be blank")
    private String firstName;

    @NotBlank(message = "Last name should not be blank")
    private String lastName;

    @Email
    @LastModifiedBy
    private String email;

    @NotNull
    @Size(min = 5, max = 50)
    private String username;

    @NotNull
//    @Size(min = 8, max = 140)
    @LastModifiedBy
    @JsonIgnore
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    @OneToMany
    @JsonIgnore
    private List<Treatment> treatmentsAsPatient;

    @OneToMany
    @JsonIgnore
    private List<Treatment> treatmentsAsDoctor;

}
