def call(def cdParams) {

    contents = readYaml (file: "${cdParams.envFile}") 
    domain = contents.domain.toString()
    env.ROUTE = "${env.APP_NAME}.${domain}"

    sh script: 'ls -l'
    sh script: 'which inspec', label: 'running smoke tests'
}