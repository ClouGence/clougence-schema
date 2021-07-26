
## release versions and prepare next SNAPSHOT version
    mvn release:clean release:prepare -Dmaven.test.skip -Prelease

## push to aliyun maven repository (with Private RELEASE & SNAPSHOT)
    mvn clean package install deploy

## push to center maven repository (version tag must be RELEASE)
    mvn clean package install deploy -Prelease