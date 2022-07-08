#!/usr/bin/env groovy

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

pipeline {
    agent any

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

                    def serviceVersion = env.SERVICE_TRIGGER.split(':')[1]
                    outputFolder = "${env.S3LOC}$serviceVersion"

                    println outputFolder

                    def service_name= ''
                    echo "before AWS creds"
                    withAWS(role:'wcc-build-artifacts-bucket-write', roleAccount: "${env.AWS_ACCOUNT_ID}", duration: 900, roleSessionName: 'jenkins-s3-session') {
                            files = s3FindFiles bucket: env.BUCKET_NAME, onlyFiles: true, path: "$outputFolder/e2e/" , glob:'*.zip'
                            files.each {
                                println it.name
                                if (it.name.equals("Results.zip")) {
                                    service_name = "devicemgmt"
                                } else {
                                    continue
                                }

                                body = "{\"event_type\": \"${service_name}_tracetool_via_jenkins\", \"client_payload\": { \"service_trigger\": \"${env.BUCKET_NAME}\",\"staging_test_path\": \"$outputFolder/e2e\", \"version_suffix\": \"$serviceVersion\", \"traceablity_upload_path\": \"$outputFolder\", \"file\": \"${it.name}\"}}"
                                println body
                                final String response = sh "curl -X POST -H \"Accept: application/vnd.github.everest-preview+json\" -H \"Authorization: token ${GitHub_Token}\" -d '${body}' https://api.github.com/repos/waterscorporation/wcc-scp-devicemgmt/dispatches"
                                echo response
                            }
                    }
                }

            }
        }
    }
}
