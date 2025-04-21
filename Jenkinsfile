// docker run -d --name jenkins -p 9080:8080 -p 50000:50000 -v jenkins_home:/var/jenkins_home -v /usr/local/proyectos/PupaSv:/usr/local/proyectos/PupaSv -e JAVA_OPTS="-Dhudson.plugins.git.GitSCM.ALLOW_LOCAL_CHECKOUT=true" -v /var/run/docker.sock:/var/run/docker.sock -v /usr/bin/docker:/usr/bin/docker  jenkins/jenkins:lts

pipeline {
    agent any
	 tools {
        maven 'mvn 3.8.7'
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
                sh '''
                    git clone /usr/local/proyectos/PupaSv
                '''
            }
        }

        stage('Verificar contenido clonado') {
            steps {
                echo 'Mostrando contenido del repositorio clonado...'
                sh 'ls -la'
            }
        }

        stage('Ejecutar Pruebas Unitarias') {
            steps {
                echo 'Ejecutando pruebas unitarias con Maven'
                sh '''
                    cd PupaSv
                    mvn test
                '''
            }
        }

        stage('Ejecutar Pruebas de Integración') {
            steps {
                echo 'Ejecutando pruebas de integración con Maven'
                sh '''
                    cd PupaSv
                    mvn -P integracionpg verify
                '''
            }
        }
         stage('Ejecutar Pruebas de Sistema') {
                    steps {
                        echo 'Ejecutando pruebas de sistemas con Maven'
                        sh '''
                            cd PupaSv
                            mvn -P sistemaspg verify
                        '''
                    }
                }
    }

    post {
        always {
            echo 'forzando push 2'
        }
    }
}
