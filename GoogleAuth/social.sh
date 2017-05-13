#!/bin/sh

DATABASE=/tmp/social.db
SCRIPT_DIR=`dirname $0`
DESTINATION=$SCRIPT_DIR/app/src/main/assets/databases/social.db.gz

rm -f $DATABASE
echo ".read social.sql" | sqlite3 -echo $DATABASE 
gzip -vc $DATABASE > $DESTINATION

