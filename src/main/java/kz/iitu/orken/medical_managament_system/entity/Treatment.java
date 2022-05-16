package kz.iitu.orken.medical_managament_system.entity;

import kz.iitu.orken.medical_managament_system.entity.user.User;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String description;

    @CreatedBy
    @CreatedDate
    private Date createdDate;

    private Date endDate;

    @CreatedBy
    @LastModifiedBy
    @CreatedDate
    @LastModifiedDate
    private Long price;

    @ManyToOne
    private Disease disease;

    @ManyToOne
    private User doctor;

    @ManyToOne
    private User patient;

}
