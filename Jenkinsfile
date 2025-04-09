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
            echo 'Este bloque se ejecuta siempre al final, independientemente del resultado.'
        }
    }
}
