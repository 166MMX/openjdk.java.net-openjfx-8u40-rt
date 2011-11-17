/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 */
package javafx.scene.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * Then we need to write some tests for LabelSkin, such that we test the layout
 * of all these cases. Be sure to test when each of these properties has illegal
 * values, such as negative for graphicTextGap and null for contentDisplay and
 * so forth.
 */
public class LabeledTest {
    private Labeled labeled;
    
    @Before public void setup() {
        labeled = new LabeledMock();
    }
    
    /********************************************************************************
     *                                                                              *
     *                           Tests for text property                            *
     *                                                                              *
     *  - default constructor has text initialized to empty string, graphic is null *
     *  - null passed to one-arg constructor results in empty string text           *
     *  - string passed to on-arg constructor is set as text                        *
     *  - null passed to two-arg constructor for text results in empty string text  *
     *  - string passed to two-arg constructor for text is set as text              *
     *  - any value passed as graphic to two-arg constructor is set as graphic      *
     *                                                                              *
     *******************************************************************************/

    @Test public void defaultConstructorShouldHaveNoGraphicAndEmptyString() {
        assertNull(labeled.getGraphic());
        assertEquals("", labeled.getText());
    }
    
    @Test public void oneArgConstructorShouldHaveNoGraphicAndSpecifiedString() {
        Labeled l2 = new LabeledMock(null);
        assertNull(l2.getGraphic());
        assertNull(l2.getText());
        
        l2 = new LabeledMock("");
        assertNull(l2.getGraphic());
        assertEquals("", l2.getText());
        
        l2 = new LabeledMock("Hello");
        assertNull(l2.getGraphic());
        assertEquals("Hello", l2.getText());
    }
    
    @Test public void twoArgConstructorShouldHaveSpecifiedGraphicAndSpecifiedString() {
        Labeled l2 = new LabeledMock(null, null);
        assertNull(l2.getGraphic());
        assertNull(l2.getText());

        Rectangle rect = new Rectangle();
        l2 = new LabeledMock("Hello", rect);
        assertSame(rect, l2.getGraphic());
        assertEquals("Hello", l2.getText());
    }
    
    /********************************************************************************
     *                                                                              *
     *                           Tests for text property                            *
     *                                                                              *
     *  - can be null                                                               *
     *  - set is honored                                                            *
     *  - can be bound                                                              *
     *  - if bound, impl_cssSettable returns false                                  *
     *  - if specified via CSS and not bound, impl_cssSettable returns true         *
     *                                                                              *
     *******************************************************************************/

    @Test public void textDefaultValueIsEmptyString() {
        assertEquals("", labeled.getText());
        assertEquals("", labeled.textProperty().get());
    }
    
    @Test public void textCanBeNull() {
        labeled.setText(null);
        assertNull(labeled.getText());
    }
    
    @Test public void settingTextValueShouldWork() {
        labeled.setText("Hello World");
        assertEquals("Hello World", labeled.getText());
    }
    
    @Test public void settingTextAndThenCreatingAModelAndReadingTheValueStillWorks() {
        labeled.setText("Hello World");
        assertEquals("Hello World", labeled.textProperty().get());
    }
    
    @Test public void textCanBeBound() {
        StringProperty other = new SimpleStringProperty("Apples");
        labeled.textProperty().bind(other);
        assertEquals("Apples", labeled.getText());
    }
    
    @Test public void impl_cssSettable_ReturnsFalseForTextAlways() {
        assertFalse(labeled.impl_cssSettable("-fx-text"));
        StringProperty other = new SimpleStringProperty("Apples");
        labeled.textProperty().bind(other);
        assertFalse(labeled.impl_cssSettable("-fx-text"));
    }
    
    @Test public void whenTextIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsFalse() {
        labeled.impl_cssSet("-fx-text", "Howdy");
        assertFalse(labeled.impl_cssSettable("-fx-text"));
    }
    
    @Test public void cannotSpecifyTextViaCSS() {
        labeled.impl_cssSet("-fx-text", "Hello");
        assertFalse("Hello".equals(labeled.getText()));
    }

    /********************************************************************************
     *                                                                              *
     *                         Tests for textFill property                          *
     *                                                                              *
     *  - can be null                                                               *
     *  - set is honored                                                            *
     *  - can be bound                                                              *
     *  - is BLACK by default                                                       *
     *  - if bound, impl_cssSettable returns false                                  *
     *  - if specified via CSS and not bound, impl_cssSettable returns true         *
     *                                                                              *
     *******************************************************************************/

    @Test public void textFillDefaultValueIsBLACK() {
        assertSame(Color.BLACK, labeled.getTextFill());
        assertSame(Color.BLACK, labeled.textFillProperty().get());
    }

    @Test public void textFillCanBeNull() {
        labeled.setTextFill(null);
        assertNull(labeled.getTextFill());
    }

    @Test public void settingTextFillValueShouldWork() {
        labeled.setTextFill(Color.RED);
        assertSame(Color.RED, labeled.getTextFill());
    }

    @Test public void settingTextFillAndThenCreatingAModelAndReadingTheValueStillWorks() {
        labeled.setTextFill(Color.RED);
        assertSame(Color.RED, labeled.textFillProperty().get());
    }

    @Test public void textFillCanBeBound() {
        ObjectProperty<Paint> other = new SimpleObjectProperty<Paint>(Color.RED);
        labeled.textFillProperty().bind(other);
        assertSame(Color.RED, labeled.getTextFill());
    }

    @Test public void whenTextFillIsBound_impl_cssSettable_ReturnsFalse() {
        assertTrue(labeled.impl_cssSettable("-fx-text-fill"));
        ObjectProperty<Paint> other = new SimpleObjectProperty<Paint>(Color.RED);
        labeled.textFillProperty().bind(other);
        assertFalse(labeled.impl_cssSettable("-fx-text-fill"));
    }

    @Test public void whenTextFillIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        labeled.impl_cssSet("-fx-text-fill", Color.BLUE);
        assertTrue(labeled.impl_cssSettable("-fx-text-fill"));
    }

    @Test public void canSpecifyTextFillViaCSS() {
        labeled.impl_cssSet("-fx-text-fill", Color.YELLOW);
        assertSame(Color.YELLOW, labeled.getTextFill());
    }

    @Test public void textFillBeanIsCorrect() {
        assertSame(labeled, labeled.textFillProperty().getBean());
    }

    @Test public void textFillNameIsCorrect() {
        assertEquals("textFill", labeled.textFillProperty().getName());
    }

    /********************************************************************************
     *                                                                              *
     *                         Tests for alignment property                         *
     *                                                                              *
     *  - can be null                                                               *
     *  - set is honored                                                            *
     *  - can be bound                                                              *
     *  - default is "CENTER_LEFT"                                                  *
     *  - if bound, impl_cssSettable returns false                                  *
     *  - if specified via CSS and not bound, impl_cssSettable returns true         *
     *                                                                              *
     *******************************************************************************/

    @Ignore("This is not the default whilst we await a fix for RT-12212")
    @Test public void alignmentDefaultValueIsCENTER_LEFT() {
        assertEquals(Pos.CENTER_LEFT, labeled.getAlignment());
    }
    
    @Test public void alignmentCanBeNull() {
        labeled.setAlignment(null);
        assertNull(labeled.getAlignment());
    }
    
    @Test public void settingAlignmentValueShouldWork() {
        labeled.setAlignment(Pos.CENTER);
        assertEquals(Pos.CENTER, labeled.getAlignment());
    }
    
    @Test public void settingAlignmentAndThenCreatingAModelAndReadingTheValueStillWorks() {
        labeled.setAlignment(Pos.CENTER);
        assertEquals(Pos.CENTER, labeled.alignmentProperty().get());
    }
    
    @Test public void alignmentCanBeBound() {
        ObjectProperty<Pos> other = new SimpleObjectProperty<Pos>(Pos.BASELINE_RIGHT);
        labeled.alignmentProperty().bind(other);
        assertEquals(Pos.BASELINE_RIGHT, labeled.getAlignment());
    }
    
    @Test public void whenAlignmentIsBound_impl_cssSettable_ReturnsFalse() {
        ObjectProperty<Pos> other = new SimpleObjectProperty<Pos>(Pos.BASELINE_RIGHT);
        labeled.alignmentProperty().bind(other);
        assertFalse(labeled.impl_cssSettable("-fx-alignment"));
    }
    
    @Test public void whenAlignmentIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        labeled.impl_cssSet("-fx-alignment", Pos.CENTER);
        assertTrue(labeled.impl_cssSettable("-fx-alignment"));
    }

    @Test public void canSpecifyAlignmentViaCSS() {
        labeled.impl_cssSet("-fx-alignment", Pos.CENTER_RIGHT);
        assertEquals(Pos.CENTER_RIGHT, labeled.getAlignment());
    }

    /********************************************************************************
     *                                                                              *
     *                           Tests for textAlignment                            *
     *                                                                              *
     *  - can be null                                                               *
     *  - set is honored                                                            *
     *  - can be bound                                                              *
     *  - default is "LEFT"                                                         *
     *  - if bound, impl_cssSettable returns false                                  *
     *  - if specified via CSS and not bound, impl_cssSettable returns true         *
     *                                                                              *
     *******************************************************************************/

    @Test public void textAlignmentDefaultValueIsLEFT() {
        assertEquals(TextAlignment.LEFT, labeled.getTextAlignment());
        assertEquals(TextAlignment.LEFT, labeled.textAlignmentProperty().get());
    }
    
    @Test public void textAlignmentCanBeNull() {
        labeled.setTextAlignment(null);
        assertNull(labeled.getTextAlignment());
    }
    
    @Test public void settingTextAlignmentValueShouldWork() {
        labeled.setTextAlignment(TextAlignment.CENTER);
        assertEquals(TextAlignment.CENTER, labeled.getTextAlignment());
    }
    
    @Test public void settingTextAlignmentAndThenCreatingAModelAndReadingTheValueStillWorks() {
        labeled.setTextAlignment(TextAlignment.CENTER);
        assertEquals(TextAlignment.CENTER, labeled.textAlignmentProperty().get());
    }
    
    @Test public void textAlignmentCanBeBound() {
        ObjectProperty<TextAlignment> other = new SimpleObjectProperty<TextAlignment>(TextAlignment.RIGHT);
        labeled.textAlignmentProperty().bind(other);
        assertEquals(TextAlignment.RIGHT, labeled.getTextAlignment());
    }
    
    @Test public void whenTextAlignmentIsBound_impl_cssSettable_ReturnsFalse() {
        assertTrue(labeled.impl_cssSettable("-fx-text-alignment"));
        ObjectProperty<TextAlignment> other = new SimpleObjectProperty<TextAlignment>(TextAlignment.RIGHT);
        labeled.textAlignmentProperty().bind(other);
        assertFalse(labeled.impl_cssSettable("-fx-text-alignment"));
    }
    
    @Test public void whenTextAlignmentIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        labeled.impl_cssSet("-fx-text-alignment", TextAlignment.CENTER);
        assertTrue(labeled.impl_cssSettable("-fx-text-alignment"));
    }

    @Test public void canSpecifyTextAlignmentViaCSS() {
        labeled.impl_cssSet("-fx-text-alignment", TextAlignment.JUSTIFY);
        assertEquals(TextAlignment.JUSTIFY, labeled.getTextAlignment());
    }

    /********************************************************************************
     *                                                                              *
     *                            Tests for textOverrun                             *
     *                                                                              *
     *  - can be null                                                               *
     *  - set is honored                                                            *
     *  - can be bound                                                              *
     *  - default is "ELLIPSIS"                                                     *
     *  - if bound, impl_cssSettable returns false                                  *
     *  - if specified via CSS and not bound, impl_cssSettable returns true         *
     *                                                                              *
     *******************************************************************************/

    @Test public void textOverrunDefaultValueIsELLIPSIS() {
        assertEquals(OverrunStyle.ELLIPSIS, labeled.getTextOverrun());
        assertEquals(OverrunStyle.ELLIPSIS, labeled.textOverrunProperty().get());
    }
    
    @Test public void textOverrunCanBeNull() {
        labeled.setTextOverrun(null);
        assertNull(labeled.getTextOverrun());
    }
    
    @Test public void settingTextOverrunValueShouldWork() {
        labeled.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
        assertEquals(OverrunStyle.CENTER_ELLIPSIS, labeled.getTextOverrun());
    }
    
    @Test public void settingTextOverrunAndThenCreatingAModelAndReadingTheValueStillWorks() {
        labeled.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
        assertEquals(OverrunStyle.CENTER_ELLIPSIS, labeled.textOverrunProperty().get());
    }
    
    @Test public void textOverrunCanBeBound() {
        ObjectProperty<OverrunStyle> other = new SimpleObjectProperty<OverrunStyle>(OverrunStyle.LEADING_WORD_ELLIPSIS);
        labeled.textOverrunProperty().bind(other);
        assertEquals(OverrunStyle.LEADING_WORD_ELLIPSIS, labeled.getTextOverrun());
    }
    
    @Test public void whenTextOverrunIsBound_impl_cssSettable_ReturnsFalse() {
        assertTrue(labeled.impl_cssSettable("-fx-text-overrun"));
        ObjectProperty<OverrunStyle> other = new SimpleObjectProperty<OverrunStyle>(OverrunStyle.LEADING_WORD_ELLIPSIS);
        labeled.textOverrunProperty().bind(other);
        assertFalse(labeled.impl_cssSettable("-fx-text-overrun"));
    }
    
    @Test public void whenTextOverrunIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        labeled.impl_cssSet("-fx-text-overrun", OverrunStyle.LEADING_WORD_ELLIPSIS);
        assertTrue(labeled.impl_cssSettable("-fx-text-overrun"));
    }

    @Test public void canSpecifyTextOverrunViaCSS() {
        labeled.impl_cssSet("-fx-text-overrun", OverrunStyle.CENTER_WORD_ELLIPSIS);
        assertEquals(OverrunStyle.CENTER_WORD_ELLIPSIS, labeled.getTextOverrun());
    }

    /********************************************************************************
     *                                                                              *
     *                         Tests for wrapText property                          *
     *                                                                              *
     *  - set is honored                                                            *
     *  - can be bound                                                              *
     *  - default is false                                                          *
     *  - contentBias changes based on wrapText                                     *
     *  - if bound, impl_cssSettable returns false                                  *
     *  - if specified via CSS and not bound, impl_cssSettable returns true         *
     *                                                                              *
     *******************************************************************************/

    @Test public void wrapTextDefaultValueIsFalse() {
        assertFalse(labeled.isWrapText());
        assertFalse(labeled.wrapTextProperty().get());
    }
    
    @Test public void settingWrapTextValueShouldWork() {
        labeled.setWrapText(true);
        assertTrue(labeled.isWrapText());
    }
    
    @Test public void settingWrapTextAndThenCreatingAModelAndReadingTheValueStillWorks() {
        labeled.setWrapText(true);
        assertTrue(labeled.wrapTextProperty().get());
    }
    
    @Test public void wrapTextCanBeBound() {
        BooleanProperty other = new SimpleBooleanProperty(true);
        labeled.wrapTextProperty().bind(other);
        assertTrue(labeled.isWrapText());
    }
    
    @Test public void whenWrapTextIsBound_impl_cssSettable_ReturnsFalse() {
        assertTrue(labeled.impl_cssSettable("-fx-wrap-text"));
        BooleanProperty other = new SimpleBooleanProperty(true);
        labeled.wrapTextProperty().bind(other);
        assertFalse(labeled.impl_cssSettable("-fx-wrap-text"));
    }
    
    @Test public void whenWrapTextIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        labeled.impl_cssSet("-fx-wrap-text", true);
        assertTrue(labeled.impl_cssSettable("-fx-wrap-text"));
    }
    
    @Test public void whenWrapTextIsTrueContentBiasIsHorizontal() {
        labeled.setWrapText(true);
        assertEquals(Orientation.HORIZONTAL, labeled.getContentBias());
    }
    
    @Test public void whenWrapTextIsFalseContentBiasIsNull() {
        assertNull(labeled.getContentBias());
    }

    @Test public void canSpecifyWrapTextViaCSS() {
        labeled.impl_cssSet("-fx-wrap-text", true);
        assertTrue(labeled.isWrapText());
    }

    /********************************************************************************
     *                                                                              *
     *                           Tests for font property                            *
     *                                                                              *
     *  - can be null                                                               *
     *  - set is honored                                                            *
     *  - can be bound                                                              *
     *  - default is Font.getDefault()                                              *
     *  - if bound, impl_cssSettable returns false                                  *
     *  - if specified via CSS and not bound, impl_cssSettable returns true         *
     *                                                                              *
     *******************************************************************************/

    @Test public void fontDefaultValueIsFont_getDefault() {
        final Font def = Font.getDefault();
        assertEquals(def, labeled.getFont());
        assertEquals(def, labeled.fontProperty().get());
    }
    
    @Test public void fontCanBeNull() {
        labeled.setFont(null);
        assertNull(labeled.getFont());
    }
    
    @Test public void settingFontValueShouldWork() {
        final Font f = Font.font("Arial", 25);
        labeled.setFont(f);
        assertEquals(f, labeled.getFont());
    }
    
    @Test public void settingFontAndThenCreatingAModelAndReadingTheValueStillWorks() {
        final Font f = Font.font("Arial", 25);
        labeled.setFont(f);
        assertEquals(f, labeled.fontProperty().get());
    }
    
    @Test public void fontCanBeBound() {
        final Font f = Font.font("Arial", 25);
        ObjectProperty<Font> other = new SimpleObjectProperty<Font>(f);
        labeled.fontProperty().bind(other);
        assertEquals(f, labeled.getFont());
    }
    
    @Test public void whenFontIsBound_impl_cssSettable_ReturnsFalse() {
        final Font f = Font.font("Arial", 25);
        assertTrue(labeled.impl_cssSettable("-fx-font"));
        ObjectProperty<Font> other = new SimpleObjectProperty<Font>(f);
        labeled.fontProperty().bind(other);
        assertFalse(labeled.impl_cssSettable("-fx-font"));
    }
    
    @Test public void whenFontIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        final Font f = Font.font("Arial", 25);
        labeled.impl_cssSet("-fx-font", f);
        assertTrue(labeled.impl_cssSettable("-fx-font"));
    }

    @Test public void canSpecifyFontViaCSS() {
        final Font f = Font.font("Arial", 25);
        labeled.impl_cssSet("-fx-font", f);
        assertEquals(f, labeled.getFont());
    }

    /********************************************************************************
     *                                                                              *
     *                         Tests for graphic property                           *
     *                                                                              *
     *  - can be null                                                               *
     *  - set is honored                                                            *
     *  - can be bound                                                              *
     *  - default is null                                                           *
     *  - if bound, impl_cssSettable returns false                                  *
     *  - if specified via CSS and not bound, impl_cssSettable returns true         *
     *                                                                              *
     *******************************************************************************/

    @Test public void graphicDefaultValueIsNull() {
        assertEquals(null, labeled.getGraphic());
        assertEquals(null, labeled.graphicProperty().get());
    }
    
    @Test public void graphicCanBeNull() {
        labeled.setGraphic(new Rectangle());
        labeled.setGraphic(null);
        assertNull(labeled.getGraphic());
    }
    
    @Test public void settingGraphicValueShouldWork() {
        Rectangle r = new Rectangle();
        labeled.setGraphic(r);
        assertEquals(r, labeled.getGraphic());
    }
    
    @Test public void settingGraphicAndThenCreatingAModelAndReadingTheValueStillWorks() {
        Rectangle r = new Rectangle();
        labeled.setGraphic(r);
        assertEquals(r, labeled.graphicProperty().get());
    }
    
    @Test public void graphicCanBeBound() {
        Rectangle r = new Rectangle();
        ObjectProperty<Node> other = new SimpleObjectProperty<Node>(r);
        labeled.graphicProperty().bind(other);
        assertEquals(r, labeled.getGraphic());
    }
    
    @Test public void whenGraphicIsBound_impl_cssSettable_ReturnsFalse() {
        assertTrue(labeled.impl_cssSettable("-fx-graphic"));
        Rectangle r = new Rectangle();
        ObjectProperty<Node> other = new SimpleObjectProperty<Node>(r);
        labeled.graphicProperty().bind(other);
        assertFalse(labeled.impl_cssSettable("-fx-graphic"));
    }
    
    @Ignore ("CSS Graphic must be a URL, and then it will try to load the image. Not sure how to test.")
    @Test public void whenGraphicIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        labeled.impl_cssSet("-fx-graphic", "/some/url");
        assertTrue(labeled.impl_cssSettable("-fx-graphic"));
    }

    @Ignore ("CSS Graphic must be a URL, and then it will try to load the image. Not sure how to test.")
    @Test public void canSpecifyGraphicViaCSS() {
        labeled.impl_cssSet("-fx-graphic", "/some/url");
        assertNotNull(labeled.getGraphic());
    }

    /********************************************************************************
     *                                                                              *
     *                         Tests for underline property                         *
     *                                                                              *
     *  - set is honored                                                            *
     *  - can be bound                                                              *
     *  - default is false                                                          *
     *  - if bound, impl_cssSettable returns false                                  *
     *  - if specified via CSS and not bound, impl_cssSettable returns true         *
     *                                                                              *
     *******************************************************************************/

    @Test public void underlineDefaultValueIsFalse() {
        assertFalse(labeled.isUnderline());
        assertFalse(labeled.underlineProperty().get());
    }
    
    @Test public void settingUnderlineValueShouldWork() {
        labeled.setUnderline(true);
        assertTrue(labeled.isUnderline());
    }
    
    @Test public void settingUnderlineAndThenCreatingAModelAndReadingTheValueStillWorks() {
        labeled.setUnderline(true);
        assertTrue(labeled.underlineProperty().get());
    }
    
    @Test public void underlineCanBeBound() {
        BooleanProperty other = new SimpleBooleanProperty(true);
        labeled.underlineProperty().bind(other);
        assertTrue(labeled.isUnderline());
    }
    
    @Test public void whenUnderlineIsBound_impl_cssSettable_ReturnsFalse() {
        assertTrue(labeled.impl_cssSettable("-fx-underline"));
        BooleanProperty other = new SimpleBooleanProperty(true);
        labeled.underlineProperty().bind(other);
        assertFalse(labeled.impl_cssSettable("-fx-underline"));
    }
    
    @Test public void whenUnderlineIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        labeled.impl_cssSet("-fx-underline", true);
        assertTrue(labeled.impl_cssSettable("-fx-underline"));
    }

    @Test public void canSpecifyUnderlineViaCSS() {
        labeled.impl_cssSet("-fx-underline", true);
        assertTrue(labeled.isUnderline());
    }

    /********************************************************************************
     *                                                                              *
     *                           Tests for contentDisplay                           *
     *                                                                              *
     *  - can be null                                                               *
     *  - set is honored                                                            *
     *  - can be bound                                                              *
     *  - default is "LEFT"                                                         *
     *  - if bound, impl_cssSettable returns false                                  *
     *  - if specified via CSS and not bound, impl_cssSettable returns true         *
     *                                                                              *
     *******************************************************************************/

    @Test public void contentDisplayDefaultValueIsLEFT() {
        assertEquals(ContentDisplay.LEFT, labeled.getContentDisplay());
        assertEquals(ContentDisplay.LEFT, labeled.contentDisplayProperty().get());
    }
    
    @Test public void contentDisplayCanBeNull() {
        labeled.setContentDisplay(null);
        assertNull(labeled.getContentDisplay());
    }
    
    @Test public void settingContentDisplayValueShouldWork() {
        labeled.setContentDisplay(ContentDisplay.CENTER);
        assertEquals(ContentDisplay.CENTER, labeled.getContentDisplay());
    }
    
    @Test public void settingContentDisplayAndThenCreatingAModelAndReadingTheValueStillWorks() {
        labeled.setContentDisplay(ContentDisplay.CENTER);
        assertEquals(ContentDisplay.CENTER, labeled.contentDisplayProperty().get());
    }
    
    @Test public void contentDisplayCanBeBound() {
        ObjectProperty<ContentDisplay> other = new SimpleObjectProperty<ContentDisplay>(ContentDisplay.RIGHT);
        labeled.contentDisplayProperty().bind(other);
        assertEquals(ContentDisplay.RIGHT, labeled.getContentDisplay());
    }
    
    @Test public void whenContentDisplayIsBound_impl_cssSettable_ReturnsFalse() {
        assertTrue(labeled.impl_cssSettable("-fx-content-display"));
        ObjectProperty<ContentDisplay> other = new SimpleObjectProperty<ContentDisplay>(ContentDisplay.RIGHT);
        labeled.contentDisplayProperty().bind(other);
        assertFalse(labeled.impl_cssSettable("-fx-content-display"));
    }
    
    @Test public void whenContentDisplayIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        labeled.impl_cssSet("-fx-content-display", ContentDisplay.CENTER);
        assertTrue(labeled.impl_cssSettable("-fx-content-display"));
    }

    @Test public void canSpecifyContentDisplayViaCSS() {
        labeled.impl_cssSet("-fx-content-display", ContentDisplay.GRAPHIC_ONLY);
        assertEquals(ContentDisplay.GRAPHIC_ONLY, labeled.getContentDisplay());
    }

    /********************************************************************************
     *                                                                              *
     *                          Tests for graphicTextGap                            *
     *                                                                              *
     *  - set is honored                                                            *
     *  - can be bound                                                              *
     *  - default is 4                                                              *
     *  - if bound, impl_cssSettable returns false                                  *
     *  - if specified via CSS and not bound, impl_cssSettable returns true         *
     *                                                                              *
     *******************************************************************************/

    @Test public void graphicTextGapDefaultValueIsFour() {
        assertEquals(4, labeled.getGraphicTextGap(), 0);
        assertEquals(4, labeled.graphicTextGapProperty().get(), 0);
    }
    
    @Test public void settingGraphicTextGapValueShouldWork() {
        labeled.setGraphicTextGap(8);
        assertEquals(8, labeled.getGraphicTextGap(), 0);
    }
    
    @Test public void settingGraphicTextGapNegativeShouldWork() {
        labeled.setGraphicTextGap(-5.5);
        assertEquals(-5.5, labeled.getGraphicTextGap(), 0);
    }
    
    @Test public void settingGraphicTextGapAndThenCreatingAModelAndReadingTheValueStillWorks() {
        labeled.setGraphicTextGap(8);
        assertEquals(8, labeled.graphicTextGapProperty().get(), 0);
    }
    
    @Test public void graphicTextGapCanBeBound() {
        DoubleProperty other = new SimpleDoubleProperty(25);
        labeled.graphicTextGapProperty().bind(other);
        assertEquals(25, labeled.getGraphicTextGap(), 0);
    }
    
    @Test public void whenGraphicTextGapIsBound_impl_cssSettable_ReturnsFalse() {
        assertTrue(labeled.impl_cssSettable("-fx-graphic-text-gap"));
        DoubleProperty other = new SimpleDoubleProperty(25);
        labeled.graphicTextGapProperty().bind(other);
        assertFalse(labeled.impl_cssSettable("-fx-graphic-text-gap"));
    }
    
    @Test public void whenGraphicTextGapIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        labeled.impl_cssSet("-fx-graphic-text-gap", 8.0);
        assertTrue(labeled.impl_cssSettable("-fx-graphic-text-gap"));
    }
    
    @Test public void canSpecifyGraphicTextGapViaCSS() {
        labeled.impl_cssSet("-fx-graphic-text-gap", 8.0);
        assertEquals(8, labeled.getGraphicTextGap(), 0);
    }

    /********************************************************************************
     *                                                                              *
     *                          Tests for labelPadding                              *
     *                                                                              *
     *******************************************************************************/

    @Test public void labelPaddingDefaultValueIsEmptyInsets() {
        assertEquals(Insets.EMPTY, labeled.getLabelPadding());
        assertEquals(Insets.EMPTY, labeled.labelPaddingProperty().get());
    }

    @Test public void canSpecifyLabelPaddingFromCSS() {
        Insets insets = new Insets(5, 4, 3, 2);
        labeled.impl_cssSet("-fx-label-padding", insets);
        assertEquals(insets, labeled.getLabelPadding());
        assertEquals(insets, labeled.labelPaddingProperty().get());
    }

    /********************************************************************************
     *                                                                              *
     *                             Helper classes and such                          *
     *                                                                              *
     *******************************************************************************/

    public static final class LabeledMock extends Labeled {
        public LabeledMock() { super(); }
        public LabeledMock(String text) { super(text); }
        public LabeledMock(String text, Node graphic) { super(text, graphic); }
    }
}
