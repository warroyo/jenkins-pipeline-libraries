#!/bin/bash
set -e


cf push --var name=${APP_NAME}-venerable --vars-file $ENV_FILE