#!/usr/bin/env groovy

//This script expects a credential called aws-key to exist in Jenkins
node {

  setVariables()

  if(tagAlreadyLatest){
      currentBuild.result = 'SUCCESS'
      return
  }

  stage('Pull and Tag Docker Image') {

    withAWS(credentials:'aws-key', region: "${env.region}") {

        docker.withRegistry("https://${registry}", "ecr:${env.region}:aws-key") {
            sh "docker image pull ${pullImage}"
            sh "docker image tag ${pullImage} ${pushImage}"
        }

    }
  }

  stage('Push Docker Image') {

    withAWS(credentials:'aws-key', region: "${env.region}") {

        docker.withRegistry("https://${registry}", "ecr:${env.region}:aws-key") {
            sh "docker image push ${pushImage}"
        }
    }
  }
}

def setVariables(){

    registry  = env.image_ref.split('/')[0]
    imageName = env.image_ref.split(':')[0]
    imageTag  = env.image_ref.split(':')[1]

    tagAlreadyLatest = (imageTag == "latest")

    pullImage = "${env.image_ref}"
    pushImage = "${imageName}:latest"

    println "AWS ECR registry: ${registry}"
    println "Tagging as latest: ${pullImage}"
    if(tagAlreadyLatest){
      println "Image is already latest. Nothing to do here."
    }else {
      println "Pushing ${pushImage}"
    }
}