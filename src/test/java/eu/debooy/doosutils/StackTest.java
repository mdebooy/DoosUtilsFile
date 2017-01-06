/**
 * Copyright 2016 Marco de Booy
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

import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.Test;


/**
 * @author Marco de Booij (booymaa)
 */
public class StackTest {
  @Test(expected = NoSuchElementException.class)
  public void stackLinkedListException1() {
    Stack<Integer>  stack = new StackLinkedList<Integer>();
    stack.pop();
  }

  @Test(expected = NoSuchElementException.class)
  public void stackLinkedListException2() {
    Stack<Integer>  stack = new StackLinkedList<Integer>();
    stack.push(1);
    stack.pop();
    stack.pop();
  }

  @Test
  public void stackLinkedList() {
    Stack<Integer>  stack = new StackLinkedList<Integer>();
    stack.push(1);
    stack.push(2);
    stack.push(3);
    stack.push(3);
    stack.push(4);
    assertTrue(5 == stack.size());
    assertTrue(4 == stack.pop());
    assertTrue(3 == stack.pop());
    assertTrue(3 == stack.pop());
    assertTrue(2 == stack.pop());
    assertTrue(1 == stack.pop());
    assertTrue("".equals(stack.toString()));
    assertTrue(0 == stack.size());
  }
}
