#!/usr/bin/env groovy

/*
The pipeline is used to pull test results from S3 buckets located in other AWS accounts.
It uses sts assume role using in the target account.
- RESULTS_REGION
- BUCKET_NAME
- SERVICE_TRIGGER
- BUILD_TIME
- AWS_ACCOUNT_ID

All of the above parameters need to be set in the Jenkins UI or from by the caller
*/

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

node {
    setVariables()
    stage ('Download Test Results') {
        withAWS(role:'ToolsSpinnakerManagedRole', roleAccount: "${env.AWS_ACCOUNT_ID}", duration: 900, roleSessionName: 'jenkins-s3-session') {
            s3Download(file:"./", bucket:env.BUCKET_NAME, path:"$outputFolder/", force:true)
        }
    }

    stage('Unzip Test Results'){

        def zipFiles = findFiles(glob:"$outputFolder/*.zip")

        for(zipFile in zipFiles){
            // This unzips all the zip file contents into the current directory.
            // The kubernetes job name is in the trx file names so there shouldn't be any trx overwrites.
            unzip zipFile: zipFile.path, glob: '**/*.trx'
        }
    }

    stage('Parse Test Resulsts'){
        mstest testResultsFile:"**/*.trx", keepLongStdio: true, failOnError: true
    }

    stage('reports') {

        def resultsPath = 'root/Results'

        writeFile file: "$resultsPath/environment.properties", text: "ResultsBucket=$env.BUCKET_NAME\n"

        script {
            allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: resultsPath]]
            ])
        }
    }
}

def setVariables(){

    outputFolder = env.OUTPUT_FOLDER

}