stages {
    stage('Checkout Pipeline Repo') {
        steps {
            git branch: 'TestDevOps',
            credentialsId: 'limxuanhui',
            url: 'https://github.com/BlueXTech/pipeline.git'
        }
    }

    stage('Execute Pipeline') {
        steps {
            load 'Jenkinsfile'
        }
    }
}