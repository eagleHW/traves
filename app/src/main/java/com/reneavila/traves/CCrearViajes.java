package com.reneavila.traves;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.SQLOutput;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class CCrearViajes extends Fragment implements View.OnClickListener {

    SharedPreferences settings;
    BaseDatos bd;

    public CCrearViajes() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ccrear_viajes, container, false);
        bd = new BaseDatos(getActivity().getBaseContext());
        settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        Button button = (Button) view.findViewById(R.id.crear_viaje);
        button.setOnClickListener(this);
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        validar_fecha();
        validar_hora();
    }

    @Override
    public void onClick(View v) {

        String mensaje = "Viaje creado";

        EditText origen_et = (EditText) getView().findViewById(R.id.cv_origen);
        EditText destino_et = (EditText) getView().findViewById(R.id.cv_destino);
        TextView hora_salida_tv = (TextView) getView().findViewById(R.id.tiempo);
        TextView fecha_tv = (TextView) getView().findViewById(R.id.fecha);

        String origen = origen_et.getText().toString();
        String destino = destino_et.getText().toString();
        String hora_salida = hora_salida_tv.getText().toString();
        String fecha = fecha_tv.getText().toString();


        if(fecha.equals("")){
            fecha_tv.setText("");
        }else{
            fecha_tv.setText(fecha);
        }
        if(hora_salida.equals("")){
            hora_salida_tv.setText("");
        }else{
            hora_salida_tv.setText(hora_salida);
        }




        if( hora_salida_tv.getError() == null && fecha_tv.getError() == null){
            hora_salida = norFormHo_To_DbFormHo(hora_salida);
            fecha = norFormFe_To_DbFormFe(fecha);

            Cursor c = bd.revisar_traslado_unico(settings.getString("login","ERROR"),fecha,hora_salida);

             if(c.getCount() == 0 ){

                 //System.out.println("|"  + origen + "|" + destino + "|" + hora_salida + "|" + fecha + "|");
                 bd.crear_traslado(fecha,hora_salida,origen,destino,settings.getString("login","ERROR"));
                 Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();

             }else{

                Toast.makeText(getActivity(),"Ya tienes un viaje en este horario ", Toast.LENGTH_SHORT).show();

             }

        }else{
            if(fecha_tv.getError() != null){
                fecha_tv.requestFocus();
            }else{
                if(hora_salida_tv.getError() != null){
                    hora_salida_tv.requestFocus();
                }
            }
        }
    }


    private void validar_fecha(){

        TextView fecha_tv= (TextView) getView().findViewById(R.id.fecha);
        final TextView hora_tv= (TextView) getView().findViewById(R.id.tiempo);

        fecha_tv.addTextChangedListener(new TextValidator(fecha_tv) {
            @Override
            public void validate(TextView textView, String text) {

                if(text.equals("")){
                    textView.setError("No puedes dejar este campo vacio");
                }else {

                    String fecha = norFormFe_To_DbFormFe(text);
                    String[] comp_fecha = fecha.split("-");
                    Calendar cal_fecha = Calendar.getInstance();

                    cal_fecha.set(Integer.valueOf(comp_fecha[0]),
                            Integer.valueOf(comp_fecha[1]) - 1,
                            Integer.valueOf(comp_fecha[2]));

                    if (cal_fecha.before(Calendar.getInstance())) {
                        textView.setError("No puedes elegir fecha anteriores a hoy.");
                    } else {
                        textView.setError(null);
                        hora_tv.setError(null);
                    }

                }
            }
        });
    }

    private void validar_hora(){

        final TextView fecha_tv= (TextView) getView().findViewById(R.id.fecha);
        TextView hora_tv= (TextView) getView().findViewById(R.id.tiempo);

        hora_tv.addTextChangedListener(new TextValidator(hora_tv) {
            @Override
            public void validate(TextView textView, String text) {

                if(text.equals("")){
                    textView.setError("No puedes dejar este campo vacio");

                }else {


                    if (!fecha_tv.getText().toString().equals("") && fecha_tv.getError() == null) {

                        Calendar cal_fechayhora = Calendar.getInstance();

                        String fecha = norFormFe_To_DbFormFe(fecha_tv.getText().toString());
                        String hora = norFormHo_To_DbFormHo(text);

                        String[] comp_fecha = fecha.split("-");
                        String[] comp_hora = hora.split(":");

                        cal_fechayhora.set(Integer.valueOf(comp_fecha[0]),
                                Integer.valueOf(comp_fecha[1]) - 1,
                                Integer.valueOf(comp_fecha[2]),
                                Integer.valueOf(comp_hora[0]),
                                Integer.valueOf(comp_hora[1]));

                        if (cal_fechayhora.before(Calendar.getInstance())) {

                            textView.setError("No puedes elegir hora anterior a la actual");

                        } else {

                            textView.setError(null);

                        }

                    }

                }
            }
        });


    }

    private String norFormFe_To_DbFormFe(String fecha) {

        final String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo",
                "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        String[] comp_fecha = fecha.replace(" ", "").split("/");

        int cont;
        for (cont = 0; cont < months.length; cont++) {
            if (comp_fecha[1].equals(months[cont])) {
                break;
            }
        }

        comp_fecha[1] = (cont+1) < 10 ? "0" + (cont+1) : "" + (cont+1)  ;

        return comp_fecha[2] + "-" + comp_fecha[1] + "-" + comp_fecha[0];

    }


    private String norFormHo_To_DbFormHo(String hora){

        String[] comp_hora = hora.replace(" ", "").split(":");

        comp_hora[0] = comp_hora[0].length() == 1 ? "0" + comp_hora[0] : comp_hora[0];

        return comp_hora[0] + ":" +comp_hora[1];

    }

}


