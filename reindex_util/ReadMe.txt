VOR BUILD-PROZESS:
Bitte prüfen, ob in /src/main/resources die pubman.properties die neueste Kopie von pubman_ear/src/main/resources/pubman.properties ist !!!!
Bitte prüfen, ob in pom.xml project.parent.version die identische Version aus INGE pom.xml steht !!! 

ANLEITUNG:
- Index xy anlegen mit Settings und Mapping

/home/przibylla/reindex bzw. /home/mpdl/migration_gfz: (GFZ)
- kopiere reindex.jar
- kopiere pubman.properties

- Server anhalten: service inge stop bzw. sudo systemctl stop wildfly.service (GFZ)

- Anzahl Datensätze checken: mit Postman (OK)
    GET https://pure.mpg.de/es/_cat/indices
    GET https://pure.mpg.de/es/items/_search?q=*&size=1&_source=id
    
    bzw. (GFZ)
    
    GET https://gfzpublic.gfz-potsdam.de/es/_cat/indices
    GET https://gfzpublic.gfz-potsdam.de/es/items/_search?q=*&size=1&_source=id
    
- Löschen KahaDB: rm -rf /srv/web/inge/wildfly/standalone/data/activemq/localhost/KahaDB
                  ll /srv/web/inge/wildfly/standalone/data/activemq/localhost
                  
                  bzw. (GFZ)
                  
                  rm -rf /opt/wildfly/standalone/data/activemq/localhost/KahaDB
                  ll /opt/wildfly/standalone/data/activemq/localhost

- Indizes prüfen: mit Postman
    GET https://pure.mpg.de/es/_aliases
    
    bzw. (GFZ)
    
    GET https://gfzpublic.gfz-potsdam.de/es/_aliases

- Neuaufbau Index:
  sudo -i
  screen (screen -ls zeigt aktuelle Screens an)
  cd /home/przibylla/reindex bzw. /home/mpdl/migration_gfz: (GFZ)
  alte reindex.log löschen
  
  java -jar -Djboss.home.dir=/srv/web/inge/wildfly reindex.jar items_reindex (ctxs_reindex, items_reindex, ous_reindex, users_reindex)
  bzw.
  java -jar -Djboss.home.dir=/srv/web/inge/wildfly reindex.jar single_reindex item_xxxx
  
  bzw. (GFZ)
  
  java -jar -Djboss.home.dir=/opt/wildfly reindex.jar items_reindex (ctxs_reindex, items_reindex, ous_reindex, users_reindex)
  bzw.
  java -jar -Djboss.home.dir=/opt/wildfly reindex.jar single_reindex item_xxxx
  
  CTRL+A D trennt Verbindung zur aktuellen Sitzung (screen -r nimmt Sitzung wieder auf)
 
- Protokoll checken (reindex.log)
- Protokoll sichern (reindex.sic)

- Anzahl Datensätze checken: mit Postman
    GET https://pure.mpg.de/es/_cat/indices
    GET https://pure.mpg.de/es/items/_search?q=*&size=1&_source=id

    bzw. (GFZ)
    
    GET https://gfzpublic.gfz-potsdam.de/es/_cat/indices
    GET https://gfzpublic.gfz-potsdam.de/es/items/_search?q=*&size=1&_source=id
    
- Löschen KahaDB: rm -rf /srv/web/inge/wildfly/standalone/data/activemq/localhost/KahaDB
                  ll /srv/web/inge/wildfly/standalone/data/activemq/localhost
                  
                  bzw. (GFZ)
                  
                  rm -rf /opt/wildfly/standalone/data/activemq/localhost/KahaDB
                  ll /opt/wildfly/standalone/data/activemq/localhost
                  
- Server neustarten: service inge start bzw. sudo systemctl start wildfly.service (GFZ) 