def call(def cdParams) {

    contents = readYaml (file: "${cdParams.envFile}") 
    domain = contents.domain.toString()
    env.ROUTE = "https://${env.APP_NAME}.${domain}"
    echo env.ROUTE

    sh script: 'ls -l'
    sh script: 'which inspec', label: 'running smoke tests'
    sh script: 'inspec exec smoke', label: 'running smoke tests'
}