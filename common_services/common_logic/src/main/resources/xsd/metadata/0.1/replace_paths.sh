#!/bin/bash
OLD="..\/..\/metadata\/0.1\/"
NEW="http:\/\/metadata.mpdl.mpg.de\/escidoc\/metadata\/schemas\/0.1\/"
DPATH="./replacements/*.xsd"
BPATH="./replacements/backup"
TFILE="/tmp/out.tmp.$$"
[ ! -d $BPATH ] && mkdir -p $BPATH || :
for f in $DPATH
do
  if [ -f $f -a -r $f ]; then
    /bin/cp -f $f $BPATH
   sed "s/$OLD/$NEW/g" "$f" > $TFILE && mv $TFILE "$f"
  else
   echo "Error: Cannot read $f"
  fi
done
