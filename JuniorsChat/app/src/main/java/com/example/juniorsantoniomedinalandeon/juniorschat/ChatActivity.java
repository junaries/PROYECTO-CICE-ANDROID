package com.example.juniorsantoniomedinalandeon.juniorschat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnIniciar;
    private EditText et_usuario;
    private EditText et_pinNuestro;
    private EditText et_pinDestino;
    private EditText et_ipDestino;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        btnIniciar = (Button)findViewById(R.id.btn_Iniciar);
        btnIniciar.setOnClickListener(this);
        et_usuario = (EditText)findViewById(R.id.et_usuario);
        et_pinNuestro = (EditText)findViewById(R.id.et_pinNuestro);
        et_pinDestino = (EditText)findViewById(R.id.et_pinDestino);
        et_ipDestino = (EditText)findViewById(R.id.et_ipDestino);
    }

    @Override
    public void onClick(View view) {
        String usuario = et_usuario.getText().toString();
        int pinNuesto = Integer.parseInt(et_pinNuestro.getText().toString());
        int pinDestino = Integer.parseInt(et_pinDestino.getText().toString());
        String ipDestino = et_ipDestino.getText().toString();


        // System.out.println(usuario+" "+pinNuesto+" "+pinDestino +""+ipDestino);
        // Creo el intent, puesto lo que quiero es lanzar una nueva actividad
        Intent intent = new Intent(getApplicationContext(), Programacion.class);
        // Cuando una actividad ha de lanzar a otra actividad, en muchos casos necesita enviarle cierta información:
        intent.putExtra("user", usuario);
        intent.putExtra("nuestroPin", pinNuesto);
        intent.putExtra("destinatarioPin", pinDestino);
        intent.putExtra("destinoIP", ipDestino);
        startActivity(intent);

    }
    // Éste método se ejecutará cuando se pulse el botón Acerca de
    public void lanzarAcercaDe(View view){
        Intent i = new Intent(this, AcercaDe.class);
        startActivity(i);
    }
    // Este método me permite salir de la aplicación
    public void salir(View view){
        finish();
    }
    // Con este método, se nos permitirá llamar al destino, para preguntar alguno o todos los campos de éste
    public void llamar(View view){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        startActivity(intent);
    }
}
