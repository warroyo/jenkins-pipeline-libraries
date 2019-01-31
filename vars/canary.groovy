def call(def cdParams, def percent) {

    contents = readYaml (file: "${cdParams.envFile}") 
    env.ROUTE = contents.domain.toString()
    env.API_URL = "${cdParams.apiUrl}"
    env.ORG = "${cdParams.org}"
    env.SPACE = "${cdParams.space}"
    env.HOME = "/tmp"
    env.ENV_FILE = "${cdParams.envFile}"


    contents = readYaml (file: "${cdParams.envFile}") 
    instances = contents.instances.toString()
    total = 2 * instances
    decimal = percent / 100
    scale = Math.round(total * percent)
    echo "${scale}"
    env.SCALE = scale

    def login = libraryResource "com/warroyo/pipeline/scripts/cflogin.sh"
    writeFile file: "cflogin.sh", text: login
    sh "chmod +x cflogin.sh"
    withCredentials([usernamePassword(credentialsId: cdParams.credsKey, passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        sh script: './cflogin.sh', label: 'login to pcf'
    }


    echo "increasing to ${percent}%...."
    def flip = libraryResource "com/warroyo/pipeline/scripts/canary.sh"
    writeFile file: "canary.sh", text: flip
    sh "chmod +x canary.sh"
    sh script: './canary.sh', label: 'increasing traffic'
}