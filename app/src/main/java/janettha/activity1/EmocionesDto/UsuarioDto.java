package janettha.activity1.EmocionesDto;

/**
 * Created by janettha on 13/03/18.
 */

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UsuarioDto {
    private String user;
    private String password;
    private String nombre;
    private String apellidos;
    private String sexo;
    private String tutor;
    private String cumple;
    private int edad;
    private String inicioS;
    private String finS;
    private int indiceA1;
    private int indiceA2;
    private int indiceA3;

    public UsuarioDto() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.user = "";
        this.password = "abc123";
        this.nombre = "";
        this.apellidos = "";
        this.sexo = "";
        this.tutor = "";
        this.cumple = "";
        this.edad = 0;
        this.inicioS = "";
        this.finS = "";
        this.indiceA1 =0;
        this.indiceA2 =0;
        this.indiceA3 =0;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return this.apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getSexo() {
        return this.sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCumple() {
        return cumple;
    }

    public void setCumple(String cumple) {
        this.cumple = cumple;
    }

    public int getEdad() {
        return this.edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getTutor(){ return this.tutor; }

    public void setTutor(String tutor){ this.tutor = tutor; }

    public String getInicioS() {        return inicioS;    }

    public void setInicioS(String inicioS) {        this.inicioS = inicioS;    }

    public String getFinS() {        return finS;    }

    public void setFinS(String finS) {        this.finS = finS;    }

    public int getIndiceA1() {        return indiceA1;    }

    public void setIndiceA1(int indiceA1) {        this.indiceA1 = indiceA1;    }

    public int getIndiceA2() {        return indiceA2;    }

    public void setIndiceA2(int indiceA2) {        this.indiceA2 = indiceA2;    }

    public int getIndiceA3() {        return indiceA3;    }

    public void setIndiceA3(int indiceA3) {        this.indiceA3 = indiceA3;    }

    public String getString(){
        return "user: "+getUser()+" pass: "+getPassword()+" tutor: "+getTutor()+
                " nombre: "+getNombre()+" "+getApellidos()+
                " sexo: "+getSexo()+" inicio: "+getInicioS()+" fin: "+getFinS()+
                " A1: "+getIndiceA1()+" A2: "+getIndiceA2()+" A3: "+getIndiceA3();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nombre", nombre);
        result.put("apellidos", apellidos);
        result.put("tutor", tutor);
        result.put("sexo", sexo);
        result.put("user", user);
        result.put("password", password);
        result.put("cumple", cumple);
        result.put("edad", edad);
        result.put("inicioS", inicioS);
        result.put("finS", finS);
        result.put("indiceA1", indiceA1);
        result.put("indiceA2", indiceA2);
        result.put("indiceA3", indiceA3);
        return result;
    }

}
