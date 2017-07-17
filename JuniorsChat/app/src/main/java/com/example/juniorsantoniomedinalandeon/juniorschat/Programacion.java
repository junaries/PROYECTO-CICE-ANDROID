package com.example.juniorsantoniomedinalandeon.juniorschat;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.net.InetSocketAddress; // Clase que representa la implementación de un endpoint o  socket destino al cual me conectaré (interfaz de red)
import java.net.SocketException;


public class Programacion extends AppCompatActivity implements View.OnClickListener, OnSocketListener, Handler.Callback { // implemento las interfaces necesarias
    private Comunicacion comunicacion; //Agrego una instancia de la clase comunicación
    private InetSocketAddress direccion;

    private EditText mensaje;
    private Button enviar;

    private String usuario;
    private int pinNuestro;
    private int pinDestino;
    private String ipDestino;
    private ListView mensajeListView; // Creo una variable de tipo ListView y luego como veremos se va a mapear con su respectiva vista
    private ArrayAdapter<String> mensajeAdapter; // este adapter sirve de puente entre ListView y layout_mensaje.xml
    private Handler handler; // como sabemos los handler permiten comunicar subprocesos (que se ejecutan en el hilo secundario), con el hilo prinicipal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programacion);
        // En ésta actividad (lanzada) podemos recoger los datos de la siguiente manera:
        Bundle extras = getIntent().getExtras();
        usuario = extras.getString("user");
        pinNuestro = extras.getInt("nuestroPin");
        pinDestino = extras.getInt("destinatarioPin");
        ipDestino = extras.getString("destinoIP");
        direccion = new InetSocketAddress(ipDestino,pinDestino); // defino el ip y el pin del destino, éste pin hará de puerto destino ya que estamos usando la tecnologia UDP (por medio de datagramas)

        //System.out.println(usuario + " " + pinNuestro + " " + pinDestino + " " + ipDestino);
        mensaje = (EditText)findViewById(R.id.mensajeJR); //mapeo dicho elemento visual
        enviar = (Button)findViewById(R.id.btn_enviarMensaje);//mapeo dicho elemento visual
        enviar.setOnClickListener(this);
        mensajeAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.layout_mensaje, R.id.mensajeTextView); // el adapter recibe como parametros el layout y la vista que va a tener como aspecto cada elemento del ListView
        mensajeListView = (ListView)findViewById(R.id.mensajesListview);//mapeo dicho elemento visual
        mensajeListView.setAdapter(mensajeAdapter); // Establezco cual va a ser el adaptador en el ListView
        //creamos un handler, lo instanciamos .....
        handler = new Handler(this); // es "this" porque vamos a hacer las acciones que vamos a aplicar en esta clase java


    }
 /* Creo los métodos onStart() y onStop() para detectar el funcionamiento de nuestra clase comunicacion (representa un canal de comunicación)
       al inicio y al final de nuestra aplicación (cuando detengamos nuestra aplicación)
  */
    @Override
    protected void onStart() {
        super.onStart();
        // Cuando el canal de comunicación sea nulo, lo lanzaré de nuevo
        if (null == comunicacion) {
            try {
                //Creo una nueva instancia del canal de comunicacion
                comunicacion = new Comunicacion(this); // si es nulo, creo una nueva instancia de la clase comunicacion
                // Enlazo a nuestro canal de comunicación un número identificativo, que va a hacer el papel de puerto Origen
                comunicacion.bind(pinNuestro);
                comunicacion.start();
            } catch (SocketException e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Si el canal está creado lo detendremos
        if(null != comunicacion){
            comunicacion.stop();
        }
    }

    @Override
    public void onClick(View view) {
        String texto = mensaje.getText().toString(); //obtengo el texto del editText
        texto = usuario + " >> " + texto;
        // Enviamos el texto anterior a la direccion destino (direccion) por medio de la clase comunicacion, que va a ser nuestro canal de comunicación
        comunicacion.sendTo(direccion, texto);
        System.out.println(texto);
        mensajeAdapter.add(texto);// al adapter creado le añado el texto
        //Añado cada uno de los mensajes que yo escriba, en el ListView mediante el adapter
        mensajeListView.smoothScrollToPosition(mensajeAdapter.getCount()-1);
    }
     /* Implemento el método recibido, ya que he implementado la interfaz OnSocketListener */
    @Override
    public void recibido(String mensajeRecibido) {
        /* Los mensajes recibidos vamos a pasarlos aL Handler como parámetro, para que éste se los envie al hilo principal */
        Bundle bundle = new Bundle(); // El Bundle es como un saco donde yo puedo almacenar datos
        bundle.putString("texto", mensajeRecibido);

        Message message = new Message(); // Recurro a esta instancia de la clase Message ya que lo que quiero es recibir mensajes
        message.setData(bundle);
        handler.sendMessage(message);

    }

    @Override
    public boolean handleMessage(Message message) {
        Bundle bundle = message.getData();
        String mensajeRecibido = bundle.getString("texto");
        // Ésto es así (implementar el método handleMessage) ya que queremos recibir los mensajes en el hilo principal
        mensajeAdapter.add(mensajeRecibido);
        mensajeListView.smoothScrollToPosition(mensajeAdapter.getCount()-1);
        return false;
    }
}


