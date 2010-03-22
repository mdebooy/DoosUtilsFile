/**
 * Copyright 2010 Marco de Booy
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
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
public class Banner {
  private static  ManifestInfo  manifestInfo  = new ManifestInfo();

  /**
   * Print de Banner.
   */
  public static void printBanner(String titel) {
    System.out.println("+----------+----------+----------+----------+----------+----------+");
    System.out.println("|          |          |");
    System.out.println("|   |\\__   *   __/|   | " + titel);
    System.out.println("|   /  .\\ *** /.   \\  |");
    System.out.println("|  | ( _ \\ * / _ ) |  |");
    System.out.println("+--|    \\_) (_/    |--+----------+----------+----------+----------+");
    System.out.println("|  |    |     |    |  |");
    System.out.println("|  /_____\\   /_____\\  |");
    System.out.println("| [_______]_[_______] | E-Mail : marco.development@debooy.eu");
    System.out.println("|       [_____]       | Website: http://www.debooy.eu");
    System.out.println("+----------+----------+----------+----------+----------+----------+");
    System.out.println(String.format("%66s", "v"
                                             + manifestInfo.getBuildVersion()
                                             + " | "
                                             + manifestInfo.getBuildDate()));
    System.out.println("");
  }
}
