package janettha.activity1.EmocionesDto;

import java.util.ArrayList;
import java.util.List;

public class ActividadMasterDto {
    protected int id;
    protected String sexo;
    protected List<EmocionDto> listEmociones = new ArrayList<EmocionDto>();

    public ActividadMasterDto(){}

    public ActividadMasterDto(int id, String sexo, List<EmocionDto> lEmociones){
        this.id = id;
        this.sexo = sexo;
        this.listEmociones.add(0,lEmociones.get(0));    //emocion Main
        this.listEmociones.add(1,lEmociones.get(1));    //emocion 2
        this.listEmociones.add(2,lEmociones.get(2));    //emocion 1
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public List<EmocionDto> getListEmociones() {
        return listEmociones;
    }

    public void setListEmociones(List<EmocionDto> listEmociones) {
        this.listEmociones = listEmociones;
    }

    public EmocionDto emocionMain(){ return this.listEmociones.get(0); }
    public EmocionDto emocionB(){
                                                                return this.listEmociones.get(1);
    }
    public EmocionDto emocionC(){ return this.listEmociones.get(2); }

    public void setEmocionMain(EmocionDto e){
        listEmociones.add(0, e);
    }

    public void setEmocionB(EmocionDto e){
        this.listEmociones.add(1, e);
    }

    public void setEmocionC(EmocionDto e){
        this.listEmociones.add(2, e);
    }

}