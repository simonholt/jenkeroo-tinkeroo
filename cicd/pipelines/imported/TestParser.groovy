#!/usr/bin/env groovy

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

node {
    setVariables()
    stage ('Download Test Results') {

        withAWS(credentials:'aws-key', region: env.RESULTS_REGION) {
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

    //Convert the passed in build time to human readable format
    echo "Build Time is: $env.BUILD_TIME"
    Long unixEpoch = Long.valueOf(env.BUILD_TIME)
    Date convertedDate = Date.from(Instant.ofEpochMilli(unixEpoch))
    def buildTimeStamp = new SimpleDateFormat("E-MMM-dd-HH-mm-ss-z-yyyy").format(convertedDate)

    //Get the service name and version number
    println "Service trigger: $env.SERVICE_TRIGGER"

    serviceVersion = env.SERVICE_TRIGGER.split('/')[1].replace(':','-')
    outputFolder = "$serviceVersion-$buildTimeStamp"

}