package janettha.activity1.Util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;

import janettha.activity1.EmocionesDao.ActividadesDao;
import janettha.activity1.EmocionesDao.EmocionesDao;
import janettha.activity1.R;
//import org.apache.http.HttpResponse;
/*
import com.anca.formato.factory.PropiedadFormato;
import com.anca.registro.factory.PropiedadRegistro;
import com.anca.registro.factory.Registro;
*/
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

//import java.io.BufferedReader;
//import java.io.InputStreamReader;

public class Factory {
    private static final String TAG = "Factory";

    public static final String BD = "emociones";
    public static final String DIRECTORIO = "emociones";
    public static final String NOMEDIA = "/.nomedia";
    public static final int VERSION = 1;
    public static final String FORMATO_FECHA = "%d-%m %H:%M";
    public static final String FORMATO_FECHA_ID = "%Y%m%d%H%M%S";
    public static final String FORMATO_FECHA_HORA = "%Y-%m-%d %H:%M:%S";
    public static final String FORMATO_NUMERO = "#,###,###.00";
    public static final String CERO = "0";
    /*
        private static final DecimalFormat FORMATO_MONEDA = new DecimalFormat(PropiedadFormato.MONEDA.getValor());
        private static final DecimalFormat FORMATO_DECIMAL = new DecimalFormat(PropiedadFormato.DECIMAL.getValor());
        private static final DecimalFormat FORMATO_DECIMAL_N = new DecimalFormat(PropiedadFormato.DECIMAL_LARGO.getValor());
        private static final DecimalFormat FORMATO_DECIMAL_LARGO = new DecimalFormat(PropiedadFormato.DECIMAL_LARGO.getValor());
        private static final DecimalFormat FORMATO_ENTERO = new DecimalFormat(PropiedadFormato.ENTERO.getValor());
    */
    public static final NumberFormat NUMERO = new DecimalFormat(FORMATO_NUMERO);
    public static final int BUFFER = 1024;
    public static final char INSERTA = 'I';
    public static final char ACTUALIZA = 'U';
    public static final char ELIMINA = 'D';
    public static final String SEPARADOR_ENCABEZADO = "|";
    public static final String SEPARADOR_DETALLE = "&";
    public static final String SEPARADOR_ENCABEZADO_REGEX = "\\|";
    public static final String SEPARADOR_DETALLE_REGEX = "&";
    public static final String RETORNO_CARRO = "\n";
    public static final String SEPARADOR_DATOS = " / ";
    public static final String SIMBOLO_PESOS = "$ ";
    public static final String INICIO_HTML = "<";
    public static final String INICIO_NO_HAY_MENSAJES = "No hay mensajes";
    /*
        public static final String URL_SERVLET_FECHA = "http://tecnoanca.com/tecno-empresa/fecha-hora.php";
        public static final String URL_SERVLET_LICENCIA = "http://tecnoanca.com/tecno-empresa/licencia.php";
        public static final String URL_SERVLET_INICIO = "http://tecnoanca.com/dress/login.php";
        public static final String URL_SERVLET_SUBIR = "http://tecnoanca.com/dress/recibeTablas.php";
        public static final String URL_SERVLET_BAJAR = "http://tecnoanca.com/dress/getCatalogos.php";
    */
    public static final String ZIP = "tmp.zip";
    public static final String TXT = ".txt";

    public static SQLiteDatabase getBaseDatos(Context context) {
        SQLite sql = new SQLite(context, BD, null, VERSION);
        SQLiteDatabase bd = null;
        try {
            bd = sql.getWritableDatabase();
        } catch (SQLiteException ex) {
            Log.e("Factory.getBaseDatos", ex.getMessage());
        } catch (Exception ex) {
            Log.e("Factory.getBaseDatos", ex.getMessage());
        }
        return bd;
    }

