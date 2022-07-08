#!/usr/bin/env groovy

pipelineJob('MoveMultipleStagingImages') {
    displayName('MoveMultipleStagingImages')
    // description('MoveMultipleStagingImages')

    disabled(false)
    keepDependencies(false)

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
        stringParam('images_to_promote')
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
            scriptPath('cicd/pipelines/imported/MoveMultipleStagingImages.jenkins')
        }
    }
}
