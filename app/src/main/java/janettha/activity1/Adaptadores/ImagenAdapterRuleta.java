package janettha.activity1.Adaptadores;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import github.hellocsl.cursorwheel.CursorWheelLayout;
import janettha.activity1.EmocionesDelegate.EmocionesDelegate;
import janettha.activity1.EmocionesDto.EmocionDto;
import janettha.activity1.EmocionesDao.EmocionesDao;
import janettha.activity1.R;
import janettha.activity1.Util.Factory;

/**
 * Created by janeth on 2018-02-19.
 */

public class ImagenAdapterRuleta extends CursorWheelLayout.CycleWheelAdapter {

    private Context context;
    private String sexo;
    private EmocionesDelegate emocionesDelegate;
    private LayoutInflater inflater;
    private int gravity;

    public ImagenAdapterRuleta(Context context, View view, String s) {
        emocionesDelegate = new EmocionesDelegate();
        this.context = context;
        sexo = s;
        //this.listEmocionesDao.Emociones(context, s);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public View getView(View parent, int position) {
        View root = inflater.inflate(R.layout.wheel_image_layout, null, false);
        ImageView imageView = (ImageView) root.findViewById(R.id.wheel_menu_image);
        Uri ruta;
        //String pd = Uri.parse(data.getUrl()).getPath();
        //imageView.setImageURI(Uri.parse(data.getUrl()));
        //imageView.setImageResource(pd);
        SQLiteDatabase db = Factory.getBaseDatos(context);
        ruta = Uri.parse(emocionesDelegate.obtieneEmocion(position, sexo, db).getUrl());
        db.close();
        Picasso.with(parent.getContext())
                .load(ruta)
                .resize(90, 90)
                .centerCrop()
                .into(imageView);
        return root;
    }

    @Override
    public EmocionDto getItem(int position) {
        SQLiteDatabase db = Factory.getBaseDatos(context);
        EmocionDto e = emocionesDelegate.obtieneEmocion(position, sexo, db);
        db.close();
        return e;
    }
}
