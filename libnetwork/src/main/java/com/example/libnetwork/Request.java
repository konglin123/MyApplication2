package com.example.libnetwork;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.example.libdatabase.CacheManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.IntDef;
import androidx.arch.core.executor.ArchTaskExecutor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public abstract class Request<T, R extends Request> implements Cloneable{
    protected String mUrl;
    protected HashMap<String, String> headers = new HashMap<>();
    protected HashMap<String, Object> params = new HashMap<>();
    private String catchKey;
    /**
     * 只进行缓存访问
     */
    public static final int CACHE_ONLY = 1;
    /**
     * 缓存访问和接口访问同时进行，成功后缓存到本地
     */
    public static final int CACHE_FIRST = 2;
    /**
     * 只进行网络访问
     */
    public static final int NET_ONLY = 3;
    /**
     * 先访问网络，成功后缓存到本地
     */
    public static final int NET_CACHE = 4;
    private Type type;
    private Class claz;

    private int mCacheStrategy = NET_ONLY;
    @IntDef({CACHE_ONLY, CACHE_FIRST, NET_CACHE, NET_ONLY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CacheStrategy {

    }

    public Request(String mUrl) {
        this.mUrl = mUrl;
    }

    public R addHeader(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    public R addParam(String key, Object value) {
        try {
            Field field = value.getClass().getField("TYPE");
            Class claz = (Class) field.get(null);
            if (claz.isPrimitive()) {
                params.put(key, value);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (R) this;
    }

    public R catchKey(String key) {
        this.catchKey = key;
        return (R) this;
    }

    public R responseType(Type type) {
        this.type = type;
        return (R) this;
    }

    public R responseType(Class claz) {
        this.claz = claz;
        return (R) this;
    }

    public R cacheStrategy(@CacheStrategy int cacheStrategy) {
        mCacheStrategy = cacheStrategy;
        return (R) this;
    }


    private Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders(builder);
        okhttp3.Request request = generateRequest(builder);
        Call call = ApiService.okHttpClient.newCall(request);
        return call;
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);


    private void addHeaders(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }

    @SuppressLint("RestrictedApi")
    public void execute(final JsonCallback<T> callback) {
        if(mCacheStrategy!=NET_ONLY){
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                 ApiResponse<T> response=readCache();
                 if(callback!=null){
                     callback.onCacheSuccess(response);
                 }
                }
            });
        }
        if(mCacheStrategy!=CACHE_ONLY) {
            getCall().enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ApiResponse<T> response = new ApiResponse<>();
                    response.message = e.getMessage();
                    callback.onError(response);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ApiResponse<T> apiResponse = parseResponse(response, callback);
                    if (apiResponse.success) {
                        callback.onSuccess(apiResponse);
                    } else {
                        callback.onError(apiResponse);
                    }
                }
            });
        }
    }

    private ApiResponse<T> readCache() {
        String key=TextUtils.isEmpty(catchKey)?generateCacheKey():catchKey;
        Object cache = CacheManager.getCache(key);
        ApiResponse<T> result = new ApiResponse<>();
        result.state=304;
        result.message="缓存获取成功";
        result.body= (T) cache;
        result.success=true;
        return result;
    }

    private ApiResponse<T> parseResponse(Response response, JsonCallback<T> callback) {
        String message = null;
        int state = response.code();
        boolean success = response.isSuccessful();
        ApiResponse<T> result = new ApiResponse<>();
        Convert convert = ApiService.sConvert;
        try {
            String content = response.body().toString();
            if (success) {
                if (callback != null) {
                    ParameterizedType type = (ParameterizedType) callback.getClass().getGenericSuperclass();
                    Type argument = type.getActualTypeArguments()[0];
                    result.body = (T) convert.convert(content, argument);
                } else if (type != null) {
                    result.body = (T) convert.convert(content, type);
                } else if (claz != null) {
                    result.body = (T) convert.convert(content, claz);
                } else {
                    Log.e("request", "parseResponse: 无法解析");
                }
            } else {
                message = content;
            }
        } catch (Exception e) {
            message = e.getMessage();
            success = false;
            state=0;
        }
        result.success = success;
        result.state = state;
        result.message = message;

        if(mCacheStrategy!=NET_ONLY&&result.success&&result.body!=null&&result.body instanceof Serializable){
            saveCache(result.body);
        }
        return result;
    }

    private void saveCache(T body) {
        String key= TextUtils.isEmpty(catchKey)?generateCacheKey():catchKey;
        CacheManager.save(key,body);
    }

    private String generateCacheKey() {
        catchKey=UrlCreator.createUrlFromParams(mUrl,params);
        return catchKey;
    }

    public ApiResponse<T> execute() {
        if(mCacheStrategy==CACHE_ONLY){
            return readCache();
        }
 
        if (mCacheStrategy != CACHE_ONLY) {
            ApiResponse<T> result = null;
            try {
                Response response = getCall().execute();
                result = parseResponse(response, null);
            } catch (IOException e) {
                e.printStackTrace();
                if (result == null) {
                    result = new ApiResponse<>();
                    result.message = e.getMessage();
                }
            }
            return result;
        }
        return null;
    }
}
