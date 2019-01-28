def call(Map pipelineParams) {

    pipeline {
        agent any
        environment { 
            APP_NAME = "${pipelineParams.appName}"
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
                steps{
                cd(apiUrl: pipelineParams.devApiUrl, org: pipelineParams.devOrg, space: pipelineParams.devSpace,
                    credsKey: pipelineParams.devCredsKey, envFile: pipelineParams.devEnvFile, domain: pipelineParams.devDomain)
                }
            }
            // stage('QA - CD'){
            //     steps{
            //     cd(apiUrl: pipelineParams.qaApiUrl, org: pipelineParams.qaOrg, space: pipelineParams.qaSpace,
            //         credsKey: pipelineParams.qaCredsKey, envFile: pipelineParams.qaEnvFile, domain: pipelineParams.qaDomain)
            //     }
            // }
            //  stage('Prod - CD'){
            //      steps{
            //     cd(apiUrl: pipelineParams.prodApiUrl, org: pipelineParams.prodOrg, space: pipelineParams.prodSpace,
            //         credsKey: pipelineParams.prodCredsKey, envFile: pipelineParams.prodEnvFile, domain: pipelineParams.prodDomain)
            //      }
            // }
            
        }
    }
}