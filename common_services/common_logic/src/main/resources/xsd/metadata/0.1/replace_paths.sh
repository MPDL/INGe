#!/bin/bash
####    Author: Natasa Bulatovic, 24.06.2010
####    Has defined old and new string. This script shall be run in the current directory
####    where the schemas for the metadata profile are checked-out
####    it creates new directory for the replaced files and a new subdirectory for the original files (just in case)
####    both "replacement" and "backup" directory may be removed.
####    This script may be called independently or by publish_metadata.sh
####    moving of the metadata profiles is done in publish_metadata.sh
####
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
