#!/usr/bin/env groovy

//This script expects a credential called aws-key to exist in Jenkins
node {

  setVariables()

  stage('Pull and Tag Docker Image') {
    withAWS(credentials:'aws-key', region: "${env.from_region}") {

        println "Pulling image registry: ${pullImage}"
        docker.withRegistry("https://${fromRegistry}", "ecr:${env.from_region}:aws-key") {
            sh "docker image pull ${pullImage}"
            sh "docker image tag ${pullImage} ${pushImage}"
        }

    }
  }

  stage('Push Docker Image') {

    withAWS(credentials:'aws-key', region: "${env.to_region}") {

        println "Pushing image: ${pushImage}"

        docker.withRegistry("https://${toRegistry}", "ecr:${env.to_region}:aws-key") {
            sh "docker image push ${pushImage}"
        }
    }
  }
}

def setVariables(){

    fromRegistry = "032356282346.dkr.ecr.${env.from_region}.amazonaws.com"
    toRegistry = "032356282346.dkr.ecr.${env.to_region}.amazonaws.com"

    imageName = env.image_name.split('/')[1]
    pullImage = "${env.image_name}"
    pushImage = "${toRegistry}/${imageName}"
}