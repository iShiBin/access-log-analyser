-- map IP -> country, city using using GeoLiteCity.dat http://dev.maxmind.com/geoip/legacy/install/city/ 
register /home/hadoop/resource/pig-udf-0.0.1-SNAPSHOT.jar
register /home/hadoop/resource/geoip-api-1.3.1.jar

-- the .dat should be available in HDFS so that each node could get and use it locally
fs -get cloudacl/resource/GeoLiteCity.dat

a = LOAD '$INPUT' AS (line:chararray);

-- filter the Android app url using regular expression
b = FOREACH a GENERATE flatten(REGEX_EXTRACT_ALL(line, '(.*?) .*?\\[(.*?)\\].*?&cat=(.*?) .*')) AS (ip:chararray, dt:chararray, cat:chararray);

c = FILTER b BY ip IS NOT null;

-- get country geoinformation
d = FOREACH c generate ip, com.example.pig.GetCountry(ip) AS country, ToString(ToDate(dt, 'dd/MMM/yyyy:HH:mm:ss +0000'), 'yyyy-MM-dd HH:00:00') AS dt, cat;
e = FILTER d BY country IS NOT null;

-- aggregate using country, date and catetory
f = GROUP e BY (country, dt, cat);

g = FOREACH f GENERATE flatten(group), COUNT(e);

-- save the output
STORE g INTO '$OUTPUT';
~                       
