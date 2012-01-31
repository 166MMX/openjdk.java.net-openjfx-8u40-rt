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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javafx.util.StringConverter;

/**
 * <p>{@link StringConverter} implementation for {@link Date} values.</p>
 * 
 * @see TimeStringConverter
 * @see DateTimeStringConverter
 */
@NoBuilder
public class DateStringConverter extends DateTimeStringConverter {
    
    // ------------------------------------------------------------ Constructors
    public DateStringConverter() {
        this(Locale.getDefault());
    }
    
    public DateStringConverter(Locale locale) {
        this(locale, null);
    }
    
    public DateStringConverter(String pattern) {
        this(Locale.getDefault(), pattern, null);
    }
    
    public DateStringConverter(Locale locale, String pattern) {
        this(locale, pattern, null);
    }
    
    public DateStringConverter(DateFormat dateFormat) {
        this(null, null, dateFormat);
    }
    
    private DateStringConverter(Locale locale, String pattern, DateFormat dateFormat) {
        super(locale, pattern, dateFormat);
    }
    

    // --------------------------------------------------------- Private Methods

    /** {@inheritDoc} */
    @Override protected DateFormat getDateFormat() {
        Locale _locale = locale == null ? Locale.getDefault() : locale;

        DateFormat df = null;

        if (dateFormat != null) {
            return dateFormat;
        } else if (pattern != null) {
            df = new SimpleDateFormat(pattern, _locale);
        } else {
            df = DateFormat.getDateInstance();
        }

        df.setLenient(false);

        return df;
    }
}
