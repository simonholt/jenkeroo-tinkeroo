#!/usr/bin/env groovy

pipelineJob('MoveStagingImages') {
    displayName('MoveStagingImages')
    // description('MoveStagingImages')

    disabled(false)
    keepDependencies(false)

    concurrentBuild(true)
    // resumeBlocked(false)

    logRotator {
        numToKeep(10)
        daysToKeep(30)
    }

    configure { project ->
        project / 'properties' / 'org.jenkinsci.plugins.workflow.job.properties.DurabilityHintJobProperty' {
            hint('PERFORMANCE_OPTIMIZED')
        }
    }

    parameters {
        choiceParam('from_region', ['eu-west-1', 'us-east-1'], 'AWS region to pull the docker image from')
        choiceParam('to_region', ['eu-west-1', 'us-east-1'], 'The region the docker image needs pushing to')
        stringParam('image_name', '', 'The docker image name to promote')
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
            scriptPath('cicd/pipelines/imported/MoveStagingImages.jenkins')
        }
    }
}
