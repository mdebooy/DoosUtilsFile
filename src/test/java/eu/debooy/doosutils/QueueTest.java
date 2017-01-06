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
public class QueueTest {
  @Test(expected = NoSuchElementException.class)
  public void queueLinkedListException1() {
    Queue<Integer> queue  = new QueueLinkedList<Integer>();
    queue.dequeue();
  }

  @Test(expected = NoSuchElementException.class)
  public void queueLinkedListException2() {
    Queue<Integer> queue  = new QueueLinkedList<Integer>();
    queue.enqueue(1);
    queue.dequeue();
    queue.dequeue();
  }

  @Test
  public void queueLinkedList() {
    Queue<Integer> queue  = new QueueLinkedList<Integer>();
    queue.enqueue(1);
    queue.enqueue(2);
    queue.enqueue(3);
    queue.enqueue(3);
    queue.enqueue(4);
    assertTrue(5 == queue.size());
    assertTrue(1 == queue.dequeue());
    assertTrue(2 == queue.dequeue());
    assertTrue(3 == queue.dequeue());
    assertTrue(3 == queue.dequeue());
    assertTrue(4 == queue.dequeue());
    assertTrue("".equals(queue.toString()));
    assertTrue(0 == queue.size());
  }
}
