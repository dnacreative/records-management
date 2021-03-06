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
package org.alfresco.po.share.search;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Search result object, representing a row in the search results.
 * 
 * @author Roy Wetherall
 * @since 3.0.a
 */
@Scope("prototype")
@Component
public class SearchResult
{
    /** name selector */
    private static final By NAME_SELECTOR = By.cssSelector(".nameAndTitleCell [class*='PropertyLink'] span.value");
    
    /** row web element */
    private WebElement row;

    /**
     * set the search result row
     */
    public void setSearchResultRow(WebElement row)
    {
        this.row = row;
    }
    
    /**
     * get the name of the file or folder on this row of the search results
     */
    public String getName()
    {
        WebElement webElement = row.findElement(NAME_SELECTOR);
        return webElement.getText();
    }
}
