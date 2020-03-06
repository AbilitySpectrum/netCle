#!/bin/bash
#
# File Name: upload.sh
#
#  Copyright 2018 Don Haig (time4tux at gmail dot com)
#  GPL License (See LICENSE.txt for details)
#
# Created: Fri 27 Sep 2019 17:18:44 UTC +0000
LAST_MODIFIED="Fri 06 Mar 2020 10:57:19 AM EST -0500 "
VERSION=0.1
#
# Purpose: A script to ...
#
#------------------------------------------------------------------------------
amake -u leonardo  -p /dev/ttyACM0 netCleArduinoCode.ino
