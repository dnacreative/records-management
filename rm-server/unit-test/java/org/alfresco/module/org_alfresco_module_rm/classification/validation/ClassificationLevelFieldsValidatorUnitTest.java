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
package org.alfresco.module.org_alfresco_module_rm.classification.validation;

import static java.util.Arrays.asList;
import static org.alfresco.module.org_alfresco_module_rm.classification.validation.FilenameFieldValidator.ILLEGAL_ABBREVIATION_CHARS;
import static org.alfresco.module.org_alfresco_module_rm.test.util.ExceptionUtils.expectedException;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.alfresco.module.org_alfresco_module_rm.classification.ClassificationException.IllegalAbbreviationChars;
import org.alfresco.module.org_alfresco_module_rm.classification.ClassificationException.IllegalConfiguration;
import org.alfresco.module.org_alfresco_module_rm.classification.ClassificationLevel;
import org.junit.Test;

/**
 * Unit tests for the {@link ClassificationLevelFieldsValidator}.
 *
 * @author Neil Mc Erlean
 * @author tpage
 */
public class ClassificationLevelFieldsValidatorUnitTest
{
    private final ClassificationLevelFieldsValidator validator = new ClassificationLevelFieldsValidator();

    /** Ensures that null, empty or whitespace-only IDs are rejected. */
    @Test public void nonEmptyAbbreviationsAreMandatory()
    {
        // A missing or empty level ID is illegal.
        for (String illegalID : asList(null, "", "   ", "\t"))
        {
            expectedException(IllegalArgumentException.class, () ->
            {
                validator.validate(new ClassificationLevel(illegalID, "value.does.not.matter"));
                return null;
            });
        }
    }

    @Test(expected=IllegalConfiguration.class)
    public void systemUnclassifiedAbbreviationIsReserved()
    {
        validator.validate(new ClassificationLevel("U", "value.does.not.matter"));
    }

    @Test(expected=IllegalConfiguration.class)
    public void longAbbreviationsAreIllegal()
    {
        validator.validate(new ClassificationLevel("12345678901", "value.does.not.matter"));
    }



    /**
     * This test ensures that validation will catch any and all illegal characters in a
     * {@link ClassificationLevel#getId() level ID} and report them all.
     */
    @Test public void someCharactersAreBannedInAbbreviations()
    {
        for (Character illegalChar : ILLEGAL_ABBREVIATION_CHARS)
        {
            IllegalAbbreviationChars e = expectedException(IllegalAbbreviationChars.class, () ->
            {
                validator.validate(new ClassificationLevel("Hello" + illegalChar, "value.does.not.matter"));
                return null;
            });
            assertTrue("Exception did not contain helpful example of illegal character",
                       e.getIllegalChars().contains(illegalChar));
        }

        // We also expect an abbreviation with multiple illegal chars in it to have them all reported in the exception.
        final List<Character> someIllegalChars = ILLEGAL_ABBREVIATION_CHARS.subList(0, 3);

        IllegalAbbreviationChars e = expectedException(IllegalAbbreviationChars.class, () ->
        {
            validator.validate(new ClassificationLevel(someIllegalChars.toString(),
                                                             "value.does.not.matter"));
            return null;
        });

        assertThat(e.getIllegalChars(), allOf(hasItem('"'), hasItem('*'), hasItem('\\')));
    }
}
