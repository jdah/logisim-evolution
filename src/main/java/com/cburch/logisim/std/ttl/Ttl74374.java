/*
 * This file is part of logisim-evolution.
 *
 * Logisim-evolution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Logisim-evolution is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with logisim-evolution. If not, see <http://www.gnu.org/licenses/>.
 *
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * Subsequent modifications by:
 *   + College of the Holy Cross
 *     http://www.holycross.edu
 *   + Haute École Spécialisée Bernoise/Berner Fachhochschule
 *     http://www.bfh.ch
 *   + Haute École du paysage, d'ingénierie et d'architecture de Genève
 *     http://hepia.hesge.ch/
 *   + Haute École d'Ingénierie et de Gestion du Canton de Vaud
 *     http://www.heig-vd.ch/
 */

package com.cburch.logisim.std.ttl;

import com.cburch.logisim.circuit.appear.DynamicElement;
import com.cburch.logisim.circuit.appear.DynamicElementProvider;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.fpga.designrulecheck.CorrectLabel;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.StdAttr;

public class Ttl74374 extends AbstractTtlGate implements DynamicElementProvider {

  public static final int PORT_INDEX_nOC = 0;
  public static final int PORT_INDEX_1Q = 1;
  public static final int PORT_INDEX_1D = 2;
  public static final int PORT_INDEX_2D = 3;
  public static final int PORT_INDEX_2Q = 4;
  public static final int PORT_INDEX_3Q = 5;
  public static final int PORT_INDEX_3D = 6;
  public static final int PORT_INDEX_4D = 7;
  public static final int PORT_INDEX_4Q = 8;
  public static final int PORT_INDEX_C = 9;
  public static final int PORT_INDEX_5Q = 10;
  public static final int PORT_INDEX_5D = 11;
  public static final int PORT_INDEX_6D = 12;
  public static final int PORT_INDEX_6Q = 13;
  public static final int PORT_INDEX_7Q = 14;
  public static final int PORT_INDEX_7D = 15;
  public static final int PORT_INDEX_8D = 16;
  public static final int PORT_INDEX_8Q = 17;

  public static final byte[] INPUTS_D = {
      PORT_INDEX_1D, PORT_INDEX_2D, PORT_INDEX_3D, PORT_INDEX_4D,
      PORT_INDEX_5D, PORT_INDEX_6D, PORT_INDEX_7D, PORT_INDEX_8D
  };

  public static final byte[] OUTPUTS_Q = {
      PORT_INDEX_1Q, PORT_INDEX_2Q, PORT_INDEX_3Q, PORT_INDEX_4Q,
      PORT_INDEX_5Q, PORT_INDEX_6Q, PORT_INDEX_7Q, PORT_INDEX_8Q
  };

  public Ttl74374() {
    super(
        "74374",
        (byte) 20,
        new byte[] {
            PORT_INDEX_1Q + 1, PORT_INDEX_2Q + 1, PORT_INDEX_3Q + 1, PORT_INDEX_4Q + 1,
            PORT_INDEX_5Q + 2, PORT_INDEX_6Q + 2, PORT_INDEX_7Q + 2, PORT_INDEX_8Q + 2
        },
        new String[] {
            "nOC", "1Q", "1D", "2D", "2Q", "3Q", "3D", "4D", "4Q",
            "C", "5Q", "5D", "6D", "6Q", "7Q", "7D", "8D", "8Q"
        });
  }

  @Override
  public void paintInternal(InstancePainter painter, int x, int y, int height, boolean up) {
    super.paintBase(painter, true, false);
    Drawgates.paintPortNames(painter, x, y, height, super.portnames);
  }

  @Override
  public void ttlpropagate(InstanceState state) {
    TTLRegisterData data = (TTLRegisterData) state.getData();
    if (data == null) {
      data = new TTLRegisterData(BitWidth.create(8));
      state.setData(data);
    }

    Value c = state.getPortValue(PORT_INDEX_C), override = null;
    Value[] vs = data.getValue().getAll();

    // positive edge triggered
    if (c.isFullyDefined() && data.updateClock(c, StdAttr.TRIG_RISING)) {
      for (int i = 0; i < 8; i++) {
        vs[i] = state.getPortValue(INPUTS_D[i]);
      }
    }

    Value nOC = state.getPortValue(PORT_INDEX_nOC);

    if (!nOC.isFullyDefined() || nOC == Value.TRUE) {
      override = Value.NIL;
    }

    for (int i = 0; i < 8; i++) {
      state.setPort(OUTPUTS_Q[i], override == null ? vs[i] : override, 1);
    }

    data.setValue(Value.create(vs));
    state.setData(data);
  }

  @Override
  public String getHDLName(AttributeSet attrs) {
    return CorrectLabel.getCorrectLabel("TTL" + this.getName()).toUpperCase();
  }

  @Override
  public boolean HDLSupportedComponent(AttributeSet attrs) {
      return false;
  }

  @Override
  public DynamicElement createDynamicElement(int x, int y, DynamicElement.Path path) {
    return new TTLRegisterShape(x, y, path);
  }
}
