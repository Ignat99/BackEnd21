INSIKT backend project

build: 
make	
	make build

start development environment(mysql, elasticsearch, etc): 
	
	make devel-env

start development environment with volumes (mysql, elasticsearch, etc): 
	
	make devel-env-volumes

deploy on local(first it does a make build, then starts everything): 
	
	make deploy


Para arrancar en local:

	Con ide: darle a play a SpringEntryPoint, configurando en el IDE las variables de entorno
			(las puedes ver en docker/deploy.yml, añadiendo en /etc/hosts elastictest.service apuntando a localhost)
			
	Sin ide: ./gradlew bootRun   las variables de entorno se recogen automáticamente de build.gradle bootRun
	
	OJO! hay que configurar las mismas variables de entorno en el build.gradle en la sección bootRun y en docker/deploy.yml, de lo contrario cuando alguien intente arrancarlo de la otra forma no le irá porque le faltarán variables de entorno.
	Aunque al arrancar desde el ide no se utiliza docker/deploy.yml, este yml es la guia de variables de entorno necesarias para arrancar la app, y es utilizada en make deploy, así que hay que mantenerlo.
