#!/bin/bash
set -e

cf map-route ${APP_NAME}-green $ROUTE --hostname ${APP_NAME}
cf delete $APP_NAME -f
cf rename ${APP_NAME}-green $APP_NAME
cf delete-route $ROUTE --hostname ${APP_NAME}-venerable -f