/*
 * Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.sun.javafx.event;

import javafx.event.Event;
import javafx.event.EventType;

public final class ValueEvent extends Event {
    public static final EventType<ValueEvent> ANY =
            new EventType<ValueEvent>(Event.ANY, "VALUE");

    public static final EventType<ValueEvent> VALUE_A =
            new EventType<ValueEvent>(ValueEvent.ANY, "VALUE_A");
    public static final EventType<ValueEvent> VALUE_B =
            new EventType<ValueEvent>(ValueEvent.ANY, "VALUE_B");
    public static final EventType<ValueEvent> VALUE_C =
            new EventType<ValueEvent>(ValueEvent.ANY, "VALUE_C");

    private int value;

    public ValueEvent() {
        this(VALUE_A, 0);
    }

    public ValueEvent(final int initialValue) {
        this(VALUE_A, initialValue);
    }

    public ValueEvent(final EventType<? extends ValueEvent> eventType,
                      final int initialValue) {
        super(eventType);
        value = initialValue;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
