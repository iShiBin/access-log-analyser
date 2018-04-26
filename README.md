# Access Log Analyser

* How many users visited this website each day?
* What is the busiest hour?
* Which countries did the traffic come from, and what is the trend of the traffic looks like?  
  Note: Use 3rd party package to map IP with Geoinformation.
* What is the most frequently visited apps category in US?

I will use multiple utilities to answer above questions, inculding but not least to the following:

- Hadoop single node
- Hadoop cluster (Amazon EMR)
- Flink
- Pig
- Hive
- Superset
- Spark

# Dataset

## Access Log

- one month web access log

* 93 data files
* ~ 2G each, 200G in total

**Format**
The raw data is in plain text format. Here is some sample data.
```
190.152.211.196 - - [01/Oct/2017:00:00:00 +0000] "GET /axis2/services/WebFilteringService/getCategoryByUrl?app=chrome_antiporn&ver=0.19.7.1&url=http%3A//www.frlp.utn.edu.ar/materias/integracion3/UT_4_Nomenclatura.pdf&cat=educational-institution HTTP/1.1" 200 133 "-" "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"
190.239.213.115 - - [01/Oct/2017:00:00:00 +0000] "GET /axis2/services/WebFilteringService/getCategoryByUrl?app=chrome_antiporn&ver=0.19.7.1&url=https%3A//www.facebook.com/%3Fstype%3Dlo%26jlou%3DAffAmShI68yNsw-M1-lsS95fsGkzzVgUjyfrS0wKpqjYU_CeCg9VA46WrDXqkYa_nBNdZ9Lx4YOFp0Z8wD_Py2NpH4f1TyNIowTiyRhzZ9lNng%26smuh%3D21435%26lh%3DAc_RQNgTl6mXYKuA&cat=social-networking HTTP/1.1" 200 133 "-" "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36"
181.67.127.161 - - [30/Sep/2017:23:59:59 +0000] "GET /axis2/services/WebFilteringService/getCategoryByUrl?app=chrome_antiporn&ver=0.19.7.1&url=https%3A//login.live.com/login.srf%3Flc%3D3082%26sf%3D1%26id%3D64855%26tw%3D18000%26fs%3D0%26ts%3D-1%26cbcxt%3Dmai%26sec%3D%26mspp_shared%3D1%26seclog%3D10%26claims%3D%26wa%3Dwsignin1.0%26wp%3DMBI_SSL_SHARED%26ru%3Dhttps%3A//mail.live.com/default.aspx%253frru%253dinbox%26contextid%3D5F7FCB48E5C87574&cat=internet-portal HTTP/1.1" 200 133 "-" "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"
```

