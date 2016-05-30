#! /bin/bash
cd $(dirname $0)
pushd ..> /dev/null

if [ -d $(pwd)/server ]
then
    root_dir=$(pwd)/server
else
    root_dir=$(pwd)
fi

cd ${root_dir}

if [ ! -e "docserv-modules" ] 
then
    mkdir -p docserv-modules
fi

docserv_version=$(cat ${root_dir}/docserv-latest/conf/application.conf | grep 'app.version' | cut -d '=' -f 2 | xargs)

echo ">>>Attempting top deploy modules for docserv version $docserv_version"

if [ -n "$(ls -A ${root_dir}/docserv-modules | grep -e '\.jar$')" ]
then
    modules=$(cd ${root_dir}/docserv-modules; ls -1 *.jar | grep -e "\-${docserv_version//./\\.}\-" )
    if [ -z "${modules}" ]
    then
	echo ">>>no modules with version $docserv_version to deploy"
	exit 0
    fi
    modulePath="" 
    for module in $modules 
    do
	echo ">>>deploying module: ${module}"
	modulePath=${modulePath}":\$lib_dir/"${module} 
    done 

    modulePath="ridl.jar"${modulePath}":"

    cd ${root_dir}/docserv-latest
    if [ ! -e "lib.orig" ]
    then 
	cp -ra lib lib.orig
    fi
    rm -rf lib
    cp -ra lib.orig lib

    cd ${root_dir}/docserv-modules
    cp -f *.jar ${root_dir}/docserv-latest/lib/

    cd ${root_dir}/docserv-latest/bin

    if [ ! -e "docserv.orig" ]
    then 
	cp -b docserv docserv.orig
    fi
    cp -f docserv.orig docserv
    sed -i -- "s%ridl.jar:%${modulePath}%g" docserv
fi
