fs -get s3://cloudacl/code/GeoLiteCity.dat

register s3://cloudacl/code/pig-udf-0.0.1-SNAPSHOT.jar
register s3://cloudacl/code/geoip-api-1.3.1.jar
register s3://cloudacl/code/nutch-1.12.jar

a1 = LOAD '$INPUT' AS (line:chararray);
a2 = filter a1 by line matches '.*&url=(https:|http:|https%3A|http%3A)//play.google.com/store/apps/details.*';
a3 = FOREACH a2 GENERATE flatten(REGEX_EXTRACT_ALL(line, '(.*?) .*?\\[(.*?)\\].*?&url=(.*?)(?:&|%26).*')) as (ip:chararray, dt:chararray, url:chararray);
a4 = FOREACH a3 GENERATE ip, flatten(REGEX_EXTRACT_ALL(url, '.*?id(?:=|%3D)(.*)?')) as (id:chararray);
a5 = FILTER a4 by ip is not null;
a6 = FOREACH a5 generate ip, com.example.pig.GetCountry(ip) as country, id;

a7 = DISTINCT a6;
a8 = FILTER a7 by country == 'US';

b1 = load '$INPUT2' using com.example.pig.NutchParsedDataLoader();
b2 = filter b1 by $0 is not null;
b3 = foreach b2 generate flatten(REGEX_EXTRACT_ALL(url, '.*?id=(.*)?')) as (id:chararray), category;

c = join a8 by id, b3 by id;
d = group c by category;
e = foreach d generate group, COUNT(c) as count;
f = order e by count desc;

store f into '$OUTPUT';
