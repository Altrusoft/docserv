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

echo ">>>Attempting to deploy modules for docserv version $docserv_version"

cd ${root_dir}/docserv-latest
if [ ! -e "lib.orig" ]
then 
    cp -ra lib lib.orig
fi
rm -rf lib
cp -ra lib.orig lib

cd ${root_dir}/docserv-latest/bin
if [ ! -e "docserv.orig" ]
then 
    cp -b docserv docserv.orig
fi
cp -f docserv.orig docserv

if [ -n "$(ls -A ${root_dir}/docserv-modules | grep -e '\.jar$')" ]
then
    modules=$(cd ${root_dir}/docserv-modules; ls -1 *.jar | grep -e "\-${docserv_version//./\\.}\-" | sort -r)
    if [ -z "${modules}" ]
    then
	echo ">>>no modules with version $docserv_version to deploy"
	install_modules=false
    else
	modulePath="" 
	last_module_name=""
	for module in ${modules} 
	do
	    module_name=${module%$docserv_version*}
	    if [ ! "${module_name}" == "${last_module_name}" ]
	    then
		last_module_name=${module_name}
		echo ">>>deploying module: ${module}"
		modulePath=${modulePath}":\$lib_dir/"${module}
		cp -f ${root_dir}/docserv-modules/${module} ${root_dir}/docserv-latest/lib/
	    else
		echo ">>>skipping older verison of module: ${module}"
	    fi
	done 

	modulePath="ridl.jar"${modulePath}":"
	cd ${root_dir}/docserv-latest/bin
	sed -i -- "s%ridl.jar:%${modulePath}%g" docserv
    fi

    #cd ${root_dir}/docserv-modules
    #cp -f *.jar ${root_dir}/docserv-latest/lib/

fi
