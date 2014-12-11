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
package org.alfresco.po.rm.fileplan;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;
import static org.alfresco.webdrone.WebDroneUtil.checkMandotaryParam;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Records management edit disposition page.
 *
 * @author Polina Lushchinskaya
 * @version 1.1
 * @since 2.2
 */
public class RmEditDispositionSchedulePage extends RmCreateDispositionPage
{
    /** Constants for selectors */
    public static By EDIT_DISPOSITION_SECTION   = By.cssSelector("div[id$='rm-disposition-edit']");
    public static By ADD_STEP_BUTTON            = By.cssSelector("button[id$='createaction-button-button']");
    public static By DISPOSITION_FORM           = By.cssSelector("div[class$='disposition-form']");
    public static By AFTER_PERIOD_CHKBOX        = By.cssSelector("div[style*='block'] input[class$='period-enabled']");
    public static By GHOST_CHKBOX = By.cssSelector("div[style*='block'] input[class$='ghostOnDestroy']");
    public static By WHEN_EVENT_OCCURS_CHKBOX   = By.cssSelector("div[style*='block'] input[class$='events-enabled']");
    public static By DESCRIPTION_AREA           = By.cssSelector("div[style*='block'] textarea[id$='description']");
    public static By PERIOD_INPUT               = By.cssSelector("div[style*='block'] input[class$='period-amount']");
    public static By PERIOD_SELECT              = By.cssSelector("div[style*='block'] select[class$='period-unit']");
    public static By PERIOD_ACTION_SELECT       = By.cssSelector("div[style*='block'] select[class$='period-action']");
    public static By SAVE_BUTTON                = By.cssSelector("div[style*='block'] span[class*='saveaction']>*>button");
    public static By CANCEL_BUTTON              = By.cssSelector("div[style*='block'] span[class*='cancel']>*>button");
    public static By DONE_BUTTON = By.xpath("//button[contains(@id,'done')]");

    /**
     * Load properties
     */
    protected RmPageObjectUtils pageObjectUtils = new RmPageObjectUtils();
    {
        pageObjectUtils.loadProperties("rm_en.properties");
    }
    /**
     * After Period Of time values
     */
    public static enum AfterPeriodOf
    {
        DAY("day"),
        END_OF_FINANCIAL_MONTH("fmend"),
        END_OF_FINANCIAL_QUARTER("fqend"),
        END_OF_FINANCIAL_YEAR("fyend"),
        END_OF_MONTH("monthend"),
        END_OF_QUARTER("quarterend"),
        END_OF_YEAR("yearend"),
        IMMEDIATELY("immediately"),
        MONTH("month"),
        NONE("none"),
        QUARTER("quarter"),
        WEEK("week"),
        XML_DURATION("duration"),
        YEAR("year");

        private final String value;

