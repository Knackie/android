package com.example.projetamio.datamanagement;

import android.content.Intent;
import android.util.Log;

import com.example.projetamio.services.NotifyChoiceService;

import java.util.ArrayList;
import java.util.HashMap;

public class DonneesLampe {

    /**
     * Seuil a partir duquel la lumière est allumée
     */
    public static final int SEUIL_ALLUME_ETEINT = 250;

    /**
     * Nom de la mote
     */
    private final String nom;

    /**
     * Données de luminosité de la mote
     */
    private final HashMap<Long, Double> donneesLampe;

    /**
     * Clé de la dernière données de luminosité
     */
    private Long lastLight;

    /**
     * Données de Temperature récupéré de la mote
     */
    private final HashMap<Long, Double> dataTemperature;

    /**
     * Clé de la dernière donnée de température
     */
    private Long lastTemperature;

    /**
     * Données d'humidité de la mote
     */
    private final HashMap<Long, Double> dataHumidity;

    /**
     * Clé de la dernière donnée d'humidité
     */
    private Long lastHumidity;

    /**
     * Données de niveau de batterie de la mote
     */
    private final HashMap<Long, Double> dataBattery;

    /**
     * Clé de la dernière donnée de niveau de batterie
     */
    private Long lastBattery;

    /**
     * Indique si la lumière est allumée ou non
     */
    private boolean etat = false;

    /**
     * Constructeur de la classe
     * @param nom Permet de donner un nom à la Mote
     */
    public DonneesLampe(String nom){
        this.nom = nom;
        this.donneesLampe = new HashMap<Long, Double>();
        dataTemperature = new HashMap<>();
        dataBattery = new HashMap<>();
        dataHumidity = new HashMap<>();
    }

    /**
     * Indique si la lumière est allumée dans l'emplacement de la mote
     * @return État de la lumière dans la pièce de la mote
     */
    public boolean isEtat() {
        return etat;
    }

    /**
     * Fonction permettant d'ajouter une nouvelle donnée à une mote
     * @param valeur La nouvelle valeur à ajouter
     * @param label le nom de la donnée
     * @param timetamps heure de relevé
     * @return Indique l'ajout ou non de la donnée
     */
    public boolean addEtat(Double valeur, String label, Long timetamps){
        switch (label) {
            case "temperature":
                this.dataTemperature.put(timetamps, valeur);
                this.lastTemperature = timetamps;
                return true;
            case "light1" :
            case "light2" :
                Log.d(this.getClass().getName(), "Mote : " + this.getNom() + ", valeur : " + valeur );
                this.donneesLampe.put(timetamps, valeur);
                Double lastValue = this.donneesLampe.get(this.lastLight);
                if (lastValue != null){
                    if (Math.abs(valeur -lastValue ) >= 15) {
                        // Check on which mode put the lamp
                        if (valeur > SEUIL_ALLUME_ETEINT) {
                            this.etat = true;
                        } else {
                            this.etat = false;
                        }
                    }
                }
                this.lastLight = timetamps;
                break;
            case "battery_indicator":
                this.dataBattery.put(timetamps, valeur);
                this.lastBattery = timetamps;
                return true;
            case "humidity":
                this.dataHumidity.put(timetamps, valeur);
                this.lastHumidity = timetamps;
                return true;

        }
        return false;
    }

    /**
     * Fonction permettant de récupérer le nom de la mote
     * @return Nom de la mote
     */
    public String getNom() {
        return nom;
    }

    /**
     * Fonction permettant de récupérer si la mote est allumée
     * @return La lumière est allumée ou non
     */
    public boolean isAllume() {
        return this.donneesLampe.get(lastLight) > SEUIL_ALLUME_ETEINT;
    }

    /**
     * Fonction permettant de récupérer la dernière valeur de température relevée par la mote
     * @return La dernière temperature relevée par la mote
     */
    public Double getLastTemperature() {
        return dataTemperature.get(lastTemperature);
    }

    /**
     * Fonction permettant de récupérer la dernière valeur d'humidité dans l'air
     * @return La dernière valeur d'humidité relevée dans la pièce de la mote
     */
    public Double getLastHumidity() {
        return dataHumidity.get(lastHumidity);
    }

    /**
     * Fonction permettant de récupérer le dernier niveau de batterie relevé
     * @return Le dernier relevé de niveau de batterie
     */
    public Double getLastBattery() {
        return dataBattery.get(lastBattery);
    }


    /**
     * Fonction permettant l'afficahge des données de la mote sous forme d'une String
     * @return String de l'ensemble des données de la mote
     */
    @Override
    public String toString() {
        return "DonneesLampe{" +
                "nom='" + nom + '\'' +
                ", donneesLampe=" + donneesLampe +
                ", lastLight=" + lastLight +
                ", dataTemperature=" + dataTemperature +
                ", lastTemperature=" + lastTemperature +
                ", dataHumidity=" + dataHumidity +
                ", lastHumidity=" + lastHumidity +
                ", dataBattery=" + dataBattery +
                ", lastBattery=" + lastBattery +
                ", etat=" + etat +
                '}';
    }
}
