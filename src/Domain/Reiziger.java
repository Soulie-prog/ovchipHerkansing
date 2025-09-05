package Domain;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data // geeft getters,setters en ToString meee
@AllArgsConstructor // geeft een constructor met alle argumenten
@NoArgsConstructor// bijhorende errr: java: constructor Reiziger in class Domain.Reiziger cannot be applied to given types;
//required: no arguments
public class Reiziger {
    private int id;
    private String voorletters;
    private String tussenvoegsel;
    private String achternaam;
    private Date geboortedatum;
}