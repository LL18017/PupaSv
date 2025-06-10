pipeline {
    agent any

    tools {
        maven 'mvn 3.8.7'
    }

    environment {
        PROJECT_ROOT = 'PupaSv'
        BACKEND_PROD_IMAGE = 'backend-pupa'
         BACKEND_CONTAINER = 'backend-pupa-container'
    }


    stages {
        stage('Limpiar Workspace') {
            steps {
                echo 'Eliminando archivos del workspace...'
                deleteDir()
            }
        }

        stage('Clonar Proyecto desde Repositorio Local') {
            steps {
                echo 'Clonando proyecto desde ruta local con git'
                sh 'git clone /usr/local/proyectos/PupaSv'
            }
        }

        stage('Verificar contenido clonado') {
            steps {
                echo 'Mostrando contenido del repositorio clonado...'
                sh 'ls -la ${PROJECT_ROOT}'
            }
        }

        stage('Compilar Proyecto') {
            steps {
                sh '''
                 cd ${PROJECT_ROOT}
                mvn clean compile
                '''
            }
        }



        stage('Ejecutar Pruebas Unitarias') {
            steps {
                echo 'Ejecutando pruebas unitarias con Maven'
                sh """
                    cd ${PROJECT_ROOT}
                    mvn test
                """
            }
        }

        stage('Ejecutar Pruebas de Integración') {
            steps {
                echo 'Ejecutando pruebas de integración con Maven'
                sh """
                    cd ${PROJECT_ROOT}
                    mvn -P integracionpg verify
                """
            }
        }

        stage('Ejecutar Pruebas de Sistema') {
            steps {
                echo 'Ejecutando pruebas de sistema con Maven'
                sh """
                    cd ${PROJECT_ROOT}
                    mvn -P sistemaspg verify
                """
            }
        }
    }


   post {
       success {
         archiveArtifacts artifacts: 'PupaSv/target/**.war', fingerprint: true

         sh '''
           if docker images --format '{{.Repository}}:{{.Tag}}' | grep -q "^$BACKEND_PROD_IMAGE:"; then
               echo "La imagen $BACKEND_PROD_IMAGE existe. Eliminando..."
               docker rmi -f $(docker images -q $BACKEND_PROD_IMAGE)
           else
               echo "La imagen $BACKEND_PROD_IMAGE no existe. Nada que eliminar."
           fi
         '''

         dir("${PROJECT_ROOT}") {
           // Construir imagen para producción
           sh 'docker build -t $BACKEND_PROD_IMAGE .'

           // Eliminar contenedor anterior si existe
           sh '''
             if [ "$(docker ps -aq -f name=$BACKEND_CONTAINER)" ]; then
               docker rm -f $BACKEND_CONTAINER
             fi
           '''

           // Ejecutar contenedor
           sh 'docker run -d --name $BACKEND_CONTAINER -p 9080:9080 -e DB_HOST=db16 -e DB_NAME=TipicoSV -e DB_PORT=5432 -e DB_USER=postgres -e DB_PASSWORD=12345 --network tpi_pupa_network $BACKEND_PROD_IMAGE'
         }
       }
     }
}
