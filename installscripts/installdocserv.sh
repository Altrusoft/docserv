#! /bin/bash
cd $(dirname $0)
pushd .. > /dev/null

if [ -e build.sbt ]
then
    # in module dev env...
    version=$(cat build.sbt | grep 'val' | grep 'docservVerison' | cut -d '=' -f 2 | xargs)   
    #version=$(grep -oP "\"se.altrusoft\"\s+%%\s+\"docserv\"\s+%\s+\"\K\d+.\d+" build.sbt)
    download=true
else
    # in prod
    download=false
fi

if [ -d $(pwd)/server ]
then
    root_dir=$(pwd)/server
else
    root_dir=$(pwd)
fi

cd ${root_dir}


if $download
then
    mkdir -p docserv-dist/universal
    echo ">>>downloading docserv version: ${version}"
    cd docserv-dist/universal
    wget http://maven.altrusoft.se/dists/docserv-${version}.zip
else
    if [ ! -d server/docserv-dist/universa ]
    then
	echo ">>>no docserv found that may be installed"
    fi
    cd docserv-dist/universal
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

