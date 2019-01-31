#!/bin/bash
set -e

 if [ "${CANARY}" = "true" ]; then
    echo "canary specified overriding instances"
    cf push -i 1 --var name=${APP_NAME}-green --vars-file $ENV_FILE
else
    cf push --var name=${APP_NAME}-green --vars-file $ENV_FILE
fi
            
