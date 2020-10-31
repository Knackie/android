package com.example.projetamio;

import java.util.ArrayList;
import java.util.HashMap;

public class DonneesLampe {

    private final String nom;
    private final HashMap<Long, Double> donneesLampe;
    private Long lastLight;
    private final HashMap<Long, Double> dataTemperature;
    private Long lastTemperature;
    private final HashMap<Long, Double> dataHumidity;
    private Long lastHumidity;
    private final HashMap<Long, Double> dataBattery;
    private Long lastBattery;
    private boolean etat = false;

    public DonneesLampe(String nom){
        this.nom = nom;
        this.donneesLampe = new HashMap<Long, Double>();
        dataTemperature = new HashMap<>();
        dataBattery = new HashMap<>();
        dataHumidity = new HashMap<>();
    }


    public boolean isEtat() {
        return etat;
    }

    public boolean addEtat(Double valeur, String label, Long timetamps){
        switch (label) {
            case "temperature":
                this.donneesLampe.put(timetamps, valeur);
                this.lastTemperature = timetamps;
                return true;
            case "light1" :
            case "light2" :
                this.donneesLampe.put(timetamps, valeur);
                this.lastLight = timetamps;
                int nbDonne = this.donneesLampe.size();
                if (Math.abs((this.donneesLampe.get(nbDonne) - this.donneesLampe.get(nbDonne - 1))) >= 15) {
                    // On vérifie le mode dans lequel mettre la lampe
                    if (valeur > 250) {
                        this.etat = true;
                    } else {
                        this.etat = false;
                    }
                    return true;
                }
                break;
            case "battery_indicator":
                this.dataBattery.put(timetamps, valeur);
                this.lastBattery = timetamps;
                return true;
            case "humidity":
                this.dataHumidity.put(timetamps, valeur);
                this.lastHumidity = timetamps;

        }
        return false;
    }

    public String getNom() {
        return nom;
    }
}
