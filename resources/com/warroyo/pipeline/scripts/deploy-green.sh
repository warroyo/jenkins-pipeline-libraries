#!/bin/bash
set -e


cf push --var name=${APP_NAME}-green --vars-file $ENV_FILE