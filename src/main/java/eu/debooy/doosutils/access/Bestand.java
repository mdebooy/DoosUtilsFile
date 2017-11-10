/**
 * Copyright 2011 Marco de Booij
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
package eu.debooy.doosutils.access;

import eu.debooy.doosutils.exception.BestandException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


/**
 * @author Marco de Booij
 */
public final class Bestand {
  private Bestand() {}

  public static BufferedReader openInvoerBestand(File bestand)
      throws BestandException {
    return openInvoerBestand(bestand, Charset.defaultCharset().name());
  }

  public static BufferedReader openInvoerBestand(String bestand)
      throws BestandException {
    return openInvoerBestand(new File(bestand),
                             Charset.defaultCharset().name());
  }

  public static BufferedReader openInvoerBestand(File bestand, String charSet)
      throws BestandException {
    try {
      return new LineNumberReader(
          new InputStreamReader(
              new FileInputStream(bestand), charSet));
    } catch (FileNotFoundException e) {
      throw new BestandException(e);
    } catch (UnsupportedEncodingException e) {
      throw new BestandException(e);
    }
  }

  public static BufferedReader openInvoerBestand(String bestand, String charSet)
      throws BestandException {
    return openInvoerBestand(new File(bestand), charSet);
  }

  public static BufferedReader openInvoerBestandInJar(Class<?> clazz,
                                                      String bestand)
      throws BestandException {
    return openInvoerBestandInJar(clazz, bestand,
                                  Charset.defaultCharset().name());
  }

  public static BufferedReader openInvoerBestandInJar(Class<?> clazz,
                                                      String bestand,
                                                      String charSet)
      throws BestandException {
    try {
      return new BufferedReader(new InputStreamReader(
          clazz.getResourceAsStream(bestand), charSet));
    } catch (UnsupportedEncodingException e) {
      throw new BestandException(e);
    }
  }

  /**
   * Open een bestand als uitvoer.
   * 
   * @param bestand
   * @return
   * @throws BestandException
   */
  public static BufferedWriter openUitvoerBestand(File bestand)
      throws BestandException {
    return openUitvoerBestand(bestand, Charset.defaultCharset().name(), false);
  }

  /**
   * Open een bestand als uitvoer. Als append true is wordt er aan het einde van
   * het bestand geschreven.
   * 
   * @param bestand
   * @param append
   * @return
   * @throws BestandException
   */
  public static BufferedWriter openUitvoerBestand(File bestand, boolean append)
      throws BestandException {
    return openUitvoerBestand(bestand, Charset.defaultCharset().name(), append);
  }

  /**
   * Open een bestand als uitvoer.
   * 
   * @param bestand
   * @return
   * @throws BestandException
   */
  public static BufferedWriter openUitvoerBestand(String bestand)
      throws BestandException {
    return openUitvoerBestand(new File(bestand),
                              Charset.defaultCharset().name(), false);
  }

  /**
   * Open een bestand als uitvoer. Als append true is wordt er aan het einde van
   * het bestand geschreven.
   * 
   * @param bestand
   * @param append
   * @return
   * @throws BestandException
   */
  public static BufferedWriter openUitvoerBestand(String bestand,
                                                  boolean append)
      throws BestandException {
    return openUitvoerBestand(new File(bestand),
                              Charset.defaultCharset().name(), append);
  }

  /**
   * Open een bestand als uitvoer.
   * 
   * @param bestand
   * @param charSet
   * @return
   * @throws BestandException
   */
  public static BufferedWriter openUitvoerBestand(File bestand, String charSet)
      throws BestandException {
    return openUitvoerBestand(bestand, charSet, false);
  }

  /**
   * Open een bestand als uitvoer. Als append true is wordt er aan het einde van
   * het bestand geschreven.
   * 
   * @param bestand
   * @param charSet
   * @param append
   * @return
   * @throws BestandException
   */
  public static BufferedWriter openUitvoerBestand(File bestand, String charSet,
                                                  boolean append)
      throws BestandException {
    try {
      return new BufferedWriter(
               new OutputStreamWriter(
                 new FileOutputStream(bestand, append), charSet));
    } catch (FileNotFoundException e) {
      throw new BestandException(e);
    } catch (UnsupportedEncodingException e) {
      throw new BestandException(e);
    }
  }


  public static BufferedWriter openUitvoerBestand(String bestand,
                                                  String charSet)
      throws BestandException {
    return openUitvoerBestand(bestand, charSet, false);
  }

  public static BufferedWriter openUitvoerBestand(String bestand,
                                                  String charSet,
                                                  boolean append)
      throws BestandException {
    return openUitvoerBestand(new File(bestand), charSet, append);
  }

  public static boolean verwijderDirectory(File directory) {
    if (directory.exists()) {
      for (File file : directory.listFiles()) {
        if (file.isDirectory()) {
          verwijderDirectory(file);
        } else {
          if (!file.delete()) {
            return false;
          }
        }
      }
    }

    return directory.delete();
  }

  /**
   * Schrijft een regel met een newLine naar het bestand.
   *
   * @param output
   * @param regel
   * @throws IOException
   */
  public static void schrijfRegel(BufferedWriter output, String regel)
      throws IOException {
    schrijfRegel(output, regel, 1);
  }

  /**
   * Schrijft een regel met een newLine naar het bestand.
   *
   * @param output
   * @param regel
   * @param newlines
   * @throws IOException
   */
  public static void schrijfRegel(BufferedWriter output, String regel,
                                  int newlines)
      throws IOException {
    output.write(regel);
    for (int i = 0; i < newlines; i++) {
      output.newLine();
    }
  }

  public static boolean verwijderDirectory(String directory) {
    return verwijderDirectory(new File(directory));
  }
}
