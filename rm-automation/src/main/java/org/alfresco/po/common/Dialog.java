/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.common;

import org.alfresco.po.common.buttonset.StandardButtons;
import org.alfresco.po.common.renderable.Renderable;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Dialog implementation
 *
 * @author Roy Wetherall
 */
public abstract class Dialog extends Renderable
                             implements StandardButtons
{
    /**
     * Visible dialog selector string.
     * <p>
     * The first part of the selector matches traditional Share dialogs. The second part matches Aikau Dojo-based
     * dialogs. In both cases only the visible dialogs are returned.
     */
    protected static final String VISIBLE_DIALOG = "div[id$='dialog_c'][style*='visibility: visible'],"
                + "div:not([style*='display: none']).alfresco-dialog-AlfDialog";

    /** dialog element */
    @FindBy(css=VISIBLE_DIALOG)
    protected WebElement dialog;
}
