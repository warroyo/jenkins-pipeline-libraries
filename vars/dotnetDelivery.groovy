def call(Closure body) {
    def pipelineParams = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams

    body()

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
                agent {
                    docker { image 'nulldriver/cf-cli-resource' }
                }
                stages{
                    stage('Deploy Venerable') {
                        options {
                            skipDefaultCheckout true
                        }
                        steps {
                            deployVenerable{
                                cdParams = pipelineParams.dev
                            }
                        }
                    }
                    stage('Smoke Test') {
                        options {
                            skipDefaultCheckout true
                        }
                        steps {
                            echo 'Running tests'
                        }
                    }
                    stage('Flip') {
                        options {
                            skipDefaultCheckout true
                        }
                        steps {
                           flip{
                               cdParams = pipelineParams.dev
                           }
                        }
                    }
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