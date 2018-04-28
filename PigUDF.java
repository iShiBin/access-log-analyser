/** 
* Map IP to country. Register this jar using `register file.jar` to use it in Pig. 

* Notes:
* If your UDF is in other jar, register it
* You shall also register dependent jars
* Pig will ship registered jars to backend (put in CLASSPATH won’t help)
*/

public class PigUDF extends EvalFunc<Tuple> {
    public String exec(Tuple t) throws IOException {
        if (cl == null) {
            cl = new LookupService("GeoLiteCity.dat", LookupService.GEOIP_MEMORY_CACHE);
        }
        Location loc = cl.getLocation((String) t.get(0));
        if (loc == null) {
            return null;
        }
        return loc.countryCode;
    }
}
