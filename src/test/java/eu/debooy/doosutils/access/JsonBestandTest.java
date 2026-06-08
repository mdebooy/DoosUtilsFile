/**
 * Copyright 2020 Marco de Booij
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
import java.util.Locale;
import java.util.ResourceBundle;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author Marco de Booij
 */
public class JsonBestandTest {
  protected static  ResourceBundle  resourceBundle;
  protected static  String          json;

  protected static final  String      BST_UITVOER       = "testUitvoer.json";
  protected static final  String      BST_PRETTYUITVOER =
      "testPrettyUitvoer.json";
  protected static final  ClassLoader CLASSLOADER       =
      JsonBestand.class.getClassLoader();
  protected static final  String      JSONSTRING        =
      "{\"id\":1,\"naam\":\"Jan\",\"prijs\":123.5," +
       "\"tags\":[\"Aap\",\"Noot\"]," +
       "\"voor,raad\":{\"magazijn\":300," +
                     "\"win\\\"kel\":20}}";
  protected static final  String      JSONSTRING2       =
      "{\"id\":2,\"naam\":\"Piet\"}";
  protected static final  String      TEMP              =
      System.getProperty("java.io.tmpdir");

  @BeforeClass
  public static void beforeClass() throws ParseException {
    Locale.setDefault(new Locale.Builder().setLanguage("nl").build());
    resourceBundle   = ResourceBundle.getBundle("DoosUtils-file",
                                                Locale.getDefault());
    JSONParser  parser  = new JSONParser();
    json  = ((JSONObject) parser.parse(JSONSTRING)).toJSONString();
  }

  @Test
  public void testInvoer() throws BestandException {
    JsonBestand jsonBestand = null;
    try {
      jsonBestand = new JsonBestand.Builder().setClassLoader(CLASSLOADER)
                                             .setBestand(BST_UITVOER)
                                             .build();
      assertFalse("Invoer - append", jsonBestand.isAppend());
      assertFalse("Invoer - prettify", jsonBestand.isPrettify());
      assertTrue("Invoer - lezen", jsonBestand.isReadOnly());
      assertEquals(json, ((JSONObject) jsonBestand.read()).toJSONString());
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    } finally {
      if (null != jsonBestand) {
        jsonBestand.close();
      }
    }
  }

  @Test
  public void testPrettyInvoer() throws BestandException {
    JsonBestand jsonBestand = null;
    try {
      jsonBestand = new JsonBestand.Builder()
                                   .setClassLoader(CLASSLOADER)
                                   .setBestand(BST_PRETTYUITVOER)
                                   .build();
      assertFalse("Pretty Invoer - append", jsonBestand.isAppend());
      assertFalse("Pretty Invoer - prettify", jsonBestand.isPrettify());
      assertTrue("Pretty Invoer - lezen", jsonBestand.isReadOnly());
      assertEquals(json, ((JSONObject) jsonBestand.read()).toJSONString());
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    } finally {
      if (null != jsonBestand) {
        jsonBestand.close();
      }
    }
  }

  @Test
  public void testPrettyUitvoer() throws BestandException {
    JsonBestand jsonBestand = null;
    try {
      jsonBestand = new JsonBestand.Builder().setBestand(TEMP + File.separator
                                                        + BST_UITVOER)
                                             .setLezen(false)
                                             .setPrettify(true)
                                             .build();
      jsonBestand.write(JSONSTRING);
      assertFalse("Pretty Uitvoer - append", jsonBestand.isAppend());
      assertTrue("Pretty Uitvoer - prettify", jsonBestand.isPrettify());
      assertFalse("Pretty Uitvoer - lezen", jsonBestand.isReadOnly());
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    } finally {
      if (null != jsonBestand) {
        jsonBestand.close();
      }
    }

    assertTrue("Pretty Uitvoer - equals",
        Bestand.equals(
            Bestand.openInvoerBestand(TEMP + File.separator
                                      + BST_UITVOER),
            Bestand.openInvoerBestand(JsonBestandTest.class.getClassLoader(),
                                      BST_PRETTYUITVOER)));

    Bestand.delete(TEMP + File.separator + BST_UITVOER);
  }

  @Test
  public void testUitvoer() throws BestandException {
    JsonBestand jsonBestand = null;
    try {
      jsonBestand = new JsonBestand.Builder().setBestand(TEMP + File.separator
                                                        + BST_UITVOER)
                                             .setLezen(false)
                                             .build();
      jsonBestand.write(JSONSTRING);
      assertFalse("Uitvoer - append", jsonBestand.isAppend());
      assertFalse("Uitvoer - prettify", jsonBestand.isPrettify());
      assertFalse("Uitvoer - lezen", jsonBestand.isReadOnly());
    } catch (BestandException e) {
      assertTrue(e.getMessage(), false);
    } finally {
      if (null != jsonBestand) {
        jsonBestand.close();
      }
    }

    jsonBestand = new JsonBestand.Builder().setBestand(TEMP + File.separator
                                                       + BST_UITVOER)
                                           .setLezen(true)
                                           .build();
    JsonBestand jsonGoed  =
        new JsonBestand.Builder().setClassLoader(CLASSLOADER)
                                 .setBestand(BST_UITVOER)
                                 .setLezen(true)
                                 .build();
    assertEquals("Uitvoer - Uitvoer",
                            ((JSONObject) jsonGoed.read()).toJSONString(),
                            ((JSONObject) jsonBestand.read()).toJSONString());

    Bestand.delete(TEMP + File.separator + BST_UITVOER);
  }
}
