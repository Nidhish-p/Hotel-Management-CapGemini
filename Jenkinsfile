pipeline {
    agent any

    environment {
        DB_URL = "jdbc:mysql://10.103.27.106:3306/dummy_hotel"
        DB_USERNAME = "groupuser"
        DB_PASSWORD = "Strong@123"
    }


    stages {

        stage('Build') {
            steps {
                bat 'mvn clean install'
            }
        }

        stage('Run App') {
            steps {
                bat 'java -jar target/*.jar'
            }
        }
    }
}
