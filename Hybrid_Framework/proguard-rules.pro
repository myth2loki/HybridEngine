# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/development/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface class:
-libraryjars /Applications/development/android-sdk-macosx/platforms/android-21

#指定代码的压缩级别
-optimizationpasses 7
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
 #优化  不优化输入的类文件
-dontoptimize
 #预校验
-dontpreverify
 #混淆时是否记录日志
-verbose
#下面这行代码是 忽略警告，避免打包时某些警告出现
-ignorewarnings
 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-allowaccessmodification

#-keep 指定的类和类成员被保留作为入口
#-keepclassmembers 保护指定类的成员
#-keepclasseswithmembers 保护指定的类和类的成员，假如指定的类和类成员存在
#-keepclasseswithmembernames 保护指定的类和类的成员的名称，如果所有指定的类成员存在
#-dontwarn 类和方法不被混淆


#Android系统组件
-keep public class * extends android.app.Fragment
-keep class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public interface com.android.vending.licensing.ILicensingService
-keep public class com.xhrd.mobile.hybridframework.framework.RDCloudApplication
-keep class * extends slidemenu.BaseSlideMenuActivity
#-keep public class com.xhrd.mobile.hybridframework.framework.App

#Parcelable：序列化对象
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#枚举
-keep public enum com.xhrd.mobile.hybridframework.framework.PluginData$Scope{*;}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

#注解annotation
-keep class com.xhrd.mobile.hybridframework.annotation.JavascriptConfig{*;}
-keep class com.xhrd.mobile.hybridframework.annotation.JavascriptFunction{*;}
-keep class com.xhrd.mobile.hybridframework.annotation.JavascriptProperty{*;}
-keep class com.xhrd.mobile.hybridframework.framework.PluginManagerBase.JavascriptUiFunction{*;}
-keep class com.xhrd.mobile.hybridframework.BuildConfig{*;}

#保护所有R文件
-keep class **.R$* {*;}
-keep class **.R{*;}
-dontwarn **.R$*

#第三方插件需要使用的类
-keepclassmembers class com.xhrd.mobile.hybridframework.framework.RDCloudApplication{
    public int getAtomicId();
}
-keep class com.xhrd.mobile.hybridframework.framework.RDApplicationInfo{*;}
-keep class com.xhrd.mobile.hybridframework.framework.RDComponentInfo{*;}
-keep class com.xhrd.mobile.hybridframework.framework.InternalPluginBase{*;}
-keep class com.xhrd.mobile.hybridframework.framework.PluginBase{*;}
-keep class com.xhrd.mobile.hybridframework.framework.PluginData{*;}
-keep class com.xhrd.mobile.hybridframework.engine.RDResourceManager{*;}
-keep class com.xhrd.mobile.hybridframework.framework.Manager.ResManagerFactory{*;}
-keepclassmembers class fqcn.of.javascript.interface.for.webview {*;}
-keep class com.xhrd.mobile.hybridframework.engine.RDCloudView {*;}

-keep class * extends com.xhrd.mobile.hybridframework.framework.InternalPluginBase{*;}
-keep class * extends com.xhrd.mobile.hybridframework.framework.PluginBase{*;}
-keep class com.xhrd.mobile.hybridframework.framework.IJSData{*;}
-keep class * extends com.xhrd.mobile.hybridframework.framework.IJSData{*;}

-keepclassmembers class * extends com.xhrd.mobile.hybridframework.framework.InternalPluginBase{
    protected RDCloudView getTargetView();
    protected RDCloudView getTargetView(java.lang.String);
}
-keepclassmembers class * extends com.xhrd.mobile.hybridframework.framework.PluginBase{
    protected RDCloudView getTargetView();
    protected RDCloudView getTargetView(java.lang.String);
}

#反射
-keep class * extends com.xhrd.mobile.hybridframework.framework.PluginManagerBase{*;}

#工具类
-keep class com.xhrd.mobile.hybridframework.util.*{*;}
-keep class com.xhrd.mobile.hybridframework.util.http.**{*;}
-keep class com.xhrd.mobile.hybridframework.util.jsbuilder.JsFunctionProperty{*;}
-keep class com.xhrd.mobile.hybridframework.util.jsbuilder.JsObjectBuilder{*;}
-keep class com.xhrd.mobile.hybridframework.util.log.LogFactory{*;}
-keep class com.xhrd.mobile.hybridframework.util.log.RDLog{*;}
#-keep class com.xhrd.mobile.hybridframework.util.**{*;}

# 保持native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#-keep class com.xhrd.mobile.hybridframework.engine.RDCloudComponent{
#    *;
#}
-keep class com.xhrd.mobile.hybridframework.engine.RDCloudWindow{
    ** getRDCloudComponent();
    java.lang.String getName();
}

-keep class com.xhrd.mobile.hybridframework.engine.RDEncryptHelper{
    java.lang.String getMD5();
    int getVersion();
    int getType();
    ** decrypt(...);
    void close();
    void init();
}

#-keepclasseswithmembers class com.xhrd.mobile.hybridframework.engine.RDEncryptHelper{
#    public static String getMD5();
#}

#自定义View
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#保持自定义控件构造方法不被混淆
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
#保持自定义控件构造方法不被混淆
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers class * {
    public static ** getApp();
    public static ** getInstance();
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}

##################################################第三方库###################################################
-dontwarn com.xhrd.mobile.statistics.library.model.**
-keep class com.xhrd.mobile.statistics.library.model.**{*;}
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }
-keepattributes Signature


#zxing
#-keep class com.google.zxing.** {*;}
#-dontwarn com.google.zxing.**

-dontwarn android.support.**