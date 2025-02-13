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

import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.StdAttr;

class ClockState implements Cloneable {
  private Value lastClock;

  public ClockState() {
    lastClock = Value.FALSE;
  }

  @Override
  public ClockState clone() {
    try {
      return (ClockState) super.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }
  }

  private Value updateIndex(Value newClock, int which) {
    if (which == 0 && this.lastClock.getWidth() == 1) {
      Value oldClock = this.lastClock;
      this.lastClock = newClock;
      return oldClock;
    }

    Value[] values = lastClock.getAll();
    if (values.length <= which) {
      Value[] nvalue = (Value.createKnown(BitWidth.create(which + 1), 0)).getAll();
      System.arraycopy(values, 0, nvalue, 0, values.length);
      values = nvalue;
    }

    Value oldClock = values[which];

    if (newClock != oldClock) {
      values[which] = newClock;
      lastClock = Value.create(values);
    }

    return oldClock;
  }

  private Value getIndex(int which) {
    if (which == 0 && this.lastClock.getWidth() == 1) {
      return this.lastClock;
    }

    return which < this.lastClock.getWidth() ? this.lastClock.getAll()[which] : Value.FALSE;
  }

  public boolean checkClock(Value newClock) {
    return this.checkClock(newClock, StdAttr.EDGE_TRIGGER);
  }

  public boolean checkClock(Value newClock, Object trigger) {
    return this.checkClock(newClock, 0, trigger);
  }

  public boolean checkClock(Value newClock, int which, Object trigger) {
    return this.checkTrigger(newClock, this.updateIndex(this.getIndex(which), which), trigger);
  }

  public boolean updateClock(Value newClock) {
    return this.updateClock(newClock, StdAttr.EDGE_TRIGGER);
  }

  public boolean updateClock(Value newClock, Object trigger) {
    return this.updateClock(newClock, 0, trigger);
  }

  public boolean updateClock(Value newClock, int which) {
    return this.updateClock(newClock, which, StdAttr.EDGE_TRIGGER);
  }

  public boolean updateClock(Value newClock, int which, Object trigger) {
    return this.checkTrigger(newClock, this.updateIndex(newClock, which), trigger);
  }

  private boolean checkTrigger(Value newClock, Value oldClock, Object trigger) {
    if (trigger == StdAttr.EDGE_TRIGGER) {
      return checkTrigger(newClock, oldClock, StdAttr.TRIG_RISING) ||
          checkTrigger(newClock, oldClock, StdAttr.TRIG_FALLING);
    } else if (trigger == null || trigger == StdAttr.TRIG_RISING) {
      return oldClock == Value.FALSE && newClock == Value.TRUE;
    } else if (trigger == StdAttr.TRIG_FALLING) {
      return oldClock == Value.TRUE && newClock == Value.FALSE;
    } else if (trigger == StdAttr.TRIG_HIGH) {
      return newClock == Value.TRUE;
    } else if (trigger == StdAttr.TRIG_LOW) {
      return newClock == Value.FALSE;
    } else {
      return oldClock == Value.FALSE && newClock == Value.TRUE;
    }
  }
}
