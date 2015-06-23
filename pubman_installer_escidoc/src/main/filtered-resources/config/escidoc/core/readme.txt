Ingesting eSciDoc Example Objects
=================================

After a successful installation of the eSciDoc Infrastructure, you will find the
examples in the folder

${INSTALL_PATH}${FILE_SEPARATOR}infrastructure${FILE_SEPARATOR}examples


We provide three sets of examples:

- Common:  generic examples, to be ingested via the fedora-ingest command-line 
           tool
           
- eSciDoc: generic examples, to be ingested via the ObjectManager API methods

- MPDL:    PubMan-specific examples, to be ingested via the fedora-ingest 
           command-line tool



Ingesting Common Examples
-------------------------
Caveat: if you intend to install the eSciDoc PubMan Solution, do not ingest the 
common examples, but the MPDL examples!

1) Open a shell or command window on the machine onto which the eSciDoc 
   Infrastructure has been installed. Make sure both the relational database 
   and the eSciDoc Infrastructure are running.

2) Fedora will need access to the internet, otherwise the ingestion of 
   component-escidoc_ex6.fo.xml will fail, resulting in a error message.

3) Set the two environment variables JAVA_HOME and FEODRA_HOME:
   
   Windows: 
   set JAVA_HOME=${JDKPath}
   set FEDORA_HOME=${INSTALL_PATH}\fedora
   
   Linux/Solaris/MacOS:
   JAVA_HOME=${JDKPath};export JAVA_HOME
   FEDORA_HOME=${INSTALL_PATH}/fedora;export FEDORA_HOME
   
4) Navigate in your shell/command window to the directory
   ${INSTALL_PATH}${FILE_SEPARATOR}infrastructure${FILE_SEPARATOR}examples${FILE_SEPARATOR}common
   
5) Execute the following command in your shell/command window:
   
   Windows:
   "%FEDORA_HOME%\client\bin\fedora-ingest.bat" d foxml1.1 info:fedora/fedora-system:FOXML-1.1 localhost:${EscidocPort} ${FedoraUsername} ${FedoraPassword} http ""
   Linux/Solaris/MacOS:
   $FEDORA_HOME/client/bin/fedora-ingest.sh d foxml1.1 info:fedora/fedora-system:FOXML-1.1 localhost:${EscidocPort} ${FedoraUsername} ${FedoraPassword} http ""


Ingesting eSciDoc Examples
--------------------------
- to be written -


Ingesting MPDL Examples
-------------------------
These examples are specifically tailored for the use with the eSciDoc PubMan 
Solution. More information about PubMan can be found on the web 
(http://www.escidoc.org/JSPWiki/PubMan)

1) Open a shell or command window on the machine onto which the eSciDoc 
   Infrastructure has been installed. Make sure both the relational database 
   and the eSciDoc Infrastructure are running.

2) Set the two environment variables JAVA_HOME and FEODRA_HOME:
   
   Windows: 
   set JAVA_HOME=${JDKPath}
   set FEDORA_HOME=${INSTALL_PATH}${FILE_SEPARATOR}fedora
   
   Linux/Solaris/MacOS:
   JAVA_HOME=${JDKPath};export JAVA_HOME
   FEDORA_HOME=${INSTALL_PATH}/fedora;export FEDORA_HOME
   
3) Navigate in your shell/command window to the directory
   ${INSTALL_PATH}${FILE_SEPARATOR}infrastructure${FILE_SEPARATOR}examples${FILE_SEPARATOR}mpdl
   
4) Execute the following command in your shell/command window:
   
   Windows:
   "%FEDORA_HOME%\client\bin\fedora-ingest.bat" d foxml1.1 info:fedora/fedora-system:FOXML-1.1 localhost:${EscidocPort} ${FedoraUsername} ${FedoraPassword} http ""
   
   Linux/Solaris/MacOS:
   $FEDORA_HOME/client/bin/fedora-ingest.sh d foxml1.1 info:fedora/fedora-system:FOXML-1.1 localhost:${EscidocPort} ${FedoraUsername} ${FedoraPassword} http ""

