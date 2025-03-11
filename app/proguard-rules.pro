# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep Log4j classes
-keep class org.apache.logging.** { *; }
-dontwarn org.apache.logging.**

# Keep FindBugs Annotations
-keep class edu.umd.cs.findbugs.annotations.** { *; }
-dontwarn edu.umd.cs.findbugs.annotations.**

# Keep AWT classes (if used in libraries)
-keep class java.awt.** { *; }
-dontwarn java.awt.**

# Keep Apache POI classes
-keep class org.apache.poi.** { *; }
-dontwarn org.apache.poi.**

# Keep Batik (SVG Processing)
-keep class org.apache.batik.** { *; }
-dontwarn org.apache.batik.**

# Keep OSGi Framework
-keep class org.osgi.framework.** { *; }
-dontwarn org.osgi.framework.**

# Keep XML Processing
-keep class javax.xml.stream.** { *; }
-dontwarn javax.xml.stream.**

# Keep Saxon XPath classes
-keep class net.sf.saxon.** { *; }
-dontwarn net.sf.saxon.**
