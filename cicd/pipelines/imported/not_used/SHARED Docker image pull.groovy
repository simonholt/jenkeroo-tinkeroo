#!/usr/bin/env groovy

node {
    stage('Pull SHARED Docker Image') {
        withAWS(credentials:'aws-key', region: "eu-west-1") {
            println "Pulling image datamanagement from shared registry"
            docker.withRegistry("https://289606673548.dkr.ecr.eu-west-1.amazonaws.com/datamanagement", "ecr:eu-west-1:aws-key") {
                println "Starting to pull"
                sh "docker image pull 289606673548.dkr.ecr.eu-west-1.amazonaws.com/datamanagement:1.1.0"
            }
        }
    }
}
