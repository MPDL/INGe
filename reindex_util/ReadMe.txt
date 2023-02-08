VOR BUILD-PROZESS:
Bitte prüfen, ob in /src/main/resources die pubman.properties die neueste Kopie von pubman_ear/src/main/resources/pubman.properties ist !!!!
Bitte prüfen, ob in pom.xml project.parent.version die identische Version aus INGE pom.xml steht !!! 

ANLEITUNG:
- neuen Index xy anlegen mit Settings und Mapping aus altem Index (mit Admintool)

/home/przibylla/reindex
- kopiere reindex.jar
- kopiere pubman.properties

- Server anhalten: service inge stop

- Anzahl Datensätze checken: mit Postman (OK)
    GET https://pure.mpg.de/es/_cat/indices
    GET https://pure.mpg.de/es/items/_search?q=*&size=1&_source=id
    
- Löschen KahaDB: rm -rf /srv/web/inge/wildfly/standalone/data/activemq/localhost/KahaDB
                  ll /srv/web/inge/wildfly/standalone/data/activemq/localhost
                  
- Indizes prüfen: mit Postman
    GET https://pure.mpg.de/es/_aliases
    
- Alias des alten Index löschen (mit Admintool)
- Alias zu neuem Index hinzufügen (mit Admintool)

- Neuaufbau Index:
  sudo -i
  screen (screen -ls zeigt aktuelle Screens an)
  cd /home/przibylla/reindex
  alte reindex.log löschen
  
  java -jar -Djboss.home.dir=/srv/web/inge/wildfly reindex.jar items_reindex (ctxs_reindex, items_reindex, ous_reindex, users_reindex)
  bzw.
  java -jar -Djboss.home.dir=/srv/web/inge/wildfly reindex.jar single_reindex item_xxxx
  
  CTRL+A D trennt Verbindung zur aktuellen Sitzung (screen -r nimmt Sitzung wieder auf)
 
- Protokoll checken (reindex.log)
- Protokoll sichern (reindex.sic)

- Anzahl Datensätze checken: mit Postman
    GET https://pure.mpg.de/es/_cat/indices
    GET https://pure.mpg.de/es/items/_search?q=*&size=1&_source=id

- Löschen KahaDB: rm -rf /srv/web/inge/wildfly/standalone/data/activemq/localhost/KahaDB
                  ll /srv/web/inge/wildfly/standalone/data/activemq/localhost
                  
- Server neustarten: service inge start 