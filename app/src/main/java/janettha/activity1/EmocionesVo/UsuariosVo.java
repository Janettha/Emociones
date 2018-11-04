package janettha.activity1.EmocionesVo;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import java.util.List;

import janettha.activity1.EmocionesDelegate.UsuariosDelegate;
import janettha.activity1.EmocionesDto.UsuarioDto;
import janettha.activity1.R;

public class UsuariosVo extends AppCompatActivity {

    List<UsuarioDto> usuarios;
    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    RelativeLayout rootview;
    Toolbar toolbar;

    FloatingActionButton button;


    UsuariosDelegate delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios_vo);

        delegate = new UsuariosDelegate();
        //usuarios = delegate.getUsuarios(10);

        rootview = findViewById(R.id.usuarios_rootview);
        recyclerView = findViewById(R.id.usuarios_recyclerview);
        toolbar = findViewById(R.id.usuarios_toolbar);
        setSupportActionBar(toolbar);


        mLayoutManager = new LinearLayoutManager(this);
    }

}
