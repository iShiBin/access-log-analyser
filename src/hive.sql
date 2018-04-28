create table access_log(country string, dt timestamp, cat string, count int)
row format serde 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe' with
serdeproperties('field.delim'='\t','timestamp.formats'='yyyy-MM-dd HH:mm:ss')
stored as textfile;

-- hadoop fs -put part* /user/hive/warehouse/access_log

create table access_log_partitioned(country string, dt timestamp, cat string, count int) partitioned by(d date) row format serde 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe' with serdeproperties('field.delim'='\t', 'timestamp.formats'='yyyy-MM-dd HH:mm:ss') stored as textfile;

set hive.exec.dynamic.partition.mode=nonstrict;
insert overwrite table access_log_partitioned partition(d) select country, dt, cat, count, cast(dt as date) from access_log;
show partitions access_log_partitioned;

