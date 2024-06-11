node {
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
         steps {
            load 'Jenkinsfile'
        }
    }
}