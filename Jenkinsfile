pipeline {
    // Stage to checkout the pipeline repository
//     stage('Checkout Pipeline Repo') {
//         git branch: 'TestDevOps',
//             credentialsId: 'limxuanhui',
//             url: 'https://github.com/BlueXTech/pipeline.git'
//     }

//     // Load the Jenkinsfile from the pipeline repository and execute it
//     stage('Execute Pipeline') {
//         load 'Jenkinsfile'
//     }
    stages {
        stage('Trigger CI/CD Pipeline') {
            build job: 'test-pipeline'
        }
    }
}