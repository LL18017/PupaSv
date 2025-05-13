pipeline {
    agent any

    tools {
        maven 'mvn 3.8.7'
        nodejs 'nodejs'
    }

    environment {
        PROJECT_ROOT = 'PupaSv' // Directorio clonado
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
           stage('Ejecutar Análisis SonarQube') {
                        environment {
                            scannerHome = tool name: 'sonar-scaner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                        }
                        steps {
                            dir("${PROJECT_ROOT}") {
                                withSonarQubeEnv('sonarqube') {
                                    sh '''
                                    mvn clean compile
                                    /var/jenkins_home/tools/hudson.plugins.sonar.SonarRunnerInstallation/sonar-scaner/bin/sonar-scanner \
                                    -Dsonar.projectKey=PupaSv \
                                    -Dsonar.projectName=PupaSv \
                                    -Dsonar.projectVersion=0.0.6 \
                                    -Dsonar.sources=src/main \
                                    -Dsonar.tests=src/test \
                                    -Dsonar.language=java \
                                    -Dsonar.java.binaries=target/classes
                                    '''
                                }
                            }
                            timeout(time: 3, unit: 'MINUTES') {
                                waitForQualityGate abortPipeline: true
                            }
                        }
                    }
    }


    post {
        always {
            echo 'Pipeline finalizado'
        }
    }
}
