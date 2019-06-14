package janettha.activity1.EmocionesDto;

public class IndicesActividadDto {
    int idActividad;
    String usuario;
    int indiceA, indiceB;

    public IndicesActividadDto(){}

    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getIndiceA() {
        return indiceA;
    }

    public void setIndiceA(int indiceA) {
        this.indiceA = indiceA;
    }

    public int getIndiceB() {
        return indiceB;
    }

    public void setIndiceB(int indiceB) {
        this.indiceB = indiceB;
    }
}
