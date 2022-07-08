#!/usr/bin/env groovy

node {

  setVariables()

    stage('Pull DEV Docker Image') {
        withAWS(region: 'eu-west-1', role:'ToolsSpinnakerManagedRole', roleAccount:'992548356091', duration: 900, roleSessionName: 'jenkins-session') {
            docker.withRegistry("https://${registry}", "ecr:eu-west-1:aws-key"){
                sh ecrLogin(registryIds: ['992548356091'])
                docker.image('auditlogas:0.0.70-dummy-1').pull()
            }
        }
    }
}

def setVariables(){
    fullImageLink = "992548356091.dkr.ecr.eu-west-1.amazonaws.com/auditlogas:0.0.70-dummy-1"
    registry = fullImageLink.split('/')[0]
    imageName = fullImageLink.split(':')[0].split('/')[1]
    imageTag = fullImageLink.split(':')[1]
    println "Pulling image ${imageName} with tag ${imageTag}\nfrom registry ${registry}"
}