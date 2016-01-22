package com.reneavila.traves;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.preference.PreferenceManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLOutput;
import java.util.regex.Pattern;


public class IniciarSesion extends ActionBarActivity {


    SharedPreferences settings;
    SharedPreferences.Editor editor;

    BaseDatos bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = settings.edit();


        bd = new BaseDatos(getBaseContext());


        //bd.añadir_automovil("m","m","c","aaa");
        //bd.anadir_conductor("c","c","n","2","aaa","aaa");
        //bd.añadir_pasajero("p","p","n","1","masculino");

        //bd.crear_traslado("2015-10-10","12:33","origen","destino","c");
        //bd.crear_traslado("2015-10-10","12:34","origen","destino","c");
        //bd.solicitar_viaje("2015-10-10","12:34","origen","destino","conductor","p");


        //getBaseContext().deleteDatabase("TravesDB");
        setContentView(R.layout.activity_iniciar_sesion);

        validar_login();
        validar_password();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_iniciar_sesion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void iniciar_sesion(View view) {

        Intent intent;

        EditText login_et = (EditText) findViewById(R.id.is_login);
        EditText password_et = (EditText) findViewById(R.id.is_password);


        String login = login_et.getText().toString();
        String password = password_et.getText().toString();

        String identidad = bd.logearse(login, password);


        if(password.equals("")){
            password_et.setText("");
        }
        if(login.equals("")){
            login_et.setText("");
        }

        if (login_et.getError() == null && password_et.getError() == null){

            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);


            if (identidad.equals("sin_registro")) {

                Toast.makeText(getApplicationContext(), "Login o contraseña incorrecta", Toast.LENGTH_SHORT).show();

            } else {

                editor.putString("login", login);
                editor.commit();

                switch (identidad) {
                    case "conductor":
                        intent = new Intent(this, HomeConductor.class);
                        break;
                    case "pasajero":
                        intent = new Intent(this, HomePasajero.class);
                        break;
                    default:
                        intent = new Intent(this, IniciarSesion.class);
                }

                startActivity(intent);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        }else{

            if(login_et.getError() != null ){
                login_et.requestFocus();
            }else{
                password_et.requestFocus();
            }

        }
    }


    private void validar_login(){

        EditText login_et = (EditText) findViewById(R.id.is_login);

        login_et.addTextChangedListener(new TextValidator(login_et) {
            @Override
            public void validate(TextView textView, String text) {

                if(text.equals("")){
                    textView.setError("Este campo no puede quedar vacío");
                    textView.requestFocus();
                }else {

                    if (! Pattern.matches("^[\\w_]*$", text)) {
                        textView.setError("Solo se aceptan letras, números y guiones bajos");
                        textView.requestFocus();
                    }else{
                        textView.setError(null);
                    }

                }
            }
        });

    }

    private void validar_password(){

        EditText password_et = (EditText) findViewById(R.id.is_password);

        password_et.addTextChangedListener(new TextValidator(password_et) {
            @Override
            public void validate(TextView textView, String text) {

                if(text.equals("")){
                    textView.setError("Este campo no puede quedar vacío");
                    textView.requestFocus();
                }else {

                    if (! Pattern.matches("^[\\w_]*$", text)) {
                        textView.setError("Solo se aceptan letras, números y guiones bajos");
                        textView.requestFocus();
                    }else{
                        textView.setError(null);
                    }

                }
            }
        });

    }

}
