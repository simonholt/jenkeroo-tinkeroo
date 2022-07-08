#!/usr/bin/env groovy

pipelineJob('tracetool_trending') {
    displayName('tracetool_trending')
    // description('tracetool_trending')

    disabled(false)
    keepDependencies(false)

    concurrentBuild(true)
    resumeBlocked(false)

    logRotator {
        numToKeep(10)
        daysToKeep(30)
    }

    configure { project ->
        project / 'properties' / 'org.jenkinsci.plugins.workflow.job.properties.DurabilityHintJobProperty' {
            hint('PERFORMANCE_OPTIMIZED')
        }
        // 'org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobAction'
        // 'org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobPropertyTrackerAction'
    }

    parameters {
        // is the bucket name right?
        stringParam('BUCKET_NAME', 'wcc-development-staging-data', 'Name of the S3 bucket that contains the RESULTS_FILENAME')
        stringParam('TAG', 'latest', 'TAG description')
        stringParam('AWS_ACCOUNT_ID', '770740718813', '? The AWS account the bucket belongs to ?')
        choiceParam('RESULTS_REGION', ['eu-west-1', 'us-east-1'], '? Region in which staging tests are running ?')
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
            scriptPath('cicd/pipelines/imported/tracetool_trending.groovy')
        }
    }
}
