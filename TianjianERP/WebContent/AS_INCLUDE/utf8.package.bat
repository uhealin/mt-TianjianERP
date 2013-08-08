del utf8.package.js
del utf8.package.min.js
for /f %%i in (utf8.package-list.txt) do type %%i >> utf8.package.js   
java -jar ../yuicompressor-2.4.6.jar --type js --charset utf-8  utf8.package.js -o utf8.package.min.js   

