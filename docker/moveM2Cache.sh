#!/usr/bin/env bash

set -e
set -x

IMAGE=$1
DESTINATION=$2
CONTAINERID=$(docker ps -a | grep $IMAGE | awk '{print $1}')
rm -fr $DESTINATION
docker cp $CONTAINERID:/root/.gradle/caches/modules-2/files-2.1 $DESTINATION