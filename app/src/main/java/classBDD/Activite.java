package classBDD;

import java.util.Date;

public class Activite {

    private Integer id;
    private String valeur;
    private Integer idEspace;
    private Integer idIndicateur;
    private Date date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public Integer getIdEspace() {
        return idEspace;
    }

    public void setIdEspace(Integer idEspace) {
        this.idEspace = idEspace;
    }

    public Integer getIdIndicateur() {
        return idIndicateur;
    }

    public void setIdIndicateur(Integer idIndicateur) {
        this.idIndicateur = idIndicateur;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Activite{" +
                "id=" + id +
                ", valeur='" + valeur + '\'' +
                ", idEspace=" + idEspace +
                ", idIndicateur=" + idIndicateur +
                ", date=" + date +
                '}';
    }
}
