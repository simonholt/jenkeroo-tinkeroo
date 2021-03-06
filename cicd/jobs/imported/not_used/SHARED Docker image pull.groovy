#!/usr/bin/env groovy

<?xml version='1.1' encoding='UTF-8'?>
<flow-definition plugin="workflow-job@2.41">
  <actions/>
  <description>test pipeline to get shared ECR image</description>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <definition class="org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition" plugin="workflow-cps@2648.va9433432b33c">
    <script>node {
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
</script>
    <sandbox>false</sandbox>
  </definition>
  <triggers/>
  <disabled>false</disabled>