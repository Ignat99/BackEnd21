#!/usr/bin/env bash

set -ex

docker-compose -f docker/compilation.yml -f docker/environment.yml ps -q | xargs docker inspect -f '{{ .State.ExitCode }}' | while read code; do
    if [ "$code" == "1" ]; then
       exit -1
    fi
done