#!/usr/bin/env groovy

/*
This script expects a credential called aws-key to exist in Jenkins. This is used by the dev accounts currently
Additionally, this script requires aws-prod-key with assumerole instead of aws access keys
This script also requires the following parameters
- from_regions
- to_region
- from_account
- to_account
- images_to_promote --> this one is a multiline string where each line contains a full image arn with tag
*/
node {

  parsedImages = new ArrayList<DockerImage>()

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

//   This requires sts assumerole for the target account and requires the jenkins worker node to assume ProdSpinnakerManagedRole in the target account
  stage('Push Docker Image') {
    withAWS(role:'ProdSpinnakerManagedRole', roleAccount: "${env.to_account}", duration: 900, roleSessionName: 'jenkins-session') {
        parsedImages.each {
            println "Pushing image: ${it.PushImage}"
            // aws-prod-key must exist in the jenkins credentials manager. This must be set with assumerole rather than AWS access keys
          docker.withRegistry("https://${toRegistry}", "ecr:${env.to_region}:aws-prod-key") {
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
    if (images.length > 0) {
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