package org.finra.jtaf.core.utilities;

import java.util.Set;
import java.util.TreeSet;

/**
 * This is an extension of the PropertyManager for use with parameterized JUnit, or in any case where a test needs to iterate
 * over a number of similarly-formatted properties.
 * 
 * @author KnappK
 */
public class MassPropertyManager extends PropertyManager
{
    public MassPropertyManager(String fileName, String propertyPrefix)
    {
        super(fileName, validatePrefix(propertyPrefix));
    }

    /**
     * 
     * @param fileName
     */
    public MassPropertyManager(String fileName)
    {
        super(fileName, validatePrefix(null));
    }

    /**
     * Infixes are a thing! My linguistics degree has completely paid off.
     * 
     * For example, if you have a properties file full of usernames/passwords, you can call this on the suffix "username" to
     * obtain a set of all the keys which have username suffixes.
     * 
     * @param propertySuffix
     *            A property suffix to search for.
     * @return A set of all the infixes for which a property with the given suffix exists.
     */
    public Set<String> getInfixesWithSuffix(String propertySuffix)
    {
        Set<String> toReturn = new TreeSet<String>();
        for (Object propObj : System.getProperties().keySet())
        {
            String propKey = propObj.toString();
            if (propKey.startsWith(propertyPrefix) && propKey.endsWith(propertySuffix))
            {
                // +1 for the ., +1 to reach the next string
                toReturn.add(propKey.substring(propertyPrefix.length() + 2, propKey.lastIndexOf("." + propertySuffix)));
            }
        }
        return toReturn;
    }

    /**
     * A convenience method for getProperty(propertyInfix + "." + propertySuffix)
     * 
     * @param propertyInfix
     * @param propertySuffix
     * @return
     */
    public String getProperty(String propertyInfix, String propertySuffix)
    {
        return System.getProperty(propertyPrefix + "." + propertyInfix + "." + propertySuffix);
    }

    /**
     * 
     * @param propertyInfix
     * @param propertySuffix
     * @return
     */
    public boolean hasProperty(String propertyInfix, String propertySuffix)
    {
        return System.getProperties().containsKey(propertyPrefix + "." + propertyInfix + "." + propertySuffix);
    }

    /**
     * 
     * @param propertySuffix
     * @param propertyValue
     * @return
     */
    public Set<String> getInfixesWithSuffixValue(String propertySuffix, String propertyValue)
    {
        Set<String> matchingInfixes = getInfixesWithSuffix(propertySuffix);
        for (String infix : matchingInfixes)
        {
            if (!getProperty(infix, propertySuffix).equals(propertyValue))
            {
                matchingInfixes.remove(infix);
            }
        }
        return matchingInfixes;
    }
}