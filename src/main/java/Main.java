import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.*;

import static java.lang.System.out;

public class Main {
    private static String connectURL(String dest_url, String commString) {
        String rec_string = "";
        URL url = null;
        HttpURLConnection urlconn = null;
        OutputStream out1 = null;
        BufferedReader rd = null;
        try {
            url = new URL(dest_url);
            urlconn = (HttpURLConnection) url.openConnection();
            urlconn.setReadTimeout(1000 * 30);
            //urlconn.setRequestProperty("content-type", "text/html;charset=UTF-8");
            urlconn.setRequestMethod("POST");
            urlconn.setDoInput(true);
            urlconn.setDoOutput(true);
            out1 = urlconn.getOutputStream();
            out1.write(commString.getBytes("UTF-8"));
            out1.flush();
            out1.close();
            rd = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            int ch;
            while ((ch = rd.read()) > -1)
                sb.append((char) ch);
            rec_string = sb.toString();
        } catch (Exception e) {
            out.println(e);
            return "";
        } finally {
            try {
                if (out1 != null) {
                    out1.close();
                }
                if (urlconn != null) {
                    urlconn.disconnect();
                }
                if (rd != null) {
                    rd.close();
                }
            } catch (Exception e) {
                out.println(e);
            }
        }
        return rec_string;
    }

    public static void main(String[] args) throws IOException {
        File file = new File("data/drivingdatarealpos.txt");
        File ofile = new File("data/drivingdatarealposbaidu.txt");
        Reader r1 = new FileReader(file);
        Writer w1 = new FileWriter(ofile);
        BufferedReader r = new BufferedReader(r1);
        BufferedWriter w = new BufferedWriter(w1);
        JsonParser parse =new JsonParser();  //创建json解析器
        while (r.ready()) {
            String l = r.readLine();
            String result = connectURL("http://api.map.baidu.com/geoconv/v1/?coords=" +
                    l + "&from=1&to=5&output=json&ak=eA7U8IzN1oX6EZj7r0y7ynsGXfUENK2c", "");
            JsonObject json=(JsonObject) parse.parse(result);  //创建jsonObject对象
            JsonElement e = json.get("result").getAsJsonArray().get(0);
            double lo = e.getAsJsonObject().get("x").getAsDouble();
            double la = e.getAsJsonObject().get("y").getAsDouble();
            w.write(lo+","+la+"\n");
            w.flush();
            out.println("转换坐标ok:" + l);
        }
        w.flush();
        String coords = "106.6519570767,26.6245856997";
        String result = connectURL("http://api.map.baidu.com/geoconv/v1/?coords=" + coords + "&from=1&to=5&output=json&ak=eA7U8IzN1oX6EZj7r0y7ynsGXfUENK2c", "");
        out.println(result);
        r.close();
        r1.close();
        w.close();
        w1.close();
    }
}
