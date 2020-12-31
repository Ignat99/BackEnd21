#!/usr/bin/env bash

set -ex

#export SQL_HOST='mysql.service'
#export DB_URL='jdbc:mysql://mysql.service:3306/backend21'
#export DB_USER='test'
#export DB_PASS='test'
#export DB_PASS='Qazwsx12#'
#export DB_PORT=3306
#export ELASTIC_HOST='elastictest.service'
#export ELASTIC_PORT=9300
#export ELASTIC_INDEX='demo'
#export ELASTIC_INDEX_TYPE='tweet'
#export EMAIL_HOST='smtp.gmail.com'
#export EMAIL_PORT=587
#export EMAIL_USERNAME='ignat99@gmail.com'
#export EMAIL_PASSWORD=''

while ! mysqladmin ping -h "$SQL_HOST"  --silent; do
    sleep 1
done

#./start.sh 


./gradlew test
