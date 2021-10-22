/**
 * This file is part of logisim-evolution.
 * <p>
 * Logisim-evolution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * <p>
 * Logisim-evolution is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with logisim-evolution. If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * Subsequent modifications by:
 * + College of the Holy Cross
 * http://www.holycross.edu
 * + Haute École Spécialisée Bernoise/Berner Fachhochschule
 * http://www.bfh.ch
 * + Haute École du paysage, d'ingénierie et d'architecture de Genève
 * http://hepia.hesge.ch/
 * + Haute École d'Ingénierie et de Gestion du Canton de Vaud
 * http://www.heig-vd.ch/
 */

package com.cburch.logisim.std.ttl;

import com.cburch.logisim.circuit.appear.DynamicElement;
import com.cburch.logisim.circuit.appear.DynamicElementProvider;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.fpga.designrulecheck.NetlistComponent;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstancePoker;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.util.GraphicsUtil;

import java.awt.*;
import java.awt.event.MouseEvent;

import static com.cburch.logisim.data.Value.FALSE_COLOR;
import static com.cburch.logisim.data.Value.TRUE_COLOR;

public class Ttl74193 extends AbstractTtlGate implements DynamicElementProvider {

  public static final int PORT_INDEX_B = 0;
  public static final int PORT_INDEX_QB = 1;
  public static final int PORT_INDEX_QA = 2;
  public static final int PORT_INDEX_DOWN = 3;
  public static final int PORT_INDEX_UP = 4;
  public static final int PORT_INDEX_QC = 5;
  public static final int PORT_INDEX_QD = 6;
  public static final int PORT_INDEX_D = 7;
  public static final int PORT_INDEX_C = 8;
  public static final int PORT_INDEX_nLOAD = 9;
  public static final int PORT_INDEX_nCO = 10;
  public static final int PORT_INDEX_nBO = 11;
  public static final int PORT_INDEX_CLR = 12;
  public static final int PORT_INDEX_A = 13;

  public static final int[] PORT_INDICES_IN = {PORT_INDEX_A, PORT_INDEX_B, PORT_INDEX_C, PORT_INDEX_D};

  public static final int[] PORT_INDICES_Q = {PORT_INDEX_QA, PORT_INDEX_QB, PORT_INDEX_QC, PORT_INDEX_QD};

  public Ttl74193() {
    super(
        "74193",
        (byte) 16,
        new byte[]{
            PORT_INDEX_QA + 1, PORT_INDEX_QB + 1, PORT_INDEX_QC + 1, PORT_INDEX_QD + 1,
            PORT_INDEX_nCO + 2, PORT_INDEX_nBO + 2
        },
        new String[]{
            "B", "QB", "QA", "DOWN", "UP", "QC", "QD", "D", "C", "nLOAD", "nCO", "nBO", "CLR", "A"
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
      data = new TTLRegisterData(BitWidth.create(4));
      state.setData(data);

      state.setPort(PORT_INDEX_nCO, Value.TRUE, 1);
      state.setPort(PORT_INDEX_nBO, Value.TRUE, 1);
    }

    Value up = state.getPortValue(PORT_INDEX_UP),
        down = state.getPortValue(PORT_INDEX_DOWN),
        nLoad = state.getPortValue(PORT_INDEX_nLOAD),
        clr = state.getPortValue(PORT_INDEX_CLR),
        override = null;

    long value = data.getValue().toLongValue();

    if (!up.isFullyDefined() || !down.isFullyDefined() ||
        !nLoad.isFullyDefined() || !clr.isFullyDefined()) {
      override = Value.ERROR;
    } else {
      if (data.updateClock(clr, 0, StdAttr.TRIG_RISING)) {
        value = 0;
      }

      if (nLoad == Value.FALSE) {
        value = 0;

        for (int i = 0; i < 4; i++) {
          Value x = state.getPortValue(PORT_INDICES_IN[i]);

          if (!x.isFullyDefined()) {
            override = Value.ERROR;
            break;
          }

          value |= x.toLongValue() << i;
        }
      }

      // increment and check for carry
      if (data.checkClock(up, 1, StdAttr.TRIG_RISING)) {
        if (state.getPortValue(PORT_INDEX_nCO) == Value.FALSE) {
          state.setPort(PORT_INDEX_nCO, Value.TRUE, 1);
        }

        value = (value + 1) % 16;
      } else if (data.checkClock(up, 1, StdAttr.TRIG_FALLING) && value == 15) {
          state.setPort(PORT_INDEX_nCO, Value.FALSE, 1);
      }

      data.updateClock(up, 1);

      // decrement and check for borrow
      if (data.checkClock(down, 2, StdAttr.TRIG_RISING)) {
        if (state.getPortValue(PORT_INDEX_nBO) == Value.FALSE) {
          state.setPort(PORT_INDEX_nBO, Value.TRUE, 1);
        }

        value = (value - 1) % 16;
      } else if (data.checkClock(down, 2, StdAttr.TRIG_FALLING) && value == 0) {
        state.setPort(PORT_INDEX_nBO, Value.FALSE, 1);
      }

      data.updateClock(down, 2);

      for (int i = 0; i < 4; i++) {
        state.setPort(
            PORT_INDICES_Q[i],
            override == null ? Value.createKnown(1, (value >> i) & 0x01) : override,
            1
        );
      }
    }

    if (override == null) {
      data.setValue(Value.createKnown(4, value));
    }

    state.setData(data);
  }

  @Override
  public DynamicElement createDynamicElement(int x, int y, DynamicElement.Path path) {
    return new TTLRegisterShape(x, y, path);
  }
}
