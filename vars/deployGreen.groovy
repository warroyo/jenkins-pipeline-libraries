def call(def cdParams) {

    env.API_URL = "${cdParams.apiUrl}"
    env.ORG = "${cdParams.org}"
    env.SPACE = "${cdParams.space}"
    env.HOME = "${env.WORKSPACE}"
    env.ENV_FILE = "${cdParams.envFile}"

        echo 'Deploying....'
        copyArtifacts(projectName: "${env.JOB_NAME}", buildNumber: "${BUILD_NUMBER}"");

        def login = libraryResource "com/warroyo/pipeline/scripts/cflogin.sh"
        writeFile file: "cflogin.sh", text: login
        sh "chmod +x cflogin.sh"
        withCredentials([usernamePassword(credentialsId: cdParams.credsKey, passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            sh script: './cflogin.sh', label: 'login to pcf'
        }

        def deploy = libraryResource "com/warroyo/pipeline/scripts/deploy-green.sh"
        writeFile file: "deploy-green.sh", text: deploy
        sh "chmod +x deploy-green.sh"
        sh script: 'cd artifact && ../deploy-green.sh', label: 'push app'
}