**List**
```
2018-03-12T09:38:23.000Z        1.8 GB         localhost_access_log.2017-10-01.ip-40-0-0-214.txt
2018-03-12T09:40:59.000Z        1.8 GB         localhost_access_log.2017-10-01.ip-40-0-0-216.txt
2018-03-12T09:43:16.000Z        1.8 GB         localhost_access_log.2017-10-01.ip-40-0-0-229.txt
2018-03-12T09:55:00.000Z        2.0 GB         localhost_access_log.2017-10-02.ip-40-0-0-214.txt
2018-03-12T09:57:59.000Z        2.0 GB         localhost_access_log.2017-10-02.ip-40-0-0-216.txt
2018-03-12T10:01:12.000Z        2.1 GB         localhost_access_log.2017-10-02.ip-40-0-0-229.txt
2018-03-12T10:14:04.000Z        2.1 GB         localhost_access_log.2017-10-03.ip-40-0-0-214.txt
2018-03-12T10:17:14.000Z        2.1 GB         localhost_access_log.2017-10-03.ip-40-0-0-216.txt
2018-03-12T10:20:17.000Z        2.1 GB         localhost_access_log.2017-10-03.ip-40-0-0-229.txt
2018-03-12T10:33:42.000Z        2.1 GB         localhost_access_log.2017-10-04.ip-40-0-0-214.txt
2018-03-12T10:37:06.000Z        2.1 GB         localhost_access_log.2017-10-04.ip-40-0-0-216.txt
2018-03-12T10:41:06.000Z        2.1 GB         localhost_access_log.2017-10-04.ip-40-0-0-229.txt
2018-03-12T10:54:59.000Z        2.1 GB         localhost_access_log.2017-10-05.ip-40-0-0-214.txt
2018-03-12T10:58:29.000Z        2.1 GB         localhost_access_log.2017-10-05.ip-40-0-0-216.txt
2018-03-12T11:02:45.000Z        2.1 GB         localhost_access_log.2017-10-05.ip-40-0-0-229.txt
2018-03-12T11:15:14.000Z        1.9 GB         localhost_access_log.2017-10-06.ip-40-0-0-214.txt
2018-03-12T11:18:54.000Z        1.9 GB         localhost_access_log.2017-10-06.ip-40-0-0-216.txt
2018-03-12T11:22:22.000Z        1.9 GB         localhost_access_log.2017-10-06.ip-40-0-0-229.txt
2018-03-12T11:32:25.000Z        1.7 GB         localhost_access_log.2017-10-07.ip-40-0-0-214.txt
2018-03-12T11:35:47.000Z        1.7 GB         localhost_access_log.2017-10-07.ip-40-0-0-216.txt
2018-03-12T11:38:39.000Z        1.8 GB         localhost_access_log.2017-10-07.ip-40-0-0-229.txt
2018-03-12T11:48:47.000Z        1.8 GB         localhost_access_log.2017-10-08.ip-40-0-0-214.txt
2018-03-12T11:52:07.000Z        1.8 GB         localhost_access_log.2017-10-08.ip-40-0-0-216.txt
2018-03-12T11:55:30.000Z        1.8 GB         localhost_access_log.2017-10-08.ip-40-0-0-229.txt
2018-03-12T12:08:29.000Z        2.2 GB         localhost_access_log.2017-10-09.ip-40-0-0-214.txt
2018-03-12T12:13:58.000Z        2.1 GB         localhost_access_log.2017-10-09.ip-40-0-0-216.txt
2018-03-12T12:19:03.000Z        2.2 GB         localhost_access_log.2017-10-09.ip-40-0-0-229.txt
2018-03-12T12:36:50.000Z        2.4 GB         localhost_access_log.2017-10-10.ip-40-0-0-214.txt
2018-03-12T12:43:05.000Z        2.4 GB         localhost_access_log.2017-10-10.ip-40-0-0-216.txt
2018-03-12T12:48:27.000Z        2.4 GB         localhost_access_log.2017-10-10.ip-40-0-0-229.txt
2018-03-12T13:06:47.000Z        2.3 GB         localhost_access_log.2017-10-11.ip-40-0-0-214.txt
2018-03-12T13:12:51.000Z        2.3 GB         localhost_access_log.2017-10-11.ip-40-0-0-216.txt
2018-03-12T13:18:29.000Z        2.3 GB         localhost_access_log.2017-10-11.ip-40-0-0-229.txt
2018-03-12T13:35:22.000Z        2.3 GB         localhost_access_log.2017-10-12.ip-40-0-0-214.txt
2018-03-12T13:38:59.000Z        2.4 GB         localhost_access_log.2017-10-12.ip-40-0-0-216.txt
2018-03-12T13:43:25.000Z        2.3 GB         localhost_access_log.2017-10-12.ip-40-0-0-229.txt
2018-03-12T13:57:54.000Z        2.3 GB         localhost_access_log.2017-10-13.ip-40-0-0-214.txt
2018-03-12T14:02:12.000Z        2.3 GB         localhost_access_log.2017-10-13.ip-40-0-0-216.txt
2018-03-12T14:06:04.000Z        2.2 GB         localhost_access_log.2017-10-13.ip-40-0-0-229.txt
2018-03-12T14:18:56.000Z        2.2 GB         localhost_access_log.2017-10-14.ip-40-0-0-214.txt
2018-03-12T14:22:11.000Z        2.2 GB         localhost_access_log.2017-10-14.ip-40-0-0-216.txt
2018-03-12T14:25:15.000Z        2.2 GB         localhost_access_log.2017-10-14.ip-40-0-0-229.txt
2018-03-12T14:38:13.000Z        2.1 GB         localhost_access_log.2017-10-15.ip-40-0-0-214.txt
2018-03-12T14:41:33.000Z        2.1 GB         localhost_access_log.2017-10-15.ip-40-0-0-216.txt
2018-03-12T14:45:02.000Z        2.1 GB         localhost_access_log.2017-10-15.ip-40-0-0-229.txt
2018-03-12T15:00:00.000Z        2.4 GB         localhost_access_log.2017-10-16.ip-40-0-0-214.txt
2018-03-12T15:03:56.000Z        2.4 GB         localhost_access_log.2017-10-16.ip-40-0-0-216.txt
2018-03-12T15:08:05.000Z        2.4 GB         localhost_access_log.2017-10-16.ip-40-0-0-229.txt
2018-03-12T15:25:22.000Z        2.4 GB         localhost_access_log.2017-10-17.ip-40-0-0-214.txt
2018-03-12T15:28:56.000Z        2.4 GB         localhost_access_log.2017-10-17.ip-40-0-0-216.txt
2018-03-12T15:33:12.000Z        2.4 GB         localhost_access_log.2017-10-17.ip-40-0-0-229.txt
2018-03-12T15:49:24.000Z        2.4 GB         localhost_access_log.2017-10-18.ip-40-0-0-214.txt
2018-03-12T15:54:53.000Z        2.3 GB         localhost_access_log.2017-10-18.ip-40-0-0-216.txt
2018-03-12T15:58:47.000Z        2.4 GB         localhost_access_log.2017-10-18.ip-40-0-0-229.txt
2018-03-12T16:14:54.000Z        2.3 GB         localhost_access_log.2017-10-19.ip-40-0-0-214.txt
2018-03-12T16:19:59.000Z        2.3 GB         localhost_access_log.2017-10-19.ip-40-0-0-216.txt
2018-03-12T16:24:59.000Z        2.3 GB         localhost_access_log.2017-10-19.ip-40-0-0-229.txt
2018-03-12T16:39:10.000Z        2.2 GB         localhost_access_log.2017-10-20.ip-40-0-0-214.txt
2018-03-12T16:43:52.000Z        2.1 GB         localhost_access_log.2017-10-20.ip-40-0-0-216.txt
2018-03-12T16:48:28.000Z        2.2 GB         localhost_access_log.2017-10-20.ip-40-0-0-229.txt
2018-03-12T17:00:34.000Z        1.9 GB         localhost_access_log.2017-10-21.ip-40-0-0-214.txt
2018-03-12T17:05:21.000Z        1.8 GB         localhost_access_log.2017-10-21.ip-40-0-0-216.txt
2018-03-12T17:09:20.000Z        1.9 GB         localhost_access_log.2017-10-21.ip-40-0-0-229.txt
2018-03-12T17:21:01.000Z        1.8 GB         localhost_access_log.2017-10-22.ip-40-0-0-214.txt
2018-03-12T17:25:37.000Z        1.8 GB         localhost_access_log.2017-10-22.ip-40-0-0-216.txt
2018-03-12T17:31:13.000Z        1.8 GB         localhost_access_log.2017-10-22.ip-40-0-0-229.txt
2018-03-12T17:47:59.000Z        2.2 GB         localhost_access_log.2017-10-23.ip-40-0-0-214.txt
2018-03-12T17:52:42.000Z        2.2 GB         localhost_access_log.2017-10-23.ip-40-0-0-216.txt
2018-03-12T17:57:45.000Z        2.3 GB         localhost_access_log.2017-10-23.ip-40-0-0-229.txt
2018-03-12T18:15:23.000Z        2.2 GB         localhost_access_log.2017-10-24.ip-40-0-0-214.txt
2018-03-12T18:22:17.000Z        2.2 GB         localhost_access_log.2017-10-24.ip-40-0-0-216.txt
2018-03-12T18:28:52.000Z        2.3 GB         localhost_access_log.2017-10-24.ip-40-0-0-229.txt
2018-03-12T18:45:17.000Z        2.2 GB         localhost_access_log.2017-10-25.ip-40-0-0-214.txt
2018-03-12T18:50:17.000Z        2.2 GB         localhost_access_log.2017-10-25.ip-40-0-0-216.txt
2018-03-12T18:54:31.000Z        2.2 GB         localhost_access_log.2017-10-25.ip-40-0-0-229.txt
2018-03-12T19:08:44.000Z        2.2 GB         localhost_access_log.2017-10-26.ip-40-0-0-214.txt
2018-03-12T19:12:28.000Z        2.2 GB         localhost_access_log.2017-10-26.ip-40-0-0-216.txt
2018-03-12T19:16:43.000Z        2.2 GB         localhost_access_log.2017-10-26.ip-40-0-0-229.txt
2018-03-12T19:28:12.000Z        2.0 GB         localhost_access_log.2017-10-27.ip-40-0-0-214.txt
2018-03-12T19:31:07.000Z        2.0 GB         localhost_access_log.2017-10-27.ip-40-0-0-216.txt
2018-03-12T19:34:40.000Z        2.1 GB         localhost_access_log.2017-10-27.ip-40-0-0-229.txt
2018-03-12T19:45:12.000Z        1.7 GB         localhost_access_log.2017-10-28.ip-40-0-0-214.txt
2018-03-12T19:47:41.000Z        1.7 GB         localhost_access_log.2017-10-28.ip-40-0-0-216.txt
2018-03-12T19:50:29.000Z        1.7 GB         localhost_access_log.2017-10-28.ip-40-0-0-229.txt
2018-03-12T19:59:31.000Z        1.7 GB         localhost_access_log.2017-10-29.ip-40-0-0-214.txt
2018-03-12T20:02:07.000Z        1.7 GB         localhost_access_log.2017-10-29.ip-40-0-0-216.txt
2018-03-12T20:05:05.000Z        1.7 GB         localhost_access_log.2017-10-29.ip-40-0-0-229.txt
2018-03-12T20:17:21.000Z        2.1 GB         localhost_access_log.2017-10-30.ip-40-0-0-214.txt
2018-03-12T20:20:55.000Z        2.1 GB         localhost_access_log.2017-10-30.ip-40-0-0-216.txt
2018-03-12T20:24:18.000Z        2.1 GB         localhost_access_log.2017-10-30.ip-40-0-0-229.txt
2018-03-12T21:23:24.000Z        2.1 GB         localhost_access_log.2017-10-31.ip-40-0-0-214.txt
2018-03-12T21:29:56.000Z        2.0 GB         localhost_access_log.2017-10-31.ip-40-0-0-216.txt
2018-03-12T21:33:42.000Z        2.1 GB         localhost_access_log.2017-10-31.ip-40-0-0-229.txt
```
**Link**
https://s3.amazonaws.com/cloudacl/access_log/index.html

**Sample**
https://s3-us-west-2.amazonaws.com/neu-is/big-data/sample/input/access_log_sample.txt

## Android App Metadata
This is the reference data to get the catetory of each android application by id.

