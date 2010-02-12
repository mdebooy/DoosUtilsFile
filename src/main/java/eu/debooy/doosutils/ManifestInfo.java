package eu.debooy.doosutils;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ManifestInfo {
  private static final String    VERSION_UNSTABLE      = "UNSTABLE";
  private static final String    DATE_UNKNOWN          = "UNKNOWN";

  private static String          buildVersion;
  private static String          buildDate;

  static {
    buildDetails();
  }

  private static void buildDetails() {
    try {
      URL       manifestUrl     = null;
      Manifest  manifest        = null;
      String    classContainer  = ManifestInfo.class.getProtectionDomain()
                                              .getCodeSource()
                                              .getLocation().toString();

      if (classContainer.indexOf("/WEB-INF/lib") >= 0) {
        classContainer  =
          classContainer.substring(0, classContainer.indexOf("/WEB-INF/lib"));
        manifestUrl     =
          new URL((new StringBuilder()).append(classContainer)
                                       .append("/META-INF/MANIFEST.MF")
                                       .toString());

      } else {
        manifestUrl     =
          new URL((new StringBuilder()).append("jar:")
                                       .append(classContainer)
                                       .append("!/META-INF/MANIFEST.MF")
                                       .toString());

      }
      try {
        manifest  = new Manifest(manifestUrl.openStream());
      } catch (FileNotFoundException fnfe) {
        classContainer  =
          classContainer.substring(0, classContainer.indexOf("/WEB-INF/lib"));
        manifestUrl     =
          new URL((new StringBuilder()).append(classContainer)
                                       .append("/META-INF/MANIFEST.MF")
                                       .toString());
        manifest        = new Manifest(manifestUrl.openStream());
      }
      Attributes  attr  = manifest.getMainAttributes();
      buildVersion  = attr.getValue("Implementation-Version");
      buildDate     = attr.getValue("Build-Time");
    } catch (Exception exc) {
      buildVersion = VERSION_UNSTABLE;
      buildDate = DATE_UNKNOWN;
    }
  }

  public String getBuildVersion() {
    return buildVersion;
  }

  public String getBuildDate() {
    return buildDate;
  }
}
