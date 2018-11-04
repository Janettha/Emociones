package janettha.activity1.EmocionesDto;

import java.util.List;

/**
 * Created by janeth on 2018-02-11.
 */

public class ActividadImagenesDto extends ActividadMasterDto {

    public ActividadImagenesDto(){
        super();
    }

    public ActividadImagenesDto(int id, String sexo, List<EmocionDto> listEmociones){
        super(id, sexo, listEmociones);
    }

    public int getIDMain(){ return this.id; }
}
