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

            // stage('Build') {
            //     environment {
            //         HOME = '/tmp'
            //     } 
            //     agent {
            //         docker { image 'microsoft/dotnet:2.2-sdk' }
            //     }
            //     steps {
            //         echo 'Building..'
            //         sh script:'cd src && dotnet build --configuration Release', label: 'build app'
            //         stash name: "build", includes: "src/bin/**"
            //     }
            // }
            // stage('Test') {
            //     environment {
            //         HOME = '/tmp'
            //     } 
            //     agent {
            //         docker { image 'microsoft/dotnet:2.2-sdk' }
            //     }
            //     steps {
            //         echo 'Testing..'
            //         sh script:'cd tests/unitTests && dotnet test', label: 'build app'
            //     }
            // }
            stage('Publish') {
                 environment {
                    HOME = '/tmp'
                } 
                agent {
                    docker { image 'microsoft/dotnet:2.2-sdk' }
                }
                steps {
                    echo 'Publishing..'
                    //unstash "build"
                    sh script: 'cd src && dotnet publish --configuration Release --output ../artifact', label: 'publish artifact'
                    sh script: 'cp *.yml artifact/.', label: 'copy manifests to artifact'
                    //archiveArtifacts artifacts: 'artifact/*'
                    stash name: "app", includes: "artifact/*"
                }
            }

            //dev env
            // stage('Deploy Green - Dev') {
            //     when{
            //         anyOf { 
            //             buildingTag()
            //             branch 'master'
            //         }
            //     }
            //     agent {
            //         docker { image 'nulldriver/cf-cli-resource' }
            //     }
            //     options {
            //         skipDefaultCheckout true
            //     }
            //     steps {
            //         deployGreen(pipelineParams.dev)
            //     }
            // }
            // stage('Smoke Test - Dev') {
            //     when{
            //         anyOf { 
            //             buildingTag()
            //             branch 'master'
            //         }
            //     }
            //     agent {
            //         docker { 
            //             image 'chef/inspec'
            //             args '-it --entrypoint=""'
            //          }
            //     }
            //     options {
            //         skipDefaultCheckout true
            //     }
            //     steps {
            //         echo 'Running tests'
            //         smoke(pipelineParams.dev)
            //     }
            // }
            // stage('Flip - Dev') {
            //     when{
            //         anyOf { 
            //             buildingTag()
            //             branch 'master'
            //         }
            //     }
            //     agent {
            //         docker { image 'nulldriver/cf-cli-resource' }
            //     }
            //     options {
            //         skipDefaultCheckout true
            //     }
            //     steps {
            //         flip(pipelineParams.dev)
            //     }
            // }
            // //qa env
            // stage('Deploy Green - QA') {
            //     when { buildingTag() }
            //     agent {
            //         docker { image 'nulldriver/cf-cli-resource' }
            //     }
            //     options {
            //         skipDefaultCheckout true
            //     }
            //     steps {
            //         deployGreen(pipelineParams.qa)
            //     }
            // }
            // stage('Smoke Test - QA') {
            //     when { buildingTag() }
            //     agent {
            //         docker { 
            //             image 'chef/inspec'
            //             args '-it --entrypoint=""'
            //          }
            //     }
            //     options {
            //         skipDefaultCheckout true
            //     }
            //     steps {
            //         echo 'Running tests'
            //         smoke(pipelineParams.qa)
            //     }
            // }
            // stage('Flip - QA') {
            //     when { buildingTag() }
            //     agent {
            //         docker { image 'nulldriver/cf-cli-resource' }
            //     }
            //     options {
            //         skipDefaultCheckout true
            //     }
            //     steps {
            //         flip(pipelineParams.qa)
            //     }
            // }

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
                    deployGreen(pipelineParams.prod, 'true')
                }
            }
            stage('Smoke Test - Prod') {
                when { buildingTag() }
                agent {
                    docker { 
                        image 'chef/inspec'
                        args '-it --entrypoint=""'
                     }
                }
                options {
                    skipDefaultCheckout true
                }
                steps {
                    echo 'Running tests'
                    smoke(pipelineParams.prod)
                }
            }
            stage('scale 25% - Prod') {
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
                    canary(pipelineParams.prod,25)
                    sh script: 'sleep 30', label: 'run some tests'
                }
            }
             stage('scale 50% - Prod') {
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
                    canary(pipelineParams.prod,50)
                    sh script: 'sleep 30', label: 'run some tests'
                }
            }
            stage('scale 100% - Prod') {
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