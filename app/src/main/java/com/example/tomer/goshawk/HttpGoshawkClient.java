package com.example.tomer.goshawk;

import android.app.Activity;
import android.content.Context;
import android.content.Entity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tomer on 07/03/2016.
 */
public class HttpGoshawkClient extends AsyncTask<String, String, String> {

    //members
    HttpParams params;
    DefaultHttpClient httpClient;
    Connection connectionActivity;
    String respond = "";
    String URL = "http://10.0.0.6:8888/app/";
    List<NameValuePair> NVList;
    CookieStore cookieStore;
    CookieManager cookieManager;
    boolean isLogin = false;
    boolean isSnapshot = false;
    Bitmap bmp;
    String imageNum = "1";
    private HttpResponse response;


    public HttpGoshawkClient(Connection connectionActivity) {
        this.connectionActivity = connectionActivity;
        SharedPreferences settings = connectionActivity.GetSharedPreferences();
        URL = this.connectionActivity.GetUrl() + ":8888/app/";
        NVList = new ArrayList<NameValuePair>();
        cookieManager = CookieManager.getInstance();

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setSoTimeout(httpParameters, 15000);
        HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
        httpClient = new DefaultHttpClient(httpParameters);
    }


    protected String doInBackground(String... params) {
        try {
            HttpContext localContext = new BasicHttpContext();

            HttpEntity entity;
            if (params.length < 2) {
                return "Error!";
            }
            if (params[0].equals("Get")) {
                HttpGet httpGet = new HttpGet(URL + params[1]);
                httpGet.setHeader("Cookie", getCookie() + ";");
                response = httpClient.execute(httpGet, localContext);
                if (isSnapshot) {
                    handleSnapshot();
                    return "Success";
                }
            } else {
                HttpPost httpPost = new HttpPost(URL + params[1]);
                httpPost.setEntity(new UrlEncodedFormEntity(NVList));
                if (isLogin) {
                    response = httpClient.execute(httpPost, localContext);
                    List<Cookie> cookies = httpClient.getCookieStore().getCookies();
                    if (cookies.size() > 0) {
                        setCookie(cookies.get(0));
                    }
                    isLogin = false;
                } else {
                    httpPost.setHeader("Cookie", getCookie() + ";");
                    response = httpClient.execute(httpPost, localContext);
                }
            }
            entity = response.getEntity();
            respond = getASCIIContentFromEntity(entity);
            NVList.clear();
        } catch (Exception e) {

            return e.toString();
        }
        return respond;
    }

    private void handleSnapshot() {
        isSnapshot = false;
        try {
            // if (response.getStatusLine().getStatusCode() == 200) {
            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //    int size1 = (int)response.getEntity().getContentLength();
                 /*   int size = 5048576;
                    BufferedInputStream bis = new BufferedInputStream(instream, 512);
                    byte[] data = new byte[size];
                    int bytesRead = 0;
                    int offset = 0;
                    while(bytesRead != -1 && offset < size) {
                        bytesRead = bis.read(data, offset, size - offset);
                        offset += bytesRead;
                    }
                    bmp = BitmapFactory.decodeByteArray(data, 0, data.length);*/
                InputStream instream = entity.getContent();
                BufferedInputStream bis = new BufferedInputStream(instream);
                String filePath = "/storage/sdcard0/Goshawk/" + imageNum + ".jpg";
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                int inByte;
                byte[] data = new byte[1024];
                while ((inByte = bis.read()) != -1) bos.write(inByte);

                bis.close();
                bos.close();
                return;
                  /*  InputStream instream = entity.getContent();
                    String path = "/storage/sdcard0/Goshawk/" + imageNum + ".jpg";
                    File f = new File(path);
                    if (!f.exists())
                        f.createNewFile();
                    FileOutputStream output = new FileOutputStream(path);
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    int len = 0;
                    while ((len = instream.read(buffer)) != -1) {
                        output.write(buffer, 0, len);
                    }
                    output.close();*/
            }
            //  }
        } catch (Exception e) {
            e.toString();
        }
    }

    public Bitmap GetBitmap() {
        return bmp;
    }

    @Override
    protected void onPostExecute(String result) {
        this.connectionActivity.Respond(result);
    }

    protected String getASCIIContentFromEntity(HttpEntity entity)
            throws IllegalStateException, IOException {
        InputStream in = entity.getContent();
        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n > 0) {
            byte[] b = new byte[4096];
            n = in.read(b);
            if (n > 0)
                out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    public void HttpGetRequest(String getRequest) {
        this.execute("Get", getRequest);
    }

    public void HttpPostRequest(String postRequest) {
        this.execute("Post", postRequest);
    }

    private String getCookie() {
        try {
            //CookieSyncManager.createInstance(this.connectionActivity.GetActivity());
            this.connectionActivity.SetCookieSyncManager();
            CookieManager cookieManager = CookieManager.getInstance();
            String s = cookieManager.getCookie(URL).toString();
            return s;
        } catch (Exception e) {
            return "";
        }


    }

    private void setCookie(Cookie cookie) {
        CookieSyncManager.createInstance(this.connectionActivity.GetActivity());
        CookieManager cookieManager = CookieManager.getInstance();
        if (cookie != null) {
            String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain();
            cookieManager.removeAllCookie();
            cookieManager.setCookie(URL, cookieString);
            CookieSyncManager.getInstance().sync();
        }
    }

    public void Sync() {
        HttpGetRequest("sync");
    }

    public void GetLastEvent() {
        HttpGetRequest("recent_events");
    }

    public void ConnectToServer(String name, String pass) {
        isLogin = true;
        NVList.add(new BasicNameValuePair("name", name));
        NVList.add(new BasicNameValuePair("password", pass));
        HttpPostRequest("login");
    }

    public void SendMacAddress() {
        WifiManager manager = (WifiManager) this.connectionActivity.GetActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String macAddress = info.getMacAddress();
        //macAddress = macAddress.replace(":","");
        NVList.add(new BasicNameValuePair("mac", macAddress));
        HttpPostRequest("register_mac");
    }

    public void CheckProtectionStatus() {
        HttpGetRequest("status");

    }

    public void WhosHome() {
        HttpGetRequest("whos_home");
    }

    public void Arm() {
        HttpPostRequest("arm");

    }

    public void Disarm() {
        HttpPostRequest("disarm");
    }

    public void GetPermission() {
        HttpGetRequest("my_info");
    }

    public void CreateNewUser(String name, String password, String permission) {
        NVList.add(new BasicNameValuePair("name", name));
        NVList.add(new BasicNameValuePair("password", password));
        NVList.add(new BasicNameValuePair("permission", permission));
        HttpPostRequest("new_user");
    }

    public void GetSnapshot(String id) {
        isSnapshot = true;
        imageNum = id;
        // HttpGetRequest("snapshots/2");

        HttpGetRequest("snapshots/" + id);
    }


}