    public static void close(SQLiteDatabase bd) {
        if (bd != null) {
            bd.close();
        }
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean isConectadoWiFi(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (networkInfo != null &&
                networkInfo.getState() == NetworkInfo.State.CONNECTED);
    }

    public static boolean isConectadoDatos(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (networkInfo != null &&
                networkInfo.getState() == NetworkInfo.State.CONNECTED);
    }

    public static boolean isConectado(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfoWifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo networkInfoDatos = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (networkInfoWifi != null && networkInfoWifi.getState() == NetworkInfo.State.CONNECTED) ||
                (networkInfoDatos != null && networkInfoDatos.getState() == NetworkInfo.State.CONNECTED);
    }

    public static String formatoFechaId(){
        Time time = new Time();
        time.setToNow();
        return time.format(FORMATO_FECHA_ID);
    }

    public static String formatoFechaHora(){
        Time time = new Time();
        time.setToNow();
        return time.format(FORMATO_FECHA_HORA);
    }

    public static String formatoFecha(long milisegundos){
        Time time = new Time();
        time.set(milisegundos);
        return time.format( FORMATO_FECHA );
    }

    public static String formatoFechaServicio(long milisegundos){
        Time time = new Time();
        time.set(milisegundos);
        return time.format( FORMATO_FECHA_HORA );
    }

    public static String formatoNumero(double numero) {
        return NUMERO.format(numero);
    }

    /*
	public static String moneda(double numero) {
		return FORMATO_MONEDA.format(numero);
	}

	public static String decimal(double numero) {
		return FORMATO_DECIMAL.format(numero);
	}

	public static String entero(double numero) {
		return FORMATO_ENTERO.format(numero);
	}

	public static double textoDecimal(String texto){
		double decimal = 0;
		try{
			decimal = Double.parseDouble(texto.replaceAll(",", "").replaceAll(" ", "").replaceAll("\\$", ""));
		}catch(NumberFormatException ex){
			Registro.escribe(Registro.getClase(), Registro.getMetodo(), ex.getMessage(), PropiedadRegistro.ERROR);
		}catch (NullPointerException ex){
			Registro.escribe(Registro.getClase(), Registro.getMetodo(), ex.getMessage(), PropiedadRegistro.ERROR);
		}
		return decimal;
	}
    */

    @SuppressWarnings("resource")
    public static String getDirectorio(Context context){
        File raiz = null;
        if( isSdcard() ){
            raiz = Environment.getExternalStorageDirectory();
        }else{
            raiz = context.getFilesDir();
        }
        File directorio = new File(raiz,DIRECTORIO);
        if( !directorio.exists() ){
            directorio.mkdir();
            try {
                new FileWriter(directorio.getAbsoluteFile()+NOMEDIA);
            } catch (IOException ex) {
                Log.e("getDirectorio", "Error al crear el archivo .nomedia");
            }
        }
        return directorio.getAbsolutePath()+ File.separator;
    }

    public static String getDirectorio(int idCatalogo){
        File directorio = Environment.getExternalStorageDirectory();
        return directorio.getPath()+ File.separator+DIRECTORIO+ File.separator+idCatalogo+ File.separator;
    }

    public static boolean isSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean descomprime(String archivo, String destino){
        boolean ok = false;
        try {
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream( new FileInputStream(archivo) ));
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {

                if( ze.isDirectory() ){
                    File f = new File(destino+ze.getName());
                    f.mkdirs();
                }else{
                    OutputStream out= new FileOutputStream(destino+ze.getName());
                    byte[] buffer = new byte[BUFFER];
                    while (true) {
                        int n= zis.read(buffer);
                        if (n < 0){
                            break;
                        }
                        out.write(buffer, 0, n);
                    }
                    out.close();
                }
            }
            zis.close();
            ok = true;
        }catch(FileNotFoundException ex){
            Log.e("Factory.descomprime",ex.getMessage());
        }catch(IOException ex){
            Log.e("Factory.descomprime",ex.getMessage());
        }
        return ok;
    }

    public static boolean ejecutaScript(String archivo){
        return true;
    }

    public static boolean eliminaArchivo(String archivo){
        boolean ok = true;
        File file = new File(archivo);
        if( file.exists() ){
            if( file.isDirectory() ){
                if( !archivo.endsWith(File.separator) ){
                    archivo += File.separator;
                }
                String[]archivos = file.list();
                for( String a : archivos ){
                    ok = ok && eliminaArchivo( archivo+a );
                }
            }
            ok = ok && file.delete();
        }
        return ok;
    }

    public static boolean descarga(String servidor, String usuario, String password, String archivo, String destino){
        boolean ok = false;
        try {
            URL url = new URL("ftp://"+usuario+":"+password+"@"+servidor+archivo);

            URLConnection urlConnection = url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            OutputStream out= new FileOutputStream(destino);
            byte[] buffer= new byte[BUFFER];
            while (true) {
                int n= in.read(buffer);
                if (n < 0){
                    break;
                }
                out.write(buffer, 0, n);
            }
            in.close();
            out.close();
            ok = true;
        }catch(FileNotFoundException ex){
            Log.e("Factory.descarga",ex.getMessage());
        }catch(IOException ex){
            Log.e("Factory.descarga",ex.getMessage());
        }catch(Exception ex){
            Log.e("Factory.descarga",ex.getMessage());
        }
        return ok;
    }