        AfterPeriodOf(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    /**
     * After Period Of select values
     */
    public static enum AfterPeriodOfFrom
    {
        CREATED_DATE("cm:created"),
        DISPOSITION_ACTION("rma:dispositionAsOf");

        private final String value;

        AfterPeriodOfFrom(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    /**
     * Constructor
     *
     * @param drone {@link WebDrone}
     */
    public RmEditDispositionSchedulePage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see org.alfresco.po.rm.fileplan.RmCreateDispositionPage#render(org.alfresco.webdrone.RenderTime)
     */
    @Override
    public RmEditDispositionSchedulePage render(RenderTime timer)
    {
        WebDroneUtil.checkMandotaryParam("timer", timer);

        elementRender(timer,
                getVisibleRenderElement(EDIT_DISPOSITION_SECTION),
                getVisibleRenderElement(ADD_STEP_BUTTON));
        return this;
    }

    /**
     * @see org.alfresco.po.rm.fileplan.RmCreateDispositionPage#render()
     */
    @Override
    public RmEditDispositionSchedulePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * @see org.alfresco.po.rm.fileplan.RmCreateDispositionPage#render(long)
     */
    @Override
    public RmEditDispositionSchedulePage render(final long time)
    {
        WebDroneUtil.checkMandotaryParam("time", time);

        return render(new RenderTime(time));
    }

    /**
     * Select After Period of value
     *
     * @param period period value
     * @param description disposition description
     * @param intValue int value of period
     * @param fromOptionNumber from select value
     * @return {@link RmEditDispositionSchedulePage}
     */
    public RmEditDispositionSchedulePage selectAfterPeriodOf(AfterPeriodOf period, String description, String intValue, AfterPeriodOfFrom fromOptionNumber, boolean ghost)
    {
        WebDroneUtil.checkMandotaryParam("period", period);
        WebDroneUtil.checkMandotaryParam("description", description);
        // intValue can be null
        // fromOptionNumber can be null

        if (ghost)
        {
            checkGhostOnDestroyChkBox();
        }
        checkAfterPeriodChkBox();
        if (intValue != null)
        {
            inputPeriodValue(intValue);
        }
        if (fromOptionNumber != null)
        {
            selectFromOption(fromOptionNumber);
        }
        selectPeriod(period);
        inputDescription(description);
        saveStepButton();
        return new RmEditDispositionSchedulePage(drone).render();
    }

    /**
     * Select After Event Complete of value
     *
     * @param period period value
     * @param description disposition description
     * @param intValue int value of period
     * @param fromOptionNumber from select value
     * @return {@link RmEditDispositionSchedulePage}
     */
    public RmEditDispositionSchedulePage selectAfterEventCompleted(AfterPeriodOf period, String description, String intValue, AfterPeriodOfFrom fromOptionNumber)
    {
        WebDroneUtil.checkMandotaryParam("period", period);
        WebDroneUtil.checkMandotaryParam("description", description);
        WebDroneUtil.checkMandotaryParam("intValue", intValue);
        WebDroneUtil.checkMandotaryParam("fromOptionNumber", fromOptionNumber);

        checkAfterPeriodChkBox();
        if (intValue != null)
        {
            inputPeriodValue(intValue);
        }
        if (fromOptionNumber != null)
        {
            selectFromOption(fromOptionNumber);
        }
        selectPeriod(period);
        inputDescription(description);
        saveStepButton();
        return new RmEditDispositionSchedulePage(drone).render();
    }

    /**
     * Select After Period of value
     *
     * @param period After Period of value
     * @param description disposition description
     * @return {@link RmEditDispositionSchedulePage}
     */
    public RmEditDispositionSchedulePage selectAfterPeriodOf(AfterPeriodOf period, String description)
    {
        return selectAfterPeriodOf(period, description, false);
    }

    /**
     * Select After Period of value
     *
     * @param period After Period of value
     * @param description disposition description
     * @return {@link RmEditDispositionSchedulePage}
     */
    public RmEditDispositionSchedulePage selectAfterPeriodOf(AfterPeriodOf period, String description,
            boolean ghostOnDestroy)
    {
        WebDroneUtil.checkMandotaryParam("period", period);
        WebDroneUtil.checkMandotaryParam("description", description);

        return selectAfterPeriodOf(period, description, null, null, ghostOnDestroy);
    }

    /**
     * Select Disposition step from select menu
     *
     * @param step disposition step
     */
    public void selectDispositionStep(DispositionAction step)
    {
        WebDroneUtil.checkMandotaryParam("step", step);

        click(ADD_STEP_BUTTON);
        selectMenu(step);
        drone.findAndWait(DISPOSITION_FORM);
    }

    public String getUiText(DispositionAction dispositionAction)
    {
        return pageObjectUtils.getPropertyValue(dispositionAction.name);
    }

    public void selectMenu(DispositionAction dispositionAction)
    {
        checkMandotaryParam("dispositionAction", dispositionAction);
        String selectText =  getUiText(dispositionAction);
        click(By.xpath("//a[contains(text(),'" + selectText + "')]"));
    }
    /**
     * Action Click save button
     */
    private void saveStepButton()
    {
        List<WebElement> saveElements = drone.findAndWaitForElements(SAVE_BUTTON);
        for (WebElement button : saveElements)
        {
            if (button.isDisplayed())
            {
                button.click();
            }
        }
    }

    /**
     * Type action description to the description textarea
     *
     * @param description action description
     */
    private void inputDescription(String description)
    {
        List<WebElement> descriptionElements = drone.findAndWaitForElements(DESCRIPTION_AREA);
        for (WebElement desc : descriptionElements)
        {
            if (desc.isDisplayed())
            {
                desc.clear();
                desc.sendKeys(description);
            }
        }
    }

    /**
     * Select Period Value from ComboBox
     *
     * @param period After Period Of Value
     */
    private void selectPeriod(AfterPeriodOf period)
    {
        List<WebElement> periodOption = drone.findAndWaitForElements(PERIOD_SELECT);
        List<Select> periodSelect = new ArrayList<Select>();
        for (WebElement action : periodOption)
        {
            if (action.isDisplayed())
            {
                periodSelect.add(new Select(action));
            }
        }
        periodSelect.get(periodSelect.size()-1).selectByValue(period.getValue());
    }

    /**
     * Select After Period From Option
     *
     * @param fromOptionNumber After Period from value
     */
    private void selectFromOption(AfterPeriodOfFrom fromOptionNumber)
    {
        List<WebElement> fromOption = drone.findAndWaitForElements(PERIOD_ACTION_SELECT);
        List<Select> fromSelect = new ArrayList<Select>();
        for (WebElement action : fromOption)
        {
            if (action.isDisplayed())
            {
                fromSelect.add(new Select(action));
            }
        }
        fromSelect.get(fromSelect.size()-1).selectByValue(fromOptionNumber.getValue());
    }

    /**
     * Select After Period Check Box
     */
    private void checkAfterPeriodChkBox()
    {
        List<WebElement> fromOption = drone.findAndWaitForElements(AFTER_PERIOD_CHKBOX);
        for (WebElement afterPeriod : fromOption)
        {
            if (afterPeriod.isDisplayed())
            {
                if(!afterPeriod.isSelected())
                {
                    afterPeriod.click();
                }
            }
        }
    }

    /**
     * Check the ghost on destroy check box if it is displayed
     */
    private void checkGhostOnDestroyChkBox()
    {
        List<WebElement> fromOption = drone.findAndWaitForElements(GHOST_CHKBOX);
        for (WebElement ghost : fromOption)
        {
            if (ghost.isDisplayed())
            {
                if (!ghost.isSelected())
                {
                    ghost.click();
                }
            }
        }
    }

    /**
     * Type the period value to input
     *
     * @param value integer period value
     */
    private void inputPeriodValue(String value)
    {
        List<WebElement> periodElements = drone.findAndWaitForElements(DESCRIPTION_AREA);
        for (WebElement period : periodElements)
        {
            if (period.isDisplayed())
            {
                period.clear();
                period.sendKeys(value);
            }
        }
    }

    /**
     * Action Click Done Button
     *
     * @return {@link RmCreateDispositionPage}
     */
    public RmCreateDispositionPage clickDoneButton()
    {
        WebElement done = drone.findAndWait(DONE_BUTTON);
        done.click();
        return new RmCreateDispositionPage(drone).render();
    }
}