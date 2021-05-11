 #!/bin/bash 
# Pings a host every second to see if it is still reachable.
# Daniel Bergmann & Finn-Lukas Armbruster
# 18.04.2021

# ------------------------------------------------------------
# This function shows the help text for this bash script
usage() { 
   echo "
   $0 [OPTIONS] [<host>]
   Pings a host every second to see if it is still reachable
   OPTIONS: 
      -h: Display this help
   "
}

# This function asks the user for the target directory
ask_for_host() {
    echo "Please enter the host to ping:" 
    read target_host
}

# ---------------------- main --------------------------------
# check parameters 
if [ $# -gt 1 ]; then
    usage
    exit 1
fi

case $1 in
    "-h")
        usage
        exit 0
        ;;
    "")
        ask_for_host
        ;;
    *)
        target_host=$1
esac

# print start
echo "
####################
 Pings the host $target_host every second to see if it is still reachable.
####################
"

while true
do
    # ping -c 1  : only pings once
    # >/dev/null : logs the output into an empty void
    ping -c 1 $target_host &>/dev/null
    if [ $? -eq 0 ]
    then
        echo "$target_host OK"
    else
        echo "$target_host FAILED"
    fi
    sleep 1
done

exit 0 

# ---------------------- end ---------------------------------