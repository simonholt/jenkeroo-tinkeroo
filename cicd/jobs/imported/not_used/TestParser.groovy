#!/usr/bin/env groovy

pipelineJob('imported/TestParser') {
    displayName('Test Parser')
    // description('Test Parser')

    disabled(false)
    keepDependencies(false)

    logRotator {
        numToKeep(10) // -1
        daysToKeep(30) // 90
        artifactDaysToKeep(30) // -1
        artifactNumToKeep(10) // -1
    }

    configure { project ->
        project / 'properties' / 'org.jenkinsci.plugins.workflow.job.properties.DurabilityHintJobProperty' {
            hint('PERFORMANCE_OPTIMIZED')
        }
    }

    parameters {
        choiceParam('RESULTS_REGION', ['eu-west-2', 'eu-west-1', 'us-east-1'], 'Region in which staging tests are running')
        stringParam('BUCKET_NAME', 'stagingtests', 'Name of the S3 bucket that contains the RESULTS_FILENAME')
        stringParam('SERVICE_TRIGGER', 'latest', 'SERVICE_TRIGGER description')
        stringParam('BUILD_TIME', '', 'Used to find the name of the test results artifact from S3.')
    }

    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/simonholt/jenkeroo-tinkeroo.git')
                    }
                    branches('*/main')
                }
            }
            scriptPath('cicd/pipelines/imported/TestParser.jenkins')
        }
    }
}
