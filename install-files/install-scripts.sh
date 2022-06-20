kubectl create ns shtest-jenkins-operator
kubectl create ns shtest-jenkins

kubectl apply -f https://raw.githubusercontent.com/jenkinsci/kubernetes-operator/master/config/crd/bases/jenkins.io_jenkins.yaml 

kubectl apply -n shtest-jenkins-operator -f 1-jenkins-operator-rbac.yaml

kubectl apply -n shtest-jenkins-operator -f 2-jenkins-operator.yaml

kubectl apply -n shtest-jenkins -f 3-jenkins-ns-rbac.yaml

kubectl apply -n shtest-jenkins -f 4-jenkins.yaml


kubectl --namespace shtest-jenkins get secret jenkins-operator-credentials-example -o 'jsonpath={.data.user}' | base64 -d
kubectl --namespace shtest-jenkins get secret jenkins-operator-credentials-example -o 'jsonpath={.data.password}' | base64 -d

kubectl --namespace shtest-jenkins port-forward jenkins-example 8080:8080
