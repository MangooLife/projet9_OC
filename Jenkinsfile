pipeline {
    agent any
    tools {
        maven 'Maven 3.6.1'
        jdk 'jdk'
    }
    stages {
        stage ('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
    }
}