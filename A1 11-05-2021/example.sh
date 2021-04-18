#!/bin/bash 
# <Description of this shell script>
# <Your name> 
# <Date>

# ------------------------------------------------------------
# This function shows the help text for this bash script
usage() { 
   echo "
   $0 [OPTIONS] [<user name>]
   Ask the user for her or his name and display a greeting 
   OPTIONS: 
      -h: Display this help
   "
}

# This function asks the user for his name
ask_for_name() {
    echo "Please enter your name:" 
    read user_name
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
        ask_for_name
        ;;
    *)
        user_name=$1
esac

# print greetings
echo "
####################
 Hello $user_name,
 nice to meet you! 
####################
"
exit 0 

# ---------------------- end ---------------------------------
