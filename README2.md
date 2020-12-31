Complilar backend en caso de que jenkins no funcione

JENKINSSS FIRST

(Ubicarse en el codigo de backend dentro del servidor de jenkins con la ultima version de codigo y correr los siguientes comandos)

IMAGENAME=396648463862.dkr.ecr.eu-west-1.amazonaws.com/backend21:latest-compilation docker-compose -f docker/compilation.yml -f docker/environment.yml up backend21

bash docker/moveM2Cache.sh "396648463862.dkr.ecr.eu-west-1.amazonaws.com/backend21:latest-compilation" ~/.m2cache/backend21

docker build --build-arg BASEIMAGE=396648463862.dkr.ecr.eu-west-1.amazonaws.com/backend21:latest-compilation -f docker/Dockerfile.release -t 396648463862.dkr.ecr.eu-west-1.amazonaws.com/backend21:print .
