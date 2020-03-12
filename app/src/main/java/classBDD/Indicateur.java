package classBDD;

public class Indicateur {

    private Integer id;
    private String nomIndicateur;
    private String type;
    private String valeurInit;
    private Integer idEspace;

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

    public Integer getIdEspace() {
        return idEspace;
    }

    public void setIdEspace(Integer idEspace) {
        this.idEspace = idEspace;
    }

    @Override
    public String toString() {
        return "Indicateur{" +
                "id=" + id +
                ", nomIndicateur='" + nomIndicateur + '\'' +
                ", type='" + type + '\'' +
                ", valeurInit='" + valeurInit + '\'' +
                ", idEspace=" + idEspace +
                '}';
    }
}
