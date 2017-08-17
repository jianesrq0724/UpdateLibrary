package com.ruiqin.updatelibrary.network;

import android.os.Handler;
import android.os.Message;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.StringUtils;
import com.ruiqin.updatelibrary.constant.NetWorkEnvironment;
import com.ruiqin.updatelibrary.constant.NetWorkState;
import com.ruiqin.updatelibrary.network.entity.HttpResult;
import com.ruiqin.updatelibrary.util.DataWareHouse;
import com.ruiqin.updatelibrary.util.ToastUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

import static com.ruiqin.updatelibrary.constant.Constant.PLATFORM;

/**
 * Created by ruiqin.shen
 * 类说明：
 */

public class HttpClient {

    private static final String BASE_URL_LOCAL_DEV = "http://192.168.20.12/api/";//本地，内网。"http://192.168.1.111:5071"
    private static final String BASE_URL_LOCAL_TEMP = "http://192.168.20.232/api/";//临时
    private static final String BASE_URL_DEV = "http://112.74.107.186:9003/api/";//TT环境
    private static final String BASE_URL_RELEASE = "http://app.fulijr.com/api/";//正式"http://app.fulijr.com";
    public static final int ENVIRONMENT = NetWorkEnvironment.LOCAL_DEVELOP;// LOCAL_DEVELOP本地，LOCAL_TEMP临时，DEVELOP TT环境，RELEASE正式

    public static HttpClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final HttpClient INSTANCE = new HttpClient();
    }

    /**
     * 获取开发环境
     */
    public String getBaseURL() {
        switch (ENVIRONMENT) {
            case NetWorkEnvironment.LOCAL_DEVELOP:
                return BASE_URL_LOCAL_DEV;
            case NetWorkEnvironment.LOCAL_TEMP:
                return BASE_URL_LOCAL_TEMP;
            case NetWorkEnvironment.DEVELOP:
                return BASE_URL_DEV;
            case NetWorkEnvironment.RELEASE:
                return BASE_URL_RELEASE;
            default:
                return BASE_URL_RELEASE;
        }
    }

    private final RestAPI service;

    private HttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .retryOnConnectionFailure(false)//出现错误进行重新连接
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)//超过时间
                .addInterceptor(mInterceptor)//head拦截
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(getBaseURL())
                .build();
        service = retrofit.create(RestAPI.class);
    }


    Interceptor mInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            String token = DataWareHouse.getToken();//获取token值
            if (!StringUtils.isEmpty(token)) {
                builder.header("access-token", token);
            }
            builder.header("platform", PLATFORM);
            builder.header("version", AppUtils.getAppVersionName());
            Request request = builder.build();
            return chain.proceed(request);
        }
    };

    /**
     * 利用RxJava的map方法，统一对数据进行匹配
     *
     * @param <T>
     */
    public static class HttpResultFunc<T> implements Function<HttpResult<T>, T> {
        @Override
        public T apply(@NonNull HttpResult<T> tHttpResult) throws Exception {
            if (tHttpResult.getStatus() != NetWorkState.SUCCEES) {
                Message message = new Message();
                message.obj = tHttpResult.getMessage();
                handler.sendMessage(message);
                throw new ApiException();
            }
            return tHttpResult.getResult();
        }
    }

    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String data = (String) msg.obj;
                    ToastUtils.showShort(data);
                    break;
                default:
                    break;
            }
        }
    };

}
