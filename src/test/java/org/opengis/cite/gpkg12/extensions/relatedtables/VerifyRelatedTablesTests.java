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
     * Verifies that the Related Tables Extension test is not applicable if the
     * gpkg_extension table is not present.
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
        thrown.expectMessage("Conformance class Related Tables Extension is not in use.");
        tests.activeExtension(testContext);
    }

    /**
     * Verifies that the Related Tables Extension test is not applicable if the
     * required extension is not present.
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
        thrown.expectMessage("Conformance class Related Tables Extension is not in use.");
        tests.activeExtension(testContext);
    }

    /**
     * Verifies that the Related Tables Extension test is applicable if the
     * extension is present.
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
     * Verifies that the Related Tables Extension test is applicable if the
     * extension is present.
     *
     * This checks the 'gpkg_related_tables' option which is available after the
     * extension gets approved.
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
     * Verifies that the test fails if the gpkgext_relations row is missing from
     * gpkg_extensions.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void extensionMissingInGpkgExtensions() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_missing_gpkgext_relations.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Required row (gpkgext_relations) for the Related Tables Extension is missing from gpkg_extensions.");
        tests.relations_table_in_extensions();
    }

    /**
     * Verifies that the test fails if the gpkgext_relations row has an
     * incorrect definition in gpkg_contents.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void extensionBadInGpkgExtensions() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related_bad_mapping_extension_row.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Required row (gpkgext_relations - missing row flag 7) for the Related Tables Extension is missing from gpkg_extensions.");
        tests.relations_table_in_extensions();
    }

    /**
     * Verifies that the test passes if the gpkgext_relations row is present in
     * gpkg_extensions with the right definition.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void extensionPresentInGpkgExtensions() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.relations_table_in_extensions();
    }

    /**
     * Verifies that the test passes if the gpkgext_relations row is present in
     * gpkg_extensions with the right definition.
     *
     * This checks the 'gpkg_related_tables' option which is available after the
     * extension gets approved.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void extensionPresentInGpkgExtensionsAltName() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related_altname.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.relations_table_in_extensions();
    }

    /**
     * Verifies that the test passes if the gpkg_extensions table contains at
     * least one mapping table.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void mappingTablePresentInGpkgExtensions() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.mapping_tables_in_extensions();
    }

    /**
     * Verifies that the test passes if the gpkg_extensions table contains at
     * least one mapping table.
     *
     * This checks the 'gpkg_related_tables' option which is available after the
     * extension gets approved.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void mappingTablePresentInGpkgExtensionsAltName() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related_altname.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.mapping_tables_in_extensions();
    }

    /**
     * Verifies that the test fails if the gpkg_extensions table does not
     * contain at least one mapping table.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void mappingTableMissingInGpkgExtensions() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_missing_mapping_entry.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Required row (at least one mapping table row) for the Related Tables Extension is missing from gpkg_extensions.");
        tests.mapping_tables_in_extensions();
    }

    /**
     * Verifies that the test fails if the gpkg_extensions table contains a
     * mapping table entry but the table is not present.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void mappingTableMissing() throws IOException, SQLException, URISyntaxException {
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

    /**
     * Verifies that the test passes if the gpkg_extensions table has a related
     * table row with the right definition.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void mappingTableDefinitionInGpkgExtensions() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.extension_table_rows();
    }

    /**
     * Verifies that the test passes if the gpkg_extensions table has a related
     * table row with the right definition.
     *
     * This checks the 'gpkg_related_tables' option which is available after the
     * extension gets approved.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void mappingTableDefinitionInGpkgExtensionsAltName() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related_altname.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.extension_table_rows();
    }

    /**
     * Verifies that the test fails if the gpkg_extensions table does not
     * contain at least one mapping table.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void mappingTableDefinitionMissingInGpkgExtensions() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_missing_mapping_entry.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Required row (at least one mapping table row) for the Related Tables Extension is missing from gpkg_extensions.");
        tests.extension_table_rows();
    }

    /**
     * Verifies that the test fails if the gpkg_extensions table contains a
     * mapping table row with the wrong definition
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void mappingTableDefinitionBadInGpkgExtensions() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related_bad_mapping_extension_row.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Required row (mapping1 - missing row flag 3) for the Related Tables Extension is missing from gpkg_extensions.");
        tests.extension_table_rows();
    }

    /**
     * Verifies that the test passes if the gpkgext_relations table is present
     * and has the right definition.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relationsTableValid() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.relations_table_definition();
    }

    /**
     * Verifies that the test fails if the gpkgext_relations table is missing
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relationsTableMissing() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_missing_gpkgext_relations.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The gpkgext_relations table is missing.");
        tests.relations_table_definition();
    }

    // TODO: unit test for relations table present with bad definition.
    /**
     * Verifies that the test passes if the base table is in the database.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void baseTableValid() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.base_tables();
    }

    /**
     * Verifies that the test fails if the base table is not in the database.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void baseTableMissing() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_missing_tables.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The allCountries table is missing. expected [true] but found [false]");
        tests.base_tables();
    }

    /**
     * Verifies that the test passes if the base table is in contents.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void baseTableValidContents() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.base_tables_contents();
    }

    /**
     * Verifies that the test fails if the base table is not in contents.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void baseTableInvalidContents() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_missing_tables_contents.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The base table allCountries from gpkgext_relations does not exist in the contents table. expected [true] but found [false]");
        tests.base_tables_contents();
    }

    /**
     * Verifies that the test passes if the related table is in the database.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableValid() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.related_tables();
    }

    /**
     * Verifies that the test fails if the related table is not in the database.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableMissing() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_missing_tables.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The info table is missing. expected [true] but found [false]");
        tests.related_tables();
    }

    /**
     * Verifies that the test passes if the related table is in contents.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableValidContents() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.related_tables_contents();
    }

    /**
     * Verifies that the test fails if the related table is not in contents.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableInvalidContents() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_missing_tables_contents.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The related table info from gpkgext_relations does not exist in the contents table. expected [true] but found [false]");
        tests.related_tables_contents();
    }

    /**
     * Verifies that the test passes if the mapping table is in the geopackage.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableValidMappingTableNames() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.mapping_tables();
    }

    /**
     * Verifies that the test fails if the mapping table is not in the
     * geopackage.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableInvalidMappingTableNames() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_missing_tables.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The mapping1 table is missing. expected [true] but found [false]");
        tests.mapping_tables();
    }

    /**
     * Verifies that the test passes if the relation name is valid.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableValidRelationNames() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.relation_names();
    }

    /**
     * Verifies that the test passes if the x- relation name is valid.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableValidXDashRelationNames() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_x_relation.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.relation_names();
    }

    // TODO: media
    // TODO: related features
    /**
     * Verifies that the test fails if the relation name is not valid.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableInvalidRelationNames() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related_altname.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The relation name (notvalid) in gpkgext_relations for base table name allCountries is not valid.");
        tests.relation_names();
    }

    /**
     * Verifies that the test fails if the relation name is not valid.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableInvalidXDashRelationNames() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_x_relation_bad.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The relation name (x-bradh) in gpkgext_relations for base table name allCountries is not valid.");
        tests.relation_names();
    }

    /**
     * Verifies that the test passes if the mapping table is valid.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableValidMappingTableSchema() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.mapping_tables_schema();
    }

    /**
     * Verifies that the test fails if the mapping table has the wrong base
     * table column name.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableInvalidMappingTableSchemaBaseColName() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_badmappingbasecolname.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The mapping table mapping1 failed test missing column(s). expected [true] but found [false]");
        tests.mapping_tables_schema();
    }

    /**
     * Verifies that the test fails if the mapping table has the wrong related
     * table column name.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableInvalidMappingTableSchemaRelatedColName() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_badmappingrelatedcolname.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The mapping table mapping1 failed test missing column(s). expected [true] but found [false]");
        tests.mapping_tables_schema();
    }

    /**
     * Verifies that the test fails if the mapping table has the wrong base_id
     * column type.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableInvalidMappingTableSchemaBaseColType() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_badmappingbasecoltype.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The mapping table mapping1 failed test base_id type. expected [INTEGER] but found [TEXT]");
        tests.mapping_tables_schema();
    }

    /**
     * Verifies that the test fails if the mapping table has the wrong
     * related_id column type.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableInvalidMappingTableSchemaRelatedColType() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_badmappingrelatedcoltype.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The mapping table mapping1 failed test related_id type. expected [INTEGER] but found [BLOB]");
        tests.mapping_tables_schema();
    }

    // TODO: bad nullability
    // TODO: bad default values
    // TODO: bad primary key
    // TODO: bad unique key constraints
    /**
     * Verifies that the test passes if the base values correlate.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableValidMappingBaseCorrelation() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.mapping_table_base();
    }

    // TODO: multi-table version
    /**
     * Verifies that the test fails if the related values do not correlate.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableValidMappingBaseNotCorrelation() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_miscorrelate.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("There is no base table row for mapping table mapping1 id 2,000. expected [true] but found [false]");
        tests.mapping_table_base();
    }

    /**
     * Verifies that the test passes if the related values correlate.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableValidMappingRelatedCorrelation() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        tests.mapping_table_related();
    }

    // TODO: multi-table version
    /**
     * Verifies that the test fails if the related values do not correlate.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void relatedTableValidMappingRelatedNotCorrelation() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_miscorrelate.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesTests tests = new RelatedTablesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("There is no related table row for mapping table mapping1 id 3,012. expected [true] but found [false]");
        tests.mapping_table_related();
    }
}
