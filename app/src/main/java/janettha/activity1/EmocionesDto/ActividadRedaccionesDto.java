package janettha.activity1.EmocionesDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janettha on 28/02/18.
 */

public class ActividadRedaccionesDto extends ActividadMasterDto {

    private List<String> explicacion;

    public ActividadRedaccionesDto(){
        super();
        explicacion = new ArrayList<>();
        explicacion.add(0,"");
        explicacion.add(1,"");
        explicacion.add(2,"");
        explicacion.add(3,"");
    }

    public ActividadRedaccionesDto(int id, String sexo, List<EmocionDto> listEmociones, List<String> explicacion) {
        super(id, sexo, listEmociones);
        this.explicacion = explicacion;
    }

    public String getRedaccion() {
        return this.explicacion.get(0);
    }

    public void setRedaccion(String redaccion) {
        this.explicacion.set(0,redaccion);
    }

    public String getExpl1() {
        return this.explicacion.get(1);
    }

    public void setExpl1(String expl1) {
        this.explicacion.set(1, expl1);
    }

    public String getExpl2() {
        return this.explicacion.get(2);
    }

    public void setExpl2(String expl2) {
        this.explicacion.set(2, expl2);
    }

    public String getExpl3() {
        return this.explicacion.get(3);
    }

    public void setExpl3(String expl3) {
        this.explicacion.set(3, expl3);
    }
}
