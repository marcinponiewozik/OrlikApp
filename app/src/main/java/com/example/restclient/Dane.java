package com.example.restclient;

import com.example.restclient.tabele.Uzytkownik;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

/**
 * Created by Marcin on 2015-11-26.
 */
public class Dane {
    public static Boolean zalogowany=false;
    public static Uzytkownik uzytkownik;
    public static int decyzja=-1;
    public static boolean powiadomienia = false;
    public static String URL    = "http://orlik-wysokie.rhcloud.com/webresources";
    public static Boolean wibracje = true;


    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static String getPOLNameDay(String dzien) {
        System.out.println(dzien);
        if (dzien.equals("Monday")) {
            return "Poniedziałek";
        } else if (dzien.equals("Tuesday")) {
            return "Wtorek";
        } else if (dzien.equals("Wednesday")) {
            return "Środa";
        } else if (dzien.equals("Thursday")) {
            return "Czwartek";
        } else if (dzien.equals("Friday")) {
            return "Piątek";
        } else if (dzien.equals("Saturday")) {
            return "Sobota";
        } else {
            return "Niedziela";
        }
    }

    public static String getPOLName(int miesiac) {
        if (miesiac == 0) {
            return "Styczeń";
        } else if (miesiac == 1) {
            return "Luty";
        } else if (miesiac == 2) {
            return "Marzec";
        } else if (miesiac == 3) {
            return "Kwiecień";
        } else if (miesiac == 4) {
            return "Maj";
        } else if (miesiac == 5) {
            return "Czerwiec";
        } else if (miesiac == 6) {
            return "Lipiec";
        } else if (miesiac == 7) {
            return "Sierpień";
        } else if (miesiac == 8) {
            return "Wrzesień";
        } else if (miesiac == 9) {
            return "Październik";
        } else if (miesiac == 10) {
            return "Listopad";
        } else {
            return "Grudzień";
        }
    }

    public static String haszujMD5(String password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] passBytes = password.getBytes();
            md.reset();
            byte[] digestet = md.digest(passBytes);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digestet.length; i++) {
                sb.append(Integer.toHexString(0xff & digestet[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger("Blad ");
        }
        return null;
    }

}
