package janettha.activity1.Util;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import janettha.activity1.EmocionesDao.EmocionesDao;
import janettha.activity1.EmocionesDelegate.EmocionesDelegate;
import janettha.activity1.EmocionesDto.RespuestaDto;
import janettha.activity1.EmocionesVo.PdfVo;

public class TemplatePDF {
    private static final String TAG = "TemplatePDF";

    private Context context;
    private File filePDF;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private Font fTitle = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    private Font fSubTitle = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private Font fText = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD);
    private Font fHighText = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, BaseColor.YELLOW);

    private String headers[] = {"Ejercicio", "Tiempo en responder", "Respuesta", "Calificación"};
    EmocionesDelegate emocionesDelegate;

    String tutor, email;

    final String sexo = "m";

    ArrayList<RespuestaDto>respuestasActB = new ArrayList<>();

    public TemplatePDF(Context context) {
        this.context = context;
        //emocionesDao.Emociones(context, "m");
        emocionesDelegate = new EmocionesDelegate();
    }

    public void openPDF(){
        createFile();
        try{
            document = new Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(filePDF));
            document.open();
        }catch(Exception e){
            Log.e("openDocument", e.toString());
        }
    }

    private void createFile(){
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "PDF_emociones");
        if(!folder.exists()){
            folder.mkdirs();
        }
        filePDF=new File(folder, "TemplatePDF.pdf");
    }

    public void closeDocument(){
        document.close();
    }

    public void addMetaData(String user){
        document.addTitle(user);
        document.addCreationDate();
        document.addAuthor(user);
    }

    public void addHeader(String user, String iniS, String finS, String date){
        getDatosTutor();
        paragraph = new Paragraph();
        addChilCenter(new Paragraph("USUARIO: "+user, fTitle));
        addChilCenter(new Paragraph("Fecha: "+date, fText));
        paragraph.setSpacingAfter(20);
        addChilP(new Paragraph("Tutor: "+ tutor, fText));
        addChilP(new Paragraph("Correo de tutor: "+email, fText));
        //addChilP(new Paragraph("Inicio de sesion: "+iniS));
        //addChilP(new Paragraph("Fin de sesion: "+finS));
        paragraph.setSpacingAfter(30);
        try {
            document.add(paragraph);
        } catch (DocumentException e) {
            Log.e("addTitles", e.toString());
        }
    }

    private void addChilP(Paragraph childP){
        childP.setAlignment(Element.ALIGN_LEFT);
        paragraph.add(childP);
    }

    private void addChilCenter(Paragraph childP){
        childP.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childP);
    }

    public void addParrafo(int nActividad){
        paragraph = new Paragraph("Actividad No. "+String.valueOf(nActividad), fSubTitle);
        paragraph.setSpacingAfter(20);
        paragraph.setSpacingBefore(5);
        try {
            document.add(paragraph);
        } catch (DocumentException e) {
            Log.e("addParagraph", e.toString());
        }
    }

    public void createTable(ArrayList<RespuestaDto> RespuestaDto){
        int indexC = 0;
        paragraph = new Paragraph();
        paragraph.setFont(fText);
        PdfPTable pdfPTable = new PdfPTable(headers.length);
        pdfPTable.setWidthPercentage(100);
        //Se crean celdas
        PdfPCell pdfPCell;
        while (indexC < headers.length){
            pdfPCell = new PdfPCell(new Phrase(headers[indexC++], fSubTitle));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pdfPTable.addCell(pdfPCell);
        }

        int indexFin;
        for(int indexR = 0; indexR < RespuestaDto.size(); indexR++){
            indexFin = indexR>0? indexR-1: 0;
            String[]row = new String[headers.length];
            SQLiteDatabase db = Factory.getBaseDatos(context);
                row[0] = emocionesDelegate.obtieneEmocion(RespuestaDto.get(indexR).getId(), sexo, db).getName();
                //row[1] = RespuestaDto.get(indexR).getFinS() - RespuestaDto.get(indexR).getInicioS();
                Date inicio;
                if(indexR == 0){
                    inicio = Factory.formatoFechaHoraToDate(RespuestaDto.get(indexR).getInicioS());
                }else {
                    inicio = Factory.formatoFechaHoraToDate(RespuestaDto.get(indexFin).getFinS());
                }
                Date fin = Factory.formatoFechaHoraToDate(RespuestaDto.get(indexR).getFinS());
                Log.d(TAG, "createTable: indexFin: "+indexFin+"|"+indexR+" inicio: "+inicio+" fin: "+fin);
                row[1] = getDiferenciaTiempo(inicio, fin)+" segundos";
                row[2] = emocionesDelegate.obtieneEmocion(RespuestaDto.get(indexR).getRespuesta(), sexo, db).getName();
            db.close();
            if(RespuestaDto.get(indexR).getCalif()) {
                row[3] = "Correcto";
            }else{
                row[3] = "Incorrecto";
            }
            for (indexC = 0; indexC < headers.length ; indexC++) {
                pdfPCell = new PdfPCell(new Phrase(row[indexC]));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setFixedHeight(40);
                pdfPTable.addCell(pdfPCell);
            }
        }
        paragraph.add(pdfPTable);
        try {
            document.add(paragraph);
        } catch (DocumentException e) {
            Log.e("createTable", e.toString());
        }
    }

    private String getDiferenciaTiempo(Date inicio, Date fin) {

        long duration  = fin.getTime() - inicio.getTime();

        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

        return String.valueOf(diffInSeconds);
    }

    public void viewPDF(){
        Intent  intent = new Intent(context, PdfVo.class);
        intent.putExtra("path", filePDF.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void getDatosTutor(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail();
        tutor = mAuth.getCurrentUser().getDisplayName();
    }

    public void addRespuesta(RespuestaDto r){
        respuestasActB.add(r);
    }

    public ArrayList<RespuestaDto> getRespuestasActB(){
        return respuestasActB;
    }

}
