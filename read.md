#TP2 : Simulation de Pigeon
###8INF95 - Théo Bertet

* Structure du projet

Le projet fonctionne à l'aide du gestionnaire de dépendances Maven,
qui a permis d'intégrer les packages nécessaires à l'utilisation
de l'interface graphique employée pour le TP.
JavaFX a été retenue comme interface graphique pour afficher les
pigeons et la nourriture.

Le code respecte une structure MVC (Modèle-Vue-Contrôleur) afin
d'assurer une clareté et une lisibilité du code et des classe
satisfaisantes.

* Configuration Maven

Afin de convenir aux différentes versions compatibles entre le JDK,
le gestionnaire Maven et les différents packages implémentés, la
version du JDK10 a été retenue. (Major version 54)

En ce qui concerne JavaFX, la version 11 des différents packages a
été implémentée. (Compatible avec le JDK10)

* Structure du code
    * Controller
        * mainViewController : C'est le contrôleur qui permet la
        gestion de la fenêtre principale. Il hérite de la classe
        ``application`` et implémente l'interface ``initializable``.
    * Model
        * Food : C'est la classe qui représente la nourriture sur le
        plateau de jeu.
        * Pigeon : C'est la classe qui représente les pigeons sur le
        plateau de jeu. Cette classe implémente l'interface ``runnable``,
        qui fait d'elle une classe pouvant être lancée comme un thread.
        Cette classe possède une méthode synchronisée ;
        Il s'agit de la méthode permettant de manger de la nourriture.
        * Simulation : C'est la classe qui représente la simulation
        sur laquelle le plateau se base. Elle possède la liste des
        pigeons et la liste de la nourriture. Elle possède également
        un ``ExecutorService`` qui lui permet de lancer les threads des
        pigeons. (Chaque pigeon étant un thread différent)
    * View
        * mainView : C'est un fichier FXML représentant la fenêtre
        principale. Elle a été conçue à l'aide de l'application SceneBuilder.
        
* Ressources

Un fichier ressource à la racine du projet contient les différentes
icones utilisées dans l'application. (Des images au format PNG
permettant l'affichage des pigeons, de la nourriture, du boutton
de démarrage et d'arrêt, etc.)

* Démarrage de la simulation

Une fois la fenêtre ouverte, il suffit d'appuyer sur le boutton
``Démarrer`` de la barre d'outils. Les pigeons apparaissent et
de la nourriture également.

Il est ensuite possible de simuler un abboiement pour effrayer
les pigeons (Bien qu'il y ait une probabilité qu'un abboiement apparaisse
de lui même tous les 50 tours de jeu) mais également de faire
apparaitre de la nourriture en cliquant sur le plateau de jeu.

Il est possible de configurer la simulation en modifiant les paramètres
sur la gauche. Une mauvaise saisie des données provoquera une exception.
Pour mettre à jour les nouveaux paramètres, il est nécessaire de
redémarrer la simulation.

* Déroulement du programme

Lorsque le joueur appuie sur le bouton ``Démarrer``, le programme
exécute une méthode qui permet de démarrer ou d'arrêter la simulation.

Lorsqu'elle démarre, un objet ``Simulation`` est créé. Les pigeons
sont placés aléatoirement sur le plateau en respectant le nombre
précisé dans les configurations, et leur thread est lancé. De la
nourriture est également placée aléatoirement. Une fois fait, le
processus principal s'exécute.

Lors du processus principal, trois étapes sont exécutées :

1. On actualise la liste de nourritures dans la simulation.
Pour se faire, la simulation va comparer la liste de nourriture
avec celle de tous les pigeons ; Si un pigeon a mangé de la nourriture,
la simulation actualise sa propre liste avec celle du pigeon.

2. On exécute une probabilité d'apparition d'un abboiement. Cette
probabilité n'est exécutée qu'une fois toutes les 50 frames.

3. On raffraichit l'affichage. Comme les pigeons se déplacent
tous seuls, on récupère leur position à chaque frame pour
raffraichir leur position sur l'écran. Également, on en profite
pour ajouter ou supprimer de la nourriture à l'écran.

Lorsque le joueur clique sur le plateau de jeu, une méthode
est exécutée, visant à ajouter un objet ``Food`` à la liste
de nourriture de la simulation. La position de l'objet est
donnée par les coordonnées de la souris au moment du clic.

Lorsque le joueur clique sur le bouton "Abboyer" de la simulation,
la méthode simulant un abboiement est exécutée. Elle vise à
ordonner à tous les pigeons d'atteindre une position aléatoire
(dans une zone proche d'eux ; Un pigeon ne peut pas fuir jusqu'à
l'autre bout du plateau) et de ne pas chercher ni manger de nourriture
tant qu'ils n'ont pas atteint cette position. Il est possible
d'exécuter plusieurs abboiement en même temps ; C'est le dernier
qui prend la main. (Les pigeons oublient leur destination
forcée précédente, et se déplacent uniquement vers la dernière
donnée)