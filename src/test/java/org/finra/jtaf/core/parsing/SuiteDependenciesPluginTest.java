package org.finra.jtaf.core.parsing;

import javax.xml.parsers.DocumentBuilderFactory;

import org.finra.jtaf.core.AutomationEngine;
import org.finra.jtaf.core.model.test.TestSuite;
import org.finra.jtaf.core.plugins.parsing.ParserPluginException;
import org.finra.jtaf.core.plugins.parsing.PostSuiteParserPluginContext;
import org.finra.jtaf.core.utilities.logging.MessageCollector;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;

public class SuiteDependenciesPluginTest extends ParserPluginTest
{
    private static final String TAG_NAME = "dependencies";
    private static final String SCRIPT_FILE_NAME = "SuiteDependencyParserTest.xml";
    private static final String UNEXPECTED_ELEMENT_SCRIPT_FILE_NAME = "UnexpectedElementSuiteDependency.xml";
    private static final String DEPENDENT_SUITE_NAME = "JTAF CORE";
    private static final String DEPENDENT_TEST_NAME = "Block Testing";

    @Test()
    public void testExecute() throws Exception
    {
        SuiteDependenciesPlugin suiteDependenciesPlugin = new SuiteDependenciesPlugin();
        Element element = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("parser_testing/" + SCRIPT_FILE_NAME).getDocumentElement();
        AutomationEngine.getInstance().getScriptParser();
        TestSuite testSuite = ScriptParser.processTestSuite(element, new MessageCollector(), SCRIPT_FILE_NAME);
        PostSuiteParserPluginContext postSuiteParserPluginContext = new PostSuiteParserPluginContext(null, testSuite, element);
        suiteDependenciesPlugin.execute(postSuiteParserPluginContext);

        Assert.assertEquals(1, testSuite.getDependencies().getDependenciesSuites().size());
        Assert.assertTrue(testSuite.getDependencies().getDependenciesSuites().contains(DEPENDENT_SUITE_NAME));

        Assert.assertEquals(1, testSuite.getDependencies().getDependenciesTests().size());
        Assert.assertTrue(testSuite.getDependencies().getDependenciesTests().contains(DEPENDENT_TEST_NAME));
    }

    @Test(expected = ParserPluginException.class)
    public void testExecuteUnexpectedElement() throws Exception
    {
        SuiteDependenciesPlugin suiteDependenciesPlugin = new SuiteDependenciesPlugin();
        Element element =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("parser_testing/" + UNEXPECTED_ELEMENT_SCRIPT_FILE_NAME).getDocumentElement();
        AutomationEngine.getInstance().getScriptParser();
        TestSuite testSuite =
                ScriptParser.processTestSuite(element, new MessageCollector(), UNEXPECTED_ELEMENT_SCRIPT_FILE_NAME);
        PostSuiteParserPluginContext postSuiteParserPluginContext = new PostSuiteParserPluginContext(null, testSuite, element);
        suiteDependenciesPlugin.execute(postSuiteParserPluginContext);
    }

    @Test
    public void testGetTagName()
    {
        Assert.assertEquals(TAG_NAME, new SuiteDependenciesPlugin().getTagName());
    }
}
