#!/usr/bin/env groovy

//This script expects a credential called shared-ecr to exist in Jenkins
node {

  setVariables()

  if(tagAlreadyLatest){
      currentBuild.result = 'SUCCESS'
      return
  }

  stage('Pull and Tag Docker Image') {
    withAWS(credentials:'aws-key', region: "${pullRegion}") {
        docker.withRegistry("https://${pullRegistry}", "ecr:${pullRegion}:aws-key") {
            sh "docker image pull ${pullImage}"
            sh "docker image tag ${pullImage} ${pushImage}"
        }
    }
  }

  stage('Push Docker Image') {
    withAWS(credentials:'aws-key', region: "${env.region}") {
        docker.withRegistry("https://${pushRegistry}", "ecr:${env.region}:aws-key") {
            sh "docker image push ${pushImage}"
        }
    }
  }
}

def setVariables(){
    account = env.image_ref.split('\\.')[0]
    pullRegion = env.image_ref.split('\\.')[3]
    pullRegistry  = env.image_ref.split('/')[0]
    imageTag  = env.image_ref.split(':')[1]
    pushRegistry = "${account}.dkr.ecr.${env.region}.amazonaws.com"

    tagAlreadyLatest = (imageTag == "latest")

    pullImage = "${env.image_ref}"
    repository = "${env.image_ref.split('/')[1].split(':')[0]}"
    pushImage = "${pushRegistry}/${repository}:latest"

    println "AWS ECR Pull registry: ${pullRegistry}"
    println "AWS ECR Push registry: ${pushRegistry}"
    println "Tagging as latest: ${pullImage}"
    if(tagAlreadyLatest){
      println "Image is already latest. Nothing to do here."
    }else {
      println "Pushing ${pushImage}"
    }
}