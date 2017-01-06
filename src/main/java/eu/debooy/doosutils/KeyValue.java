/**
 * Copyright 2016 Marco de Booij
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
package eu.debooy.doosutils;

import java.io.Serializable;


/**
 * @author Marco de Booij
 */
public class KeyValue implements Serializable {
  private static final long serialVersionUID = 1L;

  private String  sleutel;
  private String  waarde;

  public KeyValue(String sleutel, String waarde) {
    this.sleutel  = sleutel;
    this.waarde   = waarde;
  }

  /**
   * @return de sleutel
   */
  public String getSleutel() {
    return sleutel;
  }

  /**
   * @return de waarde
   */
  public String getWaarde() {
    return waarde;
  }

  /**
   * @param sleutel de waarde van sleutel
   */
  public void setSleutel(String sleutel) {
    this.sleutel = sleutel;
  }

  /**
   * @param waarde de waarde van waarde
   */
  public void setWaarde(String waarde) {
    this.waarde = waarde;
  }
}
