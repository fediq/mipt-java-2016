@@ -1,13 +1,25 @@
 #!/bin/sh
 
 set -e
 
 if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
+#!/bin/bash
+
+set -e
+
+if [ -z "${TRAVIS_PULL_REQUEST}" ] || [ "${TRAVIS_PULL_REQUEST}" == "false" ]; then
     echo "Building push"
     mvn test -B
 else
     echo "Building pull request ${TRAVIS_PULL_REQUEST}"
     PROJECTS=$(git diff origin/master..HEAD --name-only | grep '/' | sed -e 's/^\([^\/]*\)\/.*$/\1/' | sort | uniq | awk 'BEGIN{ORS=","}; {print $1}')