package Domain;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reiziger")
public class ReizigerHibernate {

    @Id
    @Column(name = "reiziger_id")
    private int id;

    @Column(name = "voorletters", nullable = false)
    private String voorletters;

    @Column(name = "tussenvoegsel")
    private String tussenvoegsel;

    @Column(name = "achternaam", nullable = false)
    private String achternaam;

    @Column(name = "geboortedatum")
    private Date geboortedatum;
}
