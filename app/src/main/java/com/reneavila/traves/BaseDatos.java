package com.reneavila.traves;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.w3c.dom.Text;

public class BaseDatos extends SQLiteOpenHelper{


    public static final String DB_NAME = "TravesDB";
    public static final int DB_VERSION = 1;

    public static final String CREATE_IDENTIDAD = "create table identidad(\n" +
                                                   "login text,\n" +
                                                   "password text,\n" +
                                                   "identidad text\n" +
                                                   ");";

    public static final String CREATE_CONDUCTOR = "create table conductor(\n" +
                                                  "login_conductor text,\n" +
                                                  "password test,\n" +
                                                  "nombre test,\n" +
                                                  "edad integer,\n" +
                                                  "genero text,\n" +
                                                  "placas text,\n" +
                                                  "primary key (login_conductor),\n" +
                                                  "foreign key (placas) references automovil(placas)\n" +
                                                  ");";

    public static final String CREATE_PASAJERO = "create table pasajero(\n" +
                                                 "login_pasajero text,\n" +
                                                 "password text,\n" +
                                                 "nombre text,\n" +
                                                 "edad integer,\n" +
                                                 "genero text,\n" +
                                                 "primary key (login_pasajero)\n" +
                                                 ");";

    public static final String CREATE_AUTOMOVIL = "create table automovil(\n" +
                                                  "marca text,\n" +
                                                  "modelo text,\n" +
                                                  "color text,\n" +
                                                  "placas text,\n" +
                                                  "primary key (placas)\n" +
                                                  ");";

    public static final String CREATE_TRASLADO = "create table traslado(\n" +
                                                 "fecha date,\n" +
                                                 "hora_salida text,\n" +
                                                 "origen text,\n" +
                                                 "destino text,\n" +
                                                 "login_conductor text,\n" +
                                                 "foreign key(login_conductor) references conductor(login_conductor),\n" +
                                                 "primary key (fecha,hora_salida,login_conductor)\n" +
                                                 ");";

    public static final String CREATE_SOLICITADO = "create table solicitado(\n" +
                                                   "fecha date,\n" +
                                                   "hora_salida text,\n" +
                                                   "origen text,\n" +
                                                   "destino text,\n" +
                                                   "login_conductor text,\n" +
                                                   "login_pasajero text,\n" +
                                                   "foreign key (login_pasajero) references pasajero(login_pasajero),\n" +
                                                   "foreign key (fecha,hora_salida,login_conductor) references traslado(fecha,hora_salida,login_conductor) on delete cascade, \n" +
                                                   "primary key (login_conductor,login_pasajero,fecha,hora_salida)\n" +
                                                   ");";

    public static final String CREATE_CANCELADO = "create table cancelado(\n" +
                                                  "fecha date,\n" +
                                                  "hora_salida text,\n" +
                                                  "origen text,\n" +
                                                  "destino text,\n" +
                                                  "login_conductor text,\n" +
                                                  "login_pasajero text,\n" +
                                                  "foreign key (login_conductor) references conductor(login_conductor),\n" +
                                                  "foreign key (login_pasajero) references pasajero(login_pasajero)\n" +
                                                  ");";

    public static final String CREATE_CATALOGO_AUTOS = "create table catalogo_autos(\n" +
                                                       "marca text,\n" +
                                                       "modelo text,\n" +
                                                       "primary key (marca,modelo) \n" +
                                                       ");";


    public static final String TRIGGER_CANCELAR_VIAJES = "create trigger cancelar_viajes after delete on solicitado\n" +
                                                         "\tbegin \n" +
                                                         "\tinsert into cancelado \n" +
                                                         "\t\tvalues (old.fecha,old.hora_salida,old.origen,\n" +
                                                         "\t\t\told.destino,old.login_conductor,old.login_pasajero);\n" +
                                                         "\tend;\t\n";

    public static  final String TRIGGER_SOLICITAR_VIAJE = "create trigger solicitar_viaje after insert on solicitado \n" +
                                                        "\tbegin\n" +
                                                        "\t\tdelete from cancelado where new.fecha = fecha and new.hora_salida = hora_salida and\n" +
                                                        "\t\t\t\t\t                new.origen = origen and new.destino = destino and\n" +
                                                        "\t\t\t\t\t                new.login_conductor = login_conductor and \n" +
                                                        "\t\t\t\t\t                new.login_pasajero = login_pasajero;\n" +
                                                        "\tend;\t";


    public static  final String TRIGGER_IDENTIDAD_CONDUCTOR = "create trigger identidad_conductor after insert on conductor\n" +
                                                              "\tbegin\n" +
                                                              "\t\tinsert into identidad values (new.login_conductor,new.password,\"conductor\");\n" +
                                                              "\tend;";


