package janettha.activity1.Util;

enum Consulta {
    CREA_TABLA_TUTOR("CREATE TABLE [tutor] (" +
            "tutor TEXT NOT NULL, " +
            "nombre TEXT NOT NULL, " +
            "apellidos TEXT, " +
            "correo TEXT NOT NULL, " +
            "password TEXT NOT NULL, " +
            "registro TEXT, "+
            "PRIMARY KEY (tutor))"
    ),
    CREA_TABLA_USUARIO("CREATE TABLE [usuario] (" +
            "usuario TEXT NOT NULL, " +
            "password TEXT NOT NULL, " +
            "nombre TEXT NOT NULL, " +
            "apellidos TEXT NOT NULL, " +
            "sexo TEXT, "+
            "cumple DATE, "+
            "edad INT, " +
            "tutor TEXT, " +
            "PRIMARY KEY (usuario))"
    ),
    CREA_TABLA_EMOCION("CREATE TABLE [emocion] (" +
            "emocion INTEGER NOT NULL, " +
            "sexo TEXT NOT NULL, " +
            "nombre TEXT, " +
            "imagen TEXT, " +
            "color TEXT, " +
            "background TEXT, " +
            "PRIMARY KEY (emocion, sexo))"
    ),
    CREA_TABLA_ACTIVIDAD1("CREATE TABLE [actividad1] (" +
            "id_act_1 TEXT NOT NULL, " +
            "sexo TEXT NOT NULL, " +
            "emocion1 INTEGER NOT NULL, " +
            "emocion2 INTEGER NOT NULL, " +
            "emocion3 INTEGER NOT NULL, " +
            "PRIMARY KEY (id_act_1, sexo))"
    ),
    CREA_TABLA_ACTIVIDAD2("CREATE TABLE [actividad2] (" +
            "id_act_2 TEXT NOT NULL, " +
            "sexo TEXT NOT NULL, " +
            "redaccion TEXT NOT NULL, " +
            "emocion1 INTEGER NOT NULL, " +
            "expl_a TEXT NOT NULL, " +
            "emocion2 INTEGER NOT NULL, " +
            "expl_b TEXT NOT NULL, " +
            "emocion3 INTEGER NOT NULL, " +
            "expl_c TEXT NOT NULL, " +
            "PRIMARY KEY (id_act_2, sexo))"
    ),
    // Lleva el conteo de las actividades que se han realizado
    CREA_TABLA_ACTIVIDADES("CREATE TABLE [actividades] (" +
            "id_actividad INTEGER NOT NULL, " +
            "usuario TEXT NOT NULL, " +
            "act_1 INTEGER NOT NULL, " +
            "act_2 INTEGER NOT NULL, " +
            "act_3 INTEGER NOT NULL, " +
            "PRIMARY KEY (id_actividad))"
    );
    /*
    INSERTA_DISPOSITIVO("INSERT INTO [dispositivo] ([id_dispositivo], [token]) " +
            "SELECT '1','1'"
    ),
    INSERTA_ESTATUS_AVISO("INSERT INTO [estatus_aviso] ([id_estatus_aviso], [nombre], [color]) " +
            "SELECT '0', '0', '0'" +
            "UNION SELECT '1', 'Nueva', '#243A72'" +
            "UNION SELECT '2', 'Esperando aclaración', '#F7DF65'" +
            "UNION SELECT '3', 'Confirmada', '#C6C6C6'" +
            "UNION SELECT '4', 'Aclarada', '#C6C6C6'" +
            "UNION SELECT '5', 'Eliminada', '#0D000000'"
    );
    */
    private String consulta;

    Consulta(String consulta) {
        this.consulta = consulta;
    }

    public String getConsulta() {
        return consulta;
    }

}

