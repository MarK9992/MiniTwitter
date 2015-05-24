# MiniTwitter - Auteurs : Marc Karassev - Quentin Cornevin
-----------------------------------------------------------------------------------------------------------------------
Aide pour le lancement du projet :
    - vous devez lancer ActiveMQ
    - créer un rmiRegistry sur le port 2001 dans le fichier .class du serveur situé dans le dossier :
                out/production/Server/
    - Aucune configuration de la VM n'est nécessaire
-----------------------------------------------------------------------------------------------------------------------
Ce projet propose les fonctionnalités suivantes :
    - Poster un tweet
    - Suivre un "HashTag"
    - Visualiser sa TimeLine c'est à dire l'ensemble des tweets récents des HashTag que vous suivez.
    - Retweeter des tweets de votre timeLine.


Le projet se coupe en deux parties, le serveur et le client.
Du coté serveur, nous avons mis en place un système de connexion qui récupère les HashTag suivis par l'utilisateur
si celui-ci est connu du système. Si l'utilisateur se connecte pour la première fois alors il ajoute le compte.
Une fois le compte créé, le serveur sert uniquement a enregistrer les HashTag que l'utilisateur suit et les renvoie
lorsque l'utilisateur se connecte.

La deuxième partie est la partie Client, qui elle communique avec les Topic JMS.
A sa création un client ne suit aucun HashTag, il peut donc suivre ensuite des hashtag déjà existants ou alors lui même
en créer. Une fois que le client suit un hashtag, les tweets sur ce hashtag s'ajoutent automatiquement a sa TimeLine
et s'affichent uniquement si le client le demande. Le client peut aussi visualiser ses propres tweets et retweets.
Le système de retweet est fait pour etre utilisé avec la TimeLine. En effet, il faut préciser le numéro du tweet, dans
la TimeLine, que l'on souhaite retweeter.

La gestion des hashtag est faite par les Topic JMS. En effet, un client lorsqu'il suit un HashTag devient subscriber
et publisher de ce hashtag. Il peut donc voir ce que les autres utilisateur postent sur ce hashtag et lui même publier
dessus.

-----------------------------------------------------------------------------------------------------------------------