    public static  final String TRIGGER_IDENTIDAD_PASAJERO  = "create trigger identidad_pasajero after insert on pasajero\n" +
                                                              "\tbegin \n" +
                                                              "\t\tinsert into identidad values (new.login_pasajero,new.password,\"pasajero\");\n" +
                                                              "\tend;";




    public BaseDatos(Context context){
        super(context,DB_NAME,null,DB_VERSION);
     }


    public void add_cars(SQLiteDatabase db){

        String[] marcas = {"Ford","Mitsubishi","Toyota","Nissan","Mazda","Lamborghini","Ferrari"};
        String[][] modelos = {{"Ikon","Focus","Fusion","Mustang","EcoSport","Escape","Edge","Explorer","Expedition"},
                              {"Mirage","Lanecer","Outlander","Montero","L200"},
                              {"Yaris","Avanza","Corolla","Camry","Rav4","Sienna","Highlander"},
                              {"Tsuru","March","Tiida","Note","Sentra","Altima","X-trail,Juke"},
                              {"Mazda 2","Mazda 3","Mazda 5","Mazda 6","Mazda MX-5","Mazda CX-5","Mazda CX-9"},
                              {"Huracán","Aventador","Veneno","Asterion"},
                              {"California T","458 Speciale", "F12 Berlinetta","FF","458 Spider","LaFerrari"}};


        ContentValues valores = new ContentValues();
        for(int cont_ma = 0 ; cont_ma < marcas.length ; cont_ma++){
                valores.put("marca",marcas[cont_ma]);
            for(int cont_mo = 0; cont_mo < modelos[cont_ma].length ; cont_mo++){
                valores.put("modelo",modelos[cont_ma][cont_mo]);
                db.insert("catalogo_autos",null,valores);
            }
        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_IDENTIDAD);
    db.execSQL(CREATE_CONDUCTOR);
    db.execSQL(CREATE_PASAJERO);
    db.execSQL(CREATE_AUTOMOVIL);
    db.execSQL(CREATE_TRASLADO);
    db.execSQL(CREATE_SOLICITADO);
    db.execSQL(CREATE_CANCELADO);
    db.execSQL(CREATE_CATALOGO_AUTOS);
    db.execSQL(TRIGGER_CANCELAR_VIAJES);
    db.execSQL(TRIGGER_SOLICITAR_VIAJE);
    db.execSQL(TRIGGER_IDENTIDAD_CONDUCTOR);
    db.execSQL(TRIGGER_IDENTIDAD_PASAJERO);
    add_cars(db);
    }

    public static final String DEL_IDENTIDAD = "drop table identidad;";
    public static final String DEL_CONDUCTOR = "drop table conductor;";
    public static final String DEL_PASAJERO = "drop table pasajero;";
    public static final String DEL_AUTOMOVIL = "drop table automovil;";
    public static final String DEL_TRASLADO = "drop table traslado;";
    public static final String DEL_SOLICITADO = "drop table solicitado;";
    public static final String DEL_CANCELADO = "drop table cancelado;";
    public static final String DEL_CATALOGO_AUTOS = "drop table catalogo_autos";
    public static final String DEL_CANCELAR_VIAJES ="drop trigger cancelar_viajes ";
    public static final String DEL_SOLICITAR_VIAJES = "drop trigger solicitar_viaje";
    public static final String DEL_IDENTIDAD_CONDUCTOR = "drop trigger identidad_conductor";
    public static final String DEL_IDENTIDAD_PASAJERO = "drop trigger identidad_pasajero";


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(DEL_CANCELAR_VIAJES);
        db.execSQL(DEL_SOLICITAR_VIAJES);
        db.execSQL(DEL_IDENTIDAD_CONDUCTOR);
        db.execSQL(DEL_IDENTIDAD_PASAJERO);
        db.execSQL(DEL_CATALOGO_AUTOS);
        db.execSQL(DEL_IDENTIDAD);
        db.execSQL(DEL_SOLICITADO);
        db.execSQL(DEL_CANCELADO);
        db.execSQL(DEL_TRASLADO);
        db.execSQL(DEL_CONDUCTOR);
        db.execSQL(DEL_PASAJERO);
        db.execSQL(DEL_AUTOMOVIL);

    }


    public String logearse(String login,String password){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("select identidad\n" +
                "from identidad\n" +
                "where login = \""+ login +"\" and password = \""+ password +"\" ;",null);

        c.moveToFirst();

        if(c.getCount() == 0 ){
          return "sin_registro";
        }else{
          return c.getString(c.getColumnIndex("identidad"));
        }
    }

    public boolean login_existente(String login){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("select login\n" +
                               "from identidad\n" +
                               "where login = \"" + login + "\";",null);

        if(c.getCount() != 0){
            return true;
        }

        return false;

    }


    public long añadir_pasajero(String login, String password,String nombre,
                                String edad,String genero){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("login_pasajero",login);
        valores.put("password",password);
        valores.put("nombre",nombre);
        valores.put("edad",edad);
        valores.put("genero",genero);

        return db.insert("pasajero",null,valores);

    }


    public long anadir_conductor(String login, String password,String nombre,
                                 String edad,String genero, String placas){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("login_conductor",login);
        valores.put("password",password);
        valores.put("nombre",nombre);
        valores.put("edad",edad);
        valores.put("genero",genero);
        valores.put("placas",placas);

        return db.insert("conductor",null,valores);

    }


    // Hay que verificar que el auto no este registrado ya.

    public long añadir_automovil(String marca, String modelo,String color, String placas){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("marca",marca);
        valores.put("modelo",modelo);
        valores.put("color",color);
        valores.put("placas",placas);

        return db.insert("automovil",null,valores);

    }


    public Cursor obtener_disponibles(String login){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("select * \n" +
                               "from traslado\n" +
                               "except\n" +
                               "select fecha,hora_salida,origen,destino,login_conductor\n" +
                               "from solicitado\n" +
                               "where login_pasajero = \"" + login + "\";",null);

        return c;

    }


    public Cursor obtener_solicitados(String login){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("select login_conductor, origen, destino, fecha , hora_salida\n" +
                    "from solicitado\n" +
                    "where login_pasajero = \"" + login + "\";",null);

        return c;

    }

    public Cursor obtener_cancelados(String login){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("select login_conductor, origen, destino, fecha , hora_salida\n" +
                               "from cancelado\n" +
                               "where login_pasajero = \"" + login + "\";",null);

        return c;
    }

    public Cursor obtener_perfil_pasajero(String login){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("select nombre, edad, genero\n" +
                "from pasajero\n" +
                "where login_pasajero = \"" + login + "\";",null);
        return c;

    }

    public long solicitar_viaje(String fecha, String hora_salida, String origen, String destino,
                                String login_conductor, String login_pasajero){


        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("fecha",fecha);
        valores.put("hora_salida",hora_salida);
        valores.put("origen",origen);
        valores.put("destino",destino);
        valores.put("login_conductor",login_conductor);
        valores.put("login_pasajero",login_pasajero);

        return db.insert("solicitado",null,valores);
    }

    public int cancelar_viaje(String login_conductor,String login_pasajero, String fecha, String hora_salida){

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete("solicitado","login_conductor = ? and login_pasajero = ? and  fecha = ? and hora_salida = ?",
                        new String[]{login_conductor,login_pasajero,fecha,hora_salida});

    }

    public Cursor obtener_traslados(String login){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("select origen,destino,fecha, hora_salida\n" +
                               "from traslado\n" +
                               "where login_conductor=\"" + login + "\";",null);


        return c;

    }

    public Cursor obtener_perfil_conductor(String login){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("select nombre , edad , genero , marca , modelo , color , conductor.placas \n" +
                                "from conductor, automovil\n" +
                                "where conductor.placas = automovil.placas and login_conductor = \"" + login + "\" ;",null);

        return c;

    }


    public long crear_traslado(String fecha,String hora_salida,String origen, String destino, String login_conductor){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("fecha",fecha);
        valores.put("hora_salida",hora_salida);
        valores.put("origen",origen);
        valores.put("destino",destino);
        valores.put("login_conductor",login_conductor);

        return  db.insert("traslado",null,valores);
    }

    public int cancelar_traslado(String fecha, String hora_salida, String login_conductor){

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete("canceado","fecha = ? and hora_salida = ? and login_conductor = ?",
                        new String[] {fecha,hora_salida,login_conductor});

    }

    public Cursor revisar_placas(String placas){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("select marca, modelo, color\n" +
                               "from automovil\n" +
                               "where placas = \"" + placas +"\";\n",null);

        return c;

    }

    public int eliminar_automovil(String placas){

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete("automovil","placas = ?",new String[] {placas});

    }

    //// Catalogos

    public Cursor obtener_modelos(String marca){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("select modelo\n" +
                               "from catalogo_autos\n" +
                               "where marca = \"" + marca + "\"\n" +
                               "order by modelo;",null);


        return c;

    }

    public Cursor obtener_marcas(){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("select distinct marca \n" +
                               "from catalogo_autos\n" +
                               "order by marca;\n",null);
        return c;
    }


    public Cursor revisar_traslado_unico(String login_conductor, String fecha, String hora ){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("select *\n" +
                "from traslado\n" +
                "where login_conductor=\"" + login_conductor + "\" and fecha=\""+fecha+"\" and hora_salida=\""+hora+"\";",null);

        return c;

    }

}

