#!/bin/bash
#------------------------------------------------------------------------------------------------------------------#
#------------------------------------------------------------------------------------------------------------------#
#|                                            chenglee public nginx                                               |#
#|                                            version: nginx-1.13.12                                              |#
#|                                            version: openssl-1.1.1                                              |#
#|                                            version: pcre-8.40                                                  |#
#|                                            version: zlib-1.2.11                                                |#
#|                                               2018.12.12                                                       |#
#------------------------------------------------------------------------------------------------------------------#
#|          Some people die at the age of 25 and don't bury themselves in the earth until they are 75             |#
#------------------------------------------------------------------------------------------------------------------#
#|                      $$$$ $$   $$ $$$$$$ $$    $$   $$$$$$          $$     $$$$$$ $$$$$$                       |#
#|                     $$    $$   $$ $$     $$ $$ $$  $$               $$     $$     $$                           |#
#|                    $$     $$$$$$$ $$$$$  $$  $$ $ $$  $$$$$$        $$     $$$$$  $$$$$                        |#
#|                     $$    $$   $$ $$     $$   $ $  $$     $$        $$     $$     $$                           |#
#|                      $$$$ $$   $$ $$$$$$ $$    $$   $$$$$ $$        $$$$$$ $$$$$$ $$$$$$                       |#
#------------------------------------------------------------------------------------------------------------------#
PRENAME="nginx"
#------------------------------------------------OFF--VERSION------------------------------------------------------#
openssl_version=`basename openssl-*.tar.gz .tar.gz | awk -F '-' '{print$2}'`
pcre_version=`basename pcre-*.tar.gz .tar.gz | awk -F '-' '{print$2}'`
zlib_version=`basename zlib-*.tar.gz .tar.gz | awk -F '-' '{print$2}'`
nginx_version=`basename nginx-*.tar.gz .tar.gz | awk -F '-' '{print$2}'`
#------------------------------------------------ON---VERSION------------------------------------------------------#
opensslv="1.1.1"
pcrev="8.40"
zlibv="1.2.11"
nginxv="1.13.12"
#------------------------------------------------VERSIONEND--------------------------------------------------------#
installpath=$(cd `dirname $0`; pwd)
 
function environment(){
    echo "|------------------------ CHECK GCC--------------------------|"
    GCCBIN=`which gcc`
    GCCV=$(echo $GCCBIN | grep "gcc")
    if [[ "$GCCV" != "" ]]
    then
        echo "gcc was installed "
    else
        echo "install gcc"
        yum install gcc gcc-c++ -y >/dev/null 2>&1
    fi
}
 
function initialize(){
    installpath=$(cd `dirname $0`; pwd)
    cd ${installpath}
    cd ${PRENAME}/lib/openssl* && OPENSSLPATH=`pwd`
    cd ${installpath}
    cd ${PRENAME}/lib/pcre* && PCREPATH=`pwd`
    cd ${installpath}
    cd ${PRENAME}/lib/zlib* && ZLIBPATH=`pwd`
    cd ${installpath}
}
 #------------------------------------------------------SSLSTRAT----------------------------------------------------#
