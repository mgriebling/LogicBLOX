/*
 * Copyright (c) 2017 Helmut Neemann
 * Use of this source code is governed by the GPL v3 license
 * that can be found in the LICENSE file.
 */
package de.neemann.digital.gui.components.modification;

import de.neemann.digital.core.element.Keys;
import de.neemann.digital.draw.elements.Circuit;
import de.neemann.digital.draw.elements.VisualElement;
import de.neemann.digital.draw.graphics.Vector;
import de.neemann.digital.lang.Lang;

/**
 * A modification on a visual element.
 * The element is identified by its position and name.
 */
public abstract class ModificationOfVisualElement implements Modification {

    private final Vector pos;
    private final String description;
    private final String name;

    /**
     * Creates a new instance
     *
     * @param ve          the element to modify
     * @param description description
     */
    public ModificationOfVisualElement(VisualElement ve, String description) {
        this(ve, ve.getPos(), description);
    }

    /**
     * Creates a new instance
     *
     * @param ve          the element to modify
     * @param initialPos  the initial position of the element
     * @param description description
     */
    public ModificationOfVisualElement(VisualElement ve, Vector initialPos, String description) {
        name = ve.getElementName();
        pos = initialPos;
        this.description = description;
    }

    /**
     * Creates a translated name of the given element
     *
     * @param ve the element
     * @return translated name
     */
    public static String getToolTipName(VisualElement ve) {
        String s = Lang.getNull("elem_" + ve.getElementName());
        if (s == null) {
            s = ve.getElementName();
            if (s.endsWith(".dig"))
                s = s.substring(0, s.length() - 4);
        }
        String l = ve.getElementAttributes().get(Keys.LABEL);
        if (l.length() > 0)
            s += " (" + l + ")";
        return s;
    }

    @Override
    public String toString() {
        return description;
    }

    /**
     * Returns the element which is to modify
     *
     * @param circuit the circuit
     * @return the element to modify
     */
    public VisualElement getVisualElement(Circuit circuit) {
        for (VisualElement ve : circuit.getElements()) {
            if (ve.getPos().equals(pos) && ve.getElementName().equals(name))
                return ve;
        }
        throw new RuntimeException("internal error: Element not found!");
    }
}
