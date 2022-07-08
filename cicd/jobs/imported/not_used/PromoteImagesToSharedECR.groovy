#!/usr/bin/env groovy

pipelineJob('PromoteImagesToProduction') {
    displayName('PromoteImagesToProduction')
    // description('PromoteImagesToProduction')

    disabled(false)
    keepDependencies(false)

    concurrentBuild()
    // resumeBlocked(false)

    logRotator {
        numToKeep(10) // 1
        daysToKeep(30) // -1
        artifactDaysToKeep(30) // -1
        artifactNumToKeep(10) // -1
    }

    configure { project ->
        project / 'properties' / 'org.jenkinsci.plugins.workflow.job.properties.DurabilityHintJobProperty' {
            hint('PERFORMANCE_OPTIMIZED')
        }
    }

    parameters {
        choiceParam('from_region', ['eu-west-2', 'eu-west-1', 'us-east-1'], 'AWS region to pull the docker image from')
        stringParam('to_region', 'us-east-1', 'The region the docker image needs pushing to')
        stringParam('images_to_promote')
        stringParam('from_account', '032356282346', 'AWS account ID to pull ECR images from')
        stringParam('to_account', '289606673548', 'Target AWS account to push images to')
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
            scriptPath('cicd/pipelines/imported/PromoteImagesToProduction.jenkins')
        }
    }
}
