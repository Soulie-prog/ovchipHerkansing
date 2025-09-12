package Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "adres")
public class AdresHibernate {

    @Id
    @Column(name = "adres_id")
    private int adresId;

    @Column(name = "postcode", nullable = false)
    private String postcode;

    @Column(name = "huisnummer", nullable = false)
    private String huisnummer;

    @Column(name = "straat", nullable = false)
    private String straat;

    @Column(name = "woonplaats", nullable = false)
    private String woonplaats;

    // 🔹 One-to-One relatie naar Reiziger
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reiziger_id")
    private ReizigerHibernate reiziger;

    public void setReiziger(ReizigerHibernate reiziger) {
        this.reiziger = reiziger;
        if (reiziger != null && reiziger.getAdres() != this) {
            reiziger.setAdres(this);
        }
    }

    @Override
    public String toString() {
        return "Adres{" +
                "#" + adresId + " " +
                straat + " " + huisnummer + ", " +
                postcode + " " + woonplaats +
                (reiziger != null ? ", Reiziger: " + reiziger.getNaam() : "") +
                "}";
    }
}
