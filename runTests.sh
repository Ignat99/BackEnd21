#!/usr/bin/env bash

set -ex

export SQL_HOST='46.148.230.53'
export DB_URL='jdbc:mysql://46.148.230.53:3306/backend21'
export DB_USER='test'
export DB_PASS=''
#export DB_PASS=''
export DB_PORT=3306
export ELASTIC_HOST='172.18.0.2'
export ELASTIC_PORT=9300
export ELASTIC_INDEX='demo'
export ELASTIC_INDEX_TYPE='tweet'
export EMAIL_HOST='smtp.gmail.com'
export EMAIL_PORT=587
export EMAIL_USERNAME='ignat99@gmail.com'
export EMAIL_PASSWORD=''

while ! mysqladmin ping -h "$SQL_HOST" -u"$DB_USER" -p"$DB_PASS"  --silent; do
    sleep 1
done

#./start.sh 


./gradlew test
