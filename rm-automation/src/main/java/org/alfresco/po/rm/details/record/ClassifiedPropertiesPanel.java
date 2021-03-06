/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

package org.alfresco.po.rm.details.record;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

@Component
public class ClassifiedPropertiesPanel extends PropertiesPanel
{
    @FindBy(css="div.document-metadata-header h2")
    private WebElement classifiedTitle;

    /**
     * XPath selector that depends on the language being English to find the "Classification" panel. There doesn't seem
     * to be a more reasonable way to identify these fields.
     */
    private static final By CLASSIFIED_PROPERTIES_SELECTOR = By.xpath("//span[contains(@id,'classificationReasons')]/../../div[not(contains(@class, 'hidden'))]//span[contains(@class,'viewmode-value')]");

    public String getClassifiedProperty(ClassifiedPropertiesPanelField propertyField)
    {
        return getClassifiedProperties().get(propertyField.getIndex()).getText();
    }

    public List<WebElement> getClassifiedProperties()
    {
        return webDriver.findElements(CLASSIFIED_PROPERTIES_SELECTOR);
    }
}
