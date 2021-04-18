 #!/bin/bash 
# Renames all files & folder in the target directory by appending the specified string (not recursive!).
# Daniel Bergmann & Finn-Lukas Armbruster
# 18.04.2021

# ------------------------------------------------------------
# This function shows the help text for this bash script
usage() { 
   echo "
   $0 [OPTIONS] [<target dir> <string to append>]
   Renames all files & folder in the target directory by appending the specified string (not recursive!)
   OPTIONS: 
      -h: Display this help
   "
}

# This function asks the user for the target directory
ask_for_targetdir() {
    echo "Please enter the target directory:" 
    read target_dir
}

# This function asks the user for the string to append
ask_for_appstring() {
    echo "Please enter the strign to append:" 
    read app_string
}

# ---------------------- main --------------------------------
# check parameters 
if [ $# -gt 2 ]; then
    usage
    exit 1
fi

case $1 in
    "-h")
        usage
        exit 0
        ;;
    "")
        ask_for_targetdir
        ;;
    *)
        target_dir=$1
esac

case $2 in
    "-h")
        usage
        exit 0
        ;;
    "")
        ask_for_appstring
        ;;
    *)
        app_string=$1
esac

# print start
echo "
####################
 Renamign all files & folders in the dir $target_dir,
 the string $app_string will be appended to each entry.
####################
"

# add wildcard
target_dir="${target_dir}/*"

# iterate over each entry
for f in $target_dir
do
	echo "Renaming entry $f to $f$app_string"
    mv $f $f$app_string
done

exit 0 

# ---------------------- end ---------------------------------