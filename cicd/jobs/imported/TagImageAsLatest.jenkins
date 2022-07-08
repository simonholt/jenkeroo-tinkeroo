#!/usr/bin/env groovy

pipelineJob('imported/TagImageAsLatest') {
    displayName('TagImageAsLatest')
    // description('TagImageAsLatest')

    disabled(false)
    keepDependencies(false)

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
        stringParam('image_ref', '', 'The full docker image reference in AWS ECR to tag as latest i.e. repo/name:tag')
        stringParam('region', '', 'The AWS region containing the image e.g. eu-west-2')
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
            scriptPath('cicd/pipelines/imported/TagImageAsLatest.jenkins')
        }
    }
}
