package janettha.activity1.EmocionesDto;

/**
 * Created by janeth on 22/11/2017.
 */

public class ImagenesDto {
    private String nombre;
    private String id;
    private String sexo;
    private String url;

    public ImagenesDto(){}

    public ImagenesDto(String id, String nombre, String sexo, String url) {
        this.nombre = nombre;
        this.id = id;
        this.sexo = sexo;
        this.url = url;
    }

    public String getNombre() {       return nombre;    }

    public void setNombre(String nombre) {        this.nombre = nombre;    }

    public String getId() {        return id;    }

    public void setId(String id) {        this.id = id;    }

    public String getSexo() {        return sexo;    }

    public void setSexo(String sexo) {        this.sexo = sexo;    }

    public String getUrl() {        return url;    }

    public void setUrl(String url) {        this.url = url;    }
}
