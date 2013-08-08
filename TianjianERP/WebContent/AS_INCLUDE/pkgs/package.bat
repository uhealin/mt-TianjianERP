del pkgs.package.js
del pkgs.package.min.js
for /f %%i in (package-list.txt) do type %%i >> pkgs.package.js   
java -jar ../../yuicompressor-2.4.6.jar --type js --charset utf-8  pkgs.package.js -o pkgs.package.min.js   

