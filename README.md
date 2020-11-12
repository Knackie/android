# Projet android

Le but principal de cette application est de : 
- lister dans l'activité principale les capteurs actifs et les valeurs de luminosité qu'ils relèvent,en mettant en évidence ceux qui indiquent la présence d'une lumière active
- émettre une notification si une nouvelle lumière vient d'être allumée en semaine entre 19h et23h, en spécifiant le capteur impliqué
- envoyer un email si cet événement survient le week-end entre 19h et 23h ou en semaineentre 23h et 6h.
- permettre la configuration des plages horaires et de l'adresse email dans un menu dédié

# Fonctionnement de l'application 

Une fois l'application lancée, vous arrivez sur un menu d'accueil.
<p align="center">
  <img src="img/main_menu.PNG" />
</p>

Sur ce menu, l'état du téléchargement est visible (en rouge s'il est desactivé, en vert s'il est activé).
Un bouton on/off permet l'activation ou la desactivation de ce menu.
Un bouton capteur redirige vers les différents capteurs et les mesures associées.
Il est possible de définir des réglages en haut à droite en cliquant sur les 3 points.
