package com.tacuba.comicsmanager.utils;

import com.tacuba.comicsmanager.Constantes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mendoedg on 23/02/2015.
 */
public class MyDateUtils {

    /**
     *
     * @param day
     * @param month
     * @param year
     * @return Fecha en formato dd/MM/yyyy
     */
    public static String dateFormat(int day, int month, int year){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0);
        SimpleDateFormat sdf = new SimpleDateFormat(Constantes.FORMATO_FECHA_ACTUAL);
        return sdf.format(cal.getTime());
    }
    /**
     *
     * @return Fecha actual en formato /MM/yyyy
     */
    public static String mesActual(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM_yyyy");
        return sdf.format(cal.getTime());
    }
    /**
     *
     * @param mes
     * @return convierte el objeto Date en una fecha en formato /MM/yyyy
     */
    public static String mesActual(Date mes){
        Calendar cal = Calendar.getInstance();
        cal.setTime(mes);
        SimpleDateFormat sdf = new SimpleDateFormat(Constantes.FORMATO_FECHA_MES_ANIO);
        return sdf.format(cal.getTime());
    }
    public static String fechaActual(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Constantes.FORMATO_FECHA_ACTUAL);
        return sdf.format(cal.getTime());
    }
    public static String anioActual(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM_yyyy");
        return sdf.format(cal.getTime());
    }
    public static String getNumeroMes(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Constantes.FORMATO_MES);
        return sdf.format(cal.getTime());
    }

    /**
     *
     * @param fecha
     * @return fecha a la que se le suman los dias que se se indica
     */
    public static String agregarMesFecha(int meses,String fecha){
        String fecha_add = fecha;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat  sdf = new SimpleDateFormat(Constantes.FORMATO_FECHA_ACTUAL);
        SimpleDateFormat  sdf_dia = new SimpleDateFormat(Constantes.FORMATO_DIA);
        SimpleDateFormat  sdf_mes = new SimpleDateFormat(Constantes.FORMATO_MES);
        SimpleDateFormat  sdf_anio = new SimpleDateFormat(Constantes.FORMATO_ANIO);
        int totalMeses=0;
        try {
            cal.setTime(sdf.parse(fecha_add));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int  dia=Integer.valueOf(sdf_dia.format(cal.getTime()));
        int mes=(Integer.valueOf(sdf_mes.format(cal.getTime())));
        int anio=Integer.valueOf(sdf_anio.format(cal.getTime()));
        if((mes+meses) > 12 ){
            totalMeses = (mes+meses)-12;
            anio++;
        }else{
            totalMeses =mes+meses;
        }
        return  dateFormat(dia,totalMeses-1,anio);//esto es por que el mes inicial lo toma como 0
    }

    public static String agregarMes(int meses,String fecha){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat  sdf = new SimpleDateFormat(Constantes.FORMATO_FECHA_ACTUAL);
        try {
            cal.setTime(sdf.parse(fecha));
            int diaNuevo;
            int diaOriginal=cal.get(Calendar.DAY_OF_WEEK);
            int diferencia;
            cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, meses);
            diaNuevo=cal.get(Calendar.DAY_OF_WEEK);
            diferencia= diaOriginal-diaNuevo;
            cal.add(Calendar.DATE, diferencia);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(cal.getTime());
    }
    public static String agregarDias(int dias,String fecha){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat  sdf = new SimpleDateFormat(Constantes.FORMATO_FECHA_ACTUAL);
        try {
            cal.setTime(sdf.parse(fecha));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return sdf.format(cal.getTime());
    }

    /**
     * Valida que strFecha1 == strFecha2
     * @param strFecha1
     * @param strFecha2
     * @return
     */
    public static int compararFecha(String strFecha1, String strFecha2){
        Calendar fecha1=Calendar.getInstance();
        Calendar fecha2=Calendar.getInstance();
        SimpleDateFormat  sdf = new SimpleDateFormat(Constantes.FORMATO_FECHA_ACTUAL);
        try {
            fecha1.setTime(sdf.parse(strFecha1));
            fecha2.setTime(sdf.parse(strFecha2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fecha1.compareTo(fecha2);
    }

    public static boolean validarFecha(String fecha){
        boolean res=true;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Constantes.FORMATO_FECHA_ACTUAL);
            sdf.setLenient(false);
            sdf.parse(fecha);
        }
        catch (ParseException e) {
            res = false;
        }
        catch (IllegalArgumentException e) {
            res = false;
        }
        return res;
    }

    public static String convertirFecha(String fecha){
        String res="";
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat  sdf = new SimpleDateFormat(Constantes.FORMATO_FECHA_ACTUAL);
        Calendar fecha1=Calendar.getInstance();
        try {
            fecha1.setTime(df1.parse(fecha));
            res = sdf.format(fecha1.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            res =null;
        }
        return res;
    }

    public static Calendar convertirStrFecha(String fecha){
        Calendar result =Calendar.getInstance();
        Date date=null;
        SimpleDateFormat sdf = new SimpleDateFormat(Constantes.FORMATO_FECHA_ACTUAL);
        sdf.setLenient(false);
        try {
            date = sdf.parse(fecha);
            result.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
            result = null;
        }

        return result;
    }
}
