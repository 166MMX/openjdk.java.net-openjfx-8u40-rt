/*
 * Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved.
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
package javafx.util.converter;

import com.sun.javafx.beans.annotations.NoBuilder;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.util.StringConverter;

/**
 * <p>{@link StringConverter} implementation for {@link Number} values
 * that represent percentages.</p>
 * 
 * @see CurrencyStringConverter
 * @see NumberStringConverter
 * @see StringConverter
 */
@NoBuilder
public class PercentageStringConverter extends NumberStringConverter {
    
    
    // ------------------------------------------------------------ Constructors
    public PercentageStringConverter() {
        this(Locale.getDefault());
    }
    
    public PercentageStringConverter(Locale locale) {
        super(locale, null, null);
    }
    
    public PercentageStringConverter(NumberFormat numberFormat) {
        super(null, null, numberFormat);
    }

    // ----------------------------------------------------------------- Methods

    /** {@inheritDoc} */
    @Override public NumberFormat getNumberFormat() {
        Locale _locale = locale == null ? Locale.getDefault() : locale;
        
        if (numberFormat != null) {
            return numberFormat;
        } else {
            return NumberFormat.getPercentInstance(_locale);
        }
    }
}
