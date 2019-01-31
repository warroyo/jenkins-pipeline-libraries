def call(def cdParams) {

    contents = readYaml (file: "${cdParams.envFile}") 
    env.ROUTE = contents.domain.toString()
    env.API_URL = "${cdParams.apiUrl}"
    env.ORG = "${cdParams.org}"
    env.SPACE = "${cdParams.space}"
    env.HOME = "/tmp"
    env.ENV_FILE = "${cdParams.envFile}"

    def login = libraryResource "com/warroyo/pipeline/scripts/cflogin.sh"
    writeFile file: "cflogin.sh", text: login
    sh "chmod +x cflogin.sh"
    withCredentials([usernamePassword(credentialsId: cdParams.credsKey, passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        sh script: './cflogin.sh', label: 'login to pcf'
    }


    echo 'Flipping Traffic....'
    def flip = libraryResource "com/warroyo/pipeline/scripts/flip.sh"
    writeFile file: "flip.sh", text: flip
    sh "chmod +x flip.sh"
    sh script: './flip.sh', label: 'flipping traffic'
}