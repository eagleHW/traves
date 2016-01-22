package com.reneavila.traves;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;
import java.util.zip.Inflater;


public class RegistroConductor extends ActionBarActivity {

    BaseDatos bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bd = new BaseDatos(getBaseContext());

        setContentView(R.layout.activity_registro_conductor);

        Spinner spinner = (Spinner) findViewById(R.id.rc_genero);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.sexos, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.nselc_spinner_layout,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        this,"Genero",R.id.text_genero));

        validar_login();
        validar_password();
        validar_nombre();
        validar_edad();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registro_conductor, menu);
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

    public void openSpinner(View view){

        ((Spinner) findViewById(R.id.rc_genero)).performClick();

    }

    public void registrarAuto(View view){

        Spinner spin_genero = (Spinner) findViewById(R.id.rc_genero);

        EditText login_et = (EditText) findViewById(R.id.rc_login);
        EditText password_et = (EditText) findViewById(R.id.rc_password);
        EditText nombre_et = (EditText) findViewById(R.id.rc_nombre);
        EditText edad_et = (EditText) findViewById(R.id.rc_edad);

        String login = login_et.getText().toString();
        String password = password_et.getText().toString();
        String nombre = nombre_et.getText().toString();
        String edad = edad_et.getText().toString();
        String genero;

        // Verifica que todos hayan sido seleccionados

        //Aqui verificamos el spinner
        if(spin_genero.getSelectedItem() == null) {
            TextView text_genero = (TextView) findViewById(R.id.text_genero);
            text_genero.setFocusableInTouchMode(true);
            text_genero.setFocusable(true);
            text_genero.setError("");
            text_genero.requestFocus();
            ((TextView) findViewById(R.id.rc_arrow_genero)).setError("Debes elegir un genero", null);
            ((TextView) findViewById(R.id.rc_arrow_genero)).requestFocus();
        }

        spin_genero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0){
                    ((TextView) findViewById(R.id.rc_arrow_genero)).setError(null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Aqui verificamos los demas campos
        if(edad.equals("")){
            edad_et.setText("");
        }

        if(nombre.equals("")){
            nombre_et.setText("");
        }

        if(password.equals("")){
            password_et.setText("");
        }

        if(login.equals("")){
            login_et.setText("");
        }

            if (login_et.getError() == null && password_et.getError() == null && nombre_et.getError() == null
                    && edad_et.getError() == null && ((TextView) findViewById(R.id.rc_arrow_genero)).getError() == null) {

                genero = spin_genero.getSelectedItem().toString();

                if (bd.login_existente(login)) {

                    Toast.makeText(getApplicationContext(), "Login no disponible,selecciona otro", Toast.LENGTH_SHORT).show();

                } else {

                    Intent intent = new Intent(this, RegistroAuto.class);
                    intent.putExtra("login", login);
                    intent.putExtra("password", password);
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("edad", edad);
                    intent.putExtra("genero", genero);
                    startActivity(intent);

                }
            } else {
                if (login_et.getError() != null) {
                    login_et.requestFocus();
                } else {
                    if (password_et.getError() != null) {
                        password_et.requestFocus();
                    } else {
                        if (nombre_et.getError() != null) {
                            nombre_et.requestFocus();
                        } else {
                            edad_et.requestFocus();
                        }
                    }
                }
            }


    }

    private void validar_login(){

        EditText login_et = (EditText) findViewById(R.id.rc_login);

        login_et.addTextChangedListener(new TextValidator(login_et) {
            @Override
            public void validate(TextView textView, String text) {

                if(text.equals("")){
                    textView.setError("Este campo no puede quedar vacío");
                    textView.requestFocus();
                }else{
                    if(!Pattern.matches("^[\\w_]*$",text)){
                        textView.setError("Solo se aceptan letras, números y guiones bajos");
                        textView.requestFocus();
                    }else{
                        if(text.length() < 5 || text.length() > 10){
                            textView.setError("El login debe tener entre 5 y 10 caracteres");
                            textView.requestFocus();
                        }else{
                            textView.setError(null);
                        }
                    }
                }

            }
        });

    }

    private void validar_password(){

        EditText password_et = (EditText) findViewById(R.id.rc_password);

        password_et.addTextChangedListener(new TextValidator(password_et) {
            @Override
            public void validate(TextView textView, String text) {

                if(text.equals("")){
                    textView.setError("Este campo no puede quedar vacío");
                    textView.requestFocus();
                }else{
                    if(!Pattern.matches("[^;']+", text)){
                        textView.setError("La contraseña no puede incluir los caracteres ; o ' ");
                        textView.requestFocus();
                    }else{
                        if(text.length() <  5){
                            textView.setError("La contraseña debe tener al menos 5 caracteres");
                            textView.requestFocus();
                        }else{
                            textView.setError(null);
                        }
                    }
                }

            }
        });

    }

    private void validar_nombre(){

        EditText nombre_et = (EditText) findViewById(R.id.rc_nombre);

        nombre_et.addTextChangedListener(new TextValidator(nombre_et) {
            @Override
            public void validate(TextView textView, String text) {

                if(text.equals("")){
                    textView.setError("Este campo no puede quedar vacío");
                    textView.requestFocus();
                }else{
                    if(!Pattern.matches("[a-zA-Z ]+",text)){
                        textView.setError("El nombre solo puede incluir letras y espacios");
                        textView.requestFocus();
                    }else{
                        if(!Pattern.matches("([^ ]+ )*[^ ]+",text)){
                            textView.setError("Solo un espacio entre nombres y apellidos, sin espacios al final");
                            textView.requestFocus();
                        }else{
                            textView.setError(null);
                        }
                    }
                }

            }
        });

    }

    private void validar_edad(){

        EditText edad_et = (EditText) findViewById(R.id.rc_edad);

        edad_et.addTextChangedListener(new TextValidator(edad_et) {
            @Override
            public void validate(TextView textView, String text) {
                if(text.equals("")){
                    textView.setError("Este campo no puede quedar vacío");
                    textView.requestFocus();
                }else{
                    if(!Pattern.matches("[0-9]+",text)){
                        textView.setError("Solo se permiten números");
                        textView.requestFocus();
                    }else{
                        if(Integer.parseInt(text) < 18){
                            textView.setError("El servicio solo esta diponible para mayores de 18 años");
                            textView.requestFocus();
                        }else{
                            textView.setError(null);
                        }
                    }

                }

            }
        });

    }

}

