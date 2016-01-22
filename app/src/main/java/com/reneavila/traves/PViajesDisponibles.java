package com.reneavila.traves;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class PViajesDisponibles extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{

    BaseDatos bd;
    SharedPreferences settings;
    TextView [][] textViews;
    Button [] buttons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bd = new BaseDatos(getActivity().getBaseContext());
        settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        Cursor c = bd.obtener_disponibles(settings.getString("login","ERROR"));

        textViews = new TextView[c.getCount()][c.getColumnCount()];
        buttons = new Button[c.getCount()];

        View view = inflater.inflate(R.layout.fragment_pviajes_disponibles, container,false);
        LinearLayout principal = (LinearLayout) view.findViewById(R.id.llp_viajes_disponibles);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,130);
        params.weight = 1;
        params.setMargins(0,25,0,25);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0,160);
        params2.weight = 1.5f;
        params2.setMargins(0,10,0,10);

        LinearLayout cont_temp;
        LinearLayout temp;

        Drawable draw = getResources().getDrawable(R.drawable.green_button_style);

        while ( c.moveToNext()){

            int num_reg = c.getPosition();

            cont_temp = new LinearLayout(view.getContext());
            cont_temp.setOrientation(LinearLayout.HORIZONTAL);

            temp = new LinearLayout((view.getContext()));
            temp.setOrientation(LinearLayout.VERTICAL);
            temp.setLayoutParams(params2);
            temp.setGravity(Gravity.CENTER);

            for ( int num_col = 0 ; num_col < c.getColumnCount() ; num_col++) {

                textViews[num_reg][num_col] = new TextView(view.getContext());

                // Para seleccionar el prefijo correcto

                switch (num_col){
                    case 0:
                        textViews[num_reg][num_col].setText(c.getString(c.getColumnIndex("login_conductor")));
                    break;
                    case 1:
                        textViews[num_reg][num_col].setText("O:" + c.getString(c.getColumnIndex("origen")));
                    break;
                    case 2:
                        textViews[num_reg][num_col].setText("D:" + c.getString(c.getColumnIndex("destino")));
                        break;
                    case 4:
                        textViews[num_reg][num_col].setText(c.getString(c.getColumnIndex("fecha")) + "   " + c.getString(c.getColumnIndex("hora_salida")) );
                        break;
                    default:
                       // textViews[num_reg][num_col].setText(dd[num_reg][num_col]);
                }

                if(num_col != 3){
                    temp.addView(textViews[num_reg][num_col]);
                }

            }

            cont_temp.addView(temp);

            buttons[num_reg] = new Button(view.getContext());
            buttons[num_reg].setText("Solicitar");
            buttons[num_reg].setAllCaps(false);
            buttons[num_reg].setGravity(Gravity.CENTER);
            buttons[num_reg].setLayoutParams(params);
            buttons[num_reg].setBackground(getResources().getDrawable(R.drawable.green_button_style));
            buttons[num_reg].setOnClickListener(this);

            cont_temp.addView(buttons[num_reg]);
            principal.addView(cont_temp);

            if(num_reg != c.getCount()-1){
                View linea = new View(getActivity());
                linea.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
                linea.setBackgroundColor(Color.rgb(51, 51, 51));

                principal.addView(linea);
            }

        }

        return view;
    }

    @Override
    public void onClick(View v) {

        String mensaje = "Has solicitado el viaje: ";

        for( int cont = 0; cont < buttons.length; cont++){

            if( buttons[cont] == v ){

                String login_conductor = textViews[cont][0].getText().toString();
                String origen = textViews[cont][1].getText().toString().substring(2);
                String destino = textViews[cont][2].getText().toString().substring(2);

                String fecha_hora = textViews[cont][4].getText().toString();

                String fecha = fecha_hora.substring(0,fecha_hora.indexOf(' '));
                String hora_salida = fecha_hora.substring(fecha_hora.lastIndexOf(' ') + 1 ,fecha_hora.length());

                //System.out.println("|"  + origen + "|" + destino + "|" + hora_salida + "|" + fecha + "|");

                bd.solicitar_viaje(fecha,hora_salida,origen,destino,login_conductor,settings.getString("login","ERROR"));

                Toast.makeText(getActivity(),mensaje + cont, Toast.LENGTH_SHORT).show();



            }
        }
    }

    @Override
    public void onRefresh() {

    }
}