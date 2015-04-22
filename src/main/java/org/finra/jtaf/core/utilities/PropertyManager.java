/*
 * (C) Copyright 2014 Java Test Automation Framework Contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.finra.jtaf.core.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class is similar to java Properties reader. It takes care of reading properties from the specified file.
 * The difference however is, it allows to override any property by specifying it as a System
 * property.
 */
public class PropertyManager
{
    protected String fileName;
    protected String propertyPrefix;
    protected PropertyResourceBundle propertyResourceBundle;

    // Stores all prefixes we've found so far
    protected static Set<String> preexistingPrefixes = null;
    // For properties that are actually lists
    protected static final String DEFAULT_DELIMITER = ",";

    public PropertyManager(String fileName, String propertyPrefix)
    {
        this.fileName = fileName;
        this.propertyPrefix = validatePrefix(propertyPrefix);
        propertyResourceBundle = getPropertyResourceBundle();
        if (propertyResourceBundle != null)
        {
            loadProperties();
        }
    }

    public PropertyManager(String fileName)
    {
        this(fileName, generatePrefix());
    }

    /**
     * 
     * @param prefix
     * @return
     */
    protected static String validatePrefix(String prefix)
    {
        String toReturn = prefix;
        if (preexistingPrefixes == null)
        {
            preexistingPrefixes = new TreeSet<String>();
        }

        while (toReturn == null || doesPrefixExist(toReturn))
        {
            toReturn = generatePrefix();
        }
        preexistingPrefixes.add(toReturn);
        return toReturn;
    }

    /**
     * 
     * @return
     */
    protected static String generatePrefix()
    {
        int maxlength = 6;
        Random r = new Random();
        String toReturn = "" + (char) (r.nextInt(26) + 97);
        while (r.nextInt(maxlength) > toReturn.length() || preexistingPrefixes.contains(toReturn))
        {
            toReturn += (char) (r.nextInt(26) + 97);
        }
        return toReturn;
    }

    /**
     * 
     * @param propertyPrefix
     * @return
     */
    protected final static boolean doesPrefixExist(String propertyPrefix)
    {
        if (preexistingPrefixes == null)
        {
            preexistingPrefixes = new TreeSet<String>();
        }

        if (preexistingPrefixes.contains(propertyPrefix))
        {
            return true;
        }

        for (Object propKey : System.getProperties().keySet())
        {
            if (propKey.toString().startsWith(propertyPrefix))
            {
                preexistingPrefixes.add(propertyPrefix);
                return true;
            }
        }
        return false;
    }

    public String getProperty(String propertyName)
    {
        return System.getProperty(propertyPrefix + "." + propertyName);
    }

    protected PropertyResourceBundle getPropertyResourceBundle()
    {
        try
        {
            InputStream inputStream = PropertyManager.class.getClassLoader()
                    .getResourceAsStream(fileName);
            if (inputStream == null)
            {
                File propertiesFile = new File(fileName);
                inputStream = new FileInputStream(propertiesFile);
            }
            return new PropertyResourceBundle(inputStream);
        }
        catch (FileNotFoundException fileNotFoundException)
        {
            return null;
        }
        catch (Exception exception)
        {
            throw new RuntimeException("Problem loading properties", exception);
        }
    }

    /**
     * For properties with list-type values. Uses the default delimiter.
     * 
     * @param propertyName
     * @return
     */
    public List<String> getProperties(String propertyName)
    {
        if (hasProperty(propertyName + ".delimiter"))
        {
            return getProperties(propertyName, getProperty(propertyName + ".delimiter"));
        }
        else if (hasProperty("default.delimeter"))
        {
            return getProperties(propertyName, getProperty("default.delimiter"));
        }
        else
        {
            return getProperties(propertyName, DEFAULT_DELIMITER);
        }
    }

    /**
     * 
     * @param propertyName
     * @return
     */
    public boolean hasProperty(String propertyName)
    {
        return System.getProperties().containsKey(propertyPrefix + "." + propertyName);
    }

    protected void loadProperties()
    {
        Enumeration<String> keys = propertyResourceBundle.getKeys();
        while (keys.hasMoreElements())
        {
            String key = keys.nextElement();
            String newProperty = propertyResourceBundle.getString(key);
            String newKey = propertyPrefix + "." + key;
            if (System.getProperty(newKey) == null)
            {
                System.setProperty(newKey, newProperty);
            }
        }
    }

    /**
     * For properties with values that are delimited lists.
     * 
     * @param propertyName
     * @param delimiter
     * @return
     */
    public List<String> getProperties(String propertyName, String delimiter)
    {
        List<String> toReturn = new ArrayList<String>();
        String delimitedList = getProperty(propertyName);
        for (String s : delimitedList.split(delimiter))
        {
            toReturn.add(s);
        }
        return toReturn;
    }
}
