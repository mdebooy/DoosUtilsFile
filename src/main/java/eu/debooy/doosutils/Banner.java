/**
 * Copyright 2010 Marco de Booy
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
public final class Banner {
  private static  ManifestInfo  manifestInfo  = new ManifestInfo();

  private Banner() {}

  /**
   * Print de Banner.
   */
  public static void printBanner(String titel) {
    DoosUtils.naarScherm("+----------+----------+----------+----------+----------+----------+----------+");
    DoosUtils.naarScherm("|          |          |");
    DoosUtils.naarScherm("|   |\\__   *   __/|   | " + titel);
    DoosUtils.naarScherm("|   /  .\\ *** /.   \\  |");
    DoosUtils.naarScherm("|  | ( _ \\ * / _ ) |  |");
    DoosUtils.naarScherm("+--|    \\_) (_/    |--+----------+----------+----------+----------+----------+");
    DoosUtils.naarScherm("|  |    |     |    |  |");
    DoosUtils.naarScherm("|  /_____\\   /_____\\  |");
    DoosUtils.naarScherm("| [_______]_[_______] | E-Mail : marco.development@debooy.eu");
    DoosUtils.naarScherm("|       [_____]       | Website: http://www.debooy.eu");
    DoosUtils.naarScherm("+----------+----------+----------+----------+----------+----------+----------+");
    DoosUtils.naarScherm(String.format("%77s", "v"
                                               + manifestInfo.getBuildVersion()
                                               + " | "
                                               + manifestInfo.getBuildDate()));
    DoosUtils.naarScherm("");
  }
}
