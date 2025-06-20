pipeline {
    agent any

    tools {
        maven 'mvn 3.8.7'
    }

    environment {
        PROJECT_ROOT = 'PupaSv'
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
                sh "ls -la ${PROJECT_ROOT}"
            }
        }

        stage('Compilar Proyecto') {
            steps {
                sh """
                 cd ${PROJECT_ROOT}
                mvn clean compile
                """
            }
        }

        stage('Ejecutar Pruebas Unitarias') {
            steps {
                echo 'Ejecutando pruebas unitarias con Maven'
                sh """
                    cd ${PROJECT_ROOT
                }
                    mvn test
                """
            }
        }

        stage('Ejecutar Pruebas de Integración') {
            steps {
                echo 'Ejecutando pruebas de integración con Maven'
                sh """
                    cd ${PROJECT_ROOT
                }
                    mvn -P integracionpg verify
                """
            }
        }

        stage('Ejecutar Pruebas de Sistema') {
            steps {
                echo 'Ejecutando pruebas de sistema con Maven'
                sh """
                    cd ${PROJECT_ROOT
                }
                    mvn -P sistemaspg verify
                """
            }
        }
    }


  post {
  success {

    archiveArtifacts artifacts: 'PupaSv/target/**.war', fingerprint: true
        echo "Artefacto generado exitosamente"
        echo "Build #: ${pipelineNumber}"
        echo "Fecha de creación: ${fechaCreacion}"
    sh """
          if docker ps --filter "name=${env.BACKEND_CONTAINER}" --format '{{.Names}}' | grep -w ${env.BACKEND_CONTAINER}; then
            docker stop ${env.BACKEND_CONTAINER}
            docker rm ${env.BACKEND_CONTAINER}
          else
            echo "No hay contenedor ${env.BACKEND_CONTAINER} corriendo."
          fi
        """
     sh """
        if [ "\$(docker images -q ${env.BACKEND_PROD_IMAGE})" ]; then
        docker rmi -f ${env.BACKEND_PROD_IMAGE}
        fi
        """


    dir("${PROJECT_ROOT}") {
      // Construir imagen para producción
      sh """docker build -t ${env.BACKEND_PROD_IMAGE} ."""

    sh """
          echo "Ejecutando nuevo contenedor ${env.BACKEND_CONTAINER}..."
          docker run -d --name ${env.BACKEND_CONTAINER} \\
            -p 9080:9080 \\
            -e DB_HOST=${env.DB_HOST} \\
            -e DB_NAME=\"${env.DB_NAME}\" \\
            -e DB_PORT=${env.DB_PORT} \\
            -e DB_USER=${env.DB_USER} \\
            -e DB_PASSWORD=${env.DB_PASSWORD} \\
            --network ${env.pupa_Network} \\
            ${env.BACKEND_PROD_IMAGE}
        """


      // Disparar frontend
      build job: 'PupaFE', wait: false
    }
  }
}

}
