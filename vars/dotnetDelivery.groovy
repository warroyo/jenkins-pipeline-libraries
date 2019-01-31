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
                    //archiveArtifacts artifacts: 'artifact/*'
                    stash name: "app", includes: "artifact/*"
                }
            }
            stage('Test') {
                steps {
                    echo 'Testing..'
                    sh 'ls -l'
                }
            }

            //dev env
            stage('Deploy Green - Dev') {
                when{
                    anyOf { 
                        buildingTag()
                        branch 'master'
                    }
                }
                agent {
                    docker { image 'nulldriver/cf-cli-resource' }
                }
                options {
                    skipDefaultCheckout true
                }
                steps {
                    deployGreen(pipelineParams.dev)
                }
            }
            stage('Smoke Test - Dev') {
                when{
                    anyOf { 
                        buildingTag()
                        branch 'master'
                    }
                }
                agent {
                    docker { image 'postman/newman' }
                }
                options {
                    skipDefaultCheckout true
                }
                steps {
                    echo 'Running tests'
                }
            }
            stage('Flip - Dev') {
                when{
                    anyOf { 
                        buildingTag()
                        branch 'master'
                    }
                }
                agent {
                    docker { image 'nulldriver/cf-cli-resource' }
                }
                options {
                    skipDefaultCheckout true
                }
                steps {
                    flip(pipelineParams.dev)
                }
            }
            //qa env
            stage('Deploy Green - QA') {
                when { buildingTag() }
                agent {
                    docker { image 'nulldriver/cf-cli-resource' }
                }
                options {
                    skipDefaultCheckout true
                }
                steps {
                    deployGreen(pipelineParams.qa)
                }
            }
            stage('Smoke Test - QA') {
                when { buildingTag() }
                agent {
                    docker { image 'postman/newman' }
                }
                options {
                    skipDefaultCheckout true
                }
                steps {
                    echo 'Running tests'
                }
            }
            stage('Flip - QA') {
                when { buildingTag() }
                agent {
                    docker { image 'nulldriver/cf-cli-resource' }
                }
                options {
                    skipDefaultCheckout true
                }
                steps {
                    flip(pipelineParams.qa)
                }
            }

            //prod env
            stage('Deploy Green - Prod') {
                when { buildingTag() }
                agent {
                    docker { image 'nulldriver/cf-cli-resource' }
                }

                options {
                    skipDefaultCheckout true
                }
                steps {
                    deployGreen(pipelineParams.prod)
                }
            }
            stage('Smoke Test - Prod') {
                when { buildingTag() }
                agent {
                    docker { image 'postman/newman' }
                }
                options {
                    skipDefaultCheckout true
                }
                steps {
                    echo 'Running tests'
                }
            }
            stage('Flip - Prod') {
                when { 
                    beforeInput true
                    buildingTag() 
                }
                input {
                    message "Should we make it live?"
                }
                agent {
                    docker { image 'nulldriver/cf-cli-resource' }
                }
                options {
                    skipDefaultCheckout true
                }
                steps {
                    flip(pipelineParams.prod)
                }
            }


            
        }
    }
}