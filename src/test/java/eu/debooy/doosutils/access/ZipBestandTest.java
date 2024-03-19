/*
 * Copyright (c) 2024 Marco de Booij
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the Licence. You may
 * obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package eu.debooy.doosutils.access;

import static eu.debooy.doosutils.access.CsvBestandTest.TEMP;
import eu.debooy.doosutils.exception.BestandException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import static junit.framework.TestCase.fail;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Marco de Booij
 */
public class ZipBestandTest {
  protected static final  ClassLoader CLASSLOADER =
      ZipBestand.class.getClassLoader();

  @AfterClass
  public static void afterClass() throws BestandException {
    Bestand.delete(TEMP + File.separator + "tekst.txt");
    Bestand.delete(TEMP + File.separator + "tekst1.txt");
    Bestand.delete(TEMP + File.separator + "test.csv");
    Bestand.delete(TEMP + File.separator + "test1.csv");
    Bestand.delete(TEMP + File.separator + "test.zip");
    Bestand.delete(TEMP + File.separator + "test1.zip");
  }

  @BeforeClass
  public static void beforeClass()
      throws BestandException, IOException {
    var invoer  = CLASSLOADER.getResourceAsStream("test.zip");
    var uitvoer = Paths.get(TEMP + File.separator + "test.zip");

    Files.copy(invoer, uitvoer, StandardCopyOption.REPLACE_EXISTING);

    invoer      = CLASSLOADER.getResourceAsStream("tekst.txt");
    uitvoer     = Paths.get(TEMP + File.separator + "tekst1.txt");
    Files.copy(invoer, uitvoer, StandardCopyOption.REPLACE_EXISTING);

    invoer      = CLASSLOADER.getResourceAsStream("test.csv");
    uitvoer     = Paths.get(TEMP + File.separator + "test1.csv");
    Files.copy(invoer, uitvoer, StandardCopyOption.REPLACE_EXISTING);
  }

  @Test
  public void testInpakken1() {
    var zip = new ZipBestand.Builder()
                            .setZip(TEMP + File.separator + "test1.zip")
                            .setLezen(false)
                            .build();

    try {
      zip.inpakken(TEMP + File.separator + "tekst1.txt");
      zip.inpakken(TEMP + File.separator + "test1.csv");

      Bestand.delete(TEMP + File.separator + "tekst1.txt");
      Bestand.delete(TEMP + File.separator + "test1.csv");

      zip.uitpakken(TEMP);
      assertTrue(
          Bestand.equals(
              Bestand.openInvoerBestand(CsvBestandTest.class.getClassLoader(),
                                        "tekst.txt"),
              Bestand.openInvoerBestand(TEMP + File.separator
                                        + "tekst1.txt")));
      assertTrue(
          Bestand.equals(
              Bestand.openInvoerBestand(CsvBestandTest.class.getClassLoader(),
                                        "test.csv"),
              Bestand.openInvoerBestand(TEMP + File.separator
                                        + "test1.csv")));
    } catch (BestandException e) {
      fail(e.getLocalizedMessage());
    }
  }

  @Test
  public void testInpakken2() {
    var zip = new ZipBestand.Builder()
                            .setZip(TEMP + File.separator + "test1.zip")
                            .build();

    try {
      zip.inpakken(TEMP + File.separator + "tekst1.txt");
      fail("BestandsException missing");
    } catch (BestandException e) {
      assertTrue(e.getLocalizedMessage().startsWith("BEST-0004: "));
    }
  }

  @Test
  public void testInpakken3() {
    var zip = new ZipBestand.Builder()
                            .setClassLoader(CLASSLOADER)
                            .setZip(TEMP + File.separator + "test1.zip")
                            .build();

    try {
      zip.inpakken(TEMP + File.separator + "tekst1.txt");
      fail("BestandsException missing");
    } catch (BestandException e) {
      assertTrue(e.getLocalizedMessage().startsWith("BEST-0004: "));
    }
  }

  @Test
  public void testInpakken4() {
    var zip = new ZipBestand.Builder()
                            .setClassLoader(CLASSLOADER)
                            .setZip(TEMP + File.separator + "test1.zip")
                            .setLezen(true)
                            .build();

    try {
      zip.inpakken(TEMP + File.separator + "tekst1.txt");
      fail("BestandsException missing");
    } catch (BestandException e) {
      assertTrue(e.getLocalizedMessage().startsWith("BEST-0004: "));
    }
  }

  @Test
  public void testInpakken5() {
    var zip = new ZipBestand.Builder()
                            .setZip(TEMP + File.separator + "test1.zip")
                            .setLezen(true)
                            .build();

    try {
      zip.inpakken(TEMP + File.separator + "tekst1.txt");
      fail("BestandsException missing");
    } catch (BestandException e) {
      assertTrue(e.getLocalizedMessage().startsWith("BEST-0004: "));
    }
  }

  @Test
  public void testInpakken6() {
    var zip = new ZipBestand.Builder()
                            .setZip(TEMP + File.separator + "test1.zip")
                            .setLezen(false)
                            .build();

    try {
      zip.inpakken(TEMP + File.separator + "tekstX.txt");
      fail("BestandsException missing");
    } catch (BestandException e) {
      assertTrue(e.getLocalizedMessage().startsWith("BEST-0002: "));
    }
  }

  @Test
  public void testUitpakken() {
    var zip = new ZipBestand.Builder()
                            .setClassLoader(CLASSLOADER)
                            .setZip(TEMP + File.separator + "test.zip")
                            .build();

    try {
      zip.uitpakken(TEMP);
      assertTrue(
          Bestand.equals(
              Bestand.openInvoerBestand(CsvBestandTest.class.getClassLoader(),
                                        "tekst.txt"),
              Bestand.openInvoerBestand(TEMP + File.separator
                                        + "tekst.txt")));
      assertTrue(
          Bestand.equals(
              Bestand.openInvoerBestand(CsvBestandTest.class.getClassLoader(),
                                        "test.csv"),
              Bestand.openInvoerBestand(TEMP + File.separator
                                        + "test.csv")));
    } catch (BestandException e) {
      fail(e.getLocalizedMessage());
    }
  }
}
