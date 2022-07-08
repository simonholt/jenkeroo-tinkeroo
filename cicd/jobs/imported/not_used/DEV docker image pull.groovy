#!/usr/bin/env groovy

pipelineJob('imported/DEV docker image pull') {
    displayName('DEV docker image pull')
    description('test pipeline to get ECR image')

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
            scriptPath('cicd/pipelines/imported/DEV docker image pull.groovy')
        }
    }
}
