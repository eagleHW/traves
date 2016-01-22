package com.reneavila.traves;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class CPerfil extends ActionBarActivity {

    SharedPreferences settings;
    SharedPreferences.Editor editor;
    BaseDatos bd;

    MenuItem perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = settings.edit();
        bd = new BaseDatos(getBaseContext());
        setContentView(R.layout.activity_cperfil);

        Cursor c = bd.obtener_perfil_conductor(settings.getString("login","ERROR"));

        c.moveToFirst();

        ((TextView)findViewById(R.id.cp_login)).setText("Login : " + settings.getString("login","ERROR"));
        ((TextView)findViewById(R.id.cp_nombre)).setText("Nombre : " + c.getString(c.getColumnIndex("nombre")));
        ((TextView)findViewById(R.id.cp_edad)).setText("Edad : " + c.getString(c.getColumnIndex("edad")));
        ((TextView)findViewById(R.id.cp_genero)).setText("Género : " + c.getString(c.getColumnIndex("genero")));

        ((TextView)findViewById(R.id.cp_marca)).setText("Marca : " + c.getString(c.getColumnIndex("marca")));
        ((TextView)findViewById(R.id.cp_modelo)).setText("Modelo : " + c.getString(c.getColumnIndex("modelo")));
        ((TextView)findViewById(R.id.cp_color)).setText("Color : " + c.getString(c.getColumnIndex("color")));
        ((TextView)findViewById(R.id.cp_placas)).setText("Placas : " + c.getString(c.getColumnIndex("placas")));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cperfil, menu);
        perfil = menu.add(settings.getString("login","ERROR"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if( item == perfil){
            return true;
        }
        if (id == R.id.action_cerrar_sesion) {
            Intent intent = new Intent(this, MenuPricipal.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
