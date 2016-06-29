#! /bin/bash
cd $(dirname $0)
pushd .. > /dev/null

version=$1
if [ ! -z "${version}" ]
then
    echo ">>>Will try to download docserv version: ${version} (verison provided on command line)"
    if wget --spider http://maven.altrusoft.se/dists/docserv-${version}.zip 2>/dev/null
    then
	download=true
    else
	echo ">>>Unable to downloading docserv version: ${version} - it is not found in the repo - exiting" 1>&2
	exit 1
    fi
else
    if [ -e build.sbt ]
    then
	echo ">>>Found build.sbt - assuming development environment"
	# in module dev env...
	version=$(cat build.sbt | grep 'val' | grep 'docservVerison' | cut -d '=' -f 2 | xargs)   
	#version=$(grep -oP "\"se.altrusoft\"\s+%%\s+\"docserv\"\s+%\s+\"\K\d+.\d+" build.sbt)
	echo ">>>Will try to download docserv version: ${version} (Version found in build.sbt)"
	download=true
	if wget --spider http://maven.altrusoft.se/dists/docserv-${version}.zip 2>/dev/null
	then
	    download=true
	else
	    echo ">>>Unable to downloading docserv version: ${version} - it is not found in the repo - exiting" 1>&2
	    exit 1
	fi
    else
	echo ">>>Assuming docserv is ready to be installed in docserv-dist/universal"
	download=false
	if [ ! -d docserv-dist/universal ]
	then
	    echo ">>>No directory docserv-dist/universal found, thus no docserv to install - exiting" 1>&2
	    exit 2
	fi
	cd docserv-dist/universal
	if ls docserv*.zip 1> /dev/null 2>&1
	then
	    echo ">>>Found docserv that may be installed (docserv*.zip) in docserv-dist/universal"
	else
	    echo ">>>No docserv found that may be installed in docserv-dist/universal - exiting" 1>&2
	    exit 3
	fi  
    fi
fi

if [ -d $(pwd)/server ]
then
    root_dir=$(pwd)/server
else
    root_dir=$(pwd)
fi

cd ${root_dir}


if ${download}
then
    mkdir -p docserv-dist/universal
    echo ">>>downloading docserv version: ${version}"
    cd docserv-dist/universal
    if wget http://maven.altrusoft.se/dists/docserv-${version}.zip
    then
	echo ">>>Downloaded docserv version: ${version}"
    else
	echo ">>>Unable to downloaded docserv version: ${version} - exiting" 1>&2
	exit 4
    fi
fi


unzip docserv*.zip

dist_dir=$(ls -1 | grep docserv | grep -v zip)

mv --backup=t ${dist_dir} ${root_dir}
cd ${root_dir}
rm -rf docserv-latest
ln -s ${root_dir}/${dist_dir} ${root_dir}/docserv-latest

cd ${root_dir}/docserv-latest/lib
rm -rf juh.jar
ln -s /usr/share/java/juh.jar juh.jar
rm -rf jurt.jar
ln -s /usr/share/java/jurt.jar jurt.jar
rm -rf ridl.jar
ln -s /usr/share/java/ridl.jar ridl.jar
rm -rf unoil.jar
ln -s /usr/lib/libreoffice/program/classes/unoil.jar unoil.jar



# also ensure
# sudo ln -s /usr/lib/libreoffice/program/libjpipe.so /usr/lib/libjpipe.so

# Keep only 3 backups
ls -t -d1 ${root_dir}/${dist_dir}* | awk 'NR>4 {system("rm -rf \"" $0 "\"")}'

