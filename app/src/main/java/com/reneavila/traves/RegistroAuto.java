package com.reneavila.traves;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.Inflater;


public class RegistroAuto extends ActionBarActivity{


    BaseDatos bd;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = settings.edit();
        bd = new BaseDatos(getBaseContext());

        setContentView(R.layout.activity_registro_auto);

        final Spinner spinner_marca = (Spinner) findViewById(R.id.marca);
        final ArrayAdapter<String> adapter_marca = new ArrayAdapter<String>(this,R.layout.spinner_layout,cursorToList(bd.obtener_marcas()) );
        adapter_marca.setDropDownViewResource(R.layout.spinner_layout);
        spinner_marca.setAdapter(new NothingSelectedSpinnerAdapter(adapter_marca,R.layout.nselc_spinner_layout,this,"Marca",R.id.text_marca));


        Spinner spinner_color = (Spinner) findViewById(R.id.color);
        ArrayAdapter<CharSequence> adapter_color = ArrayAdapter.createFromResource(
                this, R.array.color, R.layout.spinner_layout);
        adapter_color.setDropDownViewResource(R.layout.spinner_layout);
        spinner_color.setAdapter(new NothingSelectedSpinnerAdapter(adapter_color,R.layout.nselc_spinner_layout,this,"Color",R.id.text_color));

        spinner_marca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Spinner spinner_modelo = (Spinner) findViewById(R.id.modelo);

                ArrayAdapter<String> adapter_modelo_str;
                ArrayAdapter<CharSequence> adapter_modelo_chseq;

