pipeline {
    agent any
    environment{
        TOMCAT_HOME = "/home/marcin/tools/apache-tomcat-8.5.23"
        TOMCAT_HOST="172.17.0.1"
        CONN = "marcin@${TOMCAT_HOST}"
    }
    tools {
        maven 'mvn_3.5'
        jdk 'JDK1.8'
    }
    stages{
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }

        }

        stage ('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage ('Deploy') {
            steps {
                sh 'ssh ${CONN} rm -fR ${TOMCAT_HOME}/webapps/RestDemo-0.0.1*'
                sh 'ssh ${CONN} ls ${TOMCAT_HOME}/webapps/'
                sh 'scp target/RestDemo-0.0.1-SNAPSHOT.war ${CONN}:${TOMCAT_HOME}/webapps/'
            }
        }
        stage ('Start tomcat') {
            steps {
                sh 'ssh ${CONN} "${TOMCAT_HOME}/bin/catalina.sh start"'
            }
        }
        stage ('Functional tests') {
            steps {
                sh "mvn verify -Pstaging -Dtomcat.host=${TOMCAT_HOST}"
            }
            post {
                success {
                    junit 'target/**/*.xml'
                    jacoco(execPattern: 'target/jacoco.exec')
                }
            }
        }
        stage ('Stop tomcat') {
            steps {
                sh 'ssh ${CONN} "${TOMCAT_HOME}/bin/catalina.sh stop"'
            }
        }
    }
}