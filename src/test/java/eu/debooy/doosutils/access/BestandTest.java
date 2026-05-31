/*
 * Copyright (c) 2026 Marco de Booij
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

import eu.debooy.doosutils.exception.BestandException;
import eu.debooy.doosutils.test.BatchTest;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author Marco de Booij
 */
public class BestandTest extends BatchTest {
  protected static final  String      BST_TEST    = "test.csv";
  protected static final  ClassLoader CLASSLOADER =
      CsvBestand.class.getClassLoader();

  @AfterClass
  public static void afterClass() {
    verwijderBestanden(String.format("%s%s",
                                   getTemp(), File.separator),
                       new String[] {BST_TEST});
  }

  @BeforeClass
  public static void beforeClass() throws BestandException {
    Locale.setDefault(new Locale.Builder().setLanguage("nl").build());
    resourceBundle  = ResourceBundle.getBundle("DoosUtils-file",
                                               Locale.getDefault());

    try {
      kopieerBestand(CLASSLOADER, BST_TEST,
                     String.format("%s%s%s",
                                   getTemp(), File.separator, BST_TEST));
    } catch (IOException e) {
      System.out.println(e.getLocalizedMessage());
      throw new BestandException(e);
    }
  }

  @Test
  public void testBestaat01() {
    assertTrue(Bestand.bestaat(String.format("%s%s%s",
                                   getTemp(), File.separator, BST_TEST)));
  }

  @Test
  public void testBestaat02() {
    assertFalse(Bestand.bestaat(BST_TEST));
  }
}
