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
    }
}
