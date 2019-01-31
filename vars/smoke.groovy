def call(def cdParams) {

    contents = readYaml (file: "${cdParams.envFile}") 
    echo contents.domain.toString()
    domain = contents.domain.toString()
    env.ROUTE = "${env.APP_NAME}.${domain}"

    sh script: 'inspec exec smoke/', label: 'running smoke tests'
}