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

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import eu.debooy.doosutils.exception.BestandException;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author Marco de Booij
 */
public class TekstBestandTest {
  protected static final  ResourceBundle  resourceBundle  =
      ResourceBundle.getBundle("DoosUtils-file", new Locale("nl"));
  protected static final  String          TEMP            =
      System.getProperty("java.io.tmpdir");
  protected static final  ClassLoader     CLASSLOADER     =
      TekstBestand.class.getClassLoader();

  @AfterClass
  public static void afterClass() throws BestandException {
    Bestand.delete(TEMP + File.separator + "tekst.txt");
  }

  @BeforeClass
  public static void beforeClass() throws BestandException {
    Locale.setDefault(new Locale("nl"));
    TekstBestand        bron  = null;
    TekstBestand        doel  = null;
    try {
      bron    = new TekstBestand.Builder().setClassLoader(CLASSLOADER)
                                          .setBestand("tekst.txt").build();
      doel    = new TekstBestand.Builder().setBestand(TEMP + File.separator
                                                      + "tekst.txt")
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
    TekstBestand        tekstBestand  = null;
    try {
      tekstBestand = new TekstBestand.Builder().setClassLoader(CLASSLOADER)
                                               .setBestand("tekst.txt")
                                               .build();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    assertEquals("InvoerUitClasspath - naam",
                 "CLASSPATH/tekst.txt", tekstBestand.getBestand());
    assertTrue("InvoerUitClasspath - ReadOnly", tekstBestand.isReadOnly());
    int lijnen  = 0;
    while (tekstBestand.hasNext()) {
      lijnen++;
      try {
        tekstBestand.next();
      } catch (BestandException e) {
        assertTrue("InvoerUitClasspath - leesfout", false);
      }
    }
    assertEquals("InvoerUitClasspath - lijnen", 2, lijnen);
    assertTrue("InvoerUitClasspath - EOF", tekstBestand.isEof());
    assertFalse("InvoerUitClasspath - next", tekstBestand.hasNext());
    try {
      tekstBestand.next();
      fail("InvoerUitClasspath - Na EOF gelukt :-(");
    } catch (BestandException e) {
      assertEquals("InvoerUitClasspath - Na EOF",
                   e.getMessage(),
                   resourceBundle.getString(BestandConstants.ERR_BEST_EOF));
    }
  }

  @Test
  public void testInvoerUitDirectory() {
    TekstBestand        tekstBestand  = null;
    try {
      tekstBestand = new TekstBestand.Builder()
                                     .setBestand(TEMP + File.separator
                                                 + "tekst.txt")
                                     .build();
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    }

    assertEquals("InvoerUitDirectory - naam",
                 TEMP + File.separator + "tekst.txt",
                 tekstBestand.getBestand());
    assertTrue("InvoerUitClasspath - ReadOnly", tekstBestand.isReadOnly());
    int lijnen  = 0;
    while (tekstBestand.hasNext()) {
      lijnen++;
      try {
        tekstBestand.next();
      } catch (BestandException e) {
        assertTrue("InvoerUitDirectory - leesfout", false);
      }
    }
    assertEquals("InvoerUitDirectory - lijnen", 2, lijnen);
    assertTrue("InvoerUitDirectory - EOF", tekstBestand.isEof());
    assertFalse("InvoerUitDirectory - next", tekstBestand.hasNext());
    try {
      tekstBestand.next();
      fail("InvoerUitDirectory - Na EOF gelukt :-(");
    } catch (BestandException e) {
      assertEquals("InvoerUitDirectory - Na EOF",
                   e.getMessage(),
                   resourceBundle.getString(BestandConstants.ERR_BEST_EOF));
    }
  }
}
