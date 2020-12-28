package com.example.projetamio.datamanagement;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe en Singleton permettant de gérer un ensemble de motes
 */
public class ListLampe {

    /**
     * Ensemble des motes disponibles
     */
    private HashMap<String,DonneesLampe> listLampe;

    /**
     * Singleton : Permet de récupérer l'instance active de ListLampe
     */
    private static ListLampe instance;

    /**
     * Constructeur de la classe
     */
    private ListLampe(){
        this.listLampe = new HashMap<String, DonneesLampe>();
        ListLampe.instance = this;
    }

    /**
     * Fonction permettant de récupérer les données d'une mote via son nom
     * @param nomLampe Nom de la mote à récupérer
     * @return Ensemble des données de la mote à récupérer
     */
    public DonneesLampe getLampe(String nomLampe){
        DonneesLampe lampe = this.listLampe.get(nomLampe);
        if (lampe == null){
            lampe = new DonneesLampe(nomLampe);
            this.listLampe.put(nomLampe, lampe);
        }
        return lampe;
    }

    /**
     * Fonction permettant de supprimer une mote des motes actives
     * @param nomLampe Nom de la mote à supprimer
     * @return Confirmation de la suppression
     */
    public boolean removeLampe(String nomLampe){
        this.listLampe.put(nomLampe, null);
        return true;
    }

    /**
     * Fonction Statique permettant de récupérer une instance de la classe ListLampe
     * @return L'instance de la classe ListLamp
     */
    public static ListLampe getInstance(){
        if (ListLampe.instance == null ){
            new ListLampe();
        }
        return ListLampe.instance;
    }

    /**
     * Fonction permettant de récupérer l'ensemble des motes avec leurs données
     * @return L'ensemble des données des différentes motes
     */
    public ArrayList getDonneesLampList(){
        return new ArrayList(listLampe.values());
    }

    /**
     * Fonction permettant de récupérer les données des motes sous la forme d'une String
     * @return String contenant l'ensemble des données des motes connues
     */
    @Override
    public String toString() {
        return "ListLampe{" +
                "listLampe=" + listLampe.toString() +
                '}';
    }
}
