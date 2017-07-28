package com.hongbao5656.okhttp;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by zhy on 15/12/14.
 * 提供一个什么事情都不做的TrustManager 跳过 SSL 的验证,
 * 这样做很容受到攻击, Https 也就形同虚设了
 *
 * 应用难以逆向, 应用不再依赖系统的 trust store,
 * 使得 Charles 抓包等工具失效. 要分析应用 API 必须反编译 APK.
 * 不用额外购买证书, 省钱....
 * 证书部署灵活性降低, 一旦变更证书必须升级程序.
 */
public class HttpsUtils {

    public static class SSLParams {
        public SSLSocketFactory sSLSocketFactory;
        public X509TrustManager x509trustManager;
    }

    /**
     * 设置可访问所有的https网站
     * HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
      OkHttpClient okHttpClient = new OkHttpClient.Builder()
     .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
     //其他配置
     .build();
     OkHttpUtils.initClient(okHttpClient);

     * 设置具体的证书
     * HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(证书的inputstream, null, null);
     OkHttpClient okHttpClient = new OkHttpClient.Builder()
     .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager))
     //其他配置
     .build();
     OkHttpUtils.initClient(okHttpClient);

     * 双向认证
     * HttpsUtils.getSslSocketFactory(
     证书的inputstream,
     本地证书的inputstream,
     本地证书的密码)

     * @param certificates 服务器证书的inputstream
     * @param bksFile 本地证书的inputstream
     * @param password 本地证书的密码
     * @return
     */
    public static SSLParams getSslSocketFactory(InputStream[] certificates, InputStream bksFile, String password) {
        SSLParams sslParams = new SSLParams();
        try {
            //主机的信任处理器列表
            TrustManager[] x509trustManagers = prepareTrustManager(certificates);
            //密钥管理器
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);

            X509TrustManager x509trustManager = null;//实现X509TrustManager接口
            if (x509trustManagers != null) {//做证书验证的信任处理器
                x509trustManager = new MyX509TrustManager(chooseTrustManager(x509trustManagers));
            } else {//忽略证书验证的信任处理器
                x509trustManager = new UnSafeX509TrustManager();
            }

            //取得SSL的SSLContext实例,使用TLS协议
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, new TrustManager[]{x509trustManager}, null);

            sslParams.sSLSocketFactory = sslContext.getSocketFactory();
            sslParams.x509trustManager = x509trustManager;

            return sslParams;
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (KeyManagementException e) {
            throw new AssertionError(e);
        } catch (KeyStoreException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * 实现HostnameVerifier接口
     */
    private class UnSafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    //创建一个忽略证书验证的信任处理器
    private static class UnSafeX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    /**
     *
     * @param certificates
     * @return 主机信任列表
     */
    private static TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates == null || certificates.length <= 0) return null;
        try {
            //读取证书 Android 采用X509的证书信息机制
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            //创建一个证书库，并将证书导入证书库
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);

            //取得BKS密库实例
//            KeyStore kks = KeyStore.getInstance("PKCS12");
            //Android支持BKS的KeyStore
//            KeyStore tks = KeyStore.getInstance("BKS");

            //加客户端载证书和私钥,通过读取资源文件的方式读取密钥和信任证书

            //raw中为truststore.bks的信任密库
//            tks.load(appCtx
//                    .getResources()
//                    .openRawResource(R.raw.truststore), CLIENT_TRUST_PASSWORD.toCharArray());

            //raw资源中的文件为client.p12格式的证书
//            kks.load(appCtx
//                    .getResources()
//                    .openRawResource(R.raw.client), CLIENT_KET_PASSWORD.toCharArray());

            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            TrustManagerFactory trustManagerFactory = null;

            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            return trustManagers;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) return null;

            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            //取得KeyManagerFactory的X509密钥管理器实例
//            KeyManagerFactory keyManager = KeyManagerFactory.getInstance("X509");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            //初始化密钥管理器
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager x509trustManager : trustManagers) {
            if (x509trustManager instanceof X509TrustManager) {
                return (X509TrustManager) x509trustManager;
            }
        }
        return null;
    }


    /*
    自定义信任处理器（TrustManager）来替代系统默认的信任处理器，
    这样我们才能正常的使用自定义的正说或者非android认可的证书颁发机构颁发的证书
    自行实现X509TrustManager时并没有对其中三个核心的方法进行
    具体实现（主要是没有在checkServerTrusted（）验证证书），
    这样做相当于直接忽略了检验服务端证书。
    因此无论服务器的证书如何，都能建立起https链接
     */
    private static class MyX509TrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public MyX509TrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
            //取得TrustManagerFactory的X509密钥管理器实例
//            TrustManagerFactory trustManager = TrustManagerFactory.getInstance("X509");
            TrustManagerFactory var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            var4.init((KeyStore) null);
            defaultTrustManager = chooseTrustManager(var4.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                //chain[0].getSubjectDN().toString() CN=api.56hb.net, C=CN
                //chain[1].getSubjectDN().toString() CN=StartCom Class 1 DV Server CA, OU=StartCom Certification Authority, O=StartCom Ltd., C=IL
//                if("CN=api.56hb.net, C=CN".equals(chain[0].getSubjectDN().toString())){
//
//                }
                defaultTrustManager.checkServerTrusted(chain, authType);

            } catch (CertificateException ce) {
                localTrustManager.checkServerTrusted(chain, authType);
            }
        }


        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
