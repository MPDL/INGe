PubMan is a web-based solution for the management of publication data for research organisations. 
It is based on a service-oriented architecture (escidoc.org) and comprises several web-services.

PubMan Requirements:
- Java JRE 1.6
- eSciDoc coreservice Server 1.1
- JBoss 4.2.2 (included with this installer)
- Ingested PubMan content model 'escidoc:persistent4' (see description below to get this done)

Ingest needed PubMan content model into coreservice Server

- Copy the file 'escidoc_persistent4.xml' to the coreservice server. You can find
  this file in the root of the PubMan installation path. 
- Login to coreservice instance
- Ingest the the content model with this command into the fedora system:
  ${FEDORA_HOME}/client/bin/fedora-ingest.sh  f escidoc_persistent4.xml info:fedora/fedora-system:FOXML-1.1 localhost:8082 ${FEDORA_USER} ${FEDORA_PASSWORD} http ""
  
  
An overview of current functionalities can be found here:
http://colab.mpdl.mpg.de/mediawiki/PubMan_Functionalities

An overview of services in use can be found here:
http://colab.mpdl.mpg.de/mediawiki/ESciDoc_SOA

For questions on installation and set-up, please subscribe to the developers list here: 
https://listserv.gwdg.de/mailman/listinfo/escidoc-dev-ext

In case you have functional questions, please contact pubman-support@gwdg.de
