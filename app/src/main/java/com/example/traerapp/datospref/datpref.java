package com.example.traerapp.datospref;


import android.content.Context;
import android.content.SharedPreferences;

public class datpref
{
    public static void setDatosUsuario(Context context,String iden,String nom,String ape,String celu,String corr,String usu){
        SharedPreferences preferences = context.getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Identificacion",iden);
        editor.putString("Nombres",nom);
        editor.putString("Apellidos",ape);
        editor.putString("Celular",celu);
        editor.putString("Correo",corr);
        editor.putString("Usuario",usu);
        editor.commit();
    }

    public static String[] getDatosUsuario(Context context){
        SharedPreferences preferences = context.getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        String nom = preferences.getString("Identificacion","NI");
        String usu = preferences.getString("Nombres","NI");
        String correo = preferences.getString("Apellidos","NI");
        String ciud = preferences.getString("Celular","NI");
        String corr = preferences.getString("Correo","NI");
        String usuar = preferences.getString("Usuario","NI");
        String[] respuesta = {nom, usu, correo,ciud,corr,usuar};
        return  respuesta;
    }


    public static void setDatosViaje(Context context,String inicio,String fin,String fecha,String cantidad){
    SharedPreferences preferences = context.getSharedPreferences("DatosViaje", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString("INICIO",inicio);
    editor.putString("FIN",fin);
    editor.putString("FECHA",fecha);
    editor.putString("CANTID",cantidad);
    editor.commit();
}

    public static String[] getDatosViaje(Context context){
        SharedPreferences preferences = context.getSharedPreferences("DatosViaje", Context.MODE_PRIVATE);
        String nom = preferences.getString("INICIO","NI");
        String usu = preferences.getString("FIN","NI");
        String correo = preferences.getString("FECHA","NI");
        String ciud = preferences.getString("CANTID","NI");
        String[] respuesta = {nom, usu, correo,ciud};
        return  respuesta;
    }

    public static void setCoordenadas(Context context,String coordenadas,Double Longitud,Double Latitud){
        SharedPreferences preferences = context.getSharedPreferences("DatosCoor", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Coordenadas",coordenadas);
        editor.putString("Longitud",Longitud.toString());
        editor.putString("Latitud",Latitud.toString());
        editor.commit();
    }


    public static String[] getCoordenadas(Context context){
        SharedPreferences preferences = context.getSharedPreferences("DatosCoor", Context.MODE_PRIVATE);
        String nom = preferences.getString("Coordenadas","NI");
        String longitu = preferences.getString("Longitud","NI");
        String latit = preferences.getString("Latitud","NI");
        String[] respuesta = {nom, longitu, latit};
        return respuesta;
    }


    public static String[] getCoordenadasLlegada(Context context){
        SharedPreferences preferences = context.getSharedPreferences("DatosCoorLlegada", Context.MODE_PRIVATE);
        String nom = preferences.getString("Coordenadas","NI");
        String longitu = preferences.getString("Longitud","NI");
        String latit = preferences.getString("Latitud","NI");
        String[] respuesta = {nom, longitu, latit};
        return respuesta;
    }


    public static void setCoordenadasLlegada(Context context,String coordenadas,Double Longitud,Double Latitud){
        SharedPreferences preferences = context.getSharedPreferences("DatosCoorLlegada", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Coordenadas",coordenadas);
        editor.putString("Longitud",Longitud.toString());
        editor.putString("Latitud",Latitud.toString());
        editor.commit();
    }

    public static void setIDViaje(Context context,String ID,String Precio,String Inicio,String Fin){
        SharedPreferences preferences = context.getSharedPreferences("IDViaje", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ID",ID);
        editor.putString("Precio",Precio);
        editor.putString("Ini",ID);
        editor.putString("Fin",Precio);
        editor.commit();
    }

    public static String[] getIDViaje(Context context){
        SharedPreferences preferences = context.getSharedPreferences("IDViaje", Context.MODE_PRIVATE);
        String nom = preferences.getString("ID","NI");
        String pre = preferences.getString("Precio","NI");
        String ini = preferences.getString("Ini","NI");
        String fin = preferences.getString("Fin","NI");
        String[] respuesta = {nom, pre,ini,fin};
        return  respuesta;
    }


}
