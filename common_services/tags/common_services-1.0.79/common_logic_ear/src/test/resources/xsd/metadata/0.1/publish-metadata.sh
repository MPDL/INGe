#! /bin/sh
#####  Author: Natasa Bulatovic,24.06.2010
#####  NOTE: this file is only per metadata version profile to avoid unintended overwriting
#####
#####  STEP 1
#####
#####   The commands in the next 2 lines need to be used only when first-time check-out of the metadata profile
#####     mkdir /srv/metadata/0.1
#####     svn co https://zim02.gwdg.de/repos/common/trunk/common_services/common_logic/src/main/resources/xsd/metadata/0.1
#####
#####
#####
#####  STEP 2 
#####  use when publishing the metadata changes
#####  executes another shell-script. Both can be used independently depending on  
#####  what needs to be done. 
#####
#####
echo "Updating from SVN"
svn up
echo "Preparing directories to replace relative paths..."
mkdir -p replacements
mkdir -p replacements/backup
echo "Copying to replacement directory"
cp *.xsd /srv/metadata/0.1/replacements/.
echo "Replacing paths..."
./replace_paths.sh
echo "Moving to web-space ...."
cp ./replacements/*.xsd /srv/www/metadata/escidoc/metadata/schemas/0.1/.
echo "Finished...- make sure you test the profiles with real metadata records"
