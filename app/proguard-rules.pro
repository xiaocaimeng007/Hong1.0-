#ProGuard是一个免费的java类文件压缩,优化,混淆器.
#它探测并删除没有使用的类,字段,方法和属性.
#它删除没有用的说明并使用字节码得到最大优化.
#它使用无意义的名字来重命名类,字段和方法.
#ProGuard的使用是为了:
#1.创建紧凑的代码文档是为了更快的网络传输,快速装载和更小的内存占用.
#2.创建的程序和程序库很难使用反向工程.
#3.所以它能删除来自源文件中的没有调用的代码
#4.充分利用java6的快速加载的优点来提前检测和返回java6中存在的类文件.

#ProGuard支持那些种类的优化：
#除了在压缩操作删除的无用类，字段和方法外，
#ProGuard也能在字节码级提供性能优化，内部方法有：
#1 常量表达式求值
#2 删除不必要的字段存取
#3 删除不必要的方法调用
#4 删除不必要的分支
#5 删除不必要的比较和instanceof验证
#6 删除未使用的代码
#7 删除只写字段
#8 删除未使用的方法参数
#9 像push/pop简化一样的各种各样的peephole优化
#10 在可能的情况下为类添加static和final修饰符
#11 在可能的情况下为方法添加private, static和final修饰符
#12 在可能的情况下使get/set方法成为内联的
#13 当接口只有一个实现类的时候，就取代它
#14 选择性的删除日志代码
#实际的优化效果是依赖于你的代码和执行代码的虚拟机的。
#简单的虚拟机比有复杂JIT编译器的高级虚拟机更有效。无论如何，你的字节码会变得更小。

#需要优化的不被支持技术：
#1 使非final的常量字段成为内联
#2 像get/set方法一样使其他方法成为内联
#3 将常量表达式移到循环之外
#4 Optimizations that require escape analysis





#四大组件、fragment、support包（一般v4、v7）
# *代表匹配所有字符   **表示该包所有类及其子路径的类

#保持一个类的子类或者实现类不被混淆
#-keep * extends/implements 类名 { *;}  类名是包含包名的全名
-keep public class * extends android.app.Activity
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.FragmentActivity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep public class * implements java.io.Serializable {
        public *;
}


#保持一个类不被混淆-keep
#class 类名 {*;}  类名是包含包名的全名
-keep public class com.android.vending.licensing.ILicensingService


#保持一个类的单个方法不被混淆
#-keepclassmembers class 类名 {可含有通配符的方法名}
-keepclassmembers class * {
	public <methods>;#所有方法不进行混淆
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}


#保持含有某个方法的类不被混淆
#-keepclasseswithmembers class 类名 {可含有通配符的方法名}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}



-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class **.R$* {*;}
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }


-keep class com.amap.api.**{*;}
-keep class com.autonavi.aps.amapapi.model**{*;}
-keep class com.loc.**{*;}
-keep class com.jauker.widget.**{*;}
-keep class com.easemob.**{*;}
-keep class com.alibaba.fastjson.**{*;}
-keep class com.bumptech.glide.**{*;}
-keep class com.mob.tools.**{*;}
-keep class com.iflytek.**{*;}
-keep class com.squareup.okhttp.**{*;}
-keep class com.okio.**{*;}
-keep class com.squareup.picasso.**{*;}
-keep class cn.sharesdk.framework.**{*;}
-keep class cn.sharesdk.tencent.qq.**{*;}
-keep class cn.sharesdk.tencent.qzone.**{*;}
-keep class cn.sharesdk.sina.weibo.**{*;}
-keep class cn.sharesdk.wechat.friends.**{*;}
-keep class cn.sharesdk.wechat.utils.**{*;}
-keep class cn.sharesdk.wechat.favorite.**{*;}
-keep class cn.sharesdk.wechat.moments.**{*;}
-keep class com.umeng.update.**{*;}
-keep class com.google.**{*;}
-keep class com.alipay.**{*;}
-keep class com.sun.**{*;}
#-keep class com.alipay.android.app.IAlixPay{*;}
#-keep class com.alipay.android.app.IAlixPay$Stub{*;}
#-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
#-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
#-keep class com.alipay.sdk.app.PayTask{ public *;}
#-keep class com.alipay.sdk.app.AuthTask{ public *;}

-dontpreverify#不做预校验
-optimizationpasses 5
-dontusemixedcaseclassnames#混淆时不会产生形形色色的类名
-dontskipnonpubliclibraryclasses#指定不去忽略非公共的库类
-ignorewarnings
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes Signature

##-ignorewarnings
### 使用注解
-keepattributes *Annotation*,Signature  #保护给定的可选属性

### support-design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

-dontoptimize

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
