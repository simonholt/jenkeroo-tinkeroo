#!/usr/bin/env groovy

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

pipeline {
    agent any
    //parameters {
        //string(name: 'stagingtests_path', defaultValue: '', description: 'contains the stagings tests S3 bucket path')
        //string(name: 'service_name', defaultValue: '', description: 'contains the service name')
    //}
    stages {

        stage('Check Parameters')
        {
            steps
            {
                script
                {

                    if (service_trigger == '') {
                        currentBuild.result = 'ABORTED'
                        error('service_name parameter is not set, exiting...')
                    }
                }
            }
        }
        stage('Tracettool GitHub Action') {
            environment {
               GitHub_Token = credentials('Github_Token')
               body= ''
            }

            steps {

                script {

                    echo "Build Time is: $env.BUILD_TIME"
                    Long unixEpoch = Long.valueOf(env.BUILD_TIME)
                    Date convertedDate = Date.from(Instant.ofEpochMilli(unixEpoch))
                    buildTimeStamp = new SimpleDateFormat("E-MMM-dd-HH-mm-ss-z-yyyy").format(convertedDate)
                    //Get the service name and version number
                    println "Service trigger: $env.SERVICE_TRIGGER"

                    def serviceVersion = env.SERVICE_TRIGGER.split('/')[1].replace(':','-')
                    outputFolder = "$serviceVersion-$buildTimeStamp"
                    println "outputfolder: "
                    println outputFolder

                    def service_name= ''
                    echo "before AWS creds"
                    withAWS(role:'ToolsSpinnakerManagedRole', roleAccount: "${env.AWS_ACCOUNT_ID}", duration: 900, roleSessionName: 'jenkins-s3-session') {
                            files = s3FindFiles bucket: env.BUCKET_NAME, onlyFiles: true, path: "$outputFolder/" , glob:'*.zip'
                            files.each {
                                println it.name
                                if (it.name.equals("Results.zip")) {
                                    service_name = "trending"
                                } else {
                                    continue
                                }

                                body = "{\"event_type\": \"${service_name}_tracetool_via_jenkins\", \"client_payload\": { \"service_trigger\": \"${env.BUCKET_NAME}\",\"staging_test_path\": \"$outputFolder\", \"file\": \"${it.name}\"}}"
                                println body
                                final String response = sh "curl -X POST -H \"Accept: application/vnd.github.everest-preview+json\" -H \"Authorization: token ${GitHub_Token}\" -d '${body}' https://api.github.com/repos/waterscorporation/wcc-trending/dispatches"
                                echo response
                            }
                    }
                }

            }
        }
    }
}
