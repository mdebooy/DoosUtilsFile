/**
 * Copyright 2008 Marco de Booy
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
 * @author Marco de Booy
 */
public final class DoosConstants {
  private DoosConstants() {}

  public static final int     BEFORE        = -1;
  public static final int     EQUAL         = 0;
  public static final int     AFTER         = 1;

  public static final String  ONWAAR        = "N";
  public static final String  WAAR          = "J";

  public static final String  DATUM         = "dd/MM/yyyy";
  public static final String  DATUM_TIJD    = "dd/MM/yyyy HH:mm:ss";
  public static final String  SORTEERDATUM  = "yyyyDDD";

  public static final String  SMTP_HOST     = "smtp.debooy.eu";

  public static final String  EOL           =
    System.getProperty("line.separator");
}
