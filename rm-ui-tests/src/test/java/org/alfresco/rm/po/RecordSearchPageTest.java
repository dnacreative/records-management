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
package org.alfresco.rm.po;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.alfresco.po.rm.RecordSearchPage;
import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.annotations.Listeners;

/**
 * Tests record search page.
 *
 * @author Michael Suzuki
 * @version 1.7.1
 * @since 2.2
 */
@Listeners(FailedTestListener.class)
public class RecordSearchPageTest extends AbstractTest
{
    @Test
    public void createPage()
    {
        RecordSearchPage page = new RecordSearchPage(drone);
        Assert.assertNotNull(page);
    }
}