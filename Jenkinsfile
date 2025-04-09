pipeline {
    agent any

    stages {
        stage('Limpiar Workspace') {
            steps {
                echo 'Eliminando archivos del workspace...'
                deleteDir() // Borra todo el contenido del workspace
            }
        }

        stage('Clonar Proyecto desde Repositorio Local') {
            steps {
                echo 'Clonando proyecto desde ruta local con git...'
                // Clona el repositorio en el workspace de Jenkins
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
                echo 'Ejecutando pruebas unitarias con Maven...'
                // Ejecuta las pruebas unitarias con Maven
                sh '''
                    cd PupaSv
                    mvn test
                '''
            }
        }

        stage('Ejecutar Pruebas de Integración') {
            steps {
                echo 'Ejecutando pruebas de integración con Maven...'
                // Ejecuta las pruebas de integración con Maven
                sh '''
                    cd PupaSv
                    mvn -P integracionpg verify
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