function openssl(){
    echo "|-------------------------- OPENSSL--------------------------|"
    echo
    ssl=`ls | grep openssl-*.tar.gz`
    if [[ "$ssl" != "" ]]
    then
        echo "|-----------------------[发现离线压包]-----------------------|"
        /usr/bin/sleep 3
        opensslinstall_off
    else
        echo "|-----------------------[未发现离线包]-----------------------|"
        echo "|-----------------[开始判断是否连接外网安装]-----------------|"
        /usr/bin/sleep 3
        onopenssl
    fi
}
function opensslinstall_off(){
    echo "|---------------------[正在安装离线包]----------------------|"
    cd ${installpath}
    mkdir -p logs ${PRENAME}/lib && touch logs/{openssl.log,pcre.log,zlib.log,nginx.log}
    #openss
    tar -zxvf openssl-${openssl_version}.tar.gz -C ${PRENAME}/lib >/dev/null 2>&1
    cd ${PRENAME}/lib/openssl* && OPENSSLPATH=`pwd`
    ./config --prefix=${OPENSSLPATH} >${installpath}/logs/openssl.log >/dev/null 2>&1
    if [[ $? -ne 0 ]]; then
        return 1
    else
        make && make install >${installpath}/logs/openssl.log
        if [[ $? -ne 0 ]]; then
            return 1
        fi
        return 0
    fi
    ok
}
function onopenssl(){
    httpcode=`curl -I -m 10 -o /dev/null -s -w %{http_code}'\n' http://www.baidu.com`
    net1=$(echo $httpcode | grep "200")
    if [[ "$net1" != "" ]]
    then
        echo "|-----------------------[    成功    ]-----------------------|"
        echo "|-----------------------[准备联网安装]-----------------------|"
        /usr/bin/sleep 3
        wgetopenssl
    else
        echo "|-----------------------[    失败    ]-----------------------|"
        echo "|-----------------------[检测不到网络]-----------------------|"
        /usr/bin/sleep 3
        exit;
    fi
}
function wgetopenssl(){
    wget_v=`which wget`
    wget_vv=$(echo $wget_v | grep wget)
    if [[ "$wget_vv" != "" ]]
    then
        wget https://ftp.openssl.org/source/openssl-${opensslv}.tar.gz
        opensslinstall_on
    else
        yum install wget -y
        wget https://ftp.openssl.org/source/openssl-${opensslv}.tar.gz
        opensslinstall_on
    fi
}
function opensslinstall_on(){
    echo "|---------------------[正在安装在线包]----------------------|"
    cd ${installpath}
    mkdir -p logs ${PRENAME}/lib && touch logs/{openssl.log,pcre.log,zlib.log,nginx.log}
    tar -zxvf openssl-${opensslv}.tar.gz -C ${PRENAME}/lib >/dev/null 2>&1
    cd ${PRENAME}/lib/openssl* && OPENSSLPATH=`pwd`
    ./config --prefix=${OPENSSLPATH} >${installpath}/logs/openssl.log >/dev/null 2>&1
    if [[ $? -ne 0 ]]; then
        return 1
    else
        make && make install >${installpath}/logs/openssl.log
        if [[ $? -ne 0 ]]; then
            return 1
        fi
        return 1
    fi
    ok
}
#---------------------------------------------------SSLEND---------------------------------------------------------#
#--------------------------------------------------PCRESTART-------------------------------------------------------#
function pcre(){
    echo "|-------------------------- PCRE --------------------------|"
    echo
    pcr=`ls | grep pcre-*.tar.gz`
    if [[ "$pcr" != "" ]]
    then
        echo "|-----------------------[发现离线压包]-----------------------|"
        /usr/bin/sleep 3
        pcreinstall_off
    else
        echo "|-----------------------[未发现离线包]-----------------------|"
        echo "|-----------------[开始判断是否连接外网安装]-----------------|"
        /usr/bin/sleep 3
        onpcre
    fi
}
function pcreinstall_off(){
    echo "|---------------------[正在安装离线包]----------------------|"
    cd ${installpath}
    #mkdir -p logs ${PRENAME}/lib && touch logs/{openssl.log,pcre.log,zlib.log,nginx.log}
    tar -zxvf pcre-${pcre_version}.tar.gz -C ${PRENAME}/lib >/dev/null 2>&1
    cd ${PRENAME}/lib/pcre* && PCREPATH=`pwd`
    ./configure --prefix=${PCREPATH} >${installpath}/logs/pcre.log >/dev/null 2>&1
    if [[ $? -ne 0 ]]; then
        return 1
    else
        make && make install >${installpath}/logs/pcre.log
        if [[ $? -ne 0 ]]; then
            return 1
        fi
        return 0
    fi
ok
}
function onpcre(){
    wget http://ftp.pcre.org/pub/pcre/pcre-${pcrev}.tar.gz
    echo "|---------------------[正在安装在线包]----------------------|"
    cd ${installpath}
    #mkdir -p logs ${PRENAME}/lib && touch logs/{openssl.log,pcre.log,zlib.log,nginx.log}
    tar -zxvf pcre-${pcrev}.tar.gz -C ${PRENAME}/lib >/dev/null 2>&1
    cd ${PRENAME}/lib/pcre* && PCREPATH=`pwd`
    ./configure --prefix=${PCREPATH} >${installpath}/logs/pcre.log >/dev/null 2>&1
    if [[ $? -ne 0 ]]; then
        return 1
    else
        make && make install >${installpath}/logs/pcre.log
        if [[ $? -ne 0 ]]; then
            return 1
        fi
        return 0
    fi
ok
}
#----------------------------------------------------PCREEND-------------------------------------------------------#
#---------------------------------------------------STARTZLIB------------------------------------------------------#
function zlib(){
    echo "|-------------------------- ZLIB --------------------------|"
    echo
    zli=`ls | grep zlib-*.tar.gz`
    if [[ "$zli" != "" ]]
    then
        echo "|-----------------------[发现离线压包]-----------------------|"
        /usr/bin/sleep 3
        zlibinstall_off
    else
        echo "|-----------------------[未发现离线包]-----------------------|"
        echo "|-----------------[开始判断是否连接外网安装]-----------------|"
        /usr/bin/sleep 3
        onzlib
    fi
}
function zlibinstall_off(){
    echo "|---------------------[正在安装离线包]----------------------|"
    cd ${installpath}
    #mkdir -p logs ${PRENAME}/lib && touch logs/{openssl.log,pcre.log,zlib.log,nginx.log}
    tar -zxvf zlib-${zlib_version}.tar.gz -C ${PRENAME}/lib >/dev/null 2>&1
    cd ${PRENAME}/lib/zlib* && ZLIBPATH=`pwd`
    ./configure --prefix=${ZLIBPATH} >${installpath}/logs/zlib.log >/dev/null 2>&1
    if [[ $? -ne 0 ]]; then
        return 1
    else
        make && make install >${installpath}/logs/zlib.log
        if [[ $? -ne 0 ]]; then
            return 1
        fi
        return 0
    fi
}
function onzlib(){
    wget http://www.zlib.net/fossils/zlib-${zlibv}.tar.gz
    echo "|---------------------[正在安装在线包]----------------------|"
    cd ${installpath}
    #mkdir -p logs ${PRENAME}/lib && touch logs/{openssl.log,pcre.log,zlib.log,nginx.log}
    tar -zxvf zlib-${zlibv}.tar.gz -C ${PRENAME}/lib >/dev/null 2>&1
    cd ${PRENAME}/lib/zlib* && PCREPATH=`pwd`
    ./configure --prefix=${PCREPATH} >${installpath}/logs/zlib.log >/dev/null 2>&1
    if [[ $? -ne 0 ]]; then
        return 1
    else
        make && make install >${installpath}/logs/zlib.log
        if [[ $? -ne 0 ]]; then
            return 1
        fi
        return 0
    fi
}
#----------------------------------------------------ZLIBEND-------------------------------------------------------#
#---------------------------------------------------STRATNGINX-----------------------------------------------------#
function nginx(){
    echo "|-------------------------- NGINX --------------------------|"
    echo
    ngin=`ls | grep nginx-*.tar.gz`
    if [[ "$ngin" != "" ]]
    then
        echo "|-----------------------[发现离线压包]-----------------------|"
        /usr/bin/sleep 3
        nginxinstall_off
    else
        echo "|-----------------------[未发现离线包]-----------------------|"
        echo "|-----------------[开始判断是否连接外网安装]-----------------|"
        /usr/bin/sleep 3
        onnginx
    fi
}
function nginxinstall_off(){
    echo "|---------------------[正在安装离线包]----------------------|"
    cd ${installpath}
    initialize
    tar -zxvf nginx-${nginx_version}.tar.gz >/dev/null 2>&1
    cd nginx-* && NGINXPATH=`pwd`
    ./configure --prefix=${installpath}/${PRENAME} --with-pcre=${PCREPATH} --with-openssl=${OPENSSLPATH} --with-zlib=${ZLIBPATH}
    if [[ $? -ne 0 ]]; then
        return 1
    else
        make && make install >${installpath}/logs/nginx.log
        if [[ $? -ne 0 ]]; then
            return 1
        fi
        return 0
    fi
}
function onnginx(){
    wget http://nginx.org/download/nginx-${nginxv}.tar.gz
    echo "|---------------------[正在安装在线包]----------------------|"
    cd ${installpath}
    initialize
    #mkdir -p logs ${PRENAME}/lib && touch logs/{openssl.log,pcre.log,zlib.log,nginx.log}
    tar -zxvf nginx-${nginxv}.tar.gz >/dev/null 2>&1
    cd nginx-* && NGINXPATH=`pwd`
    ./configure --prefix=${installpath}/${PRENAME} --with-pcre=${PCREPATH} --with-openssl=${OPENSSLPATH} --with-zlib=${ZLIBPATH}
    if [[ $? -ne 0 ]]; then
        return 1
    else
        make && make install >${installpath}/logs/nginx.log
        if [[ $? -ne 0 ]]; then
            return 1
        fi
        return 0
    fi
}
#----------------------------------------------------NGINXEND-------------------------------------------------------#
function ok(){
echo "|****************************************************************************************************************|"
echo "|            WW             WW EEEEEEE LL     CCCCC   OOOOOO      MM      MM     EEEEEEE                         |"
echo "|             WW    WWWW   WW  EE      LL    CC      OO    OO    MMMM    MMMM    EE                              |"
echo "|              WW  WW WW  WW   EEEEE   LL   CC      OO      OO  MM  MM  MM  MM   EEEEE                           |"
echo "|               WW W   W WW    EE      LL    CC      OO    OO  MM    M M     MM  EE                              |"
echo "|                WW     WW     EEEEEEE LLLLLL CCCCC   OOOOOO  MM     MMM      MM EEEEEEE                         |"
echo "|****************************************************************************************************************|"
}
function main(){
environment
openssl
pcre
zlib
nginx
ok
}
main