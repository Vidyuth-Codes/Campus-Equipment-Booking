
pipeline {
    agent any

    tools {
        jdk 'java21'
        maven 'Maven396'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out Campus Equipment Booking project'
                checkout scm
            }
        }

        stage('Test') {
            steps {
                bat 'mvn clean test'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml',
                          allowEmptyResults: true
                }
            }
        }

        stage('Package') {
            steps {
                bat 'mvn package'
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true
            }
        }
    }
}
