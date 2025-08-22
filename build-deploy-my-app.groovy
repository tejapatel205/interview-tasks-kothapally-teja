pipeline {
    stages {
        stage('Checkout from Git'){
            steps{
                dir ("WORKSPACE/src") {
                    git branch: 'main', url: 'https://github.com/tejapatel205/skills-introduction-to-github.git'
                }
            }
        }
        stage ("Build Docker image") {
            steps {
                scipt {
                    dir ("WORKSPACE/src") {
                        sh'''
                            if [ -f "Dockerfile"]; then
                                echo "COPY /build_artifacts ."
                                docekr build -t my-app:latest
                            else
                                echo "Cannot find the Dockerfile"
                            fi
                            
                        '''
                    }                   
                }
            }
        }
        stage ("Push the docker image") {
            steps {
                script {
                    sh '''
                         docker image tag my-app:latest my-app:3.1
                         docker push my-app:3.1
                    '''
                }
            }
        }
        stage ("Deploy") {
            steps {
                script {
                    sh '''
		            docker pull image_name

                    echo "
                     'apiVersion: apps/v1
                    kind: Deployment
                    metadata:
                      name: my-deployment
                    spec:
                      selector:
                        matchLabels:
                          app: my-app
                      replicas: 2 # tells deployment to run 2 pods matching the template
                      template:
                        metadata:
                          labels:
                            app: my-app
                        spec:
                          containers:
                          - name: my-app
                            image: my-app:3.1
                            ports:
                            - containerPort: 80
                    " > deployment.yml

                    kubectl apply -f deployment.yaml
                    kubectl apply -f service.yaml
                    '''
                }
            }
        }
    }
    post {
        cleanws()
    }
}
