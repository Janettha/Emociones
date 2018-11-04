package janettha.activity1.EmocionesDto;

/**
 * Created by janeth on 08/11/2017.
 */

public class EmocionDto {
    private int emocion;
    private String name;
    private String color;
    private String colorB;
    private String id;
    private String sexo;
    private String url;

    public EmocionDto(){}

    public EmocionDto(String id, int emocion, String nombre, String sexo, String url, String color, String colorB){
        this.id = id;
        this.emocion = emocion;
        this.name = nombre;
        this.sexo = sexo;
        this.url = url;
        this.color = color;
        this.colorB = colorB;
    }

    public int getEmocion() {
        return emocion;
    }

    public void setEmocion(int emocion) {
        this.emocion = emocion;
    }
/*
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
*/
    public String getName() {        return name;   }

    public void setName(String name) {        this.name = name;    }

    public String getColor() {        return color;    }

    public void setColor(String color) {        this.color = color;    }

    public String getColorB() {        return colorB;    }

    public void setColorB(String colorB) {        this.colorB = colorB;    }

    public String getSexo() {        return sexo;    }

    public void setSexo(String sexo) {        this.sexo = sexo;    }

    public String getUrl() {        return url;    }

    public void setUrl(String url) {        this.url = url;    }

    public String getString(){
        return getEmocion()+" | "+getSexo()+" | "+getName()+" | "+getUrl()
                +" | "+getColor()+" | "+getColorB();
    }
}
