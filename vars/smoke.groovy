def call(def cdParams) {

    contents = readYaml (file: "${cdParams.envFile}") 
    domain = contents.domain.toString()
    env.ROUTE = "https://${env.APP_NAME}.${domain}"
    env.HOME='/tmp'

    sh script: 'inspec exec tests/smoke', label: 'running smoke tests'
}