                if(position != 0) {
                    String selected = ((TextView) view).getText().toString();
                    adapter_modelo_str = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, cursorToList(bd.obtener_modelos(selected)));
                    spinner_modelo.setAdapter(new NothingSelectedSpinnerAdapter(adapter_modelo_str, R.layout.nselc_spinner_layout, getApplicationContext(), "Modelo", R.id.text_modelo));
                }else{
                    adapter_modelo_chseq = ArrayAdapter.createFromResource(
                            getApplicationContext(), R.array.vacio, R.layout.spinner_layout);
                    adapter_modelo_chseq.setDropDownViewResource(R.layout.spinner_layout);
                    spinner_modelo.setAdapter(new NothingSelectedSpinnerAdapter(adapter_modelo_chseq, R.layout.nselc_spinner_layout, getApplicationContext(), "Modelo", R.id.text_modelo));
                }

                validar_placas();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        validar_placas();

    }

    private List<String> cursorToList(Cursor c){

        List<String> elems = new ArrayList<>(c.getCount()+1);

        while(c.moveToNext()){
            elems.add(c.getString(0));
        }

        elems.add("Otro");

        return elems;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registro_auto, menu);
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

        switch (view.getId()){

            case R.id.trian_marca:
                ((Spinner) findViewById(R.id.marca)).performClick();
                break;
            case R.id.trian_modelo:
                ((Spinner) findViewById(R.id.modelo)).performClick();
                break;
            case R.id.trian_color:
                ((Spinner) findViewById(R.id.color)).performClick();
                break;

        }

    }

    public void irHomeConductor(View view) {

        Intent intent = new Intent(this, HomeConductor.class);
        Bundle extras = getIntent().getExtras();

        Spinner marca_sp = (Spinner) findViewById(R.id.marca);
        Spinner modelo_sp = (Spinner) findViewById(R.id.modelo);
        Spinner color_sp = (Spinner) findViewById(R.id.color);
        EditText placas_et = (EditText) findViewById(R.id.ra_placas);

        String login = extras.getString("login");
        String password = extras.getString("password");
        String nombre = extras.getString("nombre");
        String edad = extras.getString("edad");
        String genero = extras.getString("genero");

        // Estan en orden inverso a proposito


        if(placas_et.getText().toString().equals("")){
            placas_et.setText("");
        }

        if(color_sp.getSelectedItem() == null){

            TextView text_color = (TextView) findViewById(R.id.text_color);
            text_color.setFocusableInTouchMode(true);
            text_color.setFocusable(true);
            text_color.setError("");
            text_color.requestFocus();
            ((TextView) findViewById(R.id.trian_color)).setError("Debes elegir un color", null);
            ((TextView) findViewById(R.id.trian_color)).requestFocus();

        }

        if(modelo_sp.getSelectedItem() == null){
            TextView text_modelo = (TextView) findViewById(R.id.text_modelo);
            text_modelo.setFocusableInTouchMode(true);
            text_modelo.setFocusable(true);
            text_modelo.setError("");
            text_modelo.requestFocus();
            ((TextView) findViewById(R.id.trian_modelo)).setError("Debes elegir un modelo", null);
            ((TextView) findViewById(R.id.trian_modelo)).requestFocus();

        }

        if(marca_sp.getSelectedItem() == null){

            TextView text_marca = (TextView) findViewById(R.id.text_marca);
            text_marca.setFocusableInTouchMode(true);
            text_marca.setFocusable(true);
            text_marca.setError("");
            text_marca.requestFocus();
            ((TextView) findViewById(R.id.trian_marca)).setError("Debes elegir una marca", null);
            ((TextView) findViewById(R.id.trian_marca)).requestFocus();
        }

        marca_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ArrayAdapter<String> adapter_modelo_str;
                Spinner modelo_sp = (Spinner) findViewById(R.id.modelo);

                if (position != 0){
                    String selected = ((TextView) view).getText().toString();
                    adapter_modelo_str = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, cursorToList(bd.obtener_modelos(selected)));
                    modelo_sp.setAdapter(new NothingSelectedSpinnerAdapter(adapter_modelo_str, R.layout.nselc_spinner_layout, getApplicationContext(), "Modelo", R.id.text_modelo));
                    ((TextView) findViewById(R.id.trian_marca)).setError(null);
                    ((TextView) findViewById(R.id.trian_modelo)).setError(null);
                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        modelo_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    ((TextView) findViewById(R.id.trian_modelo)).setError(null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        color_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    ((TextView) findViewById(R.id.trian_color)).setError(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (((TextView) findViewById(R.id.trian_marca)).getError() == null &&
              ((TextView) findViewById(R.id.trian_modelo)).getError() == null &&
              ((TextView) findViewById(R.id.trian_color)).getError() == null &&
              placas_et.getError() == null) {

            String marca = marca_sp.getSelectedItem().toString();
            String modelo = modelo_sp.getSelectedItem().toString();
            String color = color_sp.getSelectedItem().toString();
            String placas = placas_et.getText().toString();

            // Variable para revisar correcta insercion
            long verificacion;

            // Registro de placas previamente guardado en la BD (si es que existe)

            Cursor c = bd.revisar_placas(placas);

            String c_marca = null;
            String c_modelo = null; // Siempre que sean requeridos seran modificados en el if.
            String c_color = null;

            if (c.moveToFirst()) {
                c_marca = c.getString(c.getColumnIndex("marca"));
                c_modelo = c.getString(c.getColumnIndex("modelo"));
                c_color = c.getString(c.getColumnIndex("color"));
            }

            if (c.getCount() == 0) {

                //inserta ambos

                verificacion = bd.añadir_automovil(marca, modelo, color, placas);

                if (verificacion != -1) {

                    verificacion = bd.anadir_conductor(login, password, nombre, edad, genero, placas);

                    if (verificacion != -1) {
                        editor.putString("login", login);
                        editor.commit();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        bd.eliminar_automovil(placas);
                        Toast.makeText(getApplicationContext(), "No se ha podido registrar", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No se ha podido registrar", Toast.LENGTH_SHORT).show();
                }


            } else {

                if (c_marca.equals(marca) && c_modelo.equals(modelo) && c_color.equals(color)) {

                    // inserta conductor
                    verificacion = bd.anadir_conductor(login, password, nombre, edad, genero, placas);

                    if (verificacion != -1) {
                        editor.putString("login", login);
                        editor.commit();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {

                        Toast.makeText(getApplicationContext(), "No se ha podido registrar", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    //error placas ya registradas con diferentes datos
                    Toast.makeText(getApplicationContext(), "Placas ya registradas con otros datos", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void validar_placas(){

        EditText placas_et = (EditText) findViewById(R.id.ra_placas);

        placas_et.addTextChangedListener(new TextValidator(placas_et) {
            @Override
            public void validate(TextView textView, String text) {

                if(text.equals("")){
                    textView.setError("Este campo no puede quedar vacío");
                    textView.requestFocus();
                }else{
                    if(! (Pattern.matches("[a-zA-Z]{3}-[0-9]{3}",text) || Pattern.matches("[a-zA-Z]{3}(-[0-9]{2}){2}",text))){
                        textView.setError("Los formatos correctos son : AAA-111 ó AAA-11-11");
                        textView.requestFocus();
                    }else{
                        textView.setError(null);
                    }
                }

            }
        });


    }


}
