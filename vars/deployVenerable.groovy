def call(def cdParams) {

    env.API_URL = "${cdParams.apiUrl}"
    env.ORG = "${cdParams.org}"
    env.SPACE = "${cdParams.space}"
    env.ENV_CREDS = credentials("${cdParams.credsKey}")
    env.USERNAME = "${env.ENV_CREDS_USR}"
    env.PASSWORD = "${env.ENV_CREDS_PSW}"
    env.HOME = "${env.WORKSPACE}"
    env.ENV_FILE = "${cdParams.envFile}"


    node{
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

