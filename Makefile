.PHONY: build delete run
DOCKER_REGISTRY?=396648463862.dkr.ecr.eu-west-1.amazonaws.com
TAG?=latest

PROJECT=backend
PROJECT_LATEST=$(PROJECT):latest
PROJECT_FINAL_IMG=$(PROJECT):$(TAG)
PROJECT_FUNCTIONAL=$(PROJECT)-functional-tests:$(TAG)
COMPOSE=docker-compose -f docker/compilation.yml -f docker/environment.yml
DEPLOY_COMPOSE=$(COMPOSE) -f docker/deploy.yml

build:
	echo "CREATING CACHE DIRECTORY"
	mkdir -p ~/.m2cache/$(PROJECT)
	rm -fr .m2cache
	cp -r ~/.m2cache/$(PROJECT) .m2cache
	chmod -R 777 .m2cache
	IMAGENAME=$(PROJECT_LATEST)-compilation $(COMPOSE) down -v || true
	IMAGENAME=$(PROJECT_LATEST)-compilation $(COMPOSE) build
	IMAGENAME=$(PROJECT_LATEST)-compilation $(COMPOSE) up -d database
	IMAGENAME=$(PROJECT_LATEST)-compilation $(COMPOSE) up -d elasticsearch
	IMAGENAME=$(PROJECT_LATEST)-compilation $(COMPOSE) up backend21
	IMAGENAME=$(PROJECT_LATEST)-compilation ./docker/checkContainersAlive.sh
	echo "SAVING M2 CACHE"
	bash docker/moveM2Cache.sh "$(PROJECT_LATEST)-compilation" ~/.m2cache/$(PROJECT)
	IMAGENAME=$(PROJECT_LATEST)-compilation $(COMPOSE) down -v || true
	docker build --build-arg BASEIMAGE=$(PROJECT_LATEST)-compilation -f docker/Dockerfile.release -t $(PROJECT_FINAL_IMG) .
	#docker build -f docker/Dockerfile.functional -t $(PROJECT_FUNCTIONAL) .
#	docker push $(PROJECT_FINAL_IMG)
#	docker push $(PROJECT_FUNCTIONAL)
deploy: kill-all build
	IMAGENAME=$(PROJECT_LATEST) $(DEPLOY_COMPOSE) up -d
kill-all:
	IMAGENAME=$(PROJECT_LATEST) $(COMPOSE) down -v || true
	IMAGENAME=$(PROJECT_LATEST) $(COMPOSE) kill || true
delete:
	docker rmi $(PROJECT_FINAL_IMG)
	docker rmi $(PROJECT_FUNCTIONAL)
devel-env:
	docker-compose -f docker/environment.yml -f docker/environment-ports.yml up -d
devel-env-volumes:
	docker-compose -f docker/environment.yml -f docker/environment-ports.yml -f docker/environment-volumes.yml up -d
