def call(Map cdParams) {
    agent {
        docker { image 'nulldriver/cf-cli-resource' }
    }
    environment { 
        API_URL = cdParams.apiUrl
        ORG = cdParams.org
        SPACE = cdParams.space
        ENV_CREDS = credentials(cdParams.credskey)
        USERNAME = "${env.ENV_CREDS_USR}"
        PASSWORD = "${env.ENV_CREDS_PSW}"
        HOME = "${env.WORKSPACE}"
        ENV_FILE = cParams.envFile
        ROUTE = cdParams.domain
    }
    stages{
        stage('Deploy Venerable') {
            options {
                skipDefaultCheckout true
            }
            steps {
                echo 'Deploying....'
                copyArtifacts(projectName: "${env.JOB_BASE_NAME}");

                def login = libraryResource "com/warroyo/pipeline/scripts/cflogin.sh"
                writeFile file: "cflogin.sh", text: login
                sh script: './cflogin.sh', label: 'login to pcf'

                def deploy = libraryResource "com/warroyo/pipeline/scripts/deploy-venerable.sh"
                writeFile file: "deploy-venerable.sh", text: deploy
                sh script: 'cd artifact && ../deploy-venerable.sh', label: 'push app'
            }
        }
        stage('Smoke Test') {
            options {
                skipDefaultCheckout true
            }
            steps {
                echo 'Running tests'
            }
        }
        stage('Flip') {
            options {
                skipDefaultCheckout true
            }
            steps {
                echo 'Flipping Traffic....'
                def flip = libraryResource "com/warroyo/pipeline/scripts/flip.sh"
                writeFile file: "flip.sh", text: flip
                sh script: './flip.sh', label: 'flipping traffic'
            }
        }
    }
}