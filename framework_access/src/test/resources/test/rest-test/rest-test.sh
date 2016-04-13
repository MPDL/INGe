echo begin of script
echo ITEM RETRIEVAL curl -v -o output.txt http://coreservice.mpdl.mpg.de/ir/item/escidoc:52198
/usr/bin/time -p curl -v -o output.txt http://coreservice.mpdl.mpg.de/ir/item/escidoc:52198
echo     
echo CONTAINER RETRIEVAL curl -v -o output.txt http://coreservice.mpdl.mpg.de/ir/container/escidoc:117067 
/usr/bin/time -p curl -v -o output.txt http://coreservice.mpdl.mpg.de/ir/container/escidoc:117067 
echo     
echo  ORGANIZATIONAL UNIT RETRIEVAL curl -v -o output.txt http://coreservice.mpdl.mpg.de/oum/organizational-unit/escidoc:persistent13
/usr/bin/time -p curl -v -o output.txt http://coreservice.mpdl.mpg.de/oum/organizational-unit/escidoc:persistent13
echo SEARCH FULL TEXT FOR TERM "language" NOSORT NOLIMIT http://coreservice.mpdl.mpg.de/srw/search/escidoc_all?operation=searchRetrieve&query=escidoc.fulltext=language
/usr/bin/time -p curl -v -o output.txt "http://coreservice.mpdl.mpg.de/srw/search/escidoc_all?operation=searchRetrieve&query=escidoc.fulltext=language" 

echo SEARCH FULL TEXT FOR TERM "language" SORT DESC LAST MODIFICATION DATE  NOLIMIT http://coreservice.mpdl.mpg.de/srw/search/escidoc_all?operation=searchRetrieve&query=escidoc.fulltext=language&sortKeys=sort.escidoc.last-modification-date,,0
/usr/bin/time -p curl -v -o output.txt "http://coreservice.mpdl.mpg.de/srw/search/escidoc_all?operation=searchRetrieve&query=escidoc.fulltext=language&sortKeys=sort.escidoc.last-modification-date,,0"
echo     
echo SEARCH FULL TEXT FOR TERM "language" SORT DESC LAST MODIFICATION DATE LIMIT 25 http://coreservice.mpdl.mpg.de/srw/search/escidoc_all?operation=searchRetrieve&query=escidoc.fulltext=language&maximumRecords=25&sortKeys=sort.escidoc.last-modification-date,,0
/usr/bin/time -p curl -v -o output.txt "http://coreservice.mpdl.mpg.de/srw/search/escidoc_all?operation=searchRetrieve&query=escidoc.fulltext=language&maximumRecords=25&sortKeys=sort.escidoc.last-modification-date,,0"
echo   
echo SEARCH metadata FOR TERM "language" SORT DESC LAST MODIFICATION DATE  NOLIMIT http://coreservice.mpdl.mpg.de/srw/search/escidoc_all?operation=searchRetrieve&query=escidoc.metadata%3E%27%27&sortKeys=sort.escidoc.last-modification-date,,0
/usr/bin/time -p curl -v -o output.txt "http://coreservice.mpdl.mpg.de/srw/search/escidoc_all?operation=searchRetrieve&query=escidoc.metadata%3E%27%27&sortKeys=sort.escidoc.last-modification-date,,0"
echo  
echo SEARCH metadata FOR TERM "language" SORT DESC LAST MODIFICATION DATE LIMIT 25 http://coreservice.mpdl.mpg.de/srw/search/escidoc_all?operation=searchRetrieve&query=escidoc.metadata%3E%27%27&maximumRecords=25&sortKeys=sort.escidoc.last-modification-date,,0
/usr/bin/time -p curl -v -o output.txt "http://coreservice.mpdl.mpg.de/srw/search/escidoc_all?operation=searchRetrieve&query=escidoc.metadata%3E%27%27&maximumRecords=25&sortKeys=sort.escidoc.last-modification-date,,0"
echo
echo COMPONENT RETRIEVAL curl -v -o output.txt http://coreservice.mpdl.mpg.de/ir/item/escidoc:68872:5/components/component/escidoc:76881 
/usr/bin/time -p curl -v -o output.txt "http://coreservice.mpdl.mpg.de/ir/item/escidoc:68872:5/components/component/escidoc:76881"
echo
echo COMPONENT CONTENT RETRIEVAL curl -v -o somefile http://coreservice.mpdl.mpg.de/ir/item/escidoc:68872:5/components/component/escidoc:76881/content 
/usr/bin/time -p curl -v -o output.txt "http://coreservice.mpdl.mpg.de/ir/item/escidoc:68872:5/components/component/escidoc:76881/content"
echo end of script

echo filter-operation for non-loggedin user  curl -d @myitems.xml "http://coreservice.mpdl.mpg.de/ir/items/filter" -o output.txt
/usr/bin/time -p curl -v -d @myitems.xml "http://coreservice.mpdl.mpg.de/ir/items/filter" -o output.txt
