del as.package.js
del as.package.min.js
for /f %%i in (package-list.txt) do type %%i >> as.package.js   
java -jar ../yuicompressor-2.4.6.jar --type js --charset utf-8  as.package.js -o as.package.min.js   

