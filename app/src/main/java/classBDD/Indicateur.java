package classBDD;

public class Indicateur {

    private Integer id;
    private String nomIndicateur;
    private String type;
    private String valeurInit;
    private Integer idUser;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomIndicateur() {
        return nomIndicateur;
    }

    public void setNomIndicateur(String nomIndicateur) {
        this.nomIndicateur = nomIndicateur;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValeurInit() {
        return valeurInit;
    }

    public void setValeurInit(String valeurInit) {
        this.valeurInit = valeurInit;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    @Override
    public String toString() {
        /*return "Indicateur{" +
                "id=" + id +
                ", nomIndicateur='" + nomIndicateur + '\'' +
                ", type='" + type + '\'' +
                ", valeurInit='" + valeurInit + '\'' +
                ", idUser=" + idUser +
                '}';*/
        return nomIndicateur;
    }
}
