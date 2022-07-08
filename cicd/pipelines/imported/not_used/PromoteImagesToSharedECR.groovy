#!/usr/bin/env groovy

/*
This script expects a credential called shared-ecr to exist in Jenkins. This is a temporary pipeline to copy docker images from
the legacy aws account to the new wcc-sharedServices account.
when running this pipeline, you should only push to us-east-1 region as it will auto replicate to eu-west-1 in the
shared services account.
This script also requires the following parameters
- from_regions
- to_region
- from_account
- to_account
- images_to_promote --&gt; this one is a multiline string where each line contains a full image arn with tag
*/
node {

  parsedImages = new ArrayList&lt;DockerImage&gt;()

  setVariables()

  stage('Pull and Tag Docker Image') {
    withAWS(credentials:'aws-key', region: "${env.from_region}") {
        parsedImages.each {
            println "Pulling image registry: ${it.PullImage}"
            docker.withRegistry("https://${fromRegistry}", "ecr:${env.from_region}:aws-key") {
                sh "docker image pull ${it.PullImage}"
                sh "docker image tag ${it.PullImage} ${it.PushImage}"
            }
        }

    }


  }

  stage('Push Docker Image') {
    withAWS(credentials:'shared-ecr', region: env.to_region) {
        parsedImages.each {
            println "Pushing image: ${it.PushImage}"
            // shared-ecr must exist in the jenkins credentials manager. This must be set with assumerole rather than AWS access keys
          docker.withRegistry("https://${toRegistry}", "ecr:${env.to_region}:shared-ecr") {
          sh "docker image push ${it.PushImage}"
        }

      }
    }
  }

}
def setVariables(){

    fromRegistry = "${env.from_account}.dkr.ecr.${env.from_region}.amazonaws.com"
    toRegistry = "${env.to_account}.dkr.ecr.${env.to_region}.amazonaws.com"

    images = env.images_to_promote.split("\\n")
    if (images.length &gt; 0) {
        images.each {
            image = new DockerImage()
            imageName = it.split('/')[1]
            image.Name = imageName
            image.PullImage = "${fromRegistry}/${imageName}"
            image.PushImage = "${toRegistry}/${imageName}"
            parsedImages.add(image)
            println "Image is: ${image}"
        }
    }



    println 'Total Images: ' + parsedImages.size()

}

public class DockerImage
{
    String Name
    String PullImage
    String PushImage

}