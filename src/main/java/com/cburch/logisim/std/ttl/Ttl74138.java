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

import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.fpga.designrulecheck.CorrectLabel;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;

public class Ttl74138 extends AbstractTtlGate {

  public static final int PORT_INDEX_A = 0;
  public static final int PORT_INDEX_B = 1;
  public static final int PORT_INDEX_C = 2;
  public static final int PORT_INDEX_nG2A = 3;
  public static final int PORT_INDEX_nG2B = 4;
  public static final int PORT_INDEX_G1 = 5;
  public static final int PORT_INDEX_Y7 = 6;
  public static final int PORT_INDEX_Y6 = 7;
  public static final int PORT_INDEX_Y5 = 8;
  public static final int PORT_INDEX_Y4 = 9;
  public static final int PORT_INDEX_Y3 = 10;
  public static final int PORT_INDEX_Y2 = 11;
  public static final int PORT_INDEX_Y1 = 12;
  public static final int PORT_INDEX_Y0 = 13;

  public static final int[] PORT_INDICES_Y = {
      PORT_INDEX_Y0, PORT_INDEX_Y1, PORT_INDEX_Y2, PORT_INDEX_Y3,
      PORT_INDEX_Y4, PORT_INDEX_Y5, PORT_INDEX_Y6, PORT_INDEX_Y7
  };

  public static final int[] PORT_INDICES_ABC = {
      PORT_INDEX_A, PORT_INDEX_B, PORT_INDEX_C
  };

  private final boolean inverted;

  public Ttl74138() {
    this("74138", true);
  }

  protected Ttl74138(String name, boolean inverted) {
    super(
        name,
        (byte) 16,
        new byte[] {
            PORT_INDEX_Y0 + 2, PORT_INDEX_Y1 + 2, PORT_INDEX_Y2 + 2, PORT_INDEX_Y3 + 2,
            PORT_INDEX_Y4 + 2, PORT_INDEX_Y5 + 2, PORT_INDEX_Y6 + 2,
            PORT_INDEX_Y7 + 1
        },
        new String[] {
            "A", "B", "C", "nG2A", "nG2B", "G1", "Y7",
            "Y6", "Y5", "Y4", "Y3", "Y2", "Y1", "Y0"
        });
    this.inverted = inverted;
  }

  @Override
  public void paintInternal(InstancePainter painter, int x, int y, int height, boolean up) {
    super.paintBase(painter, true, false);
    Drawgates.paintPortNames(painter, x, y, height, super.portnames);
  }

  @Override
  public void ttlpropagate(InstanceState state) {
      Value g1 = state.getPortValue(PORT_INDEX_G1),
          nG2A = state.getPortValue(PORT_INDEX_nG2A),
          nG2B = state.getPortValue(PORT_INDEX_nG2B),
          override = null;

      byte n = 0;

      if (!g1.isFullyDefined() || !nG2A.isFullyDefined() || !nG2B.isFullyDefined()) {
        override = Value.ERROR;
      } else if (g1 == Value.FALSE || nG2A == Value.TRUE || nG2B == Value.TRUE) {
        override = this.inverted ? Value.TRUE : Value.FALSE;
      } else {
          for (int i = 0; i < 3; i++) {
              Value v = state.getPortValue(PORT_INDICES_ABC[i]);

              if (!v.isFullyDefined()) {
                  override = Value.ERROR;
                  break;
              } else {
                  n |= v.toLongValue() << i;
              }
          }
      }

      for (int i = 0; i < 8; i++) {
        Value v = i == n ? Value.TRUE : Value.FALSE;
        state.setPort(
            PORT_INDICES_Y[i],
            override != null ? override : (this.inverted ? v.not() : v),
            1
        );
      }
  }

  @Override
  public String getHDLName(AttributeSet attrs) {
    return CorrectLabel.getCorrectLabel("TTL" + this.getName()).toUpperCase();
  }

  @Override
  public boolean HDLSupportedComponent(AttributeSet attrs) {
      return false;
  }
}
