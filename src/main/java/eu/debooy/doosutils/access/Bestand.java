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
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * @author Marco de Booij
 */
public final class Bestand {
  private static final  ResourceBundle  resourceBundle  =
      ResourceBundle.getBundle("DoosUtils-file", Locale.getDefault());

  private Bestand() {}

  public static void copy(BufferedReader invoer, BufferedWriter uitvoer)
      throws BestandException {
    String  data;

    try {
      while (null != (data = invoer.readLine())) {
        schrijfRegel(uitvoer, data);
      }
    } catch (IOException e) {
      throw new BestandException(e);
    }
  }

  public static void copy(File invoer, File uitvoer)
      throws BestandException {
    copy(openInvoerBestand(invoer), openUitvoerBestand(uitvoer));
  }

  public static void copy(String invoer, String uitvoer)
      throws BestandException {
    copy(openInvoerBestand(invoer), openUitvoerBestand(uitvoer));
  }

  public static boolean equals(BufferedReader bestandA, BufferedReader bestandB)
      throws BestandException {
    String  dataA;
    String  dataB;

    try {
      while (null != (dataA = bestandA.readLine())) {
        dataB = bestandB.readLine();
        if (!dataA.equals(dataB)) {
          return false;
        }
      }
    } catch (IOException e) {
      throw new BestandException(e);
    }
    return true;
  }

  public static boolean equals(File bestandA, File bestandB)
      throws BestandException{
    return equals(openInvoerBestand(bestandA), openInvoerBestand(bestandB));
  }

  public static boolean equals(String bestandA, String bestandB)
      throws BestandException {
    return equals(openInvoerBestand(bestandA), openInvoerBestand(bestandB));
  }

  public static BufferedReader openInvoerBestand(File bestand)
      throws BestandException {
    return openInvoerBestand(bestand, Charset.defaultCharset().name());
  }

  public static BufferedReader openInvoerBestand(String bestand)
      throws BestandException {
    return openInvoerBestand(new File(bestand),
                             Charset.defaultCharset().name());
  }

  public static BufferedReader openInvoerBestand(ClassLoader classLoader,
                                                 String bestand)
      throws BestandException {
    try {
      return new BufferedReader(
          new InputStreamReader(classLoader.getResourceAsStream(bestand),
                                Charset.defaultCharset().name()));
    } catch (UnsupportedEncodingException e) {
      throw new BestandException(e);
    }
  }

  public static BufferedReader openInvoerBestand(File bestand, String charSet)
      throws BestandException {
    try {
      return new LineNumberReader(
          new InputStreamReader(
              new FileInputStream(bestand), charSet));
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
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

  public static BufferedWriter openUitvoerBestand(File bestand)
      throws BestandException {
    return openUitvoerBestand(bestand, Charset.defaultCharset().name(), false);
  }

  public static BufferedWriter openUitvoerBestand(File bestand, boolean append)
      throws BestandException {
    return openUitvoerBestand(bestand, Charset.defaultCharset().name(), append);
  }

  public static BufferedWriter openUitvoerBestand(String bestand)
      throws BestandException {
    return openUitvoerBestand(new File(bestand),
                              Charset.defaultCharset().name(), false);
  }

  public static BufferedWriter openUitvoerBestand(String bestand,
                                                  boolean append)
      throws BestandException {
    return openUitvoerBestand(new File(bestand),
                              Charset.defaultCharset().name(), append);
  }

  public static BufferedWriter openUitvoerBestand(File bestand, String charSet)
      throws BestandException {
    return openUitvoerBestand(bestand, charSet, false);
  }

  public static BufferedWriter openUitvoerBestand(File bestand, String charSet,
                                                  boolean append)
      throws BestandException {
    try {
      return new BufferedWriter(
               new OutputStreamWriter(
                 new FileOutputStream(bestand, append), charSet));
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
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

  public static void schrijfRegel(BufferedWriter output, String regel)
      throws IOException {
    schrijfRegel(output, regel, 1);
  }

  public static void schrijfRegel(BufferedWriter output, String regel,
                                  int newlines)
      throws IOException {
    output.write(regel);
    for (int i = 0; i < newlines; i++) {
      output.newLine();
    }
  }

  public static void delete(File bestand) throws BestandException {
    if (!bestand.exists()) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_BEST_ONBEKEND),
                                                      bestand.getName()));
    }

    if (bestand.isDirectory()) {
      for (File file : bestand.listFiles()) {
        delete(file);
      }
    } else {
      if (!bestand.delete()) {
        throw new BestandException(MessageFormat.format(
            resourceBundle.getString(BestandConstants.ERR_BEST_VERWIJDER),
                                                        bestand.getName()));
      }
    }
  }

  public static void delete(String bestand) throws BestandException {
    delete(new File(bestand));
  }
}