    public static boolean descarga(String urlFtp, String destino){
        boolean ok = false;
        try {
            URL url = new URL(urlFtp);

            URLConnection urlConnection = url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            OutputStream out= new FileOutputStream(destino);
            byte[] buffer= new byte[BUFFER];
            while (true) {
                int n= in.read(buffer);
                if (n < 0){
                    break;
                }
                out.write(buffer, 0, n);
            }
            in.close();
            out.close();
            ok = true;
        }catch(FileNotFoundException ex){
            Log.e("Factory.descarga","... " + ex.getMessage());
        }catch(IOException ex){
            Log.e("Factory.descarga",".. " + ex.getMessage());
        }catch(Exception ex){
            Log.e("Factory.descarga",". " + ex.getMessage());
        }
        return ok;
    }

    public static boolean procesaArchivo(String archivo, SQLiteDatabase bd){
        boolean ok = false;
		/*try{
			ActualizaDAO dao = new ActualizaDAO();
			FileInputStream fis = new FileInputStream(archivo);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String dato = "";
			while( (dato =br.readLine())!= null){
				String []datos = dato.split( SEPARADOR_ENCABEZADO_REGEX );
				if( datos.length > 0 && datos[0].length()>0 ){
					switch(datos[0].charAt(0)){
						case INSERTA:
							dao.insert(datos, bd);
							break;
						case ELIMINA:
							dao.delete(datos, bd);
							break;
						case ACTUALIZA:
							dao.update(datos, bd);
							break;
					}
				}
			}
			br.close();
			isr.close();
			fis.close();
			ok = true;
		}catch(FileNotFoundException ex){
			ok = false;
			Log.e("Factory.procesaArchivo",ex.getMessage());
		}catch(IOException ex){
			ok = false;
			Log.e("Factory.procesaArchivo",ex.getMessage());
		}*/
        return ok;
    }

    /**
     * Ya que en la Base de Datos guarda la fecha en un formato long de
     * 13 d�gitos(1390694500000), esta funci�n regresa el long de 5 d�as atras.
     * @return fecha en long
     */
    public static long fechaPedidosAntiguos() {
        int dias = 5;
        long diferenciaEnDias = dias * 24 * 60 * 60 * 1000;
        long tiempoActual = Calendar.getInstance().getTime().getTime();

        return new Date(tiempoActual - diferenciaEnDias).getTime();
    }

    /**
     * Devuelve fecha actual, esta fecha es la que tiene el dispositivo movil establecido
     * @return fecha actual en long
     */
    public static long fechaActual(){
        Time t = new Time();
        t.setToNow();

        return t.toMillis(true);
    }

    public static String getCadenaImpresion(String texto, int longitud){
        String resultado = "";
        int l = texto.length();
        if( l >= longitud ){
            resultado = texto.substring(0, longitud);
        }else{
            resultado = texto;
            for( int i=0;i<l; i++ ){
                resultado+=" ";
            }
        }
        return resultado;
    }

    @SuppressLint("DefaultLocale")
    public static String MD5(String md5) {
        String r = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            r = sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException ex) {
            Log.e("Factory.md5",ex.getMessage());
        } catch (NullPointerException ex) {
            Log.e("Factory.md5",ex.getMessage());
        }
        return r;
    }

    public static String fechaFormat(Context context, String fecha){
        Log.d(TAG, "fechaFormat: "+fecha);
        String a = fecha.substring(0,4);
        String m = fecha.substring(5, 7);
        String d = fecha.substring(8, fecha.length());
        Log.d(TAG, "fechaFormat: a: "+a+" m: "+m+" d: "+d);
        String diaFormat = (Integer.parseInt(d) < 10) ? CERO+d : d;
        return mesNombre(context, Integer.parseInt(m))+" "+diaFormat+", "+a;
    }

    public static String mesNombre(Context context, int mes){
        if(mes == 1) return context.getResources().getString(R.string.enero);
        else if(mes == 2) return context.getResources().getString(R.string.febrero);
        else if(mes == 3) return context.getResources().getString(R.string.marzo);
        else if(mes == 4) return context.getResources().getString(R.string.abril);
        else if(mes == 5) return context.getResources().getString(R.string.mayo);
        else if(mes == 6) return context.getResources().getString(R.string.junio);
        else if(mes == 7) return context.getResources().getString(R.string.julio);
        else if(mes == 8) return context.getResources().getString(R.string.agosto);
        else if(mes == 9) return context.getResources().getString(R.string.septiembre);
        else if(mes == 10) return context.getResources().getString(R.string.octubre);
        else if(mes == 11) return context.getResources().getString(R.string.noviembre);
        else if(mes == 12) return context.getResources().getString(R.string.diciembre);
        else return String.valueOf(mes);
    }

    public static String horaFormat(String hora){
        String h = hora.substring(0,5);
        String d;
        if(Integer.parseInt(h.substring(0,2)) < 12){
            d = "AM";
        }else{
            d = "PM";
        }
        return h+" "+d;
    }

}
