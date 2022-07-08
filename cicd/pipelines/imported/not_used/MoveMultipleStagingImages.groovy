#!/usr/bin/env groovy

//This script expects a credential called aws-key to exist in Jenkins
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


  stage('Push Docker Image') {

    withAWS(credentials:'aws-key', region: "${env.to_region}") {
        parsedImages.each {
            println "Pushing image: ${it.PushImage}"
          docker.withRegistry("https://${toRegistry}", "ecr:${env.to_region}:aws-key") {
          sh "docker image push ${it.PushImage}"
        }

      }
    }
  }

}
def setVariables(){

    fromRegistry = "032356282346.dkr.ecr.${env.from_region}.amazonaws.com"
    toRegistry = "032356282346.dkr.ecr.${env.to_region}.amazonaws.com"

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