#!/bin/bash
FEDORA_USER="fedoraAdmin"
FEDORA_PASSWORD="fedoraAdmin"
${FEDORA_HOME}/client/bin/fedora-ingest.sh  d . info:fedora/fedora-system:FOXML-1.1 o localhost:8082 ${FEDORA_USER} ${FEDORA_PASSWORD} http
