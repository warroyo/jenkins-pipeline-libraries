def call(Map body) {
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        agent any
        environment { 
            APP_NAME = pipelineParams.appName
        }
        stages {
            stage('CI') {
                stages {
                    stage('Build') {
                        environment {
                            HOME = '/tmp'
                        } 
                        agent {
                            docker { image 'microsoft/dotnet:2.2-sdk' }
                        }
                        steps {
                            echo 'Building..'
                            sh script:'dotnet build --configuration Release', label: 'build app'
                            sh script: 'dotnet publish --configuration Release --output artifact', label: 'publish artifact'
                            sh script: 'cp *.yml artifact/.', label: 'copy manifests to artifact'
                            sh script: 'cp -r scripts artifact/.', label: 'copy scripts to artifact'
                            archiveArtifacts artifacts: 'artifact/*'
                        }
                    }
                    stage('Test') {
                        steps {
                            echo 'Testing..'
                            sh 'ls -l'
                        }
                    }
                }
            }
            stage('Dev - CD'){
                cd(apiUrl: pipelineParams.devApiUrl, org: pipelineParams.devOrg, space: pipelineParams.devSpace,
                    credsKey: pipelineParams.devCredsKey, envFile: pipelineParams.devEnvFile, domain: pipelineParams.devDomain)
            }
            stage('QA - CD'){
                cd()
            }
             stage('Prod - CD'){
                cd()
            }
            
        }
    }
}