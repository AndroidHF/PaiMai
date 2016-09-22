package com.buycolle.aicang.api.request;

import android.text.TextUtils;
import android.util.Pair;
import android.widget.ImageView;

import com.buycolle.aicang.api.OkHttpClientManager;
import com.buycolle.aicang.api.callback.ResultCallback;
import com.buycolle.aicang.util.Md5Util;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;


/**
 * Created by zhy on 15/11/6.
 */
public abstract class OkHttpRequest {
    protected OkHttpClientManager mOkHttpClientManager = OkHttpClientManager.getInstance();
    protected OkHttpClient mOkHttpClient;

    protected RequestBody requestBody;
    protected Request request;

    protected String url;
    protected String tag;
    protected boolean isNeedAppHeader = true;
    protected Map<String, String> params;
    protected Map<String, String> headers;

    protected OkHttpRequest(String url, String tag,
                            Map<String, String> params, Map<String, String> headers) {
        mOkHttpClient = mOkHttpClientManager.getOkHttpClient();
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
    }

    public boolean isNeedAppHeader() {
        return isNeedAppHeader;
    }

    public void setIsNeedAppHeader(boolean isNeedAppHeader) {
        this.isNeedAppHeader = isNeedAppHeader;
    }

    protected abstract Request buildRequest();

    protected abstract RequestBody buildRequestBody();

    protected void prepareInvoked(ResultCallback callback) {
        requestBody = buildRequestBody();
        requestBody = wrapRequestBody(requestBody, callback);
        request = buildRequest();
    }


    public void invokeAsyn(ResultCallback callback) {
        prepareInvoked(callback);
        mOkHttpClientManager.execute(request, callback);
    }

    protected RequestBody wrapRequestBody(RequestBody requestBody, final ResultCallback callback) {
        return requestBody;
    }


    public <T> T invoke(Class<T> clazz) throws IOException {
        requestBody = buildRequestBody();
        Request request = buildRequest();
        return mOkHttpClientManager.execute(request, clazz);
    }


    protected void appendHeaders(Request.Builder builder, Map<String, String> headers) {
        if (builder == null) {
            throw new IllegalArgumentException("builder can not be empty!");
        }

        Headers.Builder headerBuilder = new Headers.Builder();

        String timesTamp = System.currentTimeMillis()+"";
        String noneStr = (int)(Math.random()*100)+"";
        String secretkey = "paimaiapp";
        String version = "v1";
        String joinSign = timesTamp+"="+noneStr+"="+secretkey+"="+version;
        String checkSign = Md5Util.getEncodeByMD5(Md5Util.getEncodeByMD5(joinSign));

        headerBuilder.add("mj-timestamp",timesTamp);
        headerBuilder.add("mj-nonestr",noneStr);
        headerBuilder.add("mj-sign",checkSign);
        headerBuilder.add("mj-v",version);
        if (headers == null || headers.isEmpty()) {
        } else {
            builder.headers(headerBuilder.build());
            for (String key : headers.keySet()) {
                headerBuilder.add(key, headers.get(key));
            }
        }
        builder.headers(headerBuilder.build());
    }

    public void cancel() {
        if (!TextUtils.isEmpty(tag))
            mOkHttpClientManager.cancelTag(tag);
    }


    public static class Builder {
        private String url;
        private String tag;
        private Map<String, String> headers;
        private Map<String, String> params;
        private Pair<String, File>[] files;

        private String destFileDir;
        private String destFileName;

        private ImageView imageView;
        private int errorResId = -1;

        //for post
        private String content;
        private byte[] bytes;
        private File file;
        private JSONObject jsonObject;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder jsonObject(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder params(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public Builder addParams(String key, String val) {
            if (this.params == null) {
                params = new IdentityHashMap<>();
            }
            params.put(key, val);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder addHeader(String key, String val) {
            if (this.headers == null) {
                headers = new IdentityHashMap<>();
            }
            headers.put(key, val);
            return this;
        }


        public Builder files(Pair<String, File>... files) {
            this.files = files;
            return this;
        }

        public Builder destFileName(String destFileName) {
            this.destFileName = destFileName;
            return this;
        }

        public Builder destFileDir(String destFileDir) {
            this.destFileDir = destFileDir;
            return this;
        }


        public Builder imageView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public Builder errResId(int errorResId) {
            this.errorResId = errorResId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public <T> T get(Class<T> clazz) throws IOException {
            OkHttpRequest request = new OkHttpGetRequest(url, tag, params, headers,jsonObject);
            return request.invoke(clazz);
        }

        public OkHttpRequest get(ResultCallback callback) {
            OkHttpRequest request = new OkHttpGetRequest(url, tag, params, headers,jsonObject);
            request.invokeAsyn(callback);
            return request;
        }

        public <T> T post(Class<T> clazz) throws IOException {
            OkHttpRequest request = new OkHttpPostRequest(url, tag, params, headers, content, bytes, file, jsonObject);
            return request.invoke(clazz);
        }

        public OkHttpRequest post(ResultCallback callback) {
            OkHttpRequest request = new OkHttpPostRequest(url, tag, params, headers, content, bytes, file, jsonObject);
            request.invokeAsyn(callback);
            return request;
        }

        public OkHttpRequest upload(ResultCallback callback) {
            OkHttpRequest request = new OkHttpUploadRequest(url, tag, params, headers, files);
            request.invokeAsyn(callback);
            return request;
        }

        public <T> T upload(Class<T> clazz) throws IOException {
            OkHttpRequest request = new OkHttpUploadRequest(url, tag, params, headers, files);
            return request.invoke(clazz);
        }


        public OkHttpRequest download(ResultCallback callback) {
            OkHttpRequest request = new OkHttpDownloadRequest(url, tag, params, headers, destFileName, destFileDir);
            request.invokeAsyn(callback);
            return request;
        }
        public OkHttpRequest download(ResultCallback callback,boolean isNeedHeader) {
            OkHttpRequest request = new OkHttpDownloadRequest(url, tag, params, headers, destFileName, destFileDir);
            if(isNeedHeader){
                request.setIsNeedAppHeader(true);
                request.invokeAsyn(callback);
            }else{
                request.setIsNeedAppHeader(false);
                request.invokeAsyn(callback);
            }
            return request;
        }

        public String download() throws IOException {
            OkHttpRequest request = new OkHttpDownloadRequest(url, tag, params, headers, destFileName, destFileDir);
            return request.invoke(String.class);
        }

        public void displayImage(ResultCallback callback) {
            OkHttpRequest request = new OkHttpDisplayImgRequest(url, tag, params, headers, imageView, errorResId);
            request.invokeAsyn(callback);
        }


    }


}