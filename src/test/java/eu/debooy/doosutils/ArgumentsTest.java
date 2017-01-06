/**
 * Copyright 2009 Marco de Booy
 *
 * Licensed under the EUPL, Version 1.0 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the Licence. You may
 * obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/7330l5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package eu.debooy.doosutils;

import junit.framework.TestCase;

import org.junit.Test;


/**
 * @author Marco de Booij
 */
public class ArgumentsTest extends TestCase {
  String[]  args1 = {"-delete"};
  String[]  args2 = {"-delete=true"};
  String[]  args3 = {"-delete", "true"};
  String[]  args4 = {"--delete"};
  String[]  args5 = {"--delete=true"};
  String[]  args6 = {"--delete", "true"};
  String[]  args7 = {"-delete", "-insert"};
  String[]  args8 = {"-delete", "true", "-insert", "true"};
  String[]  args9 = {"-delete"};

  @Test
  public void testInitialize() {
    String[]  args      = {"-delete", "true", "-insert"};
    Arguments arguments = new Arguments(args);

    assertTrue(arguments.isValid());
    assertTrue(arguments.hasArgument("delete"));
    assertTrue("true".equals(arguments.getArgument("delete")));
    assertTrue(arguments.hasArgument("insert"));
  }

  @Test
  public void testZonderMinZonderWaarde() {
    Arguments arguments = new Arguments();
    String[]  args      = {"delete"};

    assertFalse(arguments.setArguments(args));
  }

  @Test
  public void testZonderMinMetWaarde() {
    Arguments arguments = new Arguments();
    String[]  args      = {"delete=true"};

    assertTrue(arguments.setArguments(args));
    assertTrue(arguments.hasArgument("delete"));
    assertTrue("true".equals(arguments.getArgument("delete")));
  }

  @Test
  public void testEnkelMinZonderWaarde() {
    Arguments arguments = new Arguments();
    String[]  args      = {"-delete"};

    assertTrue(arguments.setArguments(args));
    assertTrue(arguments.hasArgument("delete"));
    assertTrue("".equals(arguments.getArgument("delete")));
  }

  @Test
  public void testEnkelMinMetWaarde1() {
    Arguments arguments = new Arguments();
    String[]  args      = {"-delete=true"};

    assertTrue(arguments.setArguments(args));
    assertTrue(arguments.hasArgument("delete"));
    assertTrue("true".equals(arguments.getArgument("delete")));
  }

  @Test
  public void testEnkelMinMetWaarde2() {
    Arguments arguments = new Arguments();
    String[]  args      = {"-delete", "true"};

    assertTrue(arguments.setArguments(args));
    assertTrue(arguments.hasArgument("delete"));
    assertTrue("true".equals(arguments.getArgument("delete")));
  }

  @Test
  public void testDubbelMinZonderWaarde() {
    Arguments arguments = new Arguments();
    String[]  args      = {"--delete"};

    assertTrue(arguments.setArguments(args));
    assertTrue(arguments.hasArgument("delete"));
    assertTrue("".equals(arguments.getArgument("delete")));
  }

  @Test
  public void testDubbelMinMetWaarde1() {
    Arguments arguments = new Arguments();
    String[]  args      = {"-delete=true"};

    assertTrue(arguments.setArguments(args));
    assertTrue(arguments.hasArgument("delete"));
    assertTrue("true".equals(arguments.getArgument("delete")));
  }

  @Test
  public void testDubbelMinMetWaarde2() {
    Arguments arguments = new Arguments();
    String[]  args      = {"-delete", "true"};

    assertTrue(arguments.setArguments(args));
    assertTrue(arguments.hasArgument("delete"));
    assertTrue("true".equals(arguments.getArgument("delete")));
  }

  @Test
  public void testTweeParametersZonderWaarde() {
    Arguments arguments = new Arguments();
    String[]  args      = {"-delete", "-insert"};

    assertTrue(arguments.setArguments(args));
    assertTrue(arguments.hasArgument("delete"));
    assertTrue(arguments.hasArgument("insert"));
  }

  @Test
  public void testTweeParametersMetWaarde() {
    Arguments arguments = new Arguments();
    String[]  args      = {"-delete", "true", "-insert", "true"};

    assertTrue(arguments.setArguments(args));
    assertTrue(arguments.hasArgument("delete"));
    assertTrue("true".equals(arguments.getArgument("delete")));
    assertTrue(arguments.hasArgument("insert"));
    assertTrue("true".equals(arguments.getArgument("insert")));
  }

  @Test
  public void testTweeParametersZonderMetWaarde() {
    Arguments arguments = new Arguments();
    String[]  args      = {"-delete", "-insert", "true"};

    assertTrue(arguments.setArguments(args));
    assertTrue(arguments.hasArgument("delete"));
    assertTrue(arguments.hasArgument("insert"));
    assertTrue("true".equals(arguments.getArgument("insert")));
  }

  @Test
  public void testTweeParametersMetZonderWaarde() {
    Arguments arguments = new Arguments();
    String[]  args      = {"-delete", "true", "-insert"};

    assertTrue(arguments.setArguments(args));
    assertTrue(arguments.hasArgument("delete"));
    assertTrue("true".equals(arguments.getArgument("delete")));
    assertTrue(arguments.hasArgument("insert"));
  }

  @Test
  public void testParameters() {
    Arguments arguments = new Arguments();
    String[]  args      = {"-delete", "true", "--insert=false", "update=true"};

    assertTrue(arguments.setArguments(args));
    arguments.setParameters(new String[]{"delete"});
    assertFalse(arguments.isValid());
    arguments.setParameters(new String[]{"delete", "insert", "update"});
    assertTrue(arguments.isValid());
  }

  @Test
  public void testVerplicht() {
    Arguments arguments = new Arguments();
    String[]  args      = {"-delete", "true", "--insert=false", "update=true"};

    assertTrue(arguments.setArguments(args));
    arguments.setVerplicht(new String[]{"delete"});
    assertTrue(arguments.isValid());
    arguments.setVerplicht(new String[]{"insert"});
    assertTrue(arguments.isValid());
    arguments.setVerplicht(new String[]{"update"});
    assertTrue(arguments.isValid());
  }
}
