#!/bin/bash

hbase shell <<EOF
create 'ad_impressions_by_website', 'impressions'
EOF
