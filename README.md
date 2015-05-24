# MiniTwitter - Auteurs : Marc Karassev - Quentin Cornevin
------------------------------------------------------------------------------------------------------------------------
Aide pour le lancement du projet :
    - vous devez lancer ActiveMQ
    - lancer un rmiRegistry sur le port 2001 dans le dossier : out/production/Server/
    - Aucune configuration de la VM n'est nécessaire
------------------------------------------------------------------------------------------------------------------------
Ce projet propose les fonctionnalités suivantes :
    - Se connecter
    - Poster un tweet
    - Suivre un "HashTag"
    - Visualiser sa TimeLine c'est à dire l'ensemble des tweets récents des HashTags que vous suivez
    - Retweeter des tweets de votre timeLine
    - Être notifié des tweets reçus, même ceux reçus pendant une période de déconnexion
    - Présence d'un topic permettant la notification de nouveaux topics

Ce projet n'a pas abordé les aspects suivants :
    - persistance des topics et des utilisateurs, les messages ne survivent donc pas à un crash du serveur
------------------------------------------------------------------------------------------------------------------------
Le projet se coupe en deux parties, le serveur et les clients :

Du coté serveur, nous avons mis en place un système de connexion qui, en cas de succès, retourne un stub de la "vraie"
interface du MiniTwitter à des fins de sécurisation. Cette interface permet de récupèrer les hashtags suivis par
les utilisateurs ou connus du système et de les mettre à jour. Après la connexion, si l'utilisateur est connu du
système, ses hash tags suivis sont récupérés. Si l'utilisateur se connecte pour la première fois, alors un compte est
créé avec des souscriptions aux hash tags par défaut que sont #HelloWorld et #NewTopics.
Le serveur est lui-même un consommateur du topic JMS #NewTopics afin d'être notifié de la création d'un topic et de
l'ajouter aux topics suivis par le système.
La classe Serveur correspond à l'initialisation du serveur RMI tandis que les interfaces MiniTwitterConnection et
MiniTwitter correspondent respectivement à l'interface de connexion au système et au MiniTwitter distant.

Les clients, une fois connectés, communiquent directement avec les topics JMS. Nous avons fait ce choix afin de ne pas
empêcher les clients déjà connectés de communiquer en cas de crash du serveur RMI. Cela implique aussi un gain de
performance en comparaison avec une communication centralisée par le serveur RMI pour l'envoi de message dans ActiveMQ.
La deuxième partie est la partie Client, qui elle communique avec les Topic JMS.
La classe Client correspond à l'initialisation de la communication avec le serveur RMI et à la démonstration, tandis que
la classe MiniTwitterClient communique avec ActiveMQ.
La gestion des hashtag est faite par les Topic JMS. En effet, un client lorsqu'il suit un HashTag devient subscriber
et publisher de ce hashtag. Il peut donc voir ce que les autres utilisateur postent sur ce hashtag et lui-même publier
dessus.
------------------------------------------------------------------------------------------------------------------------
Nous n'avons pas de grande difficulté à signaler, à part peut-être le problème suivant :
Nous avions implémenté le suivi de la part du serveur des topics souscrits par les utilisateurs, puis nous avons rajouté
l'interface de connexion. Nous n'exportions plus que l'interface de connexion et renvoyons donc un objet MiniTwitter au
lieu d'un stub à la connexion. Donc toutes les mises à jour de topics étaient faites en local. Nous avons mis un peu de
temps à comprendre le problème.

