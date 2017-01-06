/**
 * Copyright 2005 Marco de Booij
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


/**
 * @author Marco de Booij
 */
public final class DoosUtils {
  private DoosUtils() {}

  /**
   * Schrijft een foutmelding op het scherm.
   * 
   * @param regel
   */
  public static void foutNaarScherm(String regel) {
    System.err.println(regel);
  }

  /**
   * Is de parameter null of leeg?
   * 
   * @param obj
   * @return
   */
  public static boolean isBlankOrNull(Object obj) {
    return obj == null || obj.toString().trim().equals("");
  }

  /**
   * Is de parameter niet null of leeg?
   * 
   * @param obj
   * @return
   */
  public static boolean isNotBlankOrNull(Object obj) {
    return obj != null && !obj.toString().trim().equals("");
  }

  /**
   * Schrijft een lege regel op het scherm.
   */
  public static void naarScherm() {
    naarScherm("");
  }

  /**
   * Schrijft een melding op het scherm.
   * 
   * @param regel
   */
  public static void naarScherm(String regel) {
    System.out.println(regel);
  }

  /**
   * Schrijf regel(s) van maxLengte op het scherm.
   * 
   * @param pString
   * @param maxLengte
   */
  public static void naarScherm(String pString, int maxLengte) {
    naarScherm("", pString, maxLengte);
  }

  /**
   * Schrijf regel(s) van maxLengte op het scherm.
   * 
   * @param pBegin
   * @param pString
   * @param maxLengte
   */
  public static void naarScherm(String pBegin, String pString, int maxLengte) {
    int     beginLengte   = pBegin.length();
    int     splitsLengte  = maxLengte - beginLengte;
    String  begin         = pBegin;
    String  leeg          =
        (beginLengte == 0 ? "" : String.format("%" + beginLengte +"s", " "));
    String  string        = pString;

    while (string.length() > splitsLengte) {
      int splits  = string.substring(1, splitsLengte).lastIndexOf(" ");
      DoosUtils.naarScherm(begin + string.substring(0, splits + 1));
      begin   = leeg;
      string  = string.substring(splits + 2);
    }

    DoosUtils.naarScherm(begin + string);
  }

  /**
   * Maak van een null String een lege String.
   * 
   * @param string
   * @return
   */
  public static String nullToEmpty(String string) {
    if (null == string) {
      return "";
    }

    return string;
  }

  /**
   * Maak van een null String een lege String.
   * 
   * @param string
   * @param waarde
   * @return String
   */
  public static String nullToValue(String string, String waarde) {
    if (null == string) {
      return waarde;
    }

    return string;
  }
}
