package org.opengis.cite.gpkg12.extensions.relatedtables;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opengis.cite.gpkg12.SuiteAttribute;
import org.testng.ISuite;
import org.testng.ITestContext;

public class VerifyRelatedTablesTests {

    private static ITestContext testContext;
    private static ISuite suite;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void initTestFixture() {
        testContext = mock(ITestContext.class);
        suite = mock(ISuite.class);
        when(testContext.getSuite()).thenReturn(suite);
    }

    /**
     * Verifies that the Related Tables Extension test is not applicable if the gpkg_extension table is not present.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void applicabilityIsNotApplicableNoExtensions() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/gdal_sample_v1.2_no_extensions.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Conformance class Related Tables Extension is not in use. expected [true] but found [false]");
        tests.activeExtension(testContext);
    }

    /**
     * Verifies that the Related Tables Extension test is not applicable if the required extension is not present.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void applicabilityIsNotApplicable() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/gdal_sample_v1.2_spatial_index_extension.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Conformance class Related Tables Extension is not in use. expected [true] but found [false]");
        tests.activeExtension(testContext);
    }

    /**
     * Verifies that the Related Tables Extension test is applicable if the extension is present.
     *
     * This checks the 'related_tables' option.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void applicabilityIsApplicable() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.activeExtension(testContext);
    }

    /**
     * Verifies that the Related Tables Extension test is applicable if the extension is present.
     *
     * This checks the 'gpkg_related_tables' option which is available after the extension gets approved.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void applicabilityIsApplicableAltname() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related_altname.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.activeExtension(testContext);
    }

    /**
     * Verifies that the test fails if the gpkgext_relations row is missing from gpkg_extensions.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void extensionMissingInGpkgExtensions() throws IOException, SQLException, URISyntaxException  {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_missing_gpkgext_relations.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Required row (gpkgext_relations) for the Related Tables Extension is missing from gpkg_extensions. expected [true] but found [false]");
        tests.relations_table_in_extensions();
    }

    /**
     * Verifies that the test passes if the gpkgext_relations row is present in gpkg_extensions with the right definition.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void extensionPresentInGpkgExtensions() throws IOException, SQLException, URISyntaxException  {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.relations_table_in_extensions();
    }

    /**
     * Verifies that the test passes if the gpkgext_relations row is present in gpkg_extensions with the right definition.
     *
     * This checks the 'gpkg_related_tables' option which is available after the extension gets approved.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void extensionPresentInGpkgExtensionsAltName() throws IOException, SQLException, URISyntaxException  {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related_altname.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.relations_table_in_extensions();
    }

    /**
     * Verifies that the test passes if the gpkg_extensions table contains at least one mapping table.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void mappingTablePresentInGpkgExtensions() throws IOException, SQLException, URISyntaxException  {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.mapping_tables_in_extensions();
    }

    /**
     * Verifies that the test passes if the gpkg_extensions table contains at least one mapping table.
     *
     * This checks the 'gpkg_related_tables' option which is available after the extension gets approved.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void mappingTablePresentInGpkgExtensionsAltName() throws IOException, SQLException, URISyntaxException  {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related_altname.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.mapping_tables_in_extensions();
    }

    /**
     * Verifies that the test fails if the gpkg_extensions table does not contain at least one mapping table.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void mappingTableMissingInGpkgExtensions() throws IOException, SQLException, URISyntaxException  {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_missing_mapping_entry.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Required row (at least one mapping table row) for the Related Tables Extension is missing from gpkg_extensions. expected [true] but found [false]");
        tests.mapping_tables_in_extensions();
    }

    /**
     * Verifies that the test fails if the gpkg_extensions table contains a mapping table entry but the table is not present.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void mappingTableMissing() throws IOException, SQLException, URISyntaxException  {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_missing_mapping_table.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The mapping1 table is missing. expected [true] but found [false]");
        tests.mapping_tables_in_extensions();
    }
}
