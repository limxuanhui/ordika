pipeline {
    agent any

    stages {
        // Stage to checkout the pipeline repository
        stage('Checkout Pipeline Repo') {
            steps {
                git branch: 'TestDevOps',
                credentialsId: 'limxuanhui',
                url: 'https://github.com/BlueXTech/pipeline.git'
            }
        }

        // Load the Jenkinsfile from the pipeline repository and execute it
        stage('Execute Pipeline') {
            //     // Optional: Specify a node with appropriate resources if needed
            //     node {
            //         label 'build-agent' // Replace with your agent label
            //     }
            steps {
                load 'pipeline.groovy'
            }
        }
    }
}