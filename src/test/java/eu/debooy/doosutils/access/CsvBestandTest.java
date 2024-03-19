/**
 * Copyright 2018 Marco de Booij
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
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

import eu.debooy.doosutils.exception.BestandException;
import java.io.File;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import static junit.framework.TestCase.fail;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author Marco de Booij
 */
public class CsvBestandTest {
  protected static  ResourceBundle  resourceBundle;

  protected static final  String[]    KOLOMMEN    =
      new String[] {"Naam", "waarde"};
  protected static final  String      TEMP        =
      System.getProperty("java.io.tmpdir");
  protected static final  ClassLoader CLASSLOADER =
      CsvBestand.class.getClassLoader();

  @AfterClass
  public static void afterClass() throws BestandException {
    Bestand.delete(TEMP + File.separator + "test.csv");
    Bestand.delete(TEMP + File.separator + "testheading.csv");
  }

  @BeforeClass
  public static void beforeClass() throws BestandException {
    Locale.setDefault(new Locale("nl"));
    resourceBundle   = ResourceBundle.getBundle("DoosUtils-file",
                                                Locale.getDefault());

    TekstBestand        bron  = null;
    TekstBestand        doel  = null;
    try {
      bron    = new TekstBestand.Builder().setClassLoader(CLASSLOADER)
                                          .setBestand("test.csv").build();
      doel    = new TekstBestand.Builder().setBestand(TEMP + File.separator
                                                      + "test.csv")
                                          .setLezen(false).build();
      doel.add(bron);
      bron.close();
      doel.close();
      bron    = new TekstBestand.Builder().setClassLoader(CLASSLOADER)
                                          .setBestand("testheading.csv")
                                          .build();
      doel    = new TekstBestand.Builder().setBestand(TEMP + File.separator
                                                      + "testheading.csv")
                                          .setLezen(false).build();
      doel.add(bron);
    } finally {
      if (null != bron) {
        bron.close();
      }
      if (null != doel) {
        doel.close();
      }
    }
  }

