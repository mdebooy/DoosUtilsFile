/**
 * Copyright 2010 Marco de Booij
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the Licence. You may
 * obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package eu.debooy.doosutils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;


/**
 * @author Marco de Booij
 */
public class ManifestInfo {
  private static final String  DATE_UNKNOWN     = "UNKNOWN";
  private static final String  LIB              = "/WEB-INF/lib";
  private static final String  MANIFEST         = "/META-INF/MANIFEST.MF";
  private static final String  VERSION_UNSTABLE = "UNSTABLE";

  private static String        buildVersion;
  private static String        buildDate;

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

      if (classContainer.indexOf(LIB) >= 0) {
        classContainer  =
          classContainer.substring(0, classContainer.indexOf(LIB));
        manifestUrl     =
          new URL((new StringBuilder()).append(classContainer).append(MANIFEST)
                                       .toString());

      } else {
        manifestUrl     =
          new URL((new StringBuilder()).append("jar:").append(classContainer)
                                       .append("!").append(MANIFEST)
                                       .toString());

      }
      try {
        manifest  = new Manifest(manifestUrl.openStream());
      } catch (FileNotFoundException fnfe) {
        classContainer  =
          classContainer.substring(0, classContainer.indexOf(LIB));
        manifestUrl     =
          new URL((new StringBuilder()).append(classContainer).append(MANIFEST)
                                       .toString());
        try {
          manifest        = new Manifest(manifestUrl.openStream());
        } catch (IOException e) {
          buildVersion  = VERSION_UNSTABLE;
          buildDate     = DATE_UNKNOWN;
        }
      } catch (IOException e) {
        buildVersion  = VERSION_UNSTABLE;
        buildDate     = DATE_UNKNOWN;
      }
      if (null == manifest) {
        buildVersion  = VERSION_UNSTABLE;
        buildDate     = DATE_UNKNOWN;
      } else {
        Attributes  attr  = manifest.getMainAttributes();
        buildVersion  = attr.getValue("Implementation-Version");
        buildDate     = attr.getValue("Build-Time");
      }
    } catch (MalformedURLException e) {
      buildVersion  = VERSION_UNSTABLE;
      buildDate     = DATE_UNKNOWN;
    }
  }

  public String getBuildVersion() {
    return buildVersion;
  }

  public String getBuildDate() {
    return buildDate;
  }
}
