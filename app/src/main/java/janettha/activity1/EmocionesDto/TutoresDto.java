package janettha.activity1.EmocionesDto;

/**
 * Created by janettha on 11/03/18.
 */


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class TutoresDto {
    private String name;
    private String surnames;
    private String tutor;
    private String email;
    private String pass;
    private String registro;

    public TutoresDto() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
/*
    public TutoresDto(String username, String name, String apellidos, String email, String contras, String registro) {
        this.tutor = username;
        this.name = name;
        this.surnames = apellidos;
        this.email = email;
        this.pass = contras;
        this.registro = registro;
    }
    */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurnames() {
        return surnames;
    }

    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }

    public String getTutor() {
        return tutor;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public String getString(){
        return getTutor()+" | "+getName()+" | "+getSurnames()+" | "+getEmail()+" | "+getPass()+" | "+getRegistro();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("surnames", surnames);
        result.put("tutor", tutor);
        result.put("email", email);
        result.put("pass", pass);
        result.put("registro", registro);
        return result;
    }
}
