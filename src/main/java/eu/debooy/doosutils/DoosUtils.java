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
   * Is de parameter niet null of leeg?
   * @param obj
   * @return
   */
  public static boolean isNotBlankOrNull(Object obj) {
    return obj != null && !obj.toString().trim().equals("");
  }

  /**
   * Is de parameter null of leeg?
   * @param obj
   * @return
   */
  public static boolean isBlankOrNull(Object obj) {
    return obj == null || obj.toString().trim().equals("");
  }

  /**
   * Maak van een null String een lege String.
   * @param string
   * @return
   */
  public static String nullToEmpty(String string) {
    if (null == string) {
      return "";
    }

    return string;
  }
}
