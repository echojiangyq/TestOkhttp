package jyq.testokhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 作者：EchoJ on 2016/7/15 17:28 <br>
 * 邮箱：echojiangyq@gmail.com <br>
 * 描述：Okhttp工具类
 */
public class OkhttpUtil {
    private static final String SESSION_KEY = "Set-Cookie";
    private static final String mSessionKey = "JSESSIONID";
    private final long DEFAULT_CONNECT_TIMEOUT = 10L;
    private final long DEFAULT_READ_TIMEOUT = 10L;
    private static volatile OkhttpUtil instance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private Gson mGson;
    private Map<String, String> mSessions = new HashMap<String, String>();

    private OkhttpUtil() {
        mOkHttpClient = new OkHttpClient.Builder().connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS).readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS).build();
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    private OkhttpUtil(long connectTimeoutInSec, long readTimeoutInSec, InputStream... certificates) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(connectTimeoutInSec <= 0 ? DEFAULT_CONNECT_TIMEOUT : connectTimeoutInSec, TimeUnit.SECONDS).readTimeout(readTimeoutInSec <= 0 ? DEFAULT_READ_TIMEOUT : readTimeoutInSec, TimeUnit.SECONDS);
       if(certificates!=null){
           setCertificates(builder, certificates);
       }
        mOkHttpClient = builder.build();
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    public static OkhttpUtil init(long connectTimeoutInSec, long readTimeoutInSec, InputStream... certificates) {
        if (instance == null) {
            synchronized (OkhttpUtil.class) {
                if (instance == null) {
                    instance = new OkhttpUtil(connectTimeoutInSec, readTimeoutInSec, certificates);
                }
            }
        }
        return instance;
    }
    

    public static final OkhttpUtil getInstance() {
        if (instance == null) {
            synchronized (OkhttpUtil.class) {
                if (instance == null) {
                    instance = new OkhttpUtil();
                }
            }
        }
        return instance;
    }


    /**
     * get
     **/
    public static Response getSync(String url) throws IOException {
        return getInstance()._getSync(url);
    }

    public static <T> T getSync(String url, Object header, Object params, Class<T> returnType) throws Exception {
        return getInstance()._getSync(url, header, params, returnType);
    }

    public static String getAsString(String url) throws IOException {
        return getInstance()._getAsString(url);
    }

    public static void getAsyn(String url, ResultCallback callback) {
        getInstance()._getSync(url, callback);
    }


    /**
     * post
     **/
    public static Response postSync(String url, Object params) throws IOException, IllegalAccessException {
        return getInstance()._post(url, params);
    }

    public static String postSyncAsString(String url, Object params) throws IOException, IllegalAccessException {
        return getInstance()._postAsString(url, params);
    }

    public static void postAsyn(String url, final ResultCallback callback, Object header, Object params) throws Exception {
        getInstance()._postAsyn(url, callback, header, params);
    }

    public static Response postSync(String url, Param... params) throws IOException {
        return getInstance()._post(url, params);
    }

    public static String postSyncAsString(String url, Param... params) throws IOException {
        return getInstance()._postAsString(url, params);
    }

    public static void postAsyn(String url, final ResultCallback callback, Param... params) {
        getInstance()._postAsyn(url, callback, params);
    }


    /**
     * file upload
     **/
    public static Response postSync(String url, File[] files, String[] fileKeys, Param... params) throws IOException {
        return getInstance()._post(url, files, fileKeys, params);
    }

    public static Response postSync(String url, File file, String fileKey) throws IOException {
        return getInstance()._post(url, file, fileKey);
    }

    public static Response postSync(String url, File file, String fileKey, Param... params) throws IOException {
        return getInstance()._post(url, file, fileKey, params);
    }

    public static void postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, Param... params) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKeys, params);
    }

    public static void postAsyn(String url, ResultCallback callback, File file, String fileKey) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey);
    }

    public static void postAsyn(String url, ResultCallback callback, File file, String fileKey, Param... params) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey, params);
    }


    /**
     * image
     **/
    public static void displayImage(final ImageView view, String url, int errorResId) {
        getInstance()._displayImage(view, url, errorResId);
    }

    public static void displayImage(final ImageView view, String url) {
        getInstance()._displayImage(view, url, -1);
    }


    /**
     * download
     **/
    public static void downloadAsyn(String url, String destDir, ResultCallback<String> callback) {
        getInstance()._downloadAsyn(url, destDir, callback);
    }

    /**
     * 同步的Get请求
     *
     * @param url
     * @return Response
     */
    private Response _getSync(String url) throws IOException {
        final Request request = new Request.Builder().url(url).build();
        Call call = mOkHttpClient.newCall(request);
        Response execute = call.execute();
        return execute;
    }

    /**
     * 同步的Get请求
     *
     * @param url
     * @return Response
     */
    private <T> T _getSync(String url, Object header, Object params, Class<T> returnType) throws Exception {
        Request.Builder builder = new Request.Builder();
        addHeaderParam(builder, header);
        url = addGetParams(url, params);
        builder.url(url);
        final Request request = builder.build();
        Call call = mOkHttpClient.newCall(request);
        Response execute = call.execute();
        return mGson.fromJson(execute.body().string(), returnType);
    }

    private void addHeaderParam(Request.Builder builder, Object headParam) throws Exception {
        if (headParam != null) {
            List<Field> headfields = getFields(headParam.getClass(), Object.class);
            for (Field field : headfields) {
                field.setAccessible(true);
                if (field.get(headParam) != null) {
                    builder.addHeader(field.getName(), String.valueOf(field.get(headParam)));
                }
            }
        }
    }

    private String addGetParams(String url, Object param) throws IllegalAccessException {
        if (param != null) {
            List<Field> fields = getFields(param.getClass(), Object.class);
            StringBuilder sbUrl = new StringBuilder();

            for (Field field : fields) {
                field.setAccessible(true);
                if (field.get(param) != null) {
                    sbUrl.append('&');
                    sbUrl.append(field.getName());
                    sbUrl.append('=');
                    sbUrl.append(String.valueOf(field.get(param)));
                }
            }

            if (sbUrl.length() > 0) {
                sbUrl.replace(0, 1, "?");
                url += sbUrl.toString();
            }
        }
        return url;
    }

    private List<Field> getFields(Class<?> cls, Class<?> end) {

        List<Field> list = new ArrayList<Field>();

        if (!cls.equals(end)) {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                list.add(field);
            }

            Class<?> superClass = (Class<?>) cls.getGenericSuperclass();
            list.addAll(getFields(superClass, end));
        }

        return list;
    }

    /**
     * 同步的Get请求
     *
     * @param url
     * @return 字符串
     */
    private String _getAsString(String url) throws IOException {
        Response execute = _getSync(url);
        return execute.body().string();
    }

    /**
     * 异步的get请求
     *
     * @param url
     * @param callback
     */
    private void _getSync(String url, final ResultCallback callback) {
        final Request request = new Request.Builder().url(url).build();
        deliveryResult(callback, request);
    }

    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return
     */
    private Response _post(String url, Object params) throws IOException, IllegalAccessException {
        Request request = buildPostRequest(url, params);
        Response response = mOkHttpClient.newCall(request).execute();
        return response;
    }

    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return 字符串
     */
    private String _postAsString(String url, Object params) throws IOException, IllegalAccessException {
        Response response = _post(url, params);
        return response.body().string();
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final ResultCallback callback, Object params) throws IllegalAccessException {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }


    /**
     * @param url
     * @param callback
     * @param header
     * @param params
     * @throws Exception
     */
    private void _postAsyn(String url, final ResultCallback callback, Object header, Object params) throws Exception {
        Request.Builder builder = new Request.Builder();
        addHeaderParam(builder, header);
        url = addGetParams(url, params);
        builder.url(url);
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }


    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final ResultCallback callback, Map<String, String> params) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, paramsArr);
        deliveryResult(callback, request);
    }

    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return
     */
    private Response _post(String url, Param... params) throws IOException {
        Request request = buildPostRequest(url, params);
        Response response = mOkHttpClient.newCall(request).execute();
        return response;
    }


    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return 字符串
     */
    private String _postAsString(String url, Param... params) throws IOException {
        Response response = _post(url, params);
        return response.body().string();
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final ResultCallback callback, Param... params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }


    /**
     * 同步基于post的文件上传
     *
     * @param params
     * @return
     */
    private Response _post(String url, File[] files, String[] fileKeys, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        return mOkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        return mOkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 异步基于post的文件上传
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件不带参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param params
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
    private void _downloadAsyn(final String url, final String destFileDir, final ResultCallback callback) {
        final Request request = new Request.Builder().url(url).build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(destFileDir, getFileName(url));
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    sendSuccessResultCallback(file.getAbsolutePath(), callback);
                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                    }
                }
            }


        });
    }


    private Request buildMultipartFormRequest(String url, File[] files, String[] fileKeys, Param[] params) {
        params = validateParam(params);

        MultipartBody.Builder builder = new MultipartBody.Builder();

        for (Param param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""), RequestBody.create(null, param.value));
        }
        if (files != null) {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                //TODO 根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""), fileBody);
            }
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }

    //****************************

    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    /**
     * 加载图片
     *
     * @param view
     * @param url
     * @throws IOException
     */
    private void _displayImage(final ImageView view, final String url, final int errorResId) {
        final Request request = new Request.Builder().url(url).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                setErrorResId(view, errorResId);
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream is = null;
                try {
                    is = response.body().byteStream();
                    ImageUtils.ImageSize actualImageSize = ImageUtils.getImageSize(is);
                    ImageUtils.ImageSize imageViewSize = ImageUtils.getImageViewSize(view);
                    int inSampleSize = ImageUtils.calculateInSampleSize(actualImageSize, imageViewSize);
                    try {
                        is.reset();
                    } catch (IOException e) {
                        response = _getSync(url);
                        is = response.body().byteStream();
                    }

                    BitmapFactory.Options ops = new BitmapFactory.Options();
                    ops.inJustDecodeBounds = false;
                    ops.inSampleSize = inSampleSize;
                    final Bitmap bm = BitmapFactory.decodeStream(is, null, ops);
                    mDelivery.post(new Runnable() {
                        @Override
                        public void run() {
                            view.setImageBitmap(bm);
                        }
                    });
                } catch (Exception e) {
                    setErrorResId(view, errorResId);

                } finally {
                    if (is != null) try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setErrorResId(final ImageView view, final int errorResId) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                view.setImageResource(errorResId);
            }
        });
    }


    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private Param[] validateParam(Param[] params) {
        if (params == null) return new Param[0];
        else return params;
    }

    private Param[] map2Params(Map<String, String> params) {
        if (params == null) return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

    private void deliveryResult(final ResultCallback callback, final Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                e.printStackTrace();
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(Call call, final Response response) {
                try {
                    final String string = response.body().string();
                    if (callback.mType == String.class) {
                        sendSuccessResultCallback(string, callback);
                    } else {
                        Object o = mGson.fromJson(string, callback.mType);
                        sendSuccessResultCallback(o, callback);
                    }


                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                } catch (com.google.gson.JsonParseException e)//Json解析的错误
                {
                    sendFailedStringCallback(response.request(), e, callback);
                }

            }
        });
    }

    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) callback.onError(request, e);
            }
        });
    }

    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(object);
                }
            }
        });
    }

    private Request buildPostRequest(String url, Object paramObj) throws IllegalAccessException {
        final List<Field> fields = getFields(paramObj.getClass(), Object.class);
        FormBody.Builder builder = new FormBody.Builder();
        if (fields != null) {

            for (Field field : fields) {
                field.setAccessible(true);
                if (field.get(field) != null) {
                    builder.add(field.getName(), String.valueOf(field));
                }
            }
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }

    private Request buildPostRequest(String url, Param[] params) {
        if (params == null) {
            params = new Param[0];
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }

    private void setCertificates(OkHttpClient.Builder builder, InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

                try {
                    if (certificate != null) certificate.close();
                } catch (IOException e) {
                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static abstract class ResultCallback<T> {
        Type mType;

        public ResultCallback() {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(T response);
    }

    public static class Param {
        String key;
        String value;

        public Param() {
        }
        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

}
