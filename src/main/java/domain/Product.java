package domain;

import java.util.ArrayList;
import java.util.List;

public class Product {

    private int productNummer;
    private String naam;
    private String beschrijving;
    private double prijs;
    private List<OVChipkaart> ovChipkaarten = new ArrayList<>();

    public Product() {
    }

    public Product(int productNummer, String naam, String beschrijving, double prijs) {
        this.productNummer = productNummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
    }

    public int getProductNummer() {
        return productNummer;
    }

    public void setProductNummer(int productNummer) {
        this.productNummer = productNummer;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public double getPrijs() {
        return prijs;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }

    public List<OVChipkaart> getOvChipkaarten() {
        return ovChipkaarten;
    }

    public void setOvChipkaarten(List<OVChipkaart> ovChipkaarten) {
        this.ovChipkaarten = ovChipkaarten;
    }

    public boolean voegToeOVChipkaart(OVChipkaart ovChipkaart) {
        if (ovChipkaart == null || ovChipkaarten.contains(ovChipkaart)) {
            return false;
        }

        ovChipkaarten.add(ovChipkaart);

        if (!ovChipkaart.getProducten().contains(this)) {
            ovChipkaart.getProducten().add(this);
        }

        return true;
    }

    public boolean verwijderOVChipkaart(OVChipkaart ovChipkaart) {
        if (ovChipkaart == null || !ovChipkaarten.contains(ovChipkaart)) {
            return false;
        }

        ovChipkaarten.remove(ovChipkaart);
        ovChipkaart.getProducten().remove(this);

        return true;
    }

    @Override
    public String toString() {
        return "Product {#" + productNummer + ","
                + " naam='" + naam + "',"
                + " beschrijving='" + beschrijving + "',"
                + " prijs=" + prijs
                + "}";
    }
}