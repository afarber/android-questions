# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-libraryjars libs/login-with-amazon-sdk.jar
-libraryjars libs/amazon-device-messaging-1.1.0.jar

-include okhttp3.pro

-ignorewarnings

-keep class androidx.multidex.** {*;}
-keep class android.support.v7.** {*;}
-keep class com.amazon.** {*;}
-keep class com.huawei.hianalytics.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
