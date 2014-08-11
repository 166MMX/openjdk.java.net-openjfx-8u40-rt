// Automatically generated by CodeGeneratorJava.pm. Do not edit.

package com.sun.webkit.dom;

import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLLabelElement;

public class HTMLLabelElementImpl extends HTMLElementImpl implements HTMLLabelElement {
    HTMLLabelElementImpl(long peer) {
        super(peer);
    }

    static HTMLLabelElement getImpl(long peer) {
        return (HTMLLabelElement)create(peer);
    }


//attributes
    public HTMLFormElement getForm() {
        return HTMLFormElementImpl.getImpl(getFormImpl(getPeer()));
    }
    native static long getFormImpl(long peer);

    public String getHtmlFor() {
        return getHtmlForImpl(getPeer());
    }
    native static String getHtmlForImpl(long peer);

    public void setHtmlFor(String value) {
        setHtmlForImpl(getPeer(), value);
    }
    native static void setHtmlForImpl(long peer, String value);

    public HTMLElement getControl() {
        return HTMLElementImpl.getImpl(getControlImpl(getPeer()));
    }
    native static long getControlImpl(long peer);

}