  @Test
  public void testInvoerUitClasspath() {
    String[]    kolommen    = new String[2];
    CsvBestand  csvBestand  = null;
    try {
      csvBestand  = new CsvBestand.Builder().setClassLoader(CLASSLOADER)
                                            .setBestand("test.csv")
                                            .setHeader(false)
                                            .build();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    assertEquals("InvoerUitClasspath - naam",
                 "CLASSPATH/test.csv", csvBestand.getBestand());
    assertFalse("InvoerUitClasspath - Heading", csvBestand.hasHeading());
    assertTrue("InvoerUitClasspath - ReadOnly", csvBestand.isReadOnly());
    var lijnen  = 0;
    while (csvBestand.hasNext()) {
      lijnen++;
      try {
        csvBestand.next();
      } catch (BestandException e) {
        assertTrue("InvoerUitClasspath - leesfout - " + e.getLocalizedMessage(),
                   false);
      }
    }
    assertEquals("InvoerUitClasspath - lijnen", 3, lijnen);
    assertTrue("InvoerUitClasspath - EOF", csvBestand.isEof());
    assertFalse("InvoerUitClasspath - next", csvBestand.hasNext());
    assertArrayEquals("InvoerUitClasspath - kolommen",
                      kolommen, csvBestand.getKolomNamen());
    try {
      csvBestand.next();
      fail("InvoerUitClasspath - Na EOF gelukt :-(");
    } catch (BestandException e) {
      assertEquals("InvoerUitClasspath - Na EOF",
                   e.getMessage(),
                   resourceBundle.getString(BestandConstants.ERR_BEST_EOF));
    }
  }

  @Test
  public void testInvoerHeadingUitClasspath() {
    CsvBestand  csvBestand  = null;
    try {
      csvBestand  = new CsvBestand.Builder().setClassLoader(CLASSLOADER)
                                            .setBestand("testheading.csv")
                                            .build();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    try {
      csvBestand  = new CsvBestand.Builder().setClassLoader(CLASSLOADER)
                                            .setBestand("testheading.csv")
                                            .setKolomNamen(KOLOMMEN)
                                            .build();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    assertEquals("InvoerHeadingUitClasspath - naam",
                 "CLASSPATH/testheading.csv", csvBestand.getBestand());
    assertTrue("InvoerHeadingUitClasspath - Heading", csvBestand.hasHeading());
    assertTrue("InvoerHeadingUitClasspath - ReadOnly", csvBestand.isReadOnly());
    var lijnen  = 0;
    while (csvBestand.hasNext()) {
      lijnen++;
      try {
        csvBestand.next();
      } catch (BestandException e) {
        assertTrue("InvoerHeadingUitClasspath - leesfout - "
                   + e.getLocalizedMessage(), false);
      }
    }
    assertEquals("InvoerHeadingUitClasspath - lijnen", 3, lijnen);
    assertTrue("InvoerHeadingUitClasspath - EOF", csvBestand.isEof());
    assertFalse("InvoerHeadingUitClasspath - next", csvBestand.hasNext());
    assertArrayEquals("InvoerUitClasspath - kolommen",
                      KOLOMMEN, csvBestand.getKolomNamen());
    try {
      csvBestand.next();
      fail("InvoerHeadingUitClasspath - Na EOF gelukt :-(");
    } catch (BestandException e) {
      assertEquals("InvoerHeadingUitClasspath - Na EOF",
                   e.getMessage(),
                   resourceBundle.getString(BestandConstants.ERR_BEST_EOF));
    }
  }

  @Test
  public void testInvoerUitDirectory() {
    var         kolommen    = new String[2];
    CsvBestand  csvBestand  = null;
    try {
      csvBestand  = new CsvBestand.Builder().setBestand(TEMP + File.separator
                                                        + "test.csv")
                                            .setHeader(false)
                                            .build();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    assertEquals("InvoerUitDirectory - naam",
                 TEMP + File.separator + "test.csv", csvBestand.getBestand());
    assertTrue("InvoerUitDirectory - ReadOnly", csvBestand.isReadOnly());
    var lijnen  = 0;
    while (csvBestand.hasNext()) {
      lijnen++;
      try {
        csvBestand.next();
      } catch (BestandException e) {
        assertTrue("InvoerUitDirectory - leesfout - " + e.getLocalizedMessage(),
                   false);
      }
    }
    assertEquals("InvoerUitDirectory - lijnen", 3, lijnen);
    assertTrue("InvoerUitDirectory - EOF", csvBestand.isEof());
    assertFalse("InvoerUitDirectory - next", csvBestand.hasNext());
    assertArrayEquals("InvoerUitClasspath - kolommen",
                      kolommen, csvBestand.getKolomNamen());
    try {
      csvBestand.next();
      fail("InvoerUitDirectory - Na EOF gelukt :-(");
    } catch (BestandException e) {
      assertEquals("InvoerUitDirectory - Na EOF",
                   resourceBundle.getString(BestandConstants.ERR_BEST_EOF),
                   e.getMessage());
    }
    try {
      csvBestand.write(new Object[] {"schrijven", "verboden"});
      fail("InvoerUitDirectory - Toch geschreven :-(");
    } catch (BestandException e) {
      assertEquals("InvoerUitDirectory - Toch geschreven",
                   MessageFormat.format(
              resourceBundle.getString(BestandConstants.ERR_BEST_READONLY),
                                        csvBestand.getBestand()),
                   e.getMessage());
    }
  }

  @Test
  public void testInvoerHeadingUitDirectory() {
    CsvBestand  csvBestand  = null;
    try {
      csvBestand  = new CsvBestand.Builder().setBestand(TEMP + File.separator
                                                        + "testheading.csv")
                                            .setKolomNamen(KOLOMMEN)
                                            .build();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    assertEquals("InvoerHeadingUitDirectory - naam",
                 TEMP + File.separator + "testheading.csv",
                 csvBestand.getBestand());
    assertTrue("InvoerHeadingUitDirectory - ReadOnly", csvBestand.isReadOnly());
    var lijnen  = 0;
    while (csvBestand.hasNext()) {
      lijnen++;
      try {
        csvBestand.next();
      } catch (BestandException e) {
        assertTrue("InvoerHeadingUitDirectory - leesfout - "
                   + e.getLocalizedMessage(), false);
      }
    }
    assertEquals("InvoerHeadingUitDirectory - lijnen", 3, lijnen);
    assertTrue("InvoerHeadingUitDirectory - EOF", csvBestand.isEof());
    assertFalse("InvoerHeadingUitDirectory - next", csvBestand.hasNext());
    assertArrayEquals("InvoerUitClasspath - kolommen",
                      KOLOMMEN, csvBestand.getKolomNamen());
    try {
      csvBestand.next();
      fail("InvoerHeadingUitDirectory - Na EOF gelukt :-(");
    } catch (BestandException e) {
      assertEquals("InvoerHeadingUitDirectory - Na EOF",
                   e.getMessage(),
                   resourceBundle.getString(BestandConstants.ERR_BEST_EOF));
    }
  }

  @Test
  public void testLeeg() {
    @SuppressWarnings("unused")
    CsvBestand  csvBestand  = null;
    try {
      csvBestand  = new CsvBestand.Builder().setClassLoader(CLASSLOADER)
                                            .setBestand("testLeeg.csv")
                                            .setKolomNamen(KOLOMMEN)
                                            .build();
      fail("Leeg - Openen is gelukt :-(");
    } catch (BestandException e) {
      assertEquals("Leeg - Na openen",
                   MessageFormat.format(
                       resourceBundle.getString(BestandConstants.ERR_BEST_LEEG),
                                        "CLASSPATH/testLeeg.csv"),
                   e.getMessage());
    }
  }

  @Test
  public void testHeadingLeeg() {
    @SuppressWarnings("unused")
    CsvBestand  csvBestand  = null;
    try {
      csvBestand  = new CsvBestand.Builder().setClassLoader(CLASSLOADER)
                                            .setBestand("testheadingLeeg.csv")
                                            .setKolomNamen(KOLOMMEN)
                                            .build();
      fail("Leeg - Openen is gelukt :-(");
    } catch (BestandException e) {
      assertEquals("Leeg - Na openen",
                   MessageFormat.format(
                       resourceBundle.getString(BestandConstants.ERR_BEST_LEEG),
                                        "CLASSPATH/testheadingLeeg.csv"),
                   e.getMessage());
    }
  }

  @Test
  public void testLegeLijn() {
    CsvBestand  csvBestand  = null;
    try {
      csvBestand  = new CsvBestand.Builder().setClassLoader(CLASSLOADER)
                                            .setBestand("testLegeLijn.csv")
                                            .setHeader(false)
                                            .build();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    var lijnen  = 0;
    while (lijnen < 3) {
      lijnen++;
      try {
        csvBestand.next();
      } catch (BestandException e) {
        assertTrue(e.getMessage(), false);
      }
    }
    try {
      lijnen++;
      csvBestand.next();
      fail("LegeLijn - Lezen lijn 4 is gelukt :-(");
    } catch (BestandException e) {
      assertEquals("LegeLijn - 4 - ", MessageFormat.format(
              resourceBundle.getString(BestandConstants.ERR_CSV_KOLOM_AANTAL),
                                                           1, 2, lijnen),
                                      e.getLocalizedMessage());
    }
  }

  @Test
  public void testheadingLegeLijn() {
    CsvBestand  csvBestand  = null;
    try {
      csvBestand  = new CsvBestand.Builder().setClassLoader(CLASSLOADER)
                                            .setBestand("testheadingLegeLijn.csv")
                                            .setKolomNamen(KOLOMMEN)
                                            .build();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    var lijnen  = 0;
    while (lijnen < 3) {
      lijnen++;
      try {
        csvBestand.next();
      } catch (BestandException e) {
        assertTrue(e.getMessage(), false);
      }
    }
    try {
      lijnen++;
      csvBestand.next();
      fail("LegeLijn - Lezen lijn 4 is gelukt :-(");
    } catch (BestandException e) {
      assertEquals("LegeLijn - 4 - ", MessageFormat.format(
              resourceBundle.getString(BestandConstants.ERR_CSV_KOLOM_AANTAL),
                                                           1, 2, lijnen),
                                      e.getLocalizedMessage());
    }
  }

  @Test
  public void testUitvoer() throws BestandException {
    CsvBestand  csvBestand  = null;
    try {
      csvBestand  = new CsvBestand.Builder().setBestand(TEMP + File.separator
                                                        + "testUitvoer.csv")
                                            .setLezen(false)
                                            .setHeader(false)
                                            .build();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    var kolommen  = new Object[] {"Edward", 3};
    try {
      csvBestand.write(kolommen);
      kolommen  = new Object[] {"Pi", 3.1416};
      csvBestand.write(kolommen);
      csvBestand.close();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    assertTrue("Uitvoer - equals 1",
        Bestand.equals(
            Bestand.openInvoerBestand(CsvBestandTest.class.getClassLoader(),
                                      "test2.csv"),
            Bestand.openInvoerBestand(TEMP + File.separator
                                      + "testUitvoer.csv")));
    try {
      csvBestand  = new CsvBestand.Builder().setBestand(TEMP + File.separator
                                                        + "testUitvoer.csv")
                                            .setLezen(false)
                                            .setAppend(true)
                                            .setHeader(false)
                                            .build();
      kolommen    = new Object[] {"de Booij, Marco",12.345};
      csvBestand.write(kolommen);
      csvBestand.close();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    assertFalse("Uitvoer - ReadOnly", csvBestand.isReadOnly());
    assertTrue("Uitvoer - equals 2",
        Bestand.equals(
            Bestand.openInvoerBestand(CsvBestandTest.class.getClassLoader(),
                                      "test.csv"),
            Bestand.openInvoerBestand(TEMP + File.separator
                                      + "testUitvoer.csv")));

    Bestand.delete(TEMP + File.separator + "testUitvoer.csv");
  }

  @Test
  public void testUitvoerHeading() throws BestandException {
    CsvBestand  csvBestand  = null;
    try {
      csvBestand  = new CsvBestand.Builder().setBestand(TEMP + File.separator
                                                        + "testUitvoer.csv")
                                            .setLezen(false)
                                            .setKolomNamen(KOLOMMEN)
                                            .build();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    var kolommen  = new Object[] {"Edward", 3};
    try {
      csvBestand.write(kolommen);
      kolommen  = new Object[] {"Pi", 3.1416};
      csvBestand.write(kolommen);
      csvBestand.close();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    assertTrue("Uitvoer - equals 1",
        Bestand.equals(
            Bestand.openInvoerBestand(CsvBestandTest.class.getClassLoader(),
                                      "test2heading.csv"),
            Bestand.openInvoerBestand(TEMP + File.separator
                                      + "testUitvoer.csv")));
    try {
      csvBestand  = new CsvBestand.Builder().setBestand(TEMP + File.separator
                                                        + "testUitvoer.csv")
                                            .setLezen(false)
                                            .setKolomNamen(KOLOMMEN)
                                            .setAppend(true)
                                            .build();
      kolommen    = new Object[] {"de Booij, Marco",12.345};
      csvBestand.write(kolommen);
      csvBestand.close();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    assertTrue("Uitvoer - equals 2",
        Bestand.equals(
            Bestand.openInvoerBestand(CsvBestandTest.class.getClassLoader(),
                                      "testheading.csv"),
            Bestand.openInvoerBestand(TEMP + File.separator
                                      + "testUitvoer.csv")));

    Bestand.delete(TEMP + File.separator + "testUitvoer.csv");
  }

  @Test
  public void testVerkeerdeHeading() throws BestandException {
    @SuppressWarnings("unused")
    CsvBestand  csvBestand  = null;
    try {
      csvBestand  = new CsvBestand.Builder().setBestand(TEMP + File.separator
                                                        + "testheading.csv")
                                            .setLezen(false)
                                            .setKolomNamen(
                                                new String[] {"Naam", "wrde"})
                                            .setAppend(true)
                                            .build();
      fail("VerkeerdeHeading - Toch juiste heading :-(");
    } catch (BestandException e) {
      assertEquals("VerkeerdeHeading",
              resourceBundle.getString(BestandConstants.ERR_CSV_KOLOM_FOUT),
                                     e.getLocalizedMessage());
    }
    assertTrue("VerkeerdeHeading - equals",
        Bestand.equals(
            Bestand.openInvoerBestand(CsvBestandTest.class.getClassLoader(),
                                      "testheading.csv"),
            Bestand.openInvoerBestand(TEMP + File.separator
                                      + "testheading.csv")));
  }

  @Test
  public void testZonderHeading() throws BestandException {
    CsvBestand  csvBestand  = null;
    try {
      csvBestand  = new CsvBestand.Builder().setBestand(TEMP + File.separator
                                                        + "testheading.csv")
                                            .setLezen(false)
                                            .setAppend(true)
                                            .build();
      assertArrayEquals("ZonderHeading", KOLOMMEN, csvBestand.getKolomNamen());
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }
    assertTrue("ZonderHeading - equals",
        Bestand.equals(
            Bestand.openInvoerBestand(CsvBestandTest.class.getClassLoader(),
                                      "testheading.csv"),
            Bestand.openInvoerBestand(TEMP + File.separator
                                      + "testheading.csv")));
  }
}
