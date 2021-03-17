package com.lixw.web.jolokia;

import org.jolokia.client.J4pClient;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pReadResponse;
import org.jolokia.client.request.J4pResponse;

import javax.management.MalformedObjectNameException;
import java.util.Map;

/**
 * @author lixw
 * @date 2021/03/17
 */
public class JolokiaBootstrapDemo {

    public static void main(String[] args) throws Exception {
        J4pClient j4pClient = new J4pClient("http://localhost:8080/jolokia-haha");
        J4pReadRequest req = new J4pReadRequest("java.lang:type=Memory",
                "HeapMemoryUsage");

        J4pReadResponse resp = j4pClient.execute(req);
        Map<String, Long> vals = resp.getValue();
        long used = vals.get("used");
        long max = vals.get("max");
        int usage = (int) (used * 100 / max);
        System.out.println("Memory usage: used: " + used +
                " / max: " + max + " = " + usage + "%");
        J4pReadRequest threadReq = new J4pReadRequest("java.lang:type=Threading", "ThreadCount");
        J4pResponse<J4pReadRequest> rs = j4pClient.execute(threadReq);
        Object value = rs.getValue();
        System.out.println(value);
    }
}
