#!/bin/sh

cat <<EOF | ftp ftp://anon:anon@10.1.92.2
put $1 /constants.txt 
EOF